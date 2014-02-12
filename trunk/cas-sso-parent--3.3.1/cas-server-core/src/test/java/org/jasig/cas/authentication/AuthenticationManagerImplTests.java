/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.authentication;

import java.util.Arrays;

import org.jasig.cas.AbstractCentralAuthenticationServiceTest;
import org.jasig.cas.TestUtils;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.handler.UnsupportedCredentialsException;
import org.jasig.cas.authentication.handler.support.HttpBasedServiceCredentialsAuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentialsToPrincipalResolver;
import org.jasig.cas.util.HttpClient;

/**
 * @author Scott Battaglia
 * @version $Revision: 42053 $ $Date: 2007-06-10 09:17:55 -0400 (Sun, 10 Jun 2007) $
 * @since 3.0
 */
public class AuthenticationManagerImplTests extends
    AbstractCentralAuthenticationServiceTest {

    public void testSuccessfulAuthentication() throws Exception {
        assertEquals(TestUtils.getPrincipal(),
            getAuthenticationManager().authenticate(
                TestUtils.getCredentialsWithSameUsernameAndPassword())
                .getPrincipal());
    }

    public void testFailedAuthentication() throws Exception {
        try {
            getAuthenticationManager().authenticate(
                TestUtils.getCredentialsWithDifferentUsernameAndPassword());
            fail("Authentication should have failed.");
        } catch (AuthenticationException e) {
            return;
        }
    }

    public void testNoHandlerFound() throws AuthenticationException {
        try {
            getAuthenticationManager().authenticate(new Credentials(){

                private static final long serialVersionUID = -4897240037527663222L;
                // there is nothing to do here
            });
            fail("Authentication should have failed.");
        } catch (UnsupportedCredentialsException e) {
            return;
        }
    }

    public void testNoResolverFound() throws Exception {
        AuthenticationManagerImpl manager = new AuthenticationManagerImpl();
        HttpBasedServiceCredentialsAuthenticationHandler authenticationHandler = new HttpBasedServiceCredentialsAuthenticationHandler();
        authenticationHandler.setHttpClient(new HttpClient());
        manager
            .setAuthenticationHandlers(Arrays.asList(new AuthenticationHandler[] {authenticationHandler}));
        manager
            .setCredentialsToPrincipalResolvers(Arrays.asList(new CredentialsToPrincipalResolver[] {new UsernamePasswordCredentialsToPrincipalResolver()}));
        try {
            manager.authenticate(TestUtils.getHttpBasedServiceCredentials());
            fail("Authentication should have failed.");
        } catch (UnsupportedCredentialsException e) {
            return;
        }
    }
    
    public void testResolverReturnsNull() throws Exception {
        AuthenticationManagerImpl manager = new AuthenticationManagerImpl();
        HttpBasedServiceCredentialsAuthenticationHandler authenticationHandler = new HttpBasedServiceCredentialsAuthenticationHandler();
        authenticationHandler.setHttpClient(new HttpClient());
        manager
            .setAuthenticationHandlers(Arrays.asList(new AuthenticationHandler[] {authenticationHandler}));
        manager
            .setCredentialsToPrincipalResolvers(Arrays.asList(new CredentialsToPrincipalResolver[] {new TestCredentialsToPrincipalResolver()}));
        try {
            manager.authenticate(TestUtils.getHttpBasedServiceCredentials());
            fail("Authentication should have failed.");
        } catch (BadCredentialsAuthenticationException e) {
            return;
        }
    }
    
    protected class TestCredentialsToPrincipalResolver implements CredentialsToPrincipalResolver {

        public Principal resolvePrincipal(Credentials credentials) {
            return null;
        }

        public boolean supports(final Credentials credentials) {
            return true;
        }
    }
}
