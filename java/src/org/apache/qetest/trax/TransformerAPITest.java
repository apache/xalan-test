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
 * TransformerAPITest.java
 *
 */
package org.apache.qetest.trax;

import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Just import the whole trax package; note the packaging is likely to change
import org.apache.trax.*;

// Use Serializer classes from Xalan distro
import org.apache.serialize.Method;
import org.apache.serialize.OutputFormat;

// java classes
import java.io.File;
import java.io.FileOutputStream;

import java.util.Properties;

// Needed SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;

// Needed DOM classes
import org.w3c.dom.Node;
import org.w3c.dom.Document;

// javax JAXP classes for parser pluggability
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//-------------------------------------------------------------------------

/**
 * Basic API coverage test for the Transformer class of TRAX.
 * @author shane_curcuru@lotus.com
 */
public class TransformerAPITest extends XSLProcessorTestBase
{

    /**
     * Cheap-o filename for various output files.
     *
     */
    protected OutputNameManager outNames;

    /** Cheap-o filename set for both API tests and exampleSimple. */
    protected XSLTestfileInfo simpleTest = new XSLTestfileInfo();

    /** NEEDSDOC Field paramTest          */
    protected XSLTestfileInfo paramTest = new XSLTestfileInfo();

    /** Cache the relevant system property. */
    protected String saveXSLTProp = null;

    /** Allow user to override our default of Xalan 2.x processor classname. */
    public static final String XALAN_CLASSNAME =
        "org.apache.xalan.processor.StylesheetProcessor";

    /** NEEDSDOC Field PROCESSOR_CLASSNAME          */
    protected String PROCESSOR_CLASSNAME = "processorClassname";

    /** NEEDSDOC Field processorClassname          */
    protected String processorClassname = XALAN_CLASSNAME;

    /** NEEDSDOC Field TRAX_PROCESSOR_XSLT          */
    public static final String TRAX_PROCESSOR_XSLT = "trax.processor.xslt";

    /** NEEDSDOC Field XSLT          */
    public static final String XSLT = "xslt";

    /** NEEDSDOC Field PROPERTY_LEXICAL_HANDLER          */
    public static final String PROPERTY_LEXICAL_HANDLER =
        "http://xml.org/sax/properties/lexical-handler";

    /** NEEDSDOC Field FEATURE_DOM_INPUT          */
    public static final String FEATURE_DOM_INPUT =
        "http://xml.org/trax/features/dom/input";

    /** NEEDSDOC Field FEATURE_SAX_INPUT          */
    public static final String FEATURE_SAX_INPUT =
        "http://xml.org/trax/features/sax/input";

    /** NEEDSDOC Field XAPI          */
    public static final String XAPI = "trax";

    /** Default ctor initializes test name, comment, numTestCases. */
    public TransformerAPITest()
    {

        numTestCases = 2;  // REPLACE_num
        testName = "TransformerAPITest";
        testComment = "Basic API coverage test for the class of TRAX";
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
        File outSubDir = new File(outputDir + File.separator + XAPI);

        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: "
                                   + outSubDir);

        outNames = new OutputNameManager(outputDir + File.separator + XAPI
                                         + File.separator + testName, ".out");

        // Used for API coverage and exampleSimple
        String testBasePath = inputDir + File.separator + XAPI
                              + File.separator;
        String goldBasePath = goldDir + File.separator + XAPI
                              + File.separator;

        simpleTest.xmlName = testBasePath + "APIMinitest.xml";
        simpleTest.inputName = testBasePath + "APIMinitest.xsl";
        simpleTest.goldName = goldBasePath + "APIMinitest.out";
        paramTest.xmlName = testBasePath + "TraxMinitestParam.xml";
        paramTest.inputName = testBasePath + "TraxMinitestParam.xsl";
        paramTest.goldName = goldBasePath + "TraxMinitestParam.out";

