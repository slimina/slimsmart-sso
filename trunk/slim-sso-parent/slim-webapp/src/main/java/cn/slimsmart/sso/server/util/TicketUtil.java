package cn.slimsmart.sso.server.util;

import java.util.UUID;

public final  class TicketUtil {
	
	public final static String getTicketCode(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
