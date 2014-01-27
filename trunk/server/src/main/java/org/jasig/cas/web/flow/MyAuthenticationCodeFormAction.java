package org.jasig.cas.web.flow;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.MyUsernamePasswordCredentials;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.execution.RequestContext;

/**
 * 验证码
 * @author Zhu.TW
 *
 */
public class MyAuthenticationCodeFormAction extends AuthenticationViaFormAction {

	private final String ERROR = "error";
	private final String SUCCESS = "success";

	public final String validatorCode(final RequestContext context,
			final Credentials credentials, final MessageContext messageContext)
			throws Exception {
		
		HttpSession session = WebUtils.getHttpServletRequest(context).getSession();
		String authcode = (String)session.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
		session.removeAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
		
		MyUsernamePasswordCredentials mupc = (MyUsernamePasswordCredentials) credentials;
		if (StringUtils.isBlank(mupc.getAuthcode())
				|| StringUtils.isBlank(authcode))
			return ERROR;
		if (mupc.getAuthcode().equals(authcode)) {
			return SUCCESS;
		}
		MessageBuilder msgBuilder = new MessageBuilder();
		msgBuilder.defaultText("验证码错误");
		messageContext.addMessage(msgBuilder.error().build());
		return ERROR;
	}

}
