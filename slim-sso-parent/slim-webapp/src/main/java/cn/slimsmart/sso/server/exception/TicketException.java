package cn.slimsmart.sso.server.exception;

public abstract class TicketException extends Exception {
	private static final long serialVersionUID = 1L;
	private String code;

	public TicketException(final String code) {
		this.code = code;
	}

	public TicketException(final String code, final Throwable throwable) {
		super(throwable);
		this.code = code;
	}

	public final String getCode() {
		return (this.getCause() != null) ? this.getCause().toString() : this.code;
	}

}
