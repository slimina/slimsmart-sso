package cn.slimsmart.sso.server.ticket.support;

import java.io.Serializable;

import cn.slimsmart.sso.server.ticket.Ticket;

public interface ExpirationPolicy extends Serializable {
	 boolean isExpired(Ticket ticket);
}
