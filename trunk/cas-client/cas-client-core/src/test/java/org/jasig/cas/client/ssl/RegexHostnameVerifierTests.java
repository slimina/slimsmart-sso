/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
  $Id: RegexHostnameVerifierTests.java 22071 2010-10-23 04:03:14Z battags $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: 22071 $
  Updated: $Date: 2010-10-23 00:03:14 -0400 (Sat, 23 Oct 2010) $
*/
package org.jasig.cas.client.ssl;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Unit test for {@link RegexHostnameVerifier} class.
 *
 * @author Middleware
 * @version $Revision: 22071 $
 *
 */
public class RegexHostnameVerifierTests extends TestCase {

    /**
     * Test method for {@link RegexHostnameVerifier#verify(String, SSLSession)}.
     */
    public void testVerify() {
        final RegexHostnameVerifier verifier = new RegexHostnameVerifier("\\w+\\.vt\\.edu");
        Assert.assertTrue(verifier.verify("a.vt.edu", null));
        Assert.assertTrue(verifier.verify("host.vt.edu", null));
        Assert.assertFalse(verifier.verify("1-host.vt.edu", null));
        Assert.assertFalse(verifier.verify("mallory.example.com", null));
    }

}
