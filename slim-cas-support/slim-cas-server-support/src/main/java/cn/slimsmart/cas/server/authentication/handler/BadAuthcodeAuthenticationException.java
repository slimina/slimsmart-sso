package cn.slimsmart.cas.server.authentication.handler;

import org.jasig.cas.ticket.TicketException;

public class BadAuthcodeAuthenticationException extends TicketException {
	
	/** Serializable ID for unique id. */
    private static final long serialVersionUID = 5501212207531289993L;

    /** Code description. */
    public static final String CODE = "error.authentication.authcode.bad";

    /**
     * Constructs a TicketCreationException with the default exception code.
     */
    public BadAuthcodeAuthenticationException() {
        super(CODE);
    }

    /**
     * Constructs a TicketCreationException with the default exception code and
     * the original exception that was thrown.
     * 
     * @param throwable the chained exception
     */
    public BadAuthcodeAuthenticationException(final Throwable throwable) {
        super(CODE, throwable);
    }}
