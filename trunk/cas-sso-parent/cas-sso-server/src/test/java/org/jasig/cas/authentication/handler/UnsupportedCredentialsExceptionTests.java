/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.authentication.handler;

import junit.framework.TestCase;

/**
 * @author Scott Battaglia
 * @version $Revision: 42053 $ $Date: 2007-06-10 09:17:55 -0400 (Sun, 10 Jun 2007) $
 * @since 3.0
 */
public class UnsupportedCredentialsExceptionTests extends TestCase {
    
    private static final String CODE = "error.authentication.credentials.unsupported";

    public void testNoParamConstructor() {
        new UnsupportedCredentialsException();
    }

    public void testGetCode() {
        assertEquals(CODE,
            new UnsupportedCredentialsException().getCode());
    }
    
    public void testThrowableConstructor() {
        final RuntimeException r = new RuntimeException();
        final UnsupportedCredentialsException e = new UnsupportedCredentialsException(r);
        
        assertEquals(CODE, e.getCode());
        assertEquals(r, e.getCause());
    }
    
    
}
