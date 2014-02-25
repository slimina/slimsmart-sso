package cn.slimsmart.sso.server.ticket;

import cn.slimsmart.sso.server.ticket.support.ExpirationPolicy;

public class TGTTicket extends AbstractTicket {

	private static final long serialVersionUID = 1L;
	
	private final static String PREFIX = "TGT";

	public TGTTicket(final String id, final String ticketCode,final String userName,final ExpirationPolicy expirationPolicy) {
		super(id, PREFIX+"-"+ticketCode,userName,TicketType.TGT,expirationPolicy);
	}

}
