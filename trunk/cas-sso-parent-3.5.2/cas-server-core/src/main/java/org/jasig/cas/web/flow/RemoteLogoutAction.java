package org.jasig.cas.web.flow;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.web.support.ArgumentExtractor;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.util.StringUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class RemoteLogoutAction extends AbstractAction {
	@NotNull
	private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;
	@NotNull
	private CookieRetrievingCookieGenerator warnCookieGenerator;
	@NotNull
	private CentralAuthenticationService centralAuthenticationService;
	@NotEmpty
	private List<ArgumentExtractor> argumentExtractors;
	private boolean pathPopulated = false;

	@Override
	protected Event doExecute(final RequestContext context) throws Exception {
		final HttpServletRequest request = WebUtils
				.getHttpServletRequest(context);
		final HttpServletResponse response = WebUtils
				.getHttpServletResponse(context);

		if (!this.pathPopulated) {
			final String contextPath = context.getExternalContext()
					.getContextPath();
			final String cookiePath = StringUtils.hasText(contextPath) ? contextPath
					: "/";
			logger.info("Setting path for cookies to: " + cookiePath);
			this.warnCookieGenerator.setCookiePath(cookiePath);
			this.ticketGrantingTicketCookieGenerator.setCookiePath(cookiePath);
			this.pathPopulated = true;
		}
		context.getFlowScope().put(
				"ticketGrantingTicketId",
				this.ticketGrantingTicketCookieGenerator
						.retrieveCookieValue(request));
		context.getFlowScope().put(
				"warnCookieValue",
				Boolean.valueOf(this.warnCookieGenerator
						.retrieveCookieValue(request)));
		final Service service = WebUtils.getService(this.argumentExtractors,
				context);
		if (service != null && logger.isDebugEnabled()) {
			logger.debug("Placing service in FlowScope: " + service.getId());
		}
		context.getFlowScope().put("service", service);
		context.getFlowScope().put("remoteLoginUrl",request.getParameter("service"));
		final String ticketGrantingTicketId = this.ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);

		if (ticketGrantingTicketId != null) {
			this.centralAuthenticationService
					.destroyTicketGrantingTicket(ticketGrantingTicketId);

			this.ticketGrantingTicketCookieGenerator.removeCookie(response);
			this.warnCookieGenerator.removeCookie(response);
		}

		return result("success");
	}

	public void setTicketGrantingTicketCookieGenerator(
			final CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator) {
		this.ticketGrantingTicketCookieGenerator = ticketGrantingTicketCookieGenerator;
	}

	public void setWarnCookieGenerator(
			final CookieRetrievingCookieGenerator warnCookieGenerator) {
		this.warnCookieGenerator = warnCookieGenerator;
	}

	public void setArgumentExtractors(
			final List<ArgumentExtractor> argumentExtractors) {
		this.argumentExtractors = argumentExtractors;
	}

	public void setCentralAuthenticationService(
			final CentralAuthenticationService centralAuthenticationService) {
		this.centralAuthenticationService = centralAuthenticationService;
	}
}