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
 * TemplatesAPITest.java
 *
 */
package org.apache.qetest.trax;

import java.io.File;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.qetest.FileBasedTest;
import org.apache.qetest.OutputNameManager;
import org.apache.qetest.QetestUtils;
import org.apache.qetest.xsl.XSLTestfileInfo;

//-------------------------------------------------------------------------

/**
 * Basic API coverage test for the Templates class of TRAX.
 * @author shane_curcuru@lotus.com
 */
public class TemplatesAPITest extends FileBasedTest
{

    /**
     * Cheap-o filename for various output files.
     *
     */
    protected OutputNameManager outNames;

    /** Cheap-o filename set for both API tests and exampleSimple. */
    protected XSLTestfileInfo simpleTest = new XSLTestfileInfo();

    /** Name of a stylesheet with xsl:output HTML. */
    protected String outputFormatXSL = null;

    /** System property name javax.xml.transform.TransformerFactory.  */
    public static final String TRAX_PROCESSOR_XSLT = "javax.xml.transform.TransformerFactory";

    /** Known outputFormat property name from outputFormatTest  */
    public static final String OUTPUT_FORMAT_NAME = OutputKeys.CDATA_SECTION_ELEMENTS;

    /** Known outputFormat property value from outputFormatTest  */
    public static final String OUTPUT_FORMAT_VALUE = "cdataHere";

    /** NEEDSDOC Field TRAX_SUBDIR          */
    public static final String TRAX_SUBDIR = "trax";

    /** Default ctor initializes test name, comment, numTestCases. */
    public TemplatesAPITest()
    {

        numTestCases = 1;  // REPLACE_num
        testName = "TemplatesAPITest";
        testComment = "Basic API coverage test for the Templates class of TRAX";
    }

