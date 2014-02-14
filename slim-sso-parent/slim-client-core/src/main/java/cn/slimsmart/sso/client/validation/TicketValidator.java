package cn.slimsmart.sso.client.validation;

import cn.slimsmart.sso.client.exception.TicketValidationException;
import cn.slimsmart.sso.client.session.UserSession;

public interface TicketValidator {
	
	 UserSession validate(String ticket, String service) throws TicketValidationException;

}
