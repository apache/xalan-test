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

package org.apache.qetest.trax;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;
import org.apache.qetest.xslwrapper.TransformWrapper;
import org.apache.qetest.xslwrapper.TransformWrapperFactory;
import org.apache.qetest.xslwrapper.TransformWrapperHelper;

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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

/**
 * Minitest - developer check-in test for Xalan-J 2.x.  
 *
 * <p>Developers should always run either the minitest or smoketest 
 * target before checking any code into the xml-xalan CVS 
 * repository.  Running the minitest before checking in ensures 
 * that the Xalan CVS tree will always be in a compileable and 
 * at least basically functional state, thus ensuring a workable 
 * product for your fellow Xalan developers.  Ensuring your code 
 * passes the smoketest target will also help the nightly GUMP 
 * runs to pass the smoketest as well and avoid 'nag' emails.</p>
 *
 * <p>If you really need to make a checkin that will temporarily 
 * break or fail the minitest, then <b>please</b> be sure to send 
 * email to xalan-dev@xml.apache.org letting everyone know.</p>
 *
 * <p>For more information, please see the 
 * <a href="http://xml.apache.org/xalan-j/test/overview.html">
 * testing docs</a> and the nightly 
 * <a href="http://jakarta.apache.org/builds/gump/">
 * GUMP build page</a>.</p>
 *
 * @author shane_curcuru@us.ibm.com
 * @version $Id$
 */
public class Minitest extends FileBasedTest
{

    /**
     * Provides nextName(), currentName() functionality for tests 
     * that may produce any number of output files.
     */
    protected OutputNameManager outNames;

    /**
     * Basic output name root used throughout tests.
     */
    protected String baseOutName;

    /** The Minitest.xsl/.xml file; note goldName is version-specific.  */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();

    /** The MinitestParam.xsl/.xml file.  */
    protected XSLTestfileInfo paramFileInfo = new XSLTestfileInfo();

    /** The MinitestPerf.xsl/.xml file.  */
    protected XSLTestfileInfo perfFileInfo = new XSLTestfileInfo();


    /** Constants matching parameter names/values in paramFileInfo.  */
    public static final String PARAM1S = "param1s";
    public static final String PARAM2S = "param2s";
    public static final String PARAM1N = "param1n";
    public static final String PARAM2N = "param2n";

    /** Just initialize test name, comment, numTestCases. */
    public Minitest()
    {
        numTestCases = 5;  // REPLACE_num
        testName = "Minitest";
        testComment = "Minitest - developer check-in test for Xalan-J 2.x.";
    }


