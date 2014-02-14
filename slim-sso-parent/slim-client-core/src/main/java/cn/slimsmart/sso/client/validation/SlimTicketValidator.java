package cn.slimsmart.sso.client.validation;

import cn.slimsmart.sso.client.exception.TicketValidationException;
import cn.slimsmart.sso.client.session.UserSession;

public class SlimTicketValidator implements TicketValidator{

	public UserSession validate(String ticket, String service) throws TicketValidationException {
		return null;
	}

}
