package cn.slimsmart.sso.server.model;

public class LoginUser extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String authcode;
	private String password;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAuthcode() {
		return authcode;
	}
	public void setAuthcode(String authcode) {
		this.authcode = authcode;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
