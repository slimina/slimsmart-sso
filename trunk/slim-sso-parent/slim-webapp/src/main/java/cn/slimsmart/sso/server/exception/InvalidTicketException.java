package cn.slimsmart.sso.server.exception;

public class InvalidTicketException extends TicketException {
	
	private static final long serialVersionUID = 1L;

	private static final String CODE = "INVALID_TICKET";

	public InvalidTicketException() {
		super(CODE);
	}

	public InvalidTicketException(final Throwable throwable) {
		super(CODE, throwable);
	}

}
