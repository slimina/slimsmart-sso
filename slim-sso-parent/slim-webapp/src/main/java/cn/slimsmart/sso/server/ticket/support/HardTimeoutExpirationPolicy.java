package cn.slimsmart.sso.server.ticket.support;

import cn.slimsmart.sso.server.ticket.Ticket;

public final class HardTimeoutExpirationPolicy implements ExpirationPolicy{

	private static final long serialVersionUID = 1L;
	private final long timeToKillInMilliSeconds;

    public HardTimeoutExpirationPolicy(final long timeToKillInMilliSeconds) {
        this.timeToKillInMilliSeconds = timeToKillInMilliSeconds*1000;
    }

	public boolean isExpired(Ticket ticket) {
		return (ticket == null)
				|| (System.currentTimeMillis() - ticket.getCreationTime() >= this.timeToKillInMilliSeconds);
	}
}
