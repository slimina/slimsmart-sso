package cn.slimsmart.sso.server.exception;

public class TicketValidationException extends TicketException {

	private static final long serialVersionUID = 1L;
	private static final String CODE = "INVALID_SERVICE";
	
	public TicketValidationException(String code) {
		super(code);
	}
	
	public TicketValidationException(final Throwable throwable) {
		super(CODE, throwable);
	}
}
