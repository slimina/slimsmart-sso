package cn.slimsmart.cas.server.web.flow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.web.bind.CredentialsBinder;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.util.StringUtils;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.execution.RequestContext;

import com.google.code.kaptcha.Constants;

import cn.slimsmart.cas.server.authentication.handler.BadAuthcodeAuthenticationException;
import cn.slimsmart.cas.server.authentication.handler.NullAuthcodeAuthenticationException;
import cn.slimsmart.cas.server.authentication.principal.UsernamePasswordCredentials;

@SuppressWarnings("deprecation")
public class AuthenticationViaFormAction {

    /**
     * Binder that allows additional binding of form object beyond Spring
     * defaults.
     */
	private CredentialsBinder credentialsBinder;

    /** Core we delegate to for handling all ticket related tasks. */
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    @NotNull
    private CookieGenerator warnCookieGenerator;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public final void doBind(final RequestContext context, final Credentials credentials) throws Exception {
        final HttpServletRequest request = WebUtils.getHttpServletRequest(context);

        if (this.credentialsBinder != null && this.credentialsBinder.supports(credentials.getClass())) {
            this.credentialsBinder.bind(request, credentials);
        }
    }
    
    public final String submit(final RequestContext context, final Credentials credentials, final MessageContext messageContext) throws Exception {
        // Validate login ticket
        final String authoritativeLoginTicket = WebUtils.getLoginTicketFromFlowScope(context);
        final String providedLoginTicket = WebUtils.getLoginTicketFromRequest(context);
        if (!authoritativeLoginTicket.equals(providedLoginTicket)) {
            this.logger.warn("Invalid login ticket " + providedLoginTicket);
            final String code = "INVALID_TICKET";
            messageContext.addMessage(
                new MessageBuilder().error().code(code).arg(providedLoginTicket).defaultText(code).build());
            return "error";
        }
        
        // 验证码 认证
		final HttpSession session = WebUtils.getHttpServletRequest(context).getSession();
		final String kaptcha = (String)session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
		session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
		
		UsernamePasswordCredentials cr = (UsernamePasswordCredentials) credentials;
		if(!StringUtils.hasText(kaptcha) || !kaptcha.equalsIgnoreCase(cr.getAuthcode())){
			this.logger.warn("Invalid login authcode " + cr.getAuthcode());
			messageContext.addMessage(new MessageBuilder().error().code(BadAuthcodeAuthenticationException.CODE).build());
			return "error";
		}

        final String ticketGrantingTicketId = WebUtils.getTicketGrantingTicketId(context);
        final Service service = WebUtils.getService(context);
        if (StringUtils.hasText(context.getRequestParameters().get("renew")) && ticketGrantingTicketId != null && service != null) {

            try {
                final String serviceTicketId = this.centralAuthenticationService.grantServiceTicket(ticketGrantingTicketId, service, credentials);
                WebUtils.putServiceTicketInRequestScope(context, serviceTicketId);
                putWarnCookieIfRequestParameterPresent(context);
                return "warn";
            } catch (final TicketException e) {
                if (e.getCause() != null && AuthenticationException.class.isAssignableFrom(e.getCause().getClass())) {
                    populateErrorsInstance(e, messageContext);
                    return "error";
                }
                this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicketId);
                if (logger.isDebugEnabled()) {
                    logger.debug("Attempted to generate a ServiceTicket using renew=true with different credentials", e);
                }
            }
        }

