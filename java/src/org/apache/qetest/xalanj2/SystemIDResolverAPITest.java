/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2000, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.qetest.xalanj2;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

import org.apache.xml.utils.SystemIDResolver;
import java.util.Properties;

/**
 * Functionality/system/integration tests for SystemIDResolver.
 *
 * Very simple coverage test.
 * 
 * @author shane_curcuru@us.ibm.com
 * @version $Id$
 */
public class SystemIDResolverAPITest extends XSLProcessorTestBase
{
    /** Just initialize test name, comment, numTestCases. */
    public SystemIDResolverAPITest()
    {
        numTestCases = 1;  // REPLACE_num
        testName = "SystemIDResolverAPITest";
        testComment = "Functionality/system/integration tests for SystemIDResolver";
    }

    /** Separator for hierarchical URLs /.  */
    private static final char URL_SEP = '/';

    /** Default file:// scheme header.  */
    private static final String FILE_SCHEME = "file://";

    /** 
     * Test strings and expected data for getAbsoluteURIFromRelative(String uri).  
     * NOTE: We really need a more definitive and thorough set of 
     * test strings: this requires really reading a number of RFC's...
     */
    protected static String[][] ABS_URI_FROM_REL = 
    { /* Assumption: test prepends expected user.dir stuff */
        { "foo.out", "foo.out" },
        { "bar/foo.out", "bar/foo.out" },
        { "bar\\foo.out", "bar/foo.out" },
        { "foo.out", "foo.out" }
    };

    /** Test strings and expected data for getAbsoluteURI(String url) 
     * assuming they're relative paths from a baseURL of file:///'user.dir'.  
     * NOTE: We really need a more definitive and thorough set of 
     * test strings: this requires really reading a number of RFC's...
     */
    protected static String[][] ABS_URI_REL = 
    { /* Assumption: test prepends expected user.dir stuff */
        { "foo.out", "foo.out" },
        { "bar/foo.out", "bar/foo.out" },
        { "bar\\foo.out", "bar/foo.out" },
        { "foo.out", "foo.out" }
    };

    /** Test strings and expected data for getAbsoluteURI(String url) 
     * assuming they're absolute paths with their own scheme:.  
     * NOTE: We really need a more definitive and thorough set of 
     * test strings: this requires really reading a number of RFC's...
     */
    protected static String[][] ABS_URI_ABS = 
    {
        { "http://server.com/foo.out", "http://server.com/foo.out" },
        { "http://server.com/bar/foo.out", "http://server.com/bar/foo.out" },
        { "http://server.com/bar/foo.out#fragment", "http://server.com/bar/foo.out#fragment" },
        { "http://server.com/bar/foo/?param=value&name=value", "http://server.com/bar/foo/?param=value&name=value" }, 
        { "http://127.0.0.1/bar/foo.out#fragment", "http://127.0.0.1/bar/foo.out#fragment" },
        { "file://server.com/bar/foo.out", "file://server.com/bar/foo.out" }
    };

    /** Test strings and expected data for getAbsoluteURI(String urlString, String base).  */
    protected static String[][] ABS_URI_WITH_BASE = 
    { /*  "urlString", "base", "expected result" */
        { "foo.out", "file:///bar", "file:///foo.out" }, /* Note that trailing /bar is dropped since it's effectively a file reference */
        { "foo.out", "file:///bar/", "file:///bar/foo.out" },
        { "foo.out", "http://server.com/bar", "http://server.com/foo.out" },
        { "foo.out", "http://server.com/bar/", "http://server.com/bar/foo.out" },
        { "../foo.out", "http://server.com/bar/", "http://server.com/foo.out" }
    };


    /**
     * Using our data set of URLs, try various resolver calls.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Using our data set of URLs, try various resolver calls");

        try
        {
            String prevUserDir = System.getProperty("user.dir");
            String baseURL = prevUserDir.replace('\\', '/');
            if (null == baseURL)
                baseURL = "";

            // Add the default file scheme and a separator between 
            //  the (blank) authority component and the hierarchical 
            //  pathing component
            baseURL = FILE_SCHEME + URL_SEP + baseURL + URL_SEP;
            reporter.logStatusMsg("user.dir baseURI is: " + baseURL);

            reporter.logTraceMsg("Now testing getAbsoluteURIFromRelative...");
            for (int i = 0; i < ABS_URI_FROM_REL.length; i++)
            {
                String val = SystemIDResolver.getAbsoluteURIFromRelative(ABS_URI_FROM_REL[i][0]);
                // Automatically prepend the baseURI to expected data
                reporter.check(val, baseURL + ABS_URI_FROM_REL[i][1], "getAbsoluteURIFromRelative(" + ABS_URI_FROM_REL[i][0] + ") = " + val);
            }

            reporter.logTraceMsg("Now testing getAbsoluteURI with relative paths and default baseURL");
            for (int i = 0; i < ABS_URI_REL.length; i++)
            {
                String val = SystemIDResolver.getAbsoluteURI(ABS_URI_REL[i][0]);
                // Automatically prepend the baseURI to expected data
                reporter.check(val, baseURL + ABS_URI_REL[i][1], "getAbsoluteURI(" + ABS_URI_REL[i][0] + ") = " + val);
            }

            reporter.logTraceMsg("Now testing getAbsoluteURI with absolute paths");
            for (int i = 0; i < ABS_URI_ABS.length; i++)
            {
                String val = SystemIDResolver.getAbsoluteURI(ABS_URI_ABS[i][0]);
                reporter.check(val, ABS_URI_ABS[i][1], "getAbsoluteURI(" + ABS_URI_ABS[i][0] + ") = " + val);
            }

            reporter.logTraceMsg("Now testing getAbsoluteURI with a user-supplied baseURL");
            for (int i = 0; i < ABS_URI_WITH_BASE.length; i++)
            {
                String val = SystemIDResolver.getAbsoluteURI(ABS_URI_WITH_BASE[i][0], ABS_URI_WITH_BASE[i][1]);
                reporter.check(val, ABS_URI_WITH_BASE[i][2], "getAbsoluteURI(" + ABS_URI_WITH_BASE[i][0]
                        + ", " + ABS_URI_WITH_BASE[i][1] + ") = " + val);
            }
        }
        catch (Throwable t)
        {
            reporter.logThrowable(reporter.ERRORMSG, t, "testCase1 threw");
            reporter.checkFail("testCase1 threw: " + t.toString());
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by SystemIDResolverAPITest:\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        SystemIDResolverAPITest app = new SystemIDResolverAPITest();
        app.doMain(args);
    }
}
