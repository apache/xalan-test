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
 * TemplatesAPITest.java
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
import org.apache.serialize.Serializer;
import org.apache.serialize.SerializerFactory;

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

// java classes
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Basic API coverage test for the Templates and TemplatesBuilder classes of TRAX.
 * @author shane_curcuru@lotus.com
 */
public class TemplatesAPITest extends XSLProcessorTestBase
{

    /**
     * Cheap-o filename for various output files.
     *
     */
    protected OutputNameManager outNames;

    /** Cheap-o filename set for both API tests and exampleSimple. */
    protected XSLTestfileInfo simpleTest = new XSLTestfileInfo();

    /** Name of a stylesheet with xsl:output HTML. */
    protected String htmlStylesheet = null;

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

    // http://xml.org/sax/features/namespace-prefixes feature
    // http://xml.org/sax/features/namespaces feature
    // http://xml.org/sax/features/external-general-entities property
    // http://xml.org/sax/features/external-parameter-entities property
    // http://xml.apache.org/xslt/sourcebase property in TransformerImpl

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
    public TemplatesAPITest()
    {

        numTestCases = 2;  // REPLACE_num
        testName = "TemplatesAPITest";
        testComment =
            "Basic API coverage test for the Templates and TemplatesBuilder classes of TRAX";
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
        htmlStylesheet = inputDir + File.separator + XAPI + File.separator
                         + "TemplatesAPIHTML.xsl";

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
     * TRAX Templates: cover APIs and functionality.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase1()
    {

        reporter.testCaseInit("TRAX Templates: cover APIs and functionality");

        Processor p = null;

        try
        {
            p = Processor.newInstance(XSLT);
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

            // Cover APIs newTransformer(), getOutputFormat()
            Templates templates =
                p.process(new InputSource(simpleTest.inputName));
            Transformer transformer = templates.newTransformer();

            reporter.check((transformer != null), true,
                           "newTransformer() is non-null for "
                           + simpleTest.inputName);

            OutputFormat outputFormat = templates.getOutputFormat();

            reporter.check((outputFormat != null), true,
                           "getOutputFormat() is non-null for "
                           + simpleTest.inputName);
            reporter.check(outputFormat.getMethod(), Method.XML,
                           "outputFormat.getMethod() is xml for "
                           + simpleTest.inputName);
        }
        catch (Exception e)
        {
            reporter.checkErr("newTransformer/getOutputFormat threw: "
                              + e.toString());
            reporter.logThrowable(reporter.STATUSMSG, e,
                                  "newTransformer/getOutputFormat threw:");
        }

        try
        {
            Templates templatesHTML =
                p.process(new InputSource(htmlStylesheet));
            OutputFormat outputFormatHTML = templatesHTML.getOutputFormat();

            reporter.check(outputFormatHTML.getMethod(), Method.HTML,
                           "outputFormat() is html for " + htmlStylesheet);
        }
        catch (Exception e)
        {
            reporter.checkErr("outputFormat() is html... threw: "
                              + e.toString());
            reporter.logThrowable(reporter.STATUSMSG, e,
                                  "outputFormat() is html... threw:");
        }

        reporter.testCaseClose();

        return true;
    }

    /**
     * TRAX TemplatesBuilder: cover APIs and functionality.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase2()
    {

        reporter.testCaseInit(
            "TRAX TemplatesBuilder: cover APIs and functionality");

        try
        {
            Processor p = Processor.newInstance(XSLT);
            TemplatesBuilder templatesBuilder = p.getTemplatesBuilder();

            reporter.check((templatesBuilder != null), true,
                           "getTemplatesBuilder() is non-null");

            XMLReader reader = XMLReaderFactory.createXMLReader();

            reader.setContentHandler(templatesBuilder);

            if (templatesBuilder instanceof org.xml.sax.ext.LexicalHandler)
            {
                reader.setProperty(PROPERTY_LEXICAL_HANDLER,
                                   templatesBuilder);
            }

            reader.parse(new InputSource(htmlStylesheet));

            Templates templates = templatesBuilder.getTemplates();

            reporter.check((templates != null), true,
                           "templates from SAX build is non-null");

            // Cheap-o verification it's the right stylesheet
            OutputFormat outputFormatHTML = templates.getOutputFormat();

            reporter.check(outputFormatHTML.getMethod(), Method.HTML,
                           "outputFormat() is html for " + htmlStylesheet);
            reporter.checkAmbiguous(
                "//TODO create stylesheet both ways (t=process() and via SAX build) and compare outputs");
            reporter.checkAmbiguous(
                "//TODO setBaseID() and then create different stylesheet");
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

        return ("Common [optional] options supported by TemplatesAPITest:\n"
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

        TemplatesAPITest app = new TemplatesAPITest();

        app.doMain(args);
    }
}
