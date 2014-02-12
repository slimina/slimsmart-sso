package org.jasig.cas.web.flow;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class MyAuthenticationViaFormAction extends AuthenticationViaFormAction {

	public final Event validatorCode(final RequestContext context)
			throws Exception {
		
		HttpSession session = WebUtils.getHttpServletRequest(context).getSession();
		String authcode = (String)session.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
		session.removeAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
		
		final String authcodeParam = context.getRequestParameters().get("authcode");
		
		if (StringUtils.isBlank(authcodeParam) || StringUtils.isBlank(authcode)){
			context.getFlowScope().put("authcodeError", "required.authcode");
			return error();
		}
			
		if (authcodeParam.equals(authcode)) {
			context.getFlowScope().remove("authcodeError");
			return success();
		}
		context.getFlowScope().put("authcodeError", "error.authcode.bad");
		return error();
	}

}
