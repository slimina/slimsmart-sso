/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.uportal.org/license.html
 */
package org.jasig.cas.audit.spi;

import org.aspectj.lang.JoinPoint;
import org.inspektr.audit.spi.AuditableResourceResolver;

/**
 * Implementation of the ResourceResolver that can determine the Ticket Id from the first parameter of the method call.

 * @author Scott Battaglia
 * @version $Revision: 1.1 $ $Date: 2005/08/19 18:27:17 $
 * @since 3.1.2
 *
 */
public final class TicketAsFirstParameterResourceResolver implements
    AuditableResourceResolver {

    public String resolveFrom(final JoinPoint joinPoint, final Exception exception) {
        return joinPoint.getArgs()[0].toString();
    }

    public String resolveFrom(final JoinPoint joinPoint, final Object object) {
        return joinPoint.getArgs()[0].toString();
    }
}