        try {
            WebUtils.putTicketGrantingTicketInRequestScope(context, this.centralAuthenticationService.createTicketGrantingTicket(credentials));
            putWarnCookieIfRequestParameterPresent(context);
            return "success";
        } catch (final TicketException e) {
            populateErrorsInstance(e, messageContext);
            return "error";
        }
    }

    public final String submit(final RequestContext context, final MessageContext messageContext) throws Exception {
        final String ticketGrantingTicketId = WebUtils.getTicketGrantingTicketId(context);
        final Service service = WebUtils.getService(context);
        final HttpServletRequest request = WebUtils.getHttpServletRequest(context);
        final HttpSession session = request.getSession();
        
        // 验证码 认证
        String authcode = (String)session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
		String submitAuthcode =request.getParameter("authcode");
		
        if(!StringUtils.hasText(submitAuthcode)){
        	context.getFlowScope().put("errorCode", NullAuthcodeAuthenticationException.CODE);
        	return "error";  
        }
        if(!submitAuthcode.equalsIgnoreCase(authcode)){  
        	this.logger.warn("Invalid login authcode " + submitAuthcode);
        	context.getFlowScope().put("errorCode", BadAuthcodeAuthenticationException.CODE);
            return "error";  
        }
        
        String username = request.getParameter("username");
        if(!StringUtils.hasText(username)){
        	 context.getFlowScope().put("errorCode", "required.username");
        	 return "error";
        }
        String password = request.getParameter("password");
        if(!StringUtils.hasText(password)){
	       	 context.getFlowScope().put("errorCode", "required.password");
	       	 return "error";
       }
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        
        if (StringUtils.hasText(context.getRequestParameters().get("renew")) && ticketGrantingTicketId != null && service != null) {
            try {
                final String serviceTicketId = this.centralAuthenticationService.grantServiceTicket(ticketGrantingTicketId, service, credentials);
                WebUtils.putServiceTicketInRequestScope(context, serviceTicketId);
                putWarnCookieIfRequestParameterPresent(context);
                return "warn";
            } catch (final TicketException e) {
                if (e.getCause() != null && AuthenticationException.class.isAssignableFrom(e.getCause().getClass())) {
                    context.getFlowScope().put("errorCode", e.getCode());
                    return "error";
                }
                this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicketId);
                if (logger.isDebugEnabled()) {
                    logger.debug("Attempted to generate a ServiceTicket using renew=true with different credentials", e);
                }
            }
        }
        try {
            WebUtils.putTicketGrantingTicketInRequestScope(context, this.centralAuthenticationService.createTicketGrantingTicket(credentials));
            putWarnCookieIfRequestParameterPresent(context);
            context.getFlowScope().remove("errorCode");
            return "success";
        } catch (final TicketException e) {
            context.getFlowScope().put("errorCode", e.getCode());
            return "error";
        }
    }
    
    private void populateErrorsInstance(final TicketException e, final MessageContext messageContext) {
        try {
            messageContext.addMessage(new MessageBuilder().error().code(e.getCode()).defaultText(e.getCode()).build());
        } catch (final Exception fe) {
            logger.error(fe.getMessage(), fe);
        }
    }

    private void putWarnCookieIfRequestParameterPresent(final RequestContext context) {
        final HttpServletResponse response = WebUtils.getHttpServletResponse(context);

        if (StringUtils.hasText(context.getExternalContext().getRequestParameterMap().get("warn"))) {
            this.warnCookieGenerator.addCookie(response, "true");
        } else {
            this.warnCookieGenerator.removeCookie(response);
        }
    }

    public final void setCentralAuthenticationService(final CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    /**
     * Set a CredentialsBinder for additional binding of the HttpServletRequest
     * to the Credentials instance, beyond our default binding of the
     * Credentials as a Form Object in Spring WebMVC parlance. By the time we
     * invoke this CredentialsBinder, we have already engaged in default binding
     * such that for each HttpServletRequest parameter, if there was a JavaBean
     * property of the Credentials implementation of the same name, we have set
     * that property to be the value of the corresponding request parameter.
     * This CredentialsBinder plugin point exists to allow consideration of
     * things other than HttpServletRequest parameters in populating the
     * Credentials (or more sophisticated consideration of the
     * HttpServletRequest parameters).
     *
     * @param credentialsBinder the credentials binder to set.
     */
    public final void setCredentialsBinder(final CredentialsBinder credentialsBinder) {
        this.credentialsBinder = credentialsBinder;
    }
    
    public final void setWarnCookieGenerator(final CookieGenerator warnCookieGenerator) {
        this.warnCookieGenerator = warnCookieGenerator;
    }
}
