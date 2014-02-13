/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.authentication.handler;

import junit.framework.TestCase;

/**
 * @author Scott Battaglia
 * @version $Revision: 39552 $ $Date: 2007-01-22 15:35:37 -0500 (Mon, 22 Jan 2007) $
 * @since 3.0
 */
public final class UnknownUsernameAuthenticationExceptionTests extends TestCase {

    private static final String CODE = "error.authentication.credentials.bad.usernameorpassword.username";
    
    public void testGetCode() {
        AuthenticationException e = new UnknownUsernameAuthenticationException();
        assertEquals(CODE, e.getCode());
        assertEquals(CODE, e.toString());
    }
    
    public void testThrowableConstructor() {
        final RuntimeException r = new RuntimeException();
        final UnknownUsernameAuthenticationException e = new UnknownUsernameAuthenticationException(r);
        
        assertEquals(CODE, e.getCode());
        assertEquals(r, e.getCause());
    }
    
    public void testCodeConstructor() {
        final String MESSAGE = "GG";
        final UnknownUsernameAuthenticationException e = new UnknownUsernameAuthenticationException(MESSAGE);
        
        assertEquals(MESSAGE, e.getCode());
    }
    
    public void testThrowableConstructorWithCode() {
        final String MESSAGE = "GG";
        final RuntimeException r = new RuntimeException();
        final UnknownUsernameAuthenticationException e = new UnknownUsernameAuthenticationException(MESSAGE, r);
        
        assertEquals(MESSAGE, e.getCode());
        assertEquals(r, e.getCause());
    }
}
