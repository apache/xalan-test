/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
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
 * TraceListenerTest.java
 *
 */
package org.apache.qetest.xalanj2;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;
import org.apache.qetest.trax.LoggingErrorListener;

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;
import org.apache.xalan.trace.TraceListener;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.transformer.TransformerImpl;

// Needed SAX, DOM, JAXP classes

// java classes
import java.io.File;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Basic functionality testing of TraceListener interface and etc.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class TraceListenerTest extends XSLProcessorTestBase
{

    /** Provides nextName(), currentName() functionality.  */
    protected OutputNameManager outNames;

    /** Simple test to do tracing on.  */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();


    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String X2J_SUBDIR = "xalanj2";


    /** Just initialize test name, comment, numTestCases. */
    public TraceListenerTest()
    {
        numTestCases = 1;  // REPLACE_num
        testName = "TraceListenerTest";
        testComment = "Basic functionality testing of TraceListener interface and etc";
    }


    /**
     * Initialize this test - Set names of xml/xsl test files,
     * REPLACE_other_test_file_init.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        // NOTE: 'reporter' variable is already initialized at this point

        // Used for all tests; just dump files in trax subdir
        File outSubDir = new File(outputDir + File.separator + X2J_SUBDIR);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + X2J_SUBDIR
                                         + File.separator + testName, ".out");

        String testBasePath = inputDir 
                              + File.separator 
                              + X2J_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator 
                              + X2J_SUBDIR
                              + File.separator;

        // NOTE: validation is tied to details within this file!
        testFileInfo.inputName = testBasePath + "TraceListenerTest.xsl";
        testFileInfo.xmlName = testBasePath + "TraceListenerTest.xml";
        testFileInfo.goldName = goldBasePath + "TraceListenerTest.out";

        return true;
    }


    /**
     * Quick smoketest of TraceListener.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Quick smoketest of TraceListener");
        reporter.logWarningMsg("Note: limited validation: partly just a crash test so far.");

        try
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(QetestUtils.filenameToURL(testFileInfo.inputName)));

            reporter.logInfoMsg("Transformer created, addTraceListener..."); 
            LoggingTraceListener ltl = new LoggingTraceListener(reporter);
            TraceManager traceManager = ((TransformerImpl)transformer).getTraceManager();
            reporter.check((null != traceManager), true, "getTraceManager is non-null");
            reporter.logStatusMsg("traceManager.hasTraceListeners() is: " + traceManager.hasTraceListeners());
            traceManager.addTraceListener((TraceListener)ltl);
            reporter.check(traceManager.hasTraceListeners(), true, "traceManager.hasTraceListeners() true after adding one");
         
            reporter.logInfoMsg("About to create output: " + outNames.nextName()); 
            transformer.transform(new StreamSource(QetestUtils.filenameToURL(testFileInfo.xmlName)),
                                  new StreamResult(outNames.currentName()));
            reporter.checkPass("Crash test only: returned from transform() call");
            int[] tracedEvents = ltl.getCounters();
            reporter.logStatusMsg("Last event traced:" + ltl.getLast());
            reporter.logStatusMsg("Events traced:" + tracedEvents[LoggingTraceListener.TYPE_TRACE]
                                  + " events generated:" + tracedEvents[LoggingTraceListener.TYPE_GENERATED]
                                  + " events selected:" + tracedEvents[LoggingTraceListener.TYPE_SELECTED]);
            reporter.check(tracedEvents[LoggingTraceListener.TYPE_SELECTED], 3, 
                           "Correct number of selected events for testfile " + testFileInfo.inputName);
            reporter.logStatusMsg("//@todo add more validation of trace events");
        }
        catch (Throwable t)
        {
            reporter.checkFail("testCase1a threw: " + t.toString());
            reporter.logThrowable(Logger.ERRORMSG, t, "testCase1a threw: ");
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
        return ("Common [optional] options supported by TraceListenerTest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        TraceListenerTest app = new TraceListenerTest();
        app.doMain(args);
    }
}
