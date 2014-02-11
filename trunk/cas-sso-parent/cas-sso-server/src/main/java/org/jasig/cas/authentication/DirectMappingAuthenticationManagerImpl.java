/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.uportal.org/license.html
 */
package org.jasig.cas.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.inspektr.common.ioc.annotation.NotEmpty;
import org.inspektr.common.ioc.annotation.NotNull;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.Principal;
import org.springframework.util.Assert;

/**
 * Authentication Manager that provides a direct mapping between credentials
 * provided and the authentication handler used to authenticate the user.
 * 
 * @author Scott Battaglia
 * @version $Revision: 42776 $ $Date: 2008-01-04 09:15:42 -0500 (Fri, 04 Jan 2008) $
 * @since 3.1
 */
public final class DirectMappingAuthenticationManagerImpl implements
    AuthenticationManager {

    @NotEmpty
    private Map<Class< ? extends Credentials>, DirectAuthenticationHandlerMappingHolder> credentialsMapping;

    /** An array of AuthenticationAttributesPopulators. */
    @NotNull
    private List<AuthenticationMetaDataPopulator> authenticationMetaDataPopulators = new ArrayList<AuthenticationMetaDataPopulator>();

    /**
     * @throws IllegalArgumentException if a mapping cannot be found.
     * @see org.jasig.cas.authentication.AuthenticationManager#authenticate(org.jasig.cas.authentication.principal.Credentials)
     */
    public Authentication authenticate(final Credentials credentials)
        throws AuthenticationException {
        final Class< ? extends Credentials> credentialsClass = credentials.getClass();
        final DirectAuthenticationHandlerMappingHolder d = this.credentialsMapping
            .get(credentialsClass);

        Assert
            .notNull(d, "no mapping found for: " + credentialsClass.getName());

        if (!d.getAuthenticationHandler().authenticate(credentials)) {
            throw new BadCredentialsAuthenticationException();
        }

        final Principal p = d.getCredentialsToPrincipalResolver()
            .resolvePrincipal(credentials);

        Authentication authentication = new MutableAuthentication(p);

        for (final AuthenticationMetaDataPopulator authenticationMetaDataPopulator : this.authenticationMetaDataPopulators) {
            authentication = authenticationMetaDataPopulator
                .populateAttributes(authentication, credentials);
        }

        return new ImmutableAuthentication(authentication.getPrincipal(),
            authentication.getAttributes());
    }

    public final void setCredentialsMapping(
        final Map<Class< ? extends Credentials>, DirectAuthenticationHandlerMappingHolder> credentialsMapping) {
        this.credentialsMapping = credentialsMapping;
    }

    public final void setAuthenticationMetaDataPopulators(
        final List<AuthenticationMetaDataPopulator> authenticationMetaDataPopulators) {
        this.authenticationMetaDataPopulators = authenticationMetaDataPopulators;
    }

    public static final class DirectAuthenticationHandlerMappingHolder {

        private AuthenticationHandler authenticationHandler;

        private CredentialsToPrincipalResolver credentialsToPrincipalResolver;

        public DirectAuthenticationHandlerMappingHolder() {
            // nothing to do
        }

        public final AuthenticationHandler getAuthenticationHandler() {
            return this.authenticationHandler;
        }

        public void setAuthenticationHandler(
            final AuthenticationHandler authenticationHandler) {
            this.authenticationHandler = authenticationHandler;
        }

        public CredentialsToPrincipalResolver getCredentialsToPrincipalResolver() {
            return this.credentialsToPrincipalResolver;
        }

        public void setCredentialsToPrincipalResolver(
            final CredentialsToPrincipalResolver credentialsToPrincipalResolver) {
            this.credentialsToPrincipalResolver = credentialsToPrincipalResolver;
        }
    }

}
