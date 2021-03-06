/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.ticket.registry;

import java.util.ArrayList;
import java.util.Collection;

import org.jasig.cas.TestUtils;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.support.NeverExpiresExpirationPolicy;
import junit.framework.TestCase;

/**
 * @author Scott Battaglia
 * @version $Revision: 42053 $ $Date: 2007-06-10 09:17:55 -0400 (Sun, 10 Jun 2007) $
 * @since 3.0
 */
public abstract class AbstractTicketRegistryTests extends TestCase {

    private static final int TICKETS_IN_REGISTRY = 10;

    private TicketRegistry ticketRegistry;

    protected void setUp() throws Exception {
        super.setUp();
        this.ticketRegistry = this.getNewTicketRegistry();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Abstract method to retrieve a new ticket registry. Implementing classes
     * return the TicketRegistry they wish to test.
     * 
     * @return the TicketRegistry we wish to test
     */
    public abstract TicketRegistry getNewTicketRegistry() throws Exception;

    /**
     * Method to add a TicketGrantingTicket to the ticket cache. This should add
     * the ticket and return. Failure upon any exception.
     */
    public void testAddTicketToCache() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST",
                TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy()));
        } catch (Exception e) {
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    public void testGetNullTicket() {
        try {
            this.ticketRegistry.getTicket(null, TicketGrantingTicket.class);
        } catch (Exception e) {
            fail("Exception caught.  None expected.");
        }
    }

    public void testGetNonExistingTicket() {
        try {
            this.ticketRegistry.getTicket("FALALALALALAL",
                TicketGrantingTicket.class);
        } catch (Exception e) {
            fail("Exception caught.  None expected.");
        }
    }

    public void testGetExistingTicketWithProperClass() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST",
                TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy()));
            this.ticketRegistry.getTicket("TEST", TicketGrantingTicket.class);
        } catch (Exception e) {
            System.out.println(e);
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    public void testGetExistingTicketWithInproperClass() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST",
                TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy()));
            this.ticketRegistry.getTicket("TEST", ServiceTicket.class);
        } catch (ClassCastException e) {
            return;
        }
        fail("ClassCastException expected.");
    }

    public void testGetNullTicketWithoutClass() {
        try {
            this.ticketRegistry.getTicket(null);
        } catch (Exception e) {
            fail("Exception caught.  None expected.");
        }
    }

    public void testGetNonExistingTicketWithoutClass() {
        try {
            this.ticketRegistry.getTicket("FALALALALALAL");
        } catch (Exception e) {
            fail("Exception caught.  None expected.");
        }
    }

    public void testGetExistingTicket() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST",
                TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy()));
            this.ticketRegistry.getTicket("TEST");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    public void testDeleteExistingTicket() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST",
                TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy()));
            assertTrue("Ticket was not deleted.", this.ticketRegistry
                .deleteTicket("TEST"));
        } catch (Exception e) {
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    public void testDeleteNonExistingTicket() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST",
                TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy()));
            assertFalse("Ticket was deleted.", this.ticketRegistry
                .deleteTicket("TEST1"));
        } catch (Exception e) {
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    public void testDeleteNullTicket() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST",
                TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy()));
            assertFalse("Ticket was deleted.", this.ticketRegistry
                .deleteTicket(null));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    public void testGetTicketsIsZero() {
        try {
            assertEquals("The size of the empty registry is not zero.",
                this.ticketRegistry.getTickets().size(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    public void testGetTicketsFromRegistryEqualToTicketsAdded() {
        final Collection<Ticket> tickets = new ArrayList<Ticket>();

        for (int i = 0; i < TICKETS_IN_REGISTRY; i++) {
            final TicketGrantingTicket ticketGrantingTicket = new TicketGrantingTicketImpl(
                "TEST" + i, TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy());
            final ServiceTicket st = ticketGrantingTicket.grantServiceTicket("tests" + i, TestUtils.getService(), new NeverExpiresExpirationPolicy(), false);
            tickets.add(ticketGrantingTicket);
            tickets.add(st);
            this.ticketRegistry.addTicket(ticketGrantingTicket);
            this.ticketRegistry.addTicket(st);
        }
        
        try {
            Collection<Ticket> ticketRegistryTickets = this.ticketRegistry.getTickets();
            assertEquals(
                "The size of the registry is not the same as the collection.",
                ticketRegistryTickets.size(), tickets.size());

            for (final Ticket ticket : tickets) {
                if (!ticketRegistryTickets.contains(ticket)) {
                    fail("Ticket was added to registry but was not found in retrieval of collection of all tickets.");
                }
            }
        } catch (Exception e) {
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }
}
