package cn.slimsmart.sso.server.ticket;

import org.springframework.util.Assert;

import cn.slimsmart.sso.server.ticket.support.ExpirationPolicy;

public abstract class AbstractTicket implements Ticket{
	
	private static final long serialVersionUID = 1L;
	private TicketType ticketType;
	
	private String id;
	private long lastTimeUsed;
	private long creationTime;
	private String userName;
	
	private String ticketCode;
	
	private ExpirationPolicy expirationPolicy;
	
	public AbstractTicket(final String id,final String ticketCode,final String userName,
			final TicketType ticketType,final ExpirationPolicy expirationPolicy){
		
		Assert.notNull(id, "id cannot be null");
		Assert.notNull(ticketCode, "ticketCode cannot be null");
		Assert.notNull(ticketType, "ticketType cannot be null");
		
		this.id = id;
        this.creationTime = System.currentTimeMillis();
        this.lastTimeUsed = System.currentTimeMillis();
        this.ticketCode = ticketCode;
        this.userName = userName;
        this.ticketType = ticketType;
        this.expirationPolicy = expirationPolicy;
	}
	
	public void updateState() {
        this.lastTimeUsed = System.currentTimeMillis();
    }
	
	public String getId() {
		return id;
	}
	public long getLastTimeUsed() {
		return lastTimeUsed;
	}
	public long getCreationTime() {
		return creationTime;
	}
	public String getTicketCode() {
		return ticketCode;
	}
	
	public TicketType getTicketType() {
		return ticketType;
	}

	public final int hashCode() {
        return 34 ^ this.getId().hashCode();
    }

    public final String toString() {
        return this.id;
    }
    
    public String getUserName() {
		return userName;
	}


	public final boolean equals(final Object object) {
        if (object == null
            || !(object instanceof AbstractTicket)) {
            return false;
        }
        final Ticket ticket = (Ticket) object;
        return ticket.getId().equals(this.getId());
    }

	public boolean isExpired() {
		return expirationPolicy.isExpired(this);
	}
	
}
