/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.ticket;

import org.jasig.cas.TestUtils;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.support.MultiTimeUseOrTimeoutExpirationPolicy;
import org.jasig.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.jasig.cas.util.DefaultUniqueTicketIdGenerator;
import org.jasig.cas.util.UniqueTicketIdGenerator;

import junit.framework.TestCase;

/**
 * @author Scott Battaglia
 * @version $Revision: 43298 $ $Date: 2008-03-03 13:04:24 -0500 (Mon, 03 Mar 2008) $
 * @since 3.0
 */
public class ServiceTicketImplTests extends TestCase {

    private TicketGrantingTicketImpl ticketGrantingTicket = new TicketGrantingTicketImpl(
        "test", TestUtils.getAuthentication(),
        new NeverExpiresExpirationPolicy());

    private UniqueTicketIdGenerator uniqueTicketIdGenerator = new DefaultUniqueTicketIdGenerator();

    public void testNoService() {
        try {
            new ServiceTicketImpl("stest1", this.ticketGrantingTicket, null,
                true, new NeverExpiresExpirationPolicy());
            fail("Exception expected.");
        } catch (Exception e) {
            // ok
        }
    }

    public void testNoTicket() {
        try {
            new ServiceTicketImpl("stest1", null, TestUtils.getService(),
                true, new NeverExpiresExpirationPolicy());
            fail("Exception expected.");
        } catch (Exception e) {
            // ok
        }
    }

    public void testIsFromNewLoginTrue() {
        ServiceTicket s = new ServiceTicketImpl("stest1",
            this.ticketGrantingTicket, TestUtils.getService(), true,
            new NeverExpiresExpirationPolicy());
        assertTrue(s.isFromNewLogin());
    }

    public void testIsFromNewLoginFalse() {
        ServiceTicket s = new ServiceTicketImpl("stest1",
            this.ticketGrantingTicket, TestUtils.getService(), false,
            new NeverExpiresExpirationPolicy());
        assertFalse(s.isFromNewLogin());
    }

    public void testGetService() {
        Service simpleService = TestUtils.getService();
        ServiceTicket s = new ServiceTicketImpl("stest1",
            this.ticketGrantingTicket, simpleService, false,
            new NeverExpiresExpirationPolicy());
        assertEquals(simpleService, s.getService());
    }

    public void testGetTicket() {
        Service simpleService = TestUtils.getService();
        ServiceTicket s = new ServiceTicketImpl("stest1",
            this.ticketGrantingTicket, simpleService, false,
            new NeverExpiresExpirationPolicy());
        assertEquals(this.ticketGrantingTicket, s.getGrantingTicket());
    }

    public void testIsExpiredTrueBecauseOfRoot() {
        TicketGrantingTicket t = new TicketGrantingTicketImpl("test", TestUtils
            .getAuthentication(), new NeverExpiresExpirationPolicy());
        ServiceTicket s = t.grantServiceTicket(this.uniqueTicketIdGenerator
            .getNewTicketId(ServiceTicket.PREFIX), TestUtils.getService(),
            new NeverExpiresExpirationPolicy(), false);
        t.expire();

        assertTrue(s.isExpired());
    }

    public void testIsExpiredFalse() {
        TicketGrantingTicket t = new TicketGrantingTicketImpl("test", TestUtils
            .getAuthentication(), new NeverExpiresExpirationPolicy());
        ServiceTicket s = t.grantServiceTicket(this.uniqueTicketIdGenerator
            .getNewTicketId(ServiceTicket.PREFIX), TestUtils.getService(),
            new MultiTimeUseOrTimeoutExpirationPolicy(1, 5000), false);
        assertFalse(s.isExpired());
    }

    public void testTicketGrantingTicket() {
        Authentication a = TestUtils.getAuthentication();
        TicketGrantingTicket t = new TicketGrantingTicketImpl("test", TestUtils
            .getAuthentication(), new NeverExpiresExpirationPolicy());
        ServiceTicket s = t.grantServiceTicket(this.uniqueTicketIdGenerator
            .getNewTicketId(ServiceTicket.PREFIX), TestUtils.getService(),
            new MultiTimeUseOrTimeoutExpirationPolicy(1, 5000), false);
        TicketGrantingTicket t1 = s.grantTicketGrantingTicket(
            this.uniqueTicketIdGenerator
                .getNewTicketId(TicketGrantingTicket.PREFIX), a,
            new NeverExpiresExpirationPolicy());

        assertEquals(a, t1.getAuthentication());
    }
    
    public void testTicketGrantingTicketGrantedTwice() {
        Authentication a = TestUtils.getAuthentication();
        TicketGrantingTicket t = new TicketGrantingTicketImpl("test", TestUtils
            .getAuthentication(), new NeverExpiresExpirationPolicy());
        ServiceTicket s = t.grantServiceTicket(this.uniqueTicketIdGenerator
            .getNewTicketId(ServiceTicket.PREFIX), TestUtils.getService(),
            new MultiTimeUseOrTimeoutExpirationPolicy(1, 5000), false);
        s.grantTicketGrantingTicket(
            this.uniqueTicketIdGenerator
                .getNewTicketId(TicketGrantingTicket.PREFIX), a,
            new NeverExpiresExpirationPolicy());
        
        try {
            s.grantTicketGrantingTicket(this.uniqueTicketIdGenerator
                .getNewTicketId(TicketGrantingTicket.PREFIX), a,
            new NeverExpiresExpirationPolicy());
            fail("Exception expected.");
        } catch (Exception e) {
            return;
        }
    }
}
