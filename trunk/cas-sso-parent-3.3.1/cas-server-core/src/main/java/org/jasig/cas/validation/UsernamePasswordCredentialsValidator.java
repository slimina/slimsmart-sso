/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.validation;

import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * A validator to check if UserNamePasswordCredentials is valid.
 * 
 * @author Scott Battaglia
 * @version $Revision: 42611 $ $Date: 2007-11-02 11:39:03 -0400 (Fri, 02 Nov 2007) $
 * @since 3.0
 */
public final class UsernamePasswordCredentialsValidator implements Validator {

    public boolean supports(final Class clazz) {
        return UsernamePasswordCredentials.class.isAssignableFrom(clazz);
    }

    public void validate(final Object o, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username",
            "required.username");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
            "required.password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "authcode",
                "required.authcode");
    }
}
