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
 * ProblemListenerTest.java
 *
 */
package org.apache.qetest.xalanj1;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

import org.apache.xalan.xpath.xml.ProblemListener;
import org.apache.xalan.xslt.XSLTEngineImpl;
import org.apache.xalan.xslt.XSLTProcessor;
import org.apache.xalan.xslt.XSLTProcessorFactory;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTResultTarget;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FilenameFilter;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Verifying that ProblemListeners function with the processor.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class ProblemListenerTest extends XSLProcessorTestBase
{
    /**
     * Provides nextName(), currentName() functionality for tests 
     * that may produce any number of output files.
     */
    protected OutputNameManager outNames;

    /** 
     * Information about an xsl/xml file pair for transforming.  
     * Public members include inputName (for xsl); xmlName; goldName; etc.
     */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();

    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String XALANJ1_SUBDIR = "xalanj1";


    /** Just initialize test name, comment, numTestCases. */
    public ProblemListenerTest()
    {
        numTestCases = 3;  // REPLACE_num
        testName = "ProblemListenerTest";
        testComment = "Verifying that ProblemListeners function with the processor";
    }


    /**
     * Initialize this test - Set names of xml/xsl test files.
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        // Used for all tests; just dump files in trax subdir
        File outSubDir = new File(outputDir + File.separator + XALANJ1_SUBDIR);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + XALANJ1_SUBDIR
                                         + File.separator + testName, ".out");

        String testBasePath = inputDir 
                              + File.separator 
                              + XALANJ1_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator 
                              + XALANJ1_SUBDIR
                              + File.separator;

        testFileInfo.inputName = testBasePath + "ProblemListenerTest1.xsl";
        testFileInfo.xmlName = testBasePath + "ProblemListenerTest1.xml";
        testFileInfo.goldName = goldBasePath + "ProblemListenerTest1.out";
        return true;
    }


    /**
     * set/getProblemListener API Coverage.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("set/getProblemListener API Coverage");
        XSLTEngineImpl processor = null;
        try
        {
            if ((liaison == null)
                || ("".equals(liaison)))
            {
                processor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessor();
            }
            else
            {
                processor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
            }
        }
        catch (Exception e)
        {
            reporter.checkErr("Could not create processor, threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Could not create processor");
            return false;
        }        

        // Create the ProblemListener here
        LoggingProblemListener problemListener = new LoggingProblemListener(reporter);
        reporter.logTraceMsg("problemListener: " + problemListener.toString());
        ProblemListener cachePL = null;
        try
        {
            // Save previous problem listener, if any
            cachePL = processor.getProblemListener(); // SPR SCUU4T9L4D throws npe compatibility
            reporter.checkPass("getProblemListener is: " + cachePL);
        } 
        catch (Throwable t)
        {
            reporter.checkErr("getProblemListener threw: " + t.toString());
            reporter.logThrowable(Logger.ERRORMSG, t, "getProblemListener threw");
        }
        // Add our problemListener
        processor.setProblemListener(problemListener);
        ProblemListener gotPL = processor.getProblemListener();
        reporter.checkObject(problemListener, gotPL, "set/getProblemListener API Coverage");
        
        String problemReport1 = problemListener.getCounterString();
        reporter.logInfoMsg("After adding, problemListener reports: " + problemReport1);

        reporter.testCaseClose();
        return true;
    }


    /**
     * Basic functionality of a ProblemListener; message() and error().
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Basic functionality of a ProblemListener; message() and error()");
        XSLTEngineImpl processor = null;
        try
        {
            if ((liaison == null)
                || ("".equals(liaison)))
            {
                processor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessor();
            }
            else
            {
                processor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
            }
        }
        catch (Exception e)
        {
            reporter.checkErr("Could not create processor, threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Could not create processor");
            return false;
        }        

        // Create the ProblemListener here
        LoggingProblemListener problemListener = new LoggingProblemListener(reporter);
        reporter.logTraceMsg("problemListener: " + problemListener.toString());

		// Verify we have no problems or messages yet
		reporter.check(problemListener.getProblemCtr(), 0, "problemCtr is 0 to start");        
		reporter.check(problemListener.getMessageCtr(), 0, "messageCtr is 0 to start");        

        // Add problemListener and test individual message(), error() calls
		int mCtr = problemListener.getMessageCtr();
        processor.setProblemListener(problemListener);
        try
        {
            processor.message("This is the message()");
        }
        catch (Throwable t)
        {
            reporter.checkFail("message() should not throw");
            reporter.logThrowable(Logger.ERRORMSG, t, "message() should not throw threw");
        }
        reporter.check(problemListener.getMessageCtr(), (mCtr + 1), "message() logged one message");

		int pCtr = problemListener.getProblemCtr();
        // Tell the problemListener to never throw exceptions
		problemListener.setHaltOnError(problemListener.NEVER_HALT);
        // Tell the problemListener to call checkPass if error() is called
		problemListener.setExpectProblem(true);
        try
        {
            processor.error("This is the error()");
            reporter.checkPass("problemListener.error() prevented throwing exception");
        }
        catch (Throwable t)
        {
            reporter.checkFail("problemListener.error() prevented throwing exception");
            reporter.logThrowable(Logger.ERRORMSG, t, "problemListener.error() prevented throwing exception threw");
        }
        reporter.check(problemListener.getProblemCtr(), (pCtr + 1), "error() logged one problem");

        reporter.testCaseClose();
        return true;
    }


    /**
     * Basic functionality of a ProblemListener; hearing about xsl problems.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase3()
    {
        reporter.testCaseInit("Basic functionality of a ProblemListener; hearing about xsl problems");
        XSLTEngineImpl processor = null;
        try
        {
            if ((liaison == null)
                || ("".equals(liaison)))
            {
                processor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessor();
            }
            else
            {
                processor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
            }
        }
        catch (Exception e)
        {
            reporter.checkErr("Could not create processor, threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Could not create processor");
            return false;
        }        

        // Create the ProblemListener here
        LoggingProblemListener problemListener = new LoggingProblemListener(reporter);
        reporter.logTraceMsg("problemListener: " + problemListener.toString());

		// Verify we have no problems or messages yet
		reporter.check(problemListener.getProblemCtr(), 0, "problemCtr is 0 to start");        
		reporter.check(problemListener.getMessageCtr(), 0, "messageCtr is 0 to start");        

        try
        {
            // Add our problemListener
            processor.setProblemListener(problemListener);
            String problemReport1 = problemListener.getCounterString();
            reporter.logInfoMsg("After adding, problemListener reports: " + problemReport1);

            // Now process a stylesheet with an error
            // We rely on the problemListener to call check, since 
            //  we tell it here to expect a problem
			problemListener.setExpectProblem(true);
            try
            {
                reporter.logTraceMsg("About to process (xsl with error)(1): " + testFileInfo.inputName);
                // Use inner try-catch in case it throws an exception anyway
                processor.process(new XSLTInputSource(testFileInfo.xmlName), 
                                  new XSLTInputSource(testFileInfo.inputName), 
                                  new XSLTResultTarget(outNames.nextName()));
            }
            catch (Throwable t)
            {
                reporter.logThrowable(Logger.ERRORMSG, t, "processing with ProblemListener(1) threw:");
            }
			problemListener.setExpectProblem(false);
            String problemReport2 = problemListener.getCounterString();
            reporter.logInfoMsg("After running, problemListener reports: " + problemReport2);
            // SPR SCUU4T5QMH This problem listener never got 
            //  got notified of problems in the last process()
            reporter.check(problemReport1.equals(problemReport2), false, "Some problems were found");

        } 
        catch (Exception e)
        {
            reporter.logErrorMsg("Testcase threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Testcase threw");
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
        return ("Common [optional] options supported by ProblemListenerTest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        ProblemListenerTest app = new ProblemListenerTest();
        app.doMain(args);
    }
}
