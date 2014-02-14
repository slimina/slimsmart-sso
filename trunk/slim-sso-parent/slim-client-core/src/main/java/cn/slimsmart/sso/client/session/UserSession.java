package cn.slimsmart.sso.client.session;

import java.io.Serializable;

public class UserSession implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String username;
	
	private String ticket;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
}
