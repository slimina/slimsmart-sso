/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.web.view;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.validation.Assertion;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Abstract class to handle retrieving the Assertion from the model.
 * 
 * @author Scott Battaglia
 * @version $Revision: 42053 $ $Date: 2007-06-10 09:17:55 -0400 (Sun, 10 Jun 2007) $
 * @since 3.1
 */
public abstract class AbstractCasView extends AbstractView {
    
    protected final Log log = LogFactory.getLog(this.getClass());

    protected final Assertion getAssertionFrom(final Map model) {
        return (Assertion) model.get("assertion");
    }
}
