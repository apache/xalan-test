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
import org.apache.xalan.trace.TraceListenerEx;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.XalanProperties;

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
    protected TraxDatalet testFileInfo = new TraxDatalet();

    /** Another '2' Simple test to do tracing on.  */
    protected TraxDatalet testFileInfo2 = new TraxDatalet();

    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String X2J_SUBDIR = "xalanj2";


    /** Just initialize test name, comment, numTestCases. */
    public TraceListenerTest()
    {
        numTestCases = 4;  // REPLACE_num
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

        // NOTE: validation is tied to details within this file!
        testFileInfo.setDescription("Simple transform: TraceListenerTest");
        testFileInfo.setNames(inputDir + File.separator + X2J_SUBDIR, "TraceListenerTest");
        testFileInfo.goldName = goldDir + File.separator + X2J_SUBDIR + File.separator + "TraceListenerTest.out";
        
        testFileInfo2.setDescription("for-each transform: TraceListenerTest2");
        testFileInfo2.setNames(inputDir + File.separator + X2J_SUBDIR, "TraceListenerTest2");
        testFileInfo2.goldName = goldDir + File.separator + X2J_SUBDIR + File.separator + "TraceListenerTest2.out";
        return true;
    }


    /**
     * Quick smoketest of TraceListener; verify it traces something.
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
            Transformer transformer = factory.newTransformer(testFileInfo.getXSLSource());

            reporter.logInfoMsg("Transformer created, addTraceListener..."); 
            LoggingTraceListener ltl = new LoggingTraceListener(reporter);
            TraceManager traceManager = ((TransformerImpl)transformer).getTraceManager();
            reporter.check((null != traceManager), true, "getTraceManager is non-null");
            reporter.logStatusMsg("traceManager.hasTraceListeners() is: " + traceManager.hasTraceListeners());
            traceManager.addTraceListener((TraceListener)ltl);
            reporter.check(traceManager.hasTraceListeners(), true, "traceManager.hasTraceListeners() true after adding one");
         
            reporter.logInfoMsg("About to create output: " + outNames.nextName()); 
            transformer.transform(testFileInfo.getXMLSource(),
                                  new StreamResult(outNames.currentName()));
            reporter.checkPass("Crash test only: returned from transform() call");
            int[] tracedEvents = ltl.getCounters();
            reporter.logStatusMsg("Last event traced:" + ltl.getLast());
            reporter.logStatusMsg("Events traced:" + tracedEvents[LoggingTraceListener.TYPE_TRACE]
                                  + " events generated:" + tracedEvents[LoggingTraceListener.TYPE_GENERATED]
                                  + " events selected:" + tracedEvents[LoggingTraceListener.TYPE_SELECTED]);
            reporter.check(tracedEvents[LoggingTraceListener.TYPE_SELECTED], 3, 
                           "Correct number of selected events for testfile " + testFileInfo.getDescription());
            reporter.logStatusMsg("//@todo add more validation of trace events");
        }
        catch (Throwable t)
        {
            reporter.logThrowable(Logger.ERRORMSG, t, "testCase1a threw: ");
            reporter.checkFail("testCase1a threw: " + t.toString());
        }

        try
        {
/**** Temporarily comment out - my bad, missed checkin on testFileInfo2 tests! 27-jul-01 -sc ****
            // Try again with a different file with new Ex listener (should be parameterized)
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(testFileInfo2.getXSLSource());

            reporter.logInfoMsg("Transformer2 created, addTraceListener(Ex)..."); 
            LoggingTraceListenerEx ltl = new LoggingTraceListenerEx(reporter);
            TraceManager traceManager = ((TransformerImpl)transformer).getTraceManager();
            traceManager.addTraceListener((TraceListener)ltl);
         
            reporter.logInfoMsg("About to create output: " + outNames.nextName()); 
            transformer.transform(testFileInfo2.getXMLSource(),
                                  new StreamResult(outNames.currentName()));
            reporter.checkPass("Crash test only: returned from transform() call");
            int[] tracedEvents = ltl.getCounters();
            int selectedEndCtr = ltl.getCounterEx();
            reporter.logStatusMsg("Last event traced:" + ltl.getLast());
            reporter.logStatusMsg("Events traced:" + tracedEvents[LoggingTraceListener.TYPE_TRACE]
                                  + " events generated:" + tracedEvents[LoggingTraceListener.TYPE_GENERATED]
                                  + " events selected:" + tracedEvents[LoggingTraceListener.TYPE_SELECTED]
                                  + " events selectEnd:" + selectedEndCtr);
            reporter.check(tracedEvents[LoggingTraceListener.TYPE_SELECTED], 10, 
                           "Correct number of selected events for testfile " + testFileInfo2.getDescription());
            reporter.check(selectedEndCtr, 5, 
                           "Correct number of selectedEnd events for testfile " + testFileInfo2.getDescription());
**** Temporarily comment out - my bad, missed checkin on testFileInfo2 tests! 27-jul-01 -sc  ****/
        }
        catch (Throwable t)
        {
            reporter.logThrowable(Logger.ERRORMSG, t, "testCase1b threw: ");
            reporter.checkFail("testCase1b threw: " + t.toString());
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Test TraceListenerEx and multiple simultaneous trace listeners.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Test TraceListenerEx and multiple simultaneous trace listeners");
        reporter.logWarningMsg("Note: limited validation: partly just a crash test so far.");

        try
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(testFileInfo.getXSLSource());

            TraceManager traceManager = ((TransformerImpl)transformer).getTraceManager();
            reporter.check((null != traceManager), true, "getTraceManager is non-null");
            reporter.logStatusMsg("traceManager.hasTraceListeners() is-0: " + traceManager.hasTraceListeners());

            LoggingTraceListener ltl = new LoggingTraceListener(reporter);
            LoggingPrintTraceListener ltl2 = new LoggingPrintTraceListener(reporter);
            LoggingTraceListenerEx ltl3 = new LoggingTraceListenerEx(reporter);
            
            
            reporter.logInfoMsg("Transformer created, addTraceListener(LoggingTraceListener)..."); 
            traceManager.addTraceListener((TraceListener)ltl);
            reporter.logStatusMsg("traceManager.hasTraceListeners() is-1: " + traceManager.hasTraceListeners());
            
            reporter.logInfoMsg("... and addTraceListener(LoggingPrintTraceListener)"); 
            traceManager.addTraceListener((TraceListener)ltl2);
            reporter.logStatusMsg("traceManager.hasTraceListeners() is-2: " + traceManager.hasTraceListeners());

            reporter.logInfoMsg("... and addTraceListener(LoggingTraceListenerEx)"); 
            traceManager.addTraceListener((TraceListener)ltl3);
            
            reporter.check(traceManager.hasTraceListeners(), true, "traceManager.hasTraceListeners() true after adding several");
         
            reporter.logInfoMsg("About to create output: " + outNames.nextName()); 
            transformer.transform(testFileInfo.getXMLSource(),
                                  new StreamResult(outNames.currentName()));
            reporter.checkPass("Crash test only: returned from transform() call");
            
            // Now ask each listener how many events it traced
            int[] tracedEvents = ltl.getCounters();
            reporter.logStatusMsg("Last event traced(LTL):" + ltl.getLast());
            reporter.logStatusMsg("Events traced(LTL):" + tracedEvents[LoggingTraceListener.TYPE_TRACE]
                                  + " events generated:" + tracedEvents[LoggingTraceListener.TYPE_GENERATED]
                                  + " events selected:" + tracedEvents[LoggingTraceListener.TYPE_SELECTED]);
            reporter.check(tracedEvents[LoggingTraceListener.TYPE_SELECTED], 3, 
                           "LTL Correct number of selected events for testfile " + testFileInfo.getDescription());
            
            int[] tracedEvents2 = ltl2.getCounters();
            reporter.logStatusMsg("Last event traced(LPTL):" + ltl2.getLast());
            reporter.logStatusMsg("Events traced(LPTL):" + tracedEvents2[LoggingPrintTraceListener.TYPE_TRACE]
                                  + " events generated:" + tracedEvents2[LoggingPrintTraceListener.TYPE_GENERATED]
                                  + " events selected:" + tracedEvents2[LoggingPrintTraceListener.TYPE_SELECTED]);
            reporter.check(tracedEvents2[LoggingTraceListener.TYPE_SELECTED], 3, 
                           "LPTL Correct number of selected events for testfile " + testFileInfo.getDescription());
            
            // For some reason this returns it's parent's class counter 
            //  array, not it's own...
            int[] tracedEvents3 = ltl3.getCounters();
            int selectedEndCtr = ltl3.getCounterEx();
            reporter.logStatusMsg("Last event traced(LTLE):" + ltl3.getLast());
            reporter.logStatusMsg("Events traced(LTLE):" + tracedEvents3[LoggingTraceListenerEx.TYPE_TRACE]
                                  + " events generated:" + tracedEvents3[LoggingTraceListenerEx.TYPE_GENERATED]
                                  + " events selected:" + tracedEvents3[LoggingTraceListenerEx.TYPE_SELECTED]
                                  + " events selectEnd:" + selectedEndCtr);
            reporter.check(tracedEvents3[LoggingTraceListenerEx.TYPE_SELECTED], 3, 
                           "LTLE Correct number of selected events for testfile " + testFileInfo.getDescription());
            
        }
        catch (Throwable t)
        {
            reporter.logThrowable(Logger.ERRORMSG, t, "testCase2 threw: ");
            reporter.checkFail("testCase2 threw: " + t.toString());
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Test adding and removing multiple simultaneous trace listeners.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase3()
    {
        reporter.testCaseInit("Test adding and removing multiple simultaneous trace listeners");
        reporter.logWarningMsg("Note: limited validation: partly just a crash test so far.");

        try
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(testFileInfo.getXSLSource());

            TraceManager traceManager = ((TransformerImpl)transformer).getTraceManager();
            reporter.check((null != traceManager), true, "getTraceManager is non-null");
            reporter.check(traceManager.hasTraceListeners(), false, "traceManager.hasTraceListeners() false before adding");

            LoggingTraceListener ltl = new LoggingTraceListener(reporter);
            LoggingPrintTraceListener ltl2 = new LoggingPrintTraceListener(reporter);
            LoggingTraceListenerEx ltl3 = new LoggingTraceListenerEx(reporter);
            
            // Add one trace listener
            reporter.logInfoMsg("Transformer created, addTraceListener(LoggingTraceListener)..."); 
            traceManager.addTraceListener((TraceListener)ltl);
            reporter.check(traceManager.hasTraceListeners(), true, "traceManager.hasTraceListeners() true after adding1");

            // Remove one
            traceManager.removeTraceListener((TraceListener)ltl);
            reporter.check(traceManager.hasTraceListeners(), false, "traceManager.hasTraceListeners() false after removing1");

            // Add multiple 
            traceManager.addTraceListener((TraceListener)ltl);
            reporter.check(traceManager.hasTraceListeners(), true, "traceManager.hasTraceListeners() true after adding1b");
            
            traceManager.addTraceListener((TraceListener)ltl2);
            reporter.check(traceManager.hasTraceListeners(), true, "traceManager.hasTraceListeners() true after adding2");

            traceManager.addTraceListener((TraceListener)ltl3);
            reporter.check(traceManager.hasTraceListeners(), true, "traceManager.hasTraceListeners() true after adding3");
            
            // Remove one
            traceManager.removeTraceListener((TraceListener)ltl2);
            reporter.check(traceManager.hasTraceListeners(), true, "traceManager.hasTraceListeners() true after adding3 removing1");

            // Remove all
            traceManager.removeTraceListener((TraceListener)ltl);
            traceManager.removeTraceListener((TraceListener)ltl3);
            reporter.check(traceManager.hasTraceListeners(), false, "traceManager.hasTraceListeners() false after adding3 removing3");

            // Add one back and check transform
            traceManager.addTraceListener((TraceListener)ltl);
            reporter.check(traceManager.hasTraceListeners(), true, "traceManager.hasTraceListeners() true after adding1c");
         
            // Force trace listener to not bother logging, just capture statistics
            ltl.setLoggingLevel(100); // HACK - happens to be above 99, which is what we usually set max level tofs
            reporter.logInfoMsg("About to create output: " + outNames.nextName()); 
            transformer.transform(testFileInfo.getXMLSource(),
                                  new StreamResult(outNames.currentName()));
            reporter.checkPass("Crash test only: returned from transform() call");
            
            // Now ask each listener how many events it traced
            int[] tracedEvents = ltl.getCounters();
            reporter.logStatusMsg("Last event traced(LTL):" + ltl.getLast());
            reporter.logStatusMsg("Events traced(LTL):" + tracedEvents[LoggingTraceListener.TYPE_TRACE]
                                  + " events generated:" + tracedEvents[LoggingTraceListener.TYPE_GENERATED]
                                  + " events selected:" + tracedEvents[LoggingTraceListener.TYPE_SELECTED]);
            reporter.check(tracedEvents[LoggingTraceListener.TYPE_SELECTED], 3, 
                           "LTL Correct number of selected events for testfile " + testFileInfo.getDescription());
        }
        catch (Throwable t)
        {
            reporter.logThrowable(Logger.ERRORMSG, t, "testCase3-add/remove threw: ");
            reporter.checkFail("testCase3-add/remove threw: " + t.toString());
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Test TraceListener with XalanProperties.SOURCE_LOCATION.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase4()
    {
        reporter.testCaseInit("Test TraceListener with XalanProperties.SOURCE_LOCATION");
        reporter.logWarningMsg("Note: limited validation: partly just a crash test so far.");

        try
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(testFileInfo.getXSLSource());

            TraceManager traceManager = ((TransformerImpl)transformer).getTraceManager();
            reporter.logTraceMsg("getTraceManager is:" + traceManager);

            LoggingTraceListener ltl = new LoggingTraceListener(reporter);
            ltl.setLoggingLevel(Logger.INFOMSG + 1);
            
            reporter.logInfoMsg("Transformer created, addTraceListener(LoggingTraceListener)..."); 
            traceManager.addTraceListener((TraceListener)ltl);
            
            // Verify new Xalan-J 2.x specific property as true (non-default value)
            reporter.logInfoMsg("About to run with Source Location Property ON"); 
            ((TransformerImpl)transformer).setProperty(XalanProperties.SOURCE_LOCATION, Boolean.TRUE);
            reporter.logInfoMsg("About to create output: " + outNames.nextName()); 
            transformer.transform(testFileInfo.getXMLSource(),
                                  new StreamResult(outNames.currentName()));
            reporter.logInfoMsg("Done creating output: " + outNames.currentName());

            int[] tracedEvents = ltl.getCounters();
            reporter.logStatusMsg("Last event traced(LPTL):" + ltl.getLast());
            reporter.logStatusMsg("Events traced(LPTL):" + tracedEvents[LoggingPrintTraceListener.TYPE_TRACE]
                                  + " events generated:" + tracedEvents[LoggingPrintTraceListener.TYPE_GENERATED]
                                  + " events selected:" + tracedEvents[LoggingPrintTraceListener.TYPE_SELECTED]);
            reporter.checkPass("Crash test: completed transformations with SOURCE_LOCATION just ON");
        }
        catch (Throwable t)
        {
            reporter.logThrowable(Logger.ERRORMSG, t, "testCase4a-XalanProperties.SOURCE_LOCATION threw: ");
            reporter.checkFail("testCase4a-XalanProperties.SOURCE_LOCATION threw: " + t.toString());
        }

        try
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(testFileInfo.getXSLSource());

            TraceManager traceManager = ((TransformerImpl)transformer).getTraceManager();
            reporter.logTraceMsg("getTraceManager is:" + traceManager);

            LoggingTraceListener ltl = new LoggingTraceListener(reporter);
            ltl.setLoggingLevel(Logger.INFOMSG + 1);
            
            reporter.logInfoMsg("Transformer created, addTraceListener(LoggingTraceListener)..."); 
            traceManager.addTraceListener((TraceListener)ltl);
            
            // Verify new Xalan-J 2.x specific property; false then true
            reporter.logInfoMsg("About to run with Source Location Property OFF"); 
            ((TransformerImpl)transformer).setProperty(XalanProperties.SOURCE_LOCATION, Boolean.FALSE);
            reporter.logInfoMsg("About to create output: " + outNames.nextName()); 
            transformer.transform(testFileInfo.getXMLSource(),
                                  new StreamResult(outNames.currentName()));
            reporter.logInfoMsg("Done creating output: " + outNames.currentName());

            int[] tracedEvents1 = ltl.getCounters();
            reporter.logStatusMsg("Last event traced(LPTL):" + ltl.getLast());
            reporter.logStatusMsg("Events traced(LPTL):" + tracedEvents1[LoggingPrintTraceListener.TYPE_TRACE]
                                  + " events generated:" + tracedEvents1[LoggingPrintTraceListener.TYPE_GENERATED]
                                  + " events selected:" + tracedEvents1[LoggingPrintTraceListener.TYPE_SELECTED]);

            ltl.reset();

            // Verify new Xalan-J 2.x specific property; false then true
            reporter.logInfoMsg("About to run with Source Location Property ON"); 
            ((TransformerImpl)transformer).setProperty(XalanProperties.SOURCE_LOCATION, Boolean.TRUE);
            reporter.logInfoMsg("About to create output: " + outNames.nextName()); 
            transformer.transform(testFileInfo.getXMLSource(),
                                  new StreamResult(outNames.currentName()));
            reporter.logInfoMsg("Done creating output: " + outNames.currentName());

            int[] tracedEvents2 = ltl.getCounters();
            reporter.logStatusMsg("Last event traced(LPTL):" + ltl.getLast());
            reporter.logStatusMsg("Events traced(LPTL):" + tracedEvents2[LoggingPrintTraceListener.TYPE_TRACE]
                                  + " events generated:" + tracedEvents2[LoggingPrintTraceListener.TYPE_GENERATED]
                                  + " events selected:" + tracedEvents2[LoggingPrintTraceListener.TYPE_SELECTED]);
            reporter.checkPass("Crash test: completed transformations with SOURCE_LOCATION OFF and ON");
        }
        catch (Throwable t)
        {
            reporter.logThrowable(Logger.ERRORMSG, t, "testCase4b-XalanProperties.SOURCE_LOCATION threw: ");
            reporter.checkFail("testCase4b-XalanProperties.SOURCE_LOCATION threw: " + t.toString());
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
