/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.uportal.org/license.html
 */
package org.jasig.cas.authentication.principal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.inspektr.common.ioc.annotation.NotNull;
import org.springframework.webflow.util.Base64;

/**
 * Generates PersistentIds based on the Shibboleth algorithm.
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.1 $ $Date: 2007/04/20 19:39:31 $
 * @since 3.1
 */
public final class ShibbolethCompatiblePersistentIdGenerator implements
    PersistentIdGenerator {

    private static final byte CONST_SEPARATOR = (byte) '!';
    
    private Base64 base64 = new Base64();

    @NotNull
    private byte[] salt;

    public String generate(final Principal principal, final Service service) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(service.getId().getBytes());
            md.update(CONST_SEPARATOR);
            md.update(principal.getId().getBytes());
            md.update(CONST_SEPARATOR);

            return this.base64.encodeToString(md.digest(this.salt)).replaceAll(
                System.getProperty("line.separator"), "");
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSalt(final String salt) {
        this.salt = salt.getBytes();
    }
}
