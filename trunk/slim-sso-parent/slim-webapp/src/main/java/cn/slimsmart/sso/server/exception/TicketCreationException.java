package cn.slimsmart.sso.server.exception;

public class TicketCreationException extends TicketException {

	private static final long serialVersionUID = 1L;
	private static final String CODE = "CREATION_ERROR";

	public TicketCreationException(String code) {
		super(code);
	}

	public TicketCreationException(final Throwable throwable) {
		super(CODE, throwable);
	}
}
