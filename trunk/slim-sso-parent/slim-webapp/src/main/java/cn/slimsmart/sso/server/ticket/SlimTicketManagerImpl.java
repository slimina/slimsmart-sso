package cn.slimsmart.sso.server.ticket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.slimsmart.sso.server.ticket.support.ExpirationPolicy;
import cn.slimsmart.sso.server.util.TicketUtil;

@Service
public final class SlimTicketManagerImpl implements SlimTicketManager {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ExpirationPolicy expirationPolicy;
	
	//id --> Ticket
	private final Map<String,Ticket> tickets = new ConcurrentHashMap<String, Ticket>();
	//ST id --> TGT id
	private final Map<String,String> ticketMap = new ConcurrentHashMap<String, String>();
	// code --> id
	private final Map<String,String> codeIdMap = new ConcurrentHashMap<String, String>();
	
	
	public synchronized void remove(final Ticket ticket) {
		Assert.notNull(ticket, "ticket cannot be null");
		tickets.remove(ticket.getId());
		codeIdMap.remove(ticket.getTicketCode());
		ticketMap.remove(ticket.getId());
		
	}
	public Ticket generateTicket(final String userName,final Class<? extends Ticket> clazz,final TGTTicket tGTTicket) {
		Assert.notNull(userName, "userName cannot be null");
		Assert.notNull(clazz, "clazz cannot be null");
		
		String id = TicketUtil.getTicketCode();
		String code = TicketUtil.getTicketCode();
		
		Ticket ticket = null;
		if(clazz.isAssignableFrom(STTicket.class)){
			Assert.notNull(tGTTicket, "tGTTicket cannot be null");
			ticket = new STTicket(id,code,userName,expirationPolicy);
			synchronized (ticketMap) {
				ticketMap.put(id, tGTTicket.getId());
			}
			
		}else{
			ticket = new TGTTicket(id,code,userName,expirationPolicy);
		}
		synchronized (tickets) {
			tickets.put(id, ticket);
		}
		synchronized (codeIdMap) {
			codeIdMap.put(code, id);
		}
		return ticket;
	}
	
	public synchronized boolean validateTicket(final Ticket ticket) {
		Assert.notNull(ticket, "ticket cannot be null");
		ticket.updateState();
		if(ticket.getTicketType().equals(TicketType.ST)){
			if(tickets.containsKey(ticket.getId()) &&
					ticketMap.containsKey(ticket.getId()) && 
					codeIdMap.containsKey(ticket.getTicketCode()) 
					){
				return true;
			}
		}else{
			if(tickets.containsKey(ticket.getId()) &&
					codeIdMap.containsKey(ticket.getTicketCode()) 
					){
				return true;
			}
		}
		return false;
	}
	
	public final Ticket getTicket(final String ticketId){
		Assert.notNull(ticketId, "ticketId cannot be null");
		
		Ticket ticket = null;
		synchronized (tickets) {
			ticket = tickets.get(ticketId);
		}
		return ticket;
	}
	
	public final int serviceTicketCount() {
		int size = 0;
		synchronized (ticketMap) {
			size = ticketMap.size();
		}
		return size;
	}
	
	public final Ticket getTicketByCode(String ticketCode) {
		log.debug("getTicketByCode ticketCode="+ticketCode);
		Assert.notNull(ticketCode, "ticketCode cannot be null");
		String ticketId = null;
		synchronized (codeIdMap){
			ticketId = codeIdMap.get(ticketCode);
		}		
		return StringUtils.isEmpty(ticketId) ?  null : getTicket(ticketId);
	}
}