    /**
     * Initialize this test - Set names of xml/xsl test files, etc.
     *
     * Also cleans up any Pass-Minitest.xml file that is checked 
     * for in test.properties' minitest.passfile and generated by 
     * Reporter.writeResultsStatus().
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     * @see Reporter.writeResultsStatus(boolean)
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
        baseOutName = outputDir + File.separator + testName;
        outNames = new OutputNameManager(baseOutName, ".out");

        String testBasePath = inputDir 
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator;

        testFileInfo.inputName = testBasePath + "Minitest.xsl";
        testFileInfo.xmlName = testBasePath + "Minitest.xml";
        // Use separate output files for different versions, since 
        //  some indenting rules are implemented differently 1.x/2.x
        testFileInfo.goldName = goldBasePath + "Minitest-xalanj2.out";
        testFileInfo.description = "General minitest, covers many xsl: elems";

        paramFileInfo.inputName = testBasePath + "MinitestParam.xsl";
        paramFileInfo.xmlName = testBasePath + "MinitestParam.xml";
        paramFileInfo.goldName = goldBasePath + "MinitestParam.out";
        paramFileInfo.description = "Simple string and int params";

        perfFileInfo.inputName = testBasePath + "MinitestPerf.xsl";
        perfFileInfo.xmlName = testBasePath + "MinitestPerf.xml";
        perfFileInfo.goldName = goldBasePath + "MinitestPerf.out";
        perfFileInfo.description = "Simple performance test";

        try
        {
            // Clean up any Pass files for the minitest that exist
            //@see Reporter.writeResultsStatus(boolean)
            String logFileBase = (new File(testProps.getProperty(Logger.OPT_LOGFILE, "ResultsSummary.xml"))).getAbsolutePath();
            logFileBase = (new File(logFileBase)).getParent();

            File f = new File(logFileBase, Logger.PASS + "-" + testName + ".xml");
            reporter.logTraceMsg("Deleting previous file: " + f);
            f.delete();
        } 
        catch (Exception e)
        {
            reporter.logThrowable(reporter.ERRORMSG, e, "Deleting Pass-Minitest file threw");
            reporter.logErrorMsg("Deleting Pass-Minitest file threw: " + e.toString());
        }

        return true;
    }


    /**
     * Basic systemId transforms and params plus API coverage.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Basic systemId transforms and params plus API coverage");

        TransformerFactory factory = null;
        Templates templates = null;
        Transformer transformer = null;
        try
        {
            factory = TransformerFactory.newInstance();
            templates = factory.newTemplates(new StreamSource(QetestUtils.filenameToURL(testFileInfo.inputName)));
            reporter.check((templates != null), true, "factory.newTemplates(StreamSource) is non-null");
        }
        catch (Throwable t)
        {
            reporter.logThrowable(reporter.ERRORMSG, t, 
                                  "Problem creating Templates; cannot continue testcase");
            reporter.checkErr("Problem creating Templates; cannot continue testcase");
            return true;
        }
        try
        {
            // Validate a systemId transform
            reporter.logTraceMsg("Basic stream transform(1)(" + QetestUtils.filenameToURL(testFileInfo.xmlName) + ", "
                                 + QetestUtils.filenameToURL(testFileInfo.inputName)  + ", "
                                 + outNames.nextName());
            transformer = templates.newTransformer();
            FileOutputStream fos = new FileOutputStream(outNames.currentName());
            transformer.transform(new StreamSource(QetestUtils.filenameToURL(testFileInfo.xmlName)), 
                                  new StreamResult(fos));
            fos.close();
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
            reporter.logTraceMsg("Basic stream transform(2)(" + QetestUtils.filenameToURL(testFileInfo.xmlName) + ", "
                                 + QetestUtils.filenameToURL(testFileInfo.inputName)  + ", "
                                 + outNames.nextName());
            fos = new FileOutputStream(outNames.currentName());
            transformer.transform(new StreamSource(QetestUtils.filenameToURL(testFileInfo.xmlName)), 
                                  new StreamResult(fos));
            fos.close();
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
            Templates paramTemplates = factory.newTemplates(new StreamSource(QetestUtils.filenameToURL(paramFileInfo.inputName)));
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

            reporter.logTraceMsg("Stream-param transform(" + QetestUtils.filenameToURL(paramFileInfo.xmlName) + ", "
                                 + QetestUtils.filenameToURL(paramFileInfo.inputName)  + ", "
                                 + outNames.nextName());
            FileOutputStream fos = new FileOutputStream(outNames.currentName());
            paramTransformer.transform(new StreamSource(QetestUtils.filenameToURL(paramFileInfo.xmlName)), 
                                  new StreamResult(fos));
            fos.close();
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
        reporter.testCaseClose();
        return true;
    }


    /**
     * Basic trax.dom transformWrapper.  
     * 
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        final String FLAVOR = "trax.dom";
        final String DESC = "Basic " + FLAVOR + " transformWrapper";
        reporter.testCaseInit(DESC);
        try
        {
            testFileInfo.outputName = outNames.nextName();
            transformUsingFlavor(testFileInfo, FLAVOR);
            fileChecker.check(reporter, 
                              new File(testFileInfo.outputName), 
                              new File(testFileInfo.goldName), 
                              DESC +" into: " + testFileInfo.outputName);
        
        } 
        catch (Throwable t)
        {
            reporter.logThrowable(reporter.ERRORMSG, t, DESC + " threw: ");
            reporter.checkErr(DESC + " threw: " + t.toString());
        }
        return true;
    }


    /**
     * Basic trax.sax transformWrapper.  
     * 
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase3()
    {
        final String FLAVOR = "trax.sax";
        final String DESC = "Basic " + FLAVOR + " transformWrapper";
        reporter.testCaseInit(DESC);
        try
        {
            testFileInfo.outputName = outNames.nextName();
            transformUsingFlavor(testFileInfo, FLAVOR);
            fileChecker.check(reporter, 
                              new File(testFileInfo.outputName), 
                              new File(testFileInfo.goldName), 
                              DESC +" into: " + testFileInfo.outputName);
        } 
        catch (Throwable t)
        {
            reporter.logThrowable(reporter.ERRORMSG, t, DESC + " threw: ");
            reporter.checkErr(DESC + " threw: " + t.toString());
        }
        reporter.testCaseClose();
        return true;
    }


    /**
     * Basic trax.stream transformWrapper.  
     * 
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase4()
    {
        final String FLAVOR = "trax.stream";
        final String DESC = "Basic " + FLAVOR + " transformWrapper";
        reporter.testCaseInit(DESC);
        try
        {
            testFileInfo.outputName = outNames.nextName();
            transformUsingFlavor(testFileInfo, FLAVOR);
            fileChecker.check(reporter, 
                              new File(testFileInfo.outputName), 
                              new File(testFileInfo.goldName), 
                              DESC +" into: " + testFileInfo.outputName);
        } 
        catch (Throwable t)
        {
            reporter.logThrowable(reporter.ERRORMSG, t, DESC + " threw: ");
            reporter.checkErr(DESC + " threw: " + t.toString());
        }
        reporter.testCaseClose();
        return true;
    }


    /**
     * Basic performance measurements of sample files.  
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase5()
    {
        String flavor = null;
        final String DESC = "Simple performance measurement ";
        reporter.testCaseInit(DESC);
        // Reset the counting for outputNames for this testcase
        outNames = new OutputNameManager(baseOutName + "Perf", ".out");
        try
        {
            long[] times = null;
            Vector streamTimes = new Vector();
            Vector domTimes = new Vector();
            TransformWrapper transformWrapper = null;

            flavor = "trax.stream";
            transformWrapper = TransformWrapperFactory.newWrapper(flavor);
            transformWrapper.newProcessor(testProps);
            reporter.logHashtable(Logger.TRACEMSG, transformWrapper.getProcessorInfo(), "wrapper.getProcessorInfo() for next transforms");

            // Repeat a few times with streams
            for (int i = 1; i <= 5; i++)
            {
                perfFileInfo.outputName = outNames.nextName();
                reporter.logInfoMsg("perf-stream transform into " + perfFileInfo.outputName);
                times = transformWrapper.transform(perfFileInfo.xmlName, perfFileInfo.inputName, perfFileInfo.outputName);
                logPerfElem(times, perfFileInfo, flavor);
                streamTimes.addElement(new Long(times[TransformWrapper.IDX_OVERALL]));
            }
            // Only bother checking the *last* iteration of perfs
            fileChecker.check(reporter, 
                              new File(perfFileInfo.outputName), 
                              new File(perfFileInfo.goldName), 
                              DESC + flavor + " into: " + perfFileInfo.outputName);
            
            flavor = "trax.dom";
            transformWrapper = TransformWrapperFactory.newWrapper(flavor);
            transformWrapper.newProcessor(testProps);
            reporter.logHashtable(Logger.TRACEMSG, transformWrapper.getProcessorInfo(), "wrapper.getProcessorInfo() for next transforms");

            // Repeat a few times with DOMs
            for (int i = 1; i <= 5; i++)
            {
                perfFileInfo.outputName = outNames.nextName();
                reporter.logInfoMsg("perf-dom transform into " + perfFileInfo.outputName);
                times = transformWrapper.transform(perfFileInfo.xmlName, perfFileInfo.inputName, perfFileInfo.outputName);
                logPerfElem(times, perfFileInfo, flavor);
                domTimes.addElement(new Long(times[TransformWrapper.IDX_OVERALL]));
            }
            // Only bother checking the *last* iteration of perfs
            fileChecker.check(reporter, 
                              new File(perfFileInfo.outputName), 
                              new File(perfFileInfo.goldName), 
                              DESC + flavor + " into: " + perfFileInfo.outputName);

            // Log a big message at the very end to make it easier to see
            StringBuffer buf = new StringBuffer("Minitest.testCase5 PERFORMANCE NUMBERS\n");
            buf.append("        STREAM OVERALL TIMES: ");
            for (Enumeration enum = streamTimes.elements();
                    enum.hasMoreElements(); /* no increment portion */ )
            {
                buf.append(enum.nextElement());
                buf.append(", ");
            }
            buf.append("\n");
            buf.append("           DOM OVERALL TIMES: ");
            for (Enumeration enum = domTimes.elements();
                    enum.hasMoreElements(); /* no increment portion */ )
            {
                buf.append(enum.nextElement());
                buf.append(", ");
            }
            buf.append("\n");
            reporter.logArbitrary(Logger.CRITICALMSG, buf.toString());
        } 
        catch (Throwable t)
        {
            reporter.logThrowable(reporter.ERRORMSG, t, DESC + flavor + " threw: ");
            reporter.checkErr(DESC + flavor + " threw: " + t.toString());
        }
        reporter.testCaseClose();
        return true;
    }


    /**
     * Worker method to use a TransformWrapper to transform a file.
     * 
     * @param fileInfo inputName, xmlName of file to test
     * @param flavor of TransformWrapper to use
     * @return log number of overall millisec for transform.
     */
    public void transformUsingFlavor(XSLTestfileInfo fileInfo, String flavor)
            throws Exception
    {
        TransformWrapper transformWrapper = TransformWrapperFactory.newWrapper(flavor);
        transformWrapper.newProcessor(testProps);
        reporter.logHashtable(Logger.TRACEMSG, transformWrapper.getProcessorInfo(), "wrapper.getProcessorInfo() for next transform");
        if (null == fileInfo.inputName)
        {
            // presume it's an embedded test
            reporter.logInfoMsg("transformEmbedded(" + fileInfo.xmlName + ", " + fileInfo.outputName + ")");
            long[] times = transformWrapper.transformEmbedded(fileInfo.xmlName, fileInfo.outputName);
            logPerfElem(times, fileInfo, flavor);
        }
        else
        {
            // presume it's a normal stylesheet test
            reporter.logInfoMsg("transform(" + fileInfo.xmlName + ", " + fileInfo.inputName + ", " + fileInfo.outputName + ")");
            long[] times = transformWrapper.transform(fileInfo.xmlName, fileInfo.inputName, fileInfo.outputName);
            logPerfElem(times, fileInfo, flavor);
        }
        
    }


    /**
     * Worker method to output a &lt;perf&gt; element.  
     * @return false if we should abort the test; true otherwise
     */
    public void logPerfElem(long[] times, XSLTestfileInfo fileInfo, String flavor)
    {
        Hashtable attrs = new Hashtable();
        // Add general information about this perf elem
        attrs.put("UniqRunid", testProps.getProperty("runId", "runId;none"));
        attrs.put("processor", flavor);
        // idref is the individual filename
        attrs.put("idref", (new File(fileInfo.inputName)).getName());
        // inputName is the actual name we gave to the processor
        attrs.put("inputName", fileInfo.inputName);

        // Add all available specific timing data as well
        for (int i = 0; i < times.length; i++)
        {
            // Only log items that have actual timing data
            if (TransformWrapper.TIME_UNUSED != times[i])
            {
                attrs.put(TransformWrapperHelper.getTimeArrayDesc(i), 
                          new Long(times[i]));
            }
        }

        // Log the element out; note formatting matches
        reporter.logElement(Logger.STATUSMSG, "perf", attrs, fileInfo.description);
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
