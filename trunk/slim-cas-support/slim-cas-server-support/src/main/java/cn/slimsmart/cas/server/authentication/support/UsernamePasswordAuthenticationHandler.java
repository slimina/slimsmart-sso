package cn.slimsmart.cas.server.authentication.support;

import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;


public class UsernamePasswordAuthenticationHandler extends
		AbstractUsernamePasswordAuthenticationHandler {

	protected final boolean authenticateUsernamePasswordInternal(
			final UsernamePasswordCredentials credentials) {
		//用户名密码验证接口
		
		PasswordEncoder passwordEncoder = getPasswordEncoder();
		//密码加密MD5
		String  usernameMD5 =  passwordEncoder.encode(credentials.getUsername());
		String  passwordMD5 =  passwordEncoder.encode(credentials.getPassword());
		
		if(!usernameMD5.equals(passwordMD5)){
			return false;
		}
		
		return true;
	}

}