    /**
     * Initialize this test - Set names of xml/xsl test files, cache system property.  
     *
     * NEEDSDOC @param p
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean doTestFileInit(Properties p)
    {

        // Used for all tests; just dump files in xapi subdir
        File outSubDir = new File(outputDir + File.separator + TRAX_SUBDIR);

        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: "
                                   + outSubDir);

        outNames = new OutputNameManager(outputDir + File.separator + TRAX_SUBDIR
                                         + File.separator + testName, ".out");

        // Used for API coverage and exampleSimple
        String testBasePath = inputDir + File.separator + TRAX_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir + File.separator + TRAX_SUBDIR
                              + File.separator;

        simpleTest.xmlName = QetestUtils.filenameToURL(testBasePath + "TransformerAPIParam.xml");
        simpleTest.inputName = QetestUtils.filenameToURL(testBasePath + "TransformerAPIParam.xsl");
        simpleTest.goldName = goldBasePath + "TransformerAPIParam.out";
        outputFormatXSL = QetestUtils.filenameToURL(testBasePath + "TransformerAPIOutputFormat.xsl");

        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property is: "
                            + System.getProperty(TRAX_PROCESSOR_XSLT));
        try
        {
            TransformerFactory tf = TransformerFactory.newInstance();
            if (!(tf.getFeature(StreamSource.FEATURE)
                  && tf.getFeature(StreamResult.FEATURE)))
            {   // The rest of this test relies on Streams
                reporter.logErrorMsg("Streams not supported! Some tests may be invalid!");
            }
        }
        catch (Throwable t)
        {
            reporter.checkFail(
                "Problem creating factory; Some tests may be invalid!");
            reporter.logThrowable(reporter.ERRORMSG, t,
                                  "Problem creating factory; Some tests may be invalid!");
        }

        return true;
    }


    /**
     * TRAX Templates: cover newTransformer(), 
     * getOutputProperties() APIs and basic functionality.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("TRAX Templates: cover APIs and basic functionality");

        TransformerFactory factory = null;
        try
        {
            factory = TransformerFactory.newInstance();
        }
        catch (Exception e)
        {
            reporter.checkFail(
                "Problem creating Processor; cannot continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "Problem creating Processor");
            return true;
        }

        try
        {
            // Cover APIs newTransformer(), getOutputProperties()
            Templates templates =
                factory.newTemplates(new StreamSource(simpleTest.inputName));
            Transformer transformer = templates.newTransformer();

            reporter.check((transformer != null), true,
                           "newTransformer() is non-null for "
                           + simpleTest.inputName);

            Properties outputFormat = templates.getOutputProperties();

            reporter.check((outputFormat != null), true,
                           "getOutputProperties() is non-null for "
                           + simpleTest.inputName);
            reporter.logHashtable(reporter.STATUSMSG, outputFormat,
                                  "getOutputProperties for " + simpleTest.inputName);

            // Check that the local stylesheet.getProperty has default set, cf. getOutputProperties javadoc
            reporter.check(("xml".equals(outputFormat.getProperty(OutputKeys.METHOD))), true, simpleTest.inputName + ".op.getProperty(" 
                           + OutputKeys.METHOD + ") is default value, act: " + outputFormat.getProperty(OutputKeys.METHOD));
            // Check that the local stylesheet.get has nothing set, cf. getOutputProperties javadoc
            reporter.check((null == outputFormat.get(OutputKeys.METHOD)), true, simpleTest.inputName + ".op.get(" 
                           + OutputKeys.METHOD + ") is null value, act: " + outputFormat.get(OutputKeys.METHOD));

            // Check that the local stylesheet.getProperty has default set, cf. getOutputProperties javadoc
            reporter.check(("no".equals(outputFormat.getProperty(OutputKeys.INDENT))), true, simpleTest.inputName + ".op.getProperty(" 
                           + OutputKeys.INDENT + ") is default value, act: " + outputFormat.getProperty(OutputKeys.INDENT));
            // Check that the local stylesheet.get has nothing set, cf. getOutputProperties javadoc
            reporter.check((null == (outputFormat.get(OutputKeys.INDENT))), true, simpleTest.inputName + ".op.get(" 
                           + OutputKeys.INDENT + ") is null value, act: " + outputFormat.get(OutputKeys.INDENT));
        }
        catch (Exception e)
        {
            reporter.checkErr("newTransformer/getOutputProperties threw: "
                              + e.toString());
            reporter.logThrowable(reporter.STATUSMSG, e,
                                  "newTransformer/getOutputProperties threw:");
        }

        try
        {
            Templates templates2 =
                factory.newTemplates(new StreamSource(outputFormatXSL));
            Properties outputFormat2 = templates2.getOutputProperties();

            reporter.check((outputFormat2 != null), true,
                           "getOutputProperties() is non-null for "
                           + outputFormatXSL);
            reporter.logHashtable(reporter.STATUSMSG, outputFormat2,
                                  "getOutputProperties for " + outputFormatXSL);

            String tmp = outputFormat2.getProperty(OUTPUT_FORMAT_NAME); // SPR SCUU4RXSG5 - has extra space
            if (OUTPUT_FORMAT_VALUE.equals(tmp))    // Use if so we can put out id with checkPass/checkFail lines
                reporter.checkPass("outputProperties " + OUTPUT_FORMAT_NAME + " has known value ?" + tmp + "?", "SCUU4RXSG5");
            else
                reporter.checkFail("outputProperties " + OUTPUT_FORMAT_NAME + " has known value ?" + tmp + "?", "SCUU4RXSG5");

            tmp = outputFormat2.getProperty("omit-xml-declaration");
            reporter.check(tmp, "yes", "outputProperties omit-xml-declaration has known value ?" + tmp + "?");
        }
        catch (Exception e)
        {
            reporter.checkErr("outputFormat() is html... threw: "
                              + e.toString());
            reporter.logThrowable(reporter.STATUSMSG, e,
                                  "outputFormat() is html... threw:");
        }
        reporter.logTraceMsg("Functionality of Transformers covered in TransformerAPITest, elsewhere");
        reporter.testCaseClose();

        return true;
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String usage()
    {

        return ("Common [optional] options supported by TemplatesAPITest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + "-processorClassname classname.of.processor  (to override setPlatformDefaultProcessor to Xalan 2.x)\n"
                + super.usage());
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {

        TemplatesAPITest app = new TemplatesAPITest();

        app.doMain(args);
    }
}
