/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.ticket.registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.ticket.Ticket;
import org.springframework.util.Assert;

/**
 * @author Scott Battaglia
 * @version $Revision: 42053 $ $Date: 2007-06-10 09:17:55 -0400 (Sun, 10 Jun 2007) $
 * @since 3.0.4
 * <p>
 * This is a published and supported CAS Server 3 API.
 * </p>
 */
public abstract class AbstractTicketRegistry implements TicketRegistry {

    /** The Commons Logging log instance. */
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * @throws IllegalArgumentException if class is null.
     * @throws ClassCastException if class does not match requested ticket
     * class.
     */
    public final Ticket getTicket(final String ticketId,
        final Class<? extends Ticket> clazz) {
        Assert.notNull(clazz, "clazz cannot be null");

        final Ticket ticket = this.getTicket(ticketId);

        if (ticket == null) {
            return null;
        }

        if (!clazz.isAssignableFrom(ticket.getClass())) {
            throw new ClassCastException("Ticket [" + ticket.getId()
                + " is of type " + ticket.getClass()
                + " when we were expecting " + clazz);
        }

        return ticket;
    }
}