        // Cache trax system property
        saveXSLTProp = System.getProperty(TRAX_PROCESSOR_XSLT);

        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property is: "
                            + saveXSLTProp);

        // Check if user wants to use a processor other than Xalan 2.x
        processorClassname = testProps.getProperty(PROCESSOR_CLASSNAME,
                                                   XALAN_CLASSNAME);

        reporter.logInfoMsg(PROCESSOR_CLASSNAME + " property is: "
                            + processorClassname);
        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property is: "
                            + System.getProperty(TRAX_PROCESSOR_XSLT));

        // Just call this static method once for the whole test
        // TODO will this ever affect other tests run through a harness?
        Processor.setPlatformDefaultProcessor(processorClassname);
        reporter.logTraceMsg(
            "Processor.setPlatformDefaultProcessor(processorClassname)");

        return true;
    }

    /**
     * Cleanup this test - reset the cached system property trax.processor.xslt.  
     *
     * NEEDSDOC @param p
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean doTestFileClose(Properties p)
    {

        if (saveXSLTProp == null)
        {
            System.getProperties().remove(TRAX_PROCESSOR_XSLT);
        }
        else
        {
            System.getProperties().put(TRAX_PROCESSOR_XSLT, saveXSLTProp);
        }

        return true;
    }

    /**
     * TRAX Transformer: cover other APIs and functionality.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase1()
    {

        reporter.testCaseInit(
            "TRAX Transformer: cover other APIs and functionality");

        Processor p = null;
        Templates templates = null;

        try
        {
            p = Processor.newInstance(XSLT);

            // Use paramTest for later testing of setParameter()/resetParameters()
            templates = p.process(new InputSource(paramTest.inputName));
        }
        catch (Exception e)
        {
            reporter.checkFail(
                "Problem creating Processor; cannot continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "Problem creating Processor");

            return true;
        }

        Transformer transformer = templates.newTransformer();

        try
        {

            // Cover APIs get*Handler()
            if (p.getFeature(FEATURE_SAX_INPUT))
            {

                // Validate simply by checking for null
                reporter.check((transformer.getInputContentHandler() != null),
                               true, "getInputContentHandler() is non-null");
                reporter.check((transformer.getInputDeclHandler() != null),
                               true, "getInputDeclHandler() is non-null");
                reporter.check((transformer.getInputLexicalHandler() != null),
                               true, "getInputLexicalHandler() is non-null");
            }
            else
            {

                // Can't validate, just print out values
                reporter.logWarningMsg(
                    "getInputContentHandler is: "
                    + transformer.getInputContentHandler());
                reporter.logWarningMsg("getInputDeclHandler is: "
                                       + transformer.getInputDeclHandler());
                reporter.logWarningMsg(
                    "getInputLexicalHandler is: "
                    + transformer.getInputLexicalHandler());
            }
        }
        catch (Exception e)
        {
            reporter.checkErr("get*Handler() test threw: " + e.toString());
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "get*Handler() test threw:");
        }

        try
        {
            transformer = templates.newTransformer();

            // Basic setOutputFormat() test
            String fName1 = outNames.nextName();

            transformer.transform(new InputSource(paramTest.xmlName),
                                  new Result(new FileOutputStream(fName1)));
            reporter.logStatusMsg("Created default output: " + fName1);

            // Force output type to be different
            OutputFormat outputFormat = templates.getOutputFormat();

            outputFormat.setMethod(Method.Text);  // TODO better switch
            transformer.setOutputFormat(outputFormat);
            transformer.transform(
                new InputSource(paramTest.xmlName),
                new Result(new FileOutputStream(outNames.nextName())));
            reporter.checkAmbiguous("// TODO validate: Created TEXT output: "
                                    + outNames.currentName());
        }
        catch (Exception e)
        {
            reporter.checkErr("setOutputFormat() test threw: "
                              + e.toString());
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "setOutputFormat() test threw:");
        }

        try
        {
            transformer = templates.newTransformer();

            // Basic setParameter()/resetParameters() test
            String noParams = outNames.nextName();
            String withParams = outNames.nextName();
            String resetParams = outNames.nextName();
            String neverParams = outNames.nextName();

            transformer.transform(new InputSource(paramTest.xmlName),
                                  new Result(new FileOutputStream(noParams)));
            reporter.logStatusMsg("Created noParams output: " + noParams);

            String paramName = "paramName";
            String paramNamespace = null;  // TODO write stylesheet that uses the namespaces
            String paramValue = "paramValue";

            transformer.setParameter(paramName, paramNamespace, paramValue);
            transformer.transform(
                new InputSource(paramTest.xmlName),
                new Result(new FileOutputStream(withParams)));
            reporter.logStatusMsg("Created withParams output: " + withParams);
            transformer.resetParameters();
            transformer.transform(
                new InputSource(paramTest.xmlName),
                new Result(new FileOutputStream(resetParams)));
            reporter.checkAmbiguous(
                "// TODO validate Created resetParams output: "
                + resetParams);

            // Also check what happens when you use a new transformer
            transformer = templates.newTransformer();

            transformer.transform(
                new InputSource(paramTest.xmlName),
                new Result(new FileOutputStream(neverParams)));
            reporter.checkAmbiguous(
                "// TODO validate Created neverParams output: "
                + neverParams);
        }
        catch (Exception e)
        {
            reporter.checkErr("setParameter()/resetParameters() test threw: "
                              + e.toString());
            reporter.logThrowable(
                reporter.ERRORMSG, e,
                "setParameter()/resetParameters() test threw:");
        }

        reporter.checkAmbiguous(
            "// TODO Cover setURIResolver() API and functionality");
        reporter.testCaseClose();

        return true;
    }

    /**
     * TRAX Transformer: cover transform*() APIs and functionality.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase2()
    {

        reporter.testCaseInit(
            "TRAX Transformer: cover transform*() APIs and functionality");

        try
        {
            Processor p = Processor.newInstance(XSLT);

            // These tests are already covered in TraxWrapper.java
            reporter.checkAmbiguous("// TODO transform(InputSource) -> SAX");
            reporter.checkAmbiguous("// TODO transform(InputSource, Result)");

            if (p.getFeature(FEATURE_DOM_INPUT))
            {
                reporter.checkAmbiguous(
                    "// TODO transformNode(Node, Result)  if dom/input");
                reporter.checkAmbiguous(
                    "// TODO transformNode(Node) -> SAX  if dom/input");
            }
            else
            {
                reporter.logWarningMsg("Skipping transformNode(*) tests, "
                                       + FEATURE_DOM_INPUT
                                       + " not supported");
            }
        }
        catch (Exception e)
        {
            reporter.checkFail("TestCase threw: " + e.toString());
            reporter.logThrowable(reporter.ERRORMSG, e, "TestCase threw:");

            return true;
        }

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

        return ("Common [optional] options supported by TransformerAPITest:\n"
                + "(Note: assumes inputDir=.\\prod)\n"
                + "-processorClassname classname.of.processor  (to override setPlatformDefaultProcessor to Xalan 2.x)\n"
                + super.usage());
    }

    /**
     * Main method to run test from the command line - can be left alone.  
     *
     * NEEDSDOC @param args
     */
    public static void main(String[] args)
    {

        TransformerAPITest app = new TransformerAPITest();

        app.doMain(args);
    }
}
