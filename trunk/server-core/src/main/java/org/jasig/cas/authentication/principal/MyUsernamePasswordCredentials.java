package org.jasig.cas.authentication.principal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MyUsernamePasswordCredentials extends UsernamePasswordCredentials {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 1, message = "required.authcode")
	// 验证码
	private String authcode;

	public String getAuthcode() {
		return authcode;
	}

	public void setAuthcode(String authcode) {
		this.authcode = authcode;
	}

	@Override
	public boolean equals(final Object o) {
		if (super.equals(o)) {
			MyUsernamePasswordCredentials that = (MyUsernamePasswordCredentials) o;
			if (authcode != null ? authcode.equals(that.authcode)
					: that.authcode == null)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() * 31
				+ (authcode != null ? authcode.hashCode() : 0);
	}
}
