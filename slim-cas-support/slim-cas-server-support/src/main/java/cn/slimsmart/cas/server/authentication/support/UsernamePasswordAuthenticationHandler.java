package cn.slimsmart.cas.server.authentication.support;

import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;


public class UsernamePasswordAuthenticationHandler extends
		AbstractUsernamePasswordAuthenticationHandler {

	protected final boolean authenticateUsernamePasswordInternal(
			final UsernamePasswordCredentials credentials) {
		
		return true;
	}

}
