package cn.slimsmart.sso.client.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.slimsmart.sso.client.exception.TicketValidationException;
import cn.slimsmart.sso.client.session.UserSession;
import cn.slimsmart.sso.client.util.CommonUtil;
import cn.slimsmart.sso.client.validation.TicketValidator;

public abstract class AbstractTicketValidationFilter extends AbstractFilter {

	protected final Log log = LogFactory.getLog(getClass());

	private TicketValidator ticketValidator;

	private boolean useSession = true;

	private boolean redirectAfterValidation = false;
	private boolean exceptionOnValidationFailure = true;

	protected boolean preFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException,
			ServletException {
		return true;
	}

	protected void onSuccessfulValidation(final HttpServletRequest request, final HttpServletResponse response, final UserSession userSession) {
		// nothing to do here.
	}

	protected void onFailedValidation(final HttpServletRequest request, final HttpServletResponse response) {
		// nothing to do here.
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		if (!preFilter(servletRequest, servletResponse, filterChain)) {
			return;
		}

		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;
		final String ticket = CommonUtil.safeGetParameter(request, getArtifactParameterName());

		if (StringUtils.isNotBlank(ticket)) {
			if (log.isDebugEnabled()) {
				log.debug("Attempting to validate ticket: " + ticket);
			}

			try {
				final UserSession userSession = this.ticketValidator.validate(ticket, constructServiceUrl(request, response));

				if (log.isDebugEnabled()) {
					log.debug("Successfully authenticated user: " + userSession.getUsername());
				}

				request.setAttribute(CONST_SLIM_USERSESSION, userSession);

				if (this.useSession) {
					request.getSession().setAttribute(CONST_SLIM_USERSESSION, userSession);
				}
				onSuccessfulValidation(request, response, userSession);

				if (this.redirectAfterValidation) {
					log.debug("Redirecting after successful ticket validation.");
					response.sendRedirect(constructServiceUrl(request, response));
					return;
				}
			} catch (final TicketValidationException e) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				log.warn(e, e);
				onFailedValidation(request, response);

				if (this.exceptionOnValidationFailure) {
					throw new ServletException(e);
				}

				return;
			}
		}
		filterChain.doFilter(request, response);
	}

	public final void setTicketValidator(final TicketValidator ticketValidator) {
		this.ticketValidator = ticketValidator;
	}

	public final void setUseSession(final boolean useSession) {
		this.useSession = useSession;
	}
}
