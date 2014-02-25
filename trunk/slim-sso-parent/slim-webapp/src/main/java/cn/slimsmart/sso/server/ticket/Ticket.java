package cn.slimsmart.sso.server.ticket;

import java.io.Serializable;

public interface Ticket extends Serializable{
	
	String getId();
	TicketType getTicketType();
	String getTicketCode();
	long getLastTimeUsed();
	long getCreationTime();
	String getUserName();
	boolean equals(final Object object);
	boolean isExpired();
	void updateState();
	
}
