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
 * StylesheetReuseTest.java
 *
 */
package org.apache.qetest.xalanj1;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

import org.apache.xalan.xslt.StylesheetRoot;
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
 * Testing various cases of stylesheet re-use.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class StylesheetReuseTest extends XSLProcessorTestBase
{
    /**
     * Provides nextName(), currentName() functionality for tests 
     * that may produce any number of output files.
     */
    protected OutputNameManager outNames;

    /** Basic stylesheet for testing.    */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();

    /** Another Basic stylesheet for testing.    */
    protected XSLTestfileInfo otherFileInfo = new XSLTestfileInfo();

    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String XALANJ1_SUBDIR = "xalanj1";


    /** Just initialize test name, comment, numTestCases. */
    public StylesheetReuseTest()
    {
        numTestCases = 3;  // REPLACE_num
        testName = "StylesheetReuseTest";
        testComment = "Testing various cases of stylesheet re-use";
    }


    /**
     * Initialize this test - Set names of xml/xsl test files.
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        // Used for all tests; just dump files in subdir
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

        testFileInfo.inputName = testBasePath + "StylesheetReuseTest1.xsl";
        testFileInfo.xmlName = testBasePath + "StylesheetReuseTest1.xml";
        testFileInfo.goldName = goldBasePath + "StylesheetReuseTest1.out";

        otherFileInfo.inputName = testBasePath + "ParamTest1.xsl";
        otherFileInfo.xmlName = testBasePath + "ParamTest1.xml";
        otherFileInfo.goldName = goldBasePath + "ParamTest1.out";
        
        return true;
    }


    /**
     * Re-using one stylesheet multiple times.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Re-using one stylesheet multiple times");
        XSLTEngineImpl processor = null;
        try
        {
            if ((liaison == null) || ("".equals(liaison)))
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

        try
        {
            int result = Logger.INCP_RESULT;
            // Create one stylesheet
            XSLTInputSource xslSrc = new XSLTInputSource(testFileInfo.inputName);
            StylesheetRoot stylesheetRoot = processor.processStylesheet(xslSrc);

            // This should have implicitly set the stylesheet in the processor
            // Verify you can do a process now
            reporter.logTraceMsg("About to process1(" + testFileInfo.xmlName 
                                 + ", " + testFileInfo.xmlName + ",...)");
            processor.process(new XSLTInputSource(testFileInfo.xmlName), 
                              null,
                              new XSLTResultTarget(outNames.nextName()));
            result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "processStylesheet(1) implicit transform into: " + outNames.currentName());
            if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("processStylesheet implicit transform failure reason:" + fileChecker.getExtendedInfo());

            // Reset, explicitly set the stylesheet to the same one, 
            //  and process again
            processor.reset();
            processor.setStylesheet(stylesheetRoot);
            reporter.logTraceMsg("About to process2(" + testFileInfo.xmlName 
                                 + ", " + testFileInfo.xmlName + ",...)");
            processor.process(new XSLTInputSource(testFileInfo.xmlName), 
                              null,
                              new XSLTResultTarget(outNames.nextName()));
            result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "setStylesheet(same one) transform into: " + outNames.currentName());
            if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("setStylesheet(same one) transform failure reason:" + fileChecker.getExtendedInfo());

            // Try it a third time for luck
            processor.reset();
            processor.setStylesheet(stylesheetRoot);
            reporter.logTraceMsg("About to process3(" + testFileInfo.xmlName 
                                 + ", " + testFileInfo.xmlName + ",...)");
            processor.process(new XSLTInputSource(testFileInfo.xmlName), 
                              null,
                              new XSLTResultTarget(outNames.nextName()));
            result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "setStylesheet(same one again) transform into: " + outNames.currentName());
            if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("setStylesheet(same one again) transform failure reason:" + fileChecker.getExtendedInfo());

            // Reset, explicitly set the stylesheet to the same one, 
            //  and process again, but with a different stylesheet 
            //  specified in the process call
            processor.reset();
            processor.setStylesheet(stylesheetRoot);
            reporter.logTraceMsg("About to process4a(" + otherFileInfo.xmlName 
                                 + ", " + otherFileInfo.xmlName + ",...)");
            processor.process(new XSLTInputSource(otherFileInfo.xmlName), 
                              new XSLTInputSource(otherFileInfo.inputName),
                              new XSLTResultTarget(outNames.nextName()));
            result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(otherFileInfo.goldName), 
                              "setStylesheet(a), but process(xml,b,out) transform into: " + outNames.currentName());
            if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("setStylesheet(a), but process(xml,b,out) transform failure reason:" + fileChecker.getExtendedInfo());
        } 
        catch (Throwable t)
        {
            reporter.checkErr("Testcase threw: " + t.toString());
            reporter.logThrowable(Logger.ERRORMSG, t, "Testcase threw");
        }
        reporter.testCaseClose();
        return true;
    }


    /**
     * Re-using multiple stylesheets multiple times; testing setStylesheet().
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Re-using multiple stylesheets multiple times; testing setStylesheet()");
        XSLTEngineImpl processor = null;
        XSLTEngineImpl otherProcessor = null;
        try
        {
            if ((liaison == null) || ("".equals(liaison)))
            {
                processor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessor();
                otherProcessor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessor();
            }
            else
            {
                processor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
                otherProcessor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
            }
        }
        catch (Exception e)
        {
            reporter.checkErr("Could not create processor, threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Could not create processor");
            return false;
        }        

        try
        {
            int result = Logger.INCP_RESULT;
            // Create one stylesheet
            XSLTInputSource xslSrc = new XSLTInputSource(testFileInfo.inputName);
            StylesheetRoot stylesheetRoot = processor.processStylesheet(xslSrc);

            // Create another stylesheet
            XSLTInputSource otherXslSrc = new XSLTInputSource(otherFileInfo.inputName);
            StylesheetRoot otherStylesheetRoot = processor.processStylesheet(otherXslSrc);

            // This should have implicitly set the other stylesheet in the processor
            // Verify you can do a process now
            reporter.logTraceMsg("About to process1(" + otherFileInfo.xmlName 
                                 + ", " + otherFileInfo.xmlName + ",...)");
            processor.process(new XSLTInputSource(otherFileInfo.xmlName), 
                              null,
                              new XSLTResultTarget(outNames.nextName()));
            result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(otherFileInfo.goldName), 
                              "Second processStylesheet(1) implicit transform into: " + outNames.currentName());
            if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("Second processStylesheet(1) implicit transform failure reason:" + fileChecker.getExtendedInfo());

            // Reset, explicitly set the stylesheet to the first one, 
            //  and process again
            processor.reset();
            processor.setStylesheet(stylesheetRoot);
            reporter.logTraceMsg("About to process2(" + testFileInfo.xmlName 
                                 + ", " + testFileInfo.xmlName + ",...)");
            processor.process(new XSLTInputSource(testFileInfo.xmlName), 
                              null,
                              new XSLTResultTarget(outNames.nextName()));
            result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "setStylesheet(firstone) transform into: " + outNames.currentName());
            if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("setStylesheet(firstone) transform failure reason:" + fileChecker.getExtendedInfo());

            // Reset, explicitly set the stylesheet to the second one, 
            //  and process again
            processor.reset();
            processor.setStylesheet(otherStylesheetRoot);
            reporter.logTraceMsg("About to process3(" + otherFileInfo.xmlName 
                                 + ", " + otherFileInfo.xmlName + ",...)");
            processor.process(new XSLTInputSource(otherFileInfo.xmlName), 
                              null,
                              new XSLTResultTarget(outNames.nextName()));
            result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(otherFileInfo.goldName), 
                              "setStylesheet(secondone) transform into: " + outNames.currentName());
            if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("setStylesheet(secondone) transform failure reason:" + fileChecker.getExtendedInfo());
        } 
        catch (Throwable t)
        {
            reporter.checkErr("Testcase threw: " + t.toString());
            reporter.logThrowable(Logger.ERRORMSG, t, "Testcase threw");
        }
        reporter.testCaseClose();
        return true;
    }

    /**
     * Re-using multiple stylesheets multiple times; testing setStylesheet()(again).
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase3()
    {
        reporter.testCaseInit("Re-using multiple stylesheets multiple times; testing setStylesheet()(again)");
        XSLTEngineImpl processor = null;
        XSLTEngineImpl otherProcessor = null;
        try
        {
            if ((liaison == null) || ("".equals(liaison)))
            {
                processor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessor();
                otherProcessor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessor();
            }
            else
            {
                processor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
                otherProcessor = (XSLTEngineImpl) XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
            }
        }
        catch (Exception e)
        {
            reporter.checkErr("Could not create processor, threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Could not create processor");
            return false;
        }        

        try
        {
            /* This code is very similar, but not identical to testCase2 */
            int result = Logger.INCP_RESULT;
            // Create one stylesheet
            XSLTInputSource xslSrc = new XSLTInputSource(testFileInfo.inputName);
            StylesheetRoot stylesheetRoot = processor.processStylesheet(xslSrc);

            // Create another stylesheet
            XSLTInputSource otherXslSrc = new XSLTInputSource(otherFileInfo.inputName);
            StylesheetRoot otherStylesheetRoot = processor.processStylesheet(otherXslSrc);

            // Set the second stylesheet into the processor (different than testCase2)
            processor.setStylesheet(otherStylesheetRoot);
            // Verify you can do a process now
            reporter.logTraceMsg("About to process1(" + otherFileInfo.xmlName 
                                 + ", " + otherFileInfo.xmlName + ",...)");
            processor.process(new XSLTInputSource(otherFileInfo.xmlName), 
                              null,
                              new XSLTResultTarget(outNames.nextName()));
            result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(otherFileInfo.goldName), 
                              "Second processStylesheet(1) implicit transform into: " + outNames.currentName());
            if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("Second processStylesheet(1) implicit transform failure reason:" + fileChecker.getExtendedInfo());

            // Reset, explicitly set the stylesheet to the first one, 
            //  and process again
            processor.reset();
            processor.setStylesheet(stylesheetRoot);
            reporter.logTraceMsg("About to process2(" + testFileInfo.xmlName 
                                 + ", " + testFileInfo.xmlName + ",...)");
            processor.process(new XSLTInputSource(testFileInfo.xmlName), 
                              null,
                              new XSLTResultTarget(outNames.nextName()));
            result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "setStylesheet(firstone) transform into: " + outNames.currentName());
            if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("setStylesheet(firstone) transform failure reason:" + fileChecker.getExtendedInfo());

            // Reset, explicitly set the stylesheet to the second one, 
            //  and process again
            processor.reset();
            processor.setStylesheet(otherStylesheetRoot);
            reporter.logTraceMsg("About to process3(" + otherFileInfo.xmlName 
                                 + ", " + otherFileInfo.xmlName + ",...)");
            processor.process(new XSLTInputSource(otherFileInfo.xmlName), 
                              null,
                              new XSLTResultTarget(outNames.nextName()));
            result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(otherFileInfo.goldName), 
                              "setStylesheet(secondone) transform into: " + outNames.currentName());
            if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("setStylesheet(secondone) transform failure reason:" + fileChecker.getExtendedInfo());
        } 
        catch (Throwable t)
        {
            reporter.checkErr("Testcase threw: " + t.toString());
            reporter.logThrowable(Logger.ERRORMSG, t, "Testcase threw");
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
        return ("Common [optional] options supported by StylesheetReuseTest:\n"
                + "(Note: assumes inputDir=tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        StylesheetReuseTest app = new StylesheetReuseTest();
        app.doMain(args);
    }
}
