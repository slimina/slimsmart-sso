/*
 * All rights Reserved, tianwei7518.
 * Copyright(C) 2014-2015
 * 2014年2月20日 
 */

package cn.slimsmart.sso.client.exception;

public class TicketValidationException extends Exception {

	private static final long serialVersionUID = 1L;

	public TicketValidationException(final String string) {
		super(string);
	}

	public TicketValidationException(final String string, final Throwable throwable) {
		super(string, throwable);
	}

	public TicketValidationException(final Throwable throwable) {
		super(throwable);
	}
}
