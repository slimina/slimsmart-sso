/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.authentication.principal;

import org.jasig.cas.TestUtils;

import junit.framework.TestCase;

/**
 * @author Scott Battaglia
 * @version $Revision: 42053 $ $Date: 2007-06-10 09:17:55 -0400 (Sun, 10 Jun 2007) $
 * @since 3.0
 */
public class UsernamePasswordCredentialsTests extends TestCase {

    public void testSetGetUsername() {
        final UsernamePasswordCredentials c = new UsernamePasswordCredentials();
        final String userName = "test";

        c.setUsername(userName);

        assertEquals(userName, c.getUsername());
    }

    public void testSetGetPassword() {
        final UsernamePasswordCredentials c = new UsernamePasswordCredentials();
        final String password = "test";

        c.setPassword(password);

        assertEquals(password, c.getPassword());
    }
    
    public void testEquals() {
        assertFalse(TestUtils.getCredentialsWithDifferentUsernameAndPassword().equals(null));
        assertFalse(TestUtils.getCredentialsWithDifferentUsernameAndPassword().equals(TestUtils.getCredentialsWithSameUsernameAndPassword()));
        assertTrue(TestUtils.getCredentialsWithDifferentUsernameAndPassword().equals(TestUtils.getCredentialsWithDifferentUsernameAndPassword()));
    }
    
    public void testHashCode() {
        final UsernamePasswordCredentials c = TestUtils.getCredentialsWithDifferentUsernameAndPassword();
        
        assertEquals(c.getUsername().hashCode() ^ c.getPassword().hashCode(), c.hashCode());
    }
        
    }
