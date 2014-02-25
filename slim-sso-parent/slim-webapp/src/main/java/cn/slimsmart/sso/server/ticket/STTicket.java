package cn.slimsmart.sso.server.ticket;

import cn.slimsmart.sso.server.ticket.support.ExpirationPolicy;

public class STTicket extends AbstractTicket {
	
	private static final long serialVersionUID = 1L;
	
	private final static String PREFIX = "ST";
	
	public STTicket(final String id, final String ticketCode,final String userName,final ExpirationPolicy expirationPolicy) {
		super(id, PREFIX+"-"+ticketCode,userName,TicketType.ST,expirationPolicy);
	}
}
