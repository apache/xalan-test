/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights 
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
 * Minitest.java
 *
 */
package org.apache.qetest.trax;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

// Needed SAX, DOM, JAXP classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// java classes
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Minitest - developer check-in test for Xalan-J 2.x.
 * <p>Developers should run the minitest.xalan2 target to compile 
 * and run this test before checking in (and obviously the test 
 * should pass, as well!).  If you do have problems with this test, 
 * contact our mailing list xalan-dev@xml.apache.org for help.</p>
 * <p>Running the minitest before checking in ensures that the 
 * Xalan CVS tree will always be in a compileable and at least 
 * basically functional state, thus ensuring a workable product 
 * for your fellow Xalan developers.</p>
 * <p>If you really need to make a checkin that will temporarily 
 * break or fail the minitest, then <b>please</b> be sure to send 
 * email to xalan-dev letting everyone know.</p>
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class Minitest extends XSLProcessorTestBase
{

    /**
     * Provides nextName(), currentName() functionality for tests 
     * that may produce any number of output files.
     */
    protected OutputNameManager outNames;

    /** The Minitest.xsl/.xml file; note goldName is version-specific.  */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();

    /** The MinitestParam.xsl/.xml file.  */
    protected XSLTestfileInfo paramFileInfo = new XSLTestfileInfo();

    /** Cheap-o overall results aggregator for Minitest.pass/Minitest.fail file. */
    protected String aggregateResults ="";
     
    /** Constants matching parameter names/values in paramFileInfo.  */
    public static final String PARAM1S = "param1s";
    public static final String PARAM2S = "param2s";
    public static final String PARAM1N = "param1n";
    public static final String PARAM2N = "param2n";

    /** Cheap-o extensions for Minitest.pass/.fail. */
    public static final String PASS_EXT = "." + Reporter.PASS;

    /** Cheap-o extensions for Minitest.pass/.fail. */
    public static final String FAIL_EXT = "." + Reporter.FAIL;

    /** Just initialize test name, comment, numTestCases. */
    public Minitest()
    {
        numTestCases = 3;  // REPLACE_num
        testName = "Minitest";
        testComment = "Minitest - developer check-in test for Xalan-J 2.x.";
    }


    /**
     * Initialize this test - Set names of xml/xsl test files, etc.
     * Also cleans up the special Minitest.pass/fail files.
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        // Used for all tests; just dump files in outputDir
        File outSubDir = new File(outputDir);
        if (!outSubDir.mkdirs())
        {
            if (!outSubDir.exists())
                reporter.logErrorMsg("Problem creating output dir: " + outSubDir);
        }
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir
                                         + File.separator + testName, ".out");

        String testBasePath = inputDir 
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator;

        testFileInfo.inputName = testBasePath + "Minitest.xsl";
        testFileInfo.xmlName = testBasePath + "Minitest.xml";
        // Use separate output files for different versions, since 
        //  some indenting rules are implemented differently 1.x/2.x
        testFileInfo.goldName = goldBasePath + "Minitest-xalanj2.out";

        paramFileInfo.inputName = testBasePath + "MinitestParam.xsl";
        paramFileInfo.xmlName = testBasePath + "MinitestParam.xml";
        paramFileInfo.goldName = goldBasePath + "MinitestParam.out";

        reporter.logHashtable(reporter.INFOMSG, System.getProperties(),
                              "System.getProperties");
        reporter.logHashtable(reporter.INFOMSG, testProps, "testProps");
        try
        {
            // Clean up the special minitest result files we output in doTestFileClose
            File f = new File(outputDir + File.separator + testName + PASS_EXT);
            f.delete();
            f = new File(outputDir + File.separator + testName + FAIL_EXT);
            f.delete();
        } 
        catch (Exception e)
        {
            reporter.logErrorMsg("Deleting minitest.pass/fail files threw: " + e.toString());
            reporter.logThrowable(reporter.ERRORMSG, e, "Deleting minitest.pass/fail files threw");
        }
        return true;
    }


    /**
     * Cleanup this test - write out special Minitest results file.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileClose(Properties p)
    {
        String passFailFilename = outputDir + File.separator + testName;
        if (reporter.getCurrentFileResult() == reporter.PASS_RESULT)
        {
            passFailFilename += PASS_EXT;
        }
        else
        {
            // Note any non-pass is called a 'fail' here, including 
            //  any Fail/Incp/Ambg/Errr results
            passFailFilename += FAIL_EXT;
        }
        try
        {
            reporter.logTraceMsg("About to write out passFailFilename: " + passFailFilename);
            PrintWriter passFailFile = new PrintWriter(new FileOutputStream(passFailFilename));
            passFailFile.println("Minitest-result:" + reporter.resultToString(reporter.getCurrentFileResult()));
            passFailFile.println("Minitest-testCases:" + aggregateResults);
            passFailFile.println("Minitest-logFile:" + testProps.getProperty(Logger.OPT_LOGFILE, "none"));
            passFailFile.println("Minitest-date:" + (new Date()).toString());
            passFailFile.close();
        } 
        catch (Exception e)
        {
            reporter.logErrorMsg("NOTE! writing " + passFailFilename + " threw: " + e.toString());
            reporter.logThrowable(reporter.ERRORMSG, e, "NOTE! writing " + passFailFilename + " threw");
        }
        return true;
    }


    /**
     * Basic stream transforms and simple API coverage.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Basic stream transforms and simple API coverage");

        TransformerFactory factory = null;
        Templates templates = null;
        Transformer transformer = null;
        try
        {
            factory = TransformerFactory.newInstance();
            templates = factory.newTemplates(new StreamSource(filenameToURI(testFileInfo.inputName)));
            reporter.check((templates != null), true, "factory.newTemplates(StreamSource) is non-null");
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem creating Templates; cannot continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, t, 
                                  "Problem creating Templates; cannot continue testcase");
            return testCaseClose(true);
        }
        try
        {
            // Validate a stream transform
            reporter.logTraceMsg("Basic stream transform(1)(" + filenameToURI(testFileInfo.xmlName) + ", "
                                 + filenameToURI(testFileInfo.inputName)  + ", "
                                 + outNames.nextName());
            transformer = templates.newTransformer();
            transformer.transform(new StreamSource(filenameToURI(testFileInfo.xmlName)), 
                                  new StreamResult(new FileOutputStream(outNames.currentName())));
            int fileCheckStatus = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "Basic stream transform(1) into: " + outNames.currentName());
            if (fileCheckStatus != reporter.PASS_RESULT)
            {
                reporter.logWarningMsg("Basic stream transform(1) into: " + outNames.currentName()
                                       + fileChecker.getExtendedInfo());
            }

            // Validate transformer reuse
            reporter.logTraceMsg("Basic stream transform(2)(" + filenameToURI(testFileInfo.xmlName) + ", "
                                 + filenameToURI(testFileInfo.inputName)  + ", "
                                 + outNames.nextName());
            transformer.transform(new StreamSource(filenameToURI(testFileInfo.xmlName)), 
                                  new StreamResult(new FileOutputStream(outNames.currentName())));
            fileCheckStatus = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "Basic stream transform(2) into: " + outNames.currentName());
            if (fileCheckStatus != reporter.PASS_RESULT)
            {
                reporter.logWarningMsg("Basic stream transform(2) into: " + outNames.currentName()
                                       + fileChecker.getExtendedInfo());
            }
        } 
        catch (Throwable t)
        {
            reporter.checkFail("Problem with simple stream transform");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with simple stream transform");
        }

        try
        {
            // Validate selected API's - primarily Parameters
            Templates paramTemplates = factory.newTemplates(new StreamSource(filenameToURI(paramFileInfo.inputName)));
            Transformer paramTransformer = paramTemplates.newTransformer();
            String paramStr = "paramVal";
            paramTransformer.setParameter(PARAM1S, paramStr);
            reporter.logTraceMsg("Just set " + PARAM1S + " to " + paramStr);
            Object tmp = paramTransformer.getParameter(PARAM1S);    // SPR SCUU4QWTVZ - returns an XObject - fixed
            if (tmp == null)
            {
                reporter.checkFail(PARAM1S + " is still set to null!");
            }
            else
            {   // Validate SPR SCUU4QWTVZ - should return the same type you set
                if (tmp instanceof String)
                {
                    reporter.checkObject(tmp, paramStr, PARAM1S + " is now set to ?" + tmp + "?");
                }
                else
                {
                    reporter.checkFail(PARAM1S + " is now ?" + tmp + "?, isa " + tmp.getClass().getName());
                }
            }

            // Verify simple re-set/get of a single parameter - new Integer
            Integer paramInteger = new Integer(1234);
            paramTransformer.setParameter(PARAM1S, paramInteger);   // SPR SCUU4R3JGY - can't re-set
            reporter.logTraceMsg("Just reset " + PARAM1S + " to new Integer(99)");
            tmp = null;
            tmp = paramTransformer.getParameter(PARAM1S);
            if (tmp == null)
            {
                reporter.checkFail(PARAM1S + " is still set to null!");
            }
            else
            {   // Validate SPR SCUU4QWTVZ - should return the same type you set
                if (tmp instanceof Integer)
                {
                    reporter.checkObject(tmp, paramInteger, PARAM1S + " is now set to ?" + tmp + "?");
                }
                else
                {
                    reporter.checkFail(PARAM1S + " is now ?" + tmp + "?, isa " + tmp.getClass().getName());
                }
            }
            // Validate a transform with two params set
            paramTransformer.setParameter(PARAM1N, "new-param1n-value");
            reporter.logTraceMsg("Just reset " + PARAM1N + " to new-param1n-value");

            reporter.logTraceMsg("Stream-param transform(" + filenameToURI(paramFileInfo.xmlName) + ", "
                                 + filenameToURI(paramFileInfo.inputName)  + ", "
                                 + outNames.nextName());
            paramTransformer.transform(new StreamSource(filenameToURI(paramFileInfo.xmlName)), 
                                  new StreamResult(new FileOutputStream(outNames.currentName())));
            int fileCheckStatus = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(paramFileInfo.goldName), 
                              "Stream transform with params into: " + outNames.currentName());
            if (fileCheckStatus != reporter.PASS_RESULT)
            {
                reporter.logWarningMsg("Stream transform with params into: " + outNames.currentName()
                                       + fileChecker.getExtendedInfo());
            }
            // Validate params are still set after transform
            tmp = paramTransformer.getParameter(PARAM1S);
            reporter.checkObject(tmp, paramInteger, PARAM1S + " is now set to ?" + tmp + "?");
            tmp = paramTransformer.getParameter(PARAM1N);
            reporter.checkObject(tmp, "new-param1n-value", PARAM1N + " is now set to ?" + tmp + "?");
        } 
        catch (Throwable t)
        {
            reporter.checkFail("Problem with parameters");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with parameters");
        }
            

        return testCaseClose(true);
    }


    /**
     * Basic DOM transforms and simple API coverage.
     * // @todo!
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Basic DOM transforms and simple API coverage");

        TransformerFactory factory = null;
        Templates templates = null;
        try
        {
            factory = TransformerFactory.newInstance();
            // templates = factory.newTemplates(new StreamSource(filenameToURI(testFileInfo.inputName)));
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem creating Templates; cannot continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, t, 
                                  "Problem creating Templates; cannot continue testcase");
            return testCaseClose(true);
        }
        try
        {
            // Add DOM test code here
            reporter.check(true, true, "@todo Add DOM test code here");
        } 
        catch (Throwable t)
        {
            reporter.checkFail("Problem with test(1)");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with test(1)");
        }
        return testCaseClose(true);
    }


    /**
     * Basic SAX transforms and simple API coverage.
     * // @todo!
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase3()
    {
        reporter.testCaseInit("Basic SAX transforms and simple API coverage");

        TransformerFactory factory = null;
        Templates templates = null;
        try
        {
            factory = TransformerFactory.newInstance();
            // templates = factory.newTemplates(new StreamSource(filenameToURI(testFileInfo.inputName)));
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem creating Templates; cannot continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, t, 
                                  "Problem creating Templates; cannot continue testcase");
            return testCaseClose(true);
        }
        try
        {
            // Add SAX test code here
            reporter.check(true, true, "@todo Add SAX test code here");
        } 
        catch (Throwable t)
        {
            reporter.checkFail("Problem with test(1)");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with test(1)");
        }
        return testCaseClose(true);
    }


    /**
     * Worker method to translate String to URI.  
     * Note: Xerces and Crimson appear to handle some URI references 
     * differently - this method needs further work once we figure out 
     * exactly what kind of format each parser wants (esp. considering 
     * relative vs. absolute references).
     * @param String path\filename of test file
     * @return URL to pass to SystemId
     */
    public String filenameToURI(String filename)
    {
        File f = new File(filename);
        String tmp = f.getAbsolutePath();
	    if (File.separatorChar == '\\') {
	        tmp = tmp.replace('\\', '/');
	    }
        return "file:///" + tmp;
    }


    /**
     * Convenience method to close a testCase and return.  
     * This is kind of a hack for the minitest, so it can dump a 
     * cheap overview report into the passFailFilename above.
     * @param boolean to return
     * @return value passed in
     */
    public boolean testCaseClose(boolean flag)
    {
        aggregateResults += "\ntestCase(" + reporter.getCurrentCaseNum() 
                          + ") " + reporter.getCurrentCaseComment() 
                          + " result:" + reporter.resultToString(reporter.getCurrentCaseResult());
        reporter.testCaseClose();
        return flag;
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by Minitest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        Minitest app = new Minitest();
        app.doMain(args);
    }
}
