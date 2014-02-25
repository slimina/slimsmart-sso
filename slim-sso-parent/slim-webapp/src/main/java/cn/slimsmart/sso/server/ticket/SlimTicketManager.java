package cn.slimsmart.sso.server.ticket;


public interface SlimTicketManager {
	void remove(final Ticket ticket);
	Ticket generateTicket(final String userName,final Class<? extends Ticket> Ticket,TGTTicket tGTTicket);
	boolean validateTicket(final Ticket ticket);
	Ticket getTicket(final String ticketId);
	Ticket getTicketByCode(final String ticketCode);
	int serviceTicketCount();

}
