/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights 
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

/*
 *
 * ErrorListenerAPITest.java
 *
 */
package org.apache.qetest.trax;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

// java classes
import java.io.File;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * API Coverage test for ErrorListener; defaults to Xalan impl.
 * Only very basic API coverage.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class ErrorListenerAPITest extends FileBasedTest
{
    /** FQCN for Xalan-J 2.x impl.  */
    public static final String XALAN_ERRORLISTENER_IMPL = "org.apache.xml.utils.DefaultErrorHandler";

    /** FQCN for the Logging* impl that the tests provide.  */
    public static final String QETEST_ERRORLISTENER_IMPL = "org.apache.qetest.trax.LoggingErrorHandler";

    /** Name of ErrorListener implementation we're going to test.  */
    public String errorListenerClassname = XALAN_ERRORLISTENER_IMPL;

    /** Just initialize test name, comment, numTestCases. */
    public ErrorListenerAPITest()
    {
        numTestCases = 1;  // REPLACE_num
        testName = "ErrorListenerAPITest";
        testComment = "API Coverage test for ErrorListener; defaults to Xalan impl";
    }


    /**
     * Initialize this test - Set names of xml/xsl test files,
     * //@todo read in name of alternate ErrorListener class!  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        reporter.logInfoMsg("//@todo allow user to change name of ErrorListener implementation used");
        return true;
    }


    /**
     * API Coverage of ErrorListener class, using Xalan-J 2.x impl.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("API Coverage of ErrorListener class, using Xalan-J 2.x impl");
        Class elClass = null;
        ErrorListener errorListener = null;
        try
        {
            elClass = Class.forName(errorListenerClassname);
            errorListener = (ErrorListener)elClass.newInstance();
        }
        catch (Exception e)
        {
            reporter.checkErr("Loading errorListener implementation " + errorListenerClassname
                              + " threw: " + e.toString());
            reporter.testCaseClose();
            return true;
        }

        Exception ex = new Exception("Exception-message-here");
        TransformerException tex = new TransformerException("TransformerException-message-here", ex);

        try
        {
            errorListener.warning(tex);
            reporter.checkPass("warning did not throw any exception");
            reporter.logTraceMsg("//@todo also validate System.err stream!");
        }
        catch (TransformerException te)
        {
            reporter.checkFail("warning threw TransformerException, threw: " + te.toString());
        }
        catch (Throwable t)
        {
            reporter.checkFail("warning threw non-TransformerException, threw: " + t.toString());
        }

        try
        {
            // Default error impl in Xalan throws exception
            errorListener.error(tex);
            reporter.checkFail("error did not throw any exception");
            reporter.logTraceMsg("//@todo also validate System.err stream!");
        }
        catch (TransformerException te)
        {
            reporter.checkPass("error expectedly threw TransformerException, threw: " + te.toString());
            reporter.check((te.toString().indexOf("TransformerException-message-here") > -1), 
                           true, "error's exception includes proper text");
        }
        catch (Throwable t)
        {
            reporter.checkFail("error threw non-TransformerException, threw: " + t.toString());
        }

        try
        {
            // Default fatalError impl in Xalan throws exception
            errorListener.error(tex);
            reporter.checkFail("fatalError did not throw any exception");
            reporter.logTraceMsg("//@todo also validate System.err stream!");
        }
        catch (TransformerException te)
        {
            reporter.checkPass("fatalError expectedly threw TransformerException, threw: " + te.toString());
            reporter.check((te.toString().indexOf("TransformerException-message-here") > -1), 
                           true, "fatalError's exception includes proper text");
        }
        catch (Throwable t)
        {
            reporter.checkFail("fatalError threw non-TransformerException, threw: " + t.toString());
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
        return ("Common [optional] options supported by ErrorListenerAPITest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        ErrorListenerAPITest app = new ErrorListenerAPITest();
        app.doMain(args);
    }
}
