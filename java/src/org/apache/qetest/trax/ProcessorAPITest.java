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
 * ProcessorAPITest.java
 *
 */
package org.apache.qetest.trax;

import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Just import the whole trax package; note the packaging is likely to change
import org.apache.trax.*;

// Use Serializer classes from Xalan distro
import org.apache.serialize.OutputFormat;
import org.apache.serialize.Serializer;
import org.apache.serialize.SerializerFactory;

// Needed SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Parser;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;

// Needed DOM classes
import org.w3c.dom.Node;

// javax JAXP classes for parser pluggability
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// java classes
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.IOException;

import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Basic API coverage test for the Processor class of TRAX.
 * Currently assumes you're using Xalan 2.x.
 * @author shane_curcuru@lotus.com
 */
public class ProcessorAPITest extends XSLProcessorTestBase
{

    /**
     * Cheap-o filename for various output files.
     *
     */
    protected OutputNameManager outNames;

    /** Cheap-o filename set for both API tests and exampleSimple. */
    protected XSLTestfileInfo simpleTest = new XSLTestfileInfo();

    /** Cache the relevant system property. */
    protected String saveXSLTProp = null;

    /** NEEDSDOC Field TRAX_PROCESSOR          */
    public static final String TRAX_PROCESSOR = "trax.processor";

    /** NEEDSDOC Field XSLT          */
    public static final String XSLT = "xslt";

    /** NEEDSDOC Field TRAX_PROCESSOR_XSLT          */
    public static final String TRAX_PROCESSOR_XSLT = TRAX_PROCESSOR + "."
                                                         + XSLT;

    /** Allow user to override our default of Xalan 2.x processor classname. */
    public static final String XALAN_CLASSNAME =
        "org.apache.xalan.processor.StylesheetProcessor";

    /** NEEDSDOC Field PROCESSOR_CLASSNAME          */
    protected String PROCESSOR_CLASSNAME = "processorClassname";

    /** NEEDSDOC Field processorClassname          */
    protected String processorClassname = XALAN_CLASSNAME;

    /** NEEDSDOC Field INVALID_NAME          */
    public static final String INVALID_NAME = "invalid.name";

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
    public ProcessorAPITest()
    {

        numTestCases = 4;  // REPLACE_num
        testName = "ProcessorAPITest";
        testComment =
            "Basic API coverage test for the Processor class of TRAX";
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

        simpleTest.xmlName = testBasePath + "ProcessorAPITest.xml";
        simpleTest.inputName = testBasePath + "ProcessorAPITest.xsl";
        simpleTest.goldName = goldBasePath + "ProcessorAPITest.out";

        // Cache trax system property
        saveXSLTProp = System.getProperty(TRAX_PROCESSOR_XSLT);

        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property is: "
                            + saveXSLTProp);

        // Check if user wants to use a processor other than Xalan 2.x
        processorClassname = testProps.getProperty(PROCESSOR_CLASSNAME,
                                                   XALAN_CLASSNAME);

        reporter.logInfoMsg(PROCESSOR_CLASSNAME + " property is: "
                            + processorClassname);

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
     * TRAX Processor: cover static methods (creating processors).
     * Simple coverage tests; most should work with any trax processor <b>but</b>
     * we explicitly set the trax.processor.xslt property to the Xalan 2.x class.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase1()
    {

        reporter.testCaseInit(
            "TRAX Processor: cover static methods (creating processors)");

        // To be thorough, we should call Processor.getPlatformDefaultProcessor() and log it's value
        //  But: it's not in the trax spec yet (Sep-00)
        // Some negative test cases: bad processor types
        boolean bit = true;

        try
        {
            Object o = Processor.newInstance(INVALID_NAME);
        }
        catch (ProcessorFactoryException pfe)
        {
            reporter.checkPass("newInstance(" + INVALID_NAME
                               + ")1 properly threw: " + pfe.toString());

            bit = false;
        }

        if (bit)
            reporter.checkFail("newInstance(" + INVALID_NAME
                               + ")1 did not throw exception");

        System.getProperties().put(TRAX_PROCESSOR + "." + INVALID_NAME,
                                   "this.class.does.not.exist");

        bit = true;

        try
        {
            Object o = Processor.newInstance(INVALID_NAME);
        }
        catch (ProcessorFactoryException pfe)
        {
            reporter.checkPass("newInstance(" + INVALID_NAME
                               + ")2 properly threw: " + pfe.toString());

            bit = false;
        }

        if (bit)
            reporter.checkFail("newInstance(" + INVALID_NAME
                               + ")2 did not throw exception");

        // Negative test case: default is not valid, property is not valid
        System.getProperties().put(TRAX_PROCESSOR_XSLT,
                                   "this.class.does.not.exist");
        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property set to: "
                            + System.getProperty(TRAX_PROCESSOR_XSLT));
        Processor.setPlatformDefaultProcessor(INVALID_NAME);
        reporter.logTraceMsg(
            "Processor.setPlatformDefaultProcessor(INVALID_NAME)");

        bit = true;

        try
        {
            Object o = Processor.newInstance(XSLT);
        }
        catch (ProcessorFactoryException pfe)
        {
            reporter.checkPass(
                "newInstance(xslt)3 of invalid default and prop properly threw: "
                + pfe.toString());

            bit = false;
        }

        if (bit)
            reporter.checkFail(
                "newInstance(xslt)3 of invalid default and prop did not throw exception");

        // Negative test case: default is valid, property is not valid
        Processor.setPlatformDefaultProcessor(processorClassname);
        reporter.logTraceMsg(
            "Processor.setPlatformDefaultProcessor(processorClassname)");

        bit = true;

        try
        {
            Object o = Processor.newInstance(XSLT);
        }
        catch (ProcessorFactoryException pfe)
        {
            reporter.checkPass(
                "newInstance(xslt)4 of valid default and invalid prop properly threw: "
                + pfe.toString());

            bit = false;
        }

        if (bit)
            reporter.checkFail(
                "newInstance(xslt)4 of valid default and invalid prop did not throw exception");

        // Positive test cases: property is valid Xalan 2.x, default is not
        Processor p = null;

        System.getProperties().put(TRAX_PROCESSOR_XSLT, processorClassname);
        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property set to: "
                            + System.getProperty(TRAX_PROCESSOR_XSLT));
        Processor.setPlatformDefaultProcessor(INVALID_NAME);
        reporter.logTraceMsg(
            "Processor.setPlatformDefaultProcessor(INVALID_NAME)");

        try
        {
            p = Processor.newInstance(XSLT);

            reporter.checkBool(
                (p != null), true,
                "newInstance(xslt)5 of invalid default but prop:Xalan 2.x");
        }
        catch (ProcessorFactoryException pfe)
        {
            reporter.checkFail(
                "newInstance(xslt)5 of invalid default but prop:Xalan 2.x threw: "
                + pfe.toString());
        }

        p = null;

        // Positive test case: property is null, default is valid
        System.getProperties().remove(TRAX_PROCESSOR_XSLT);
        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property is: "
                            + System.getProperty(TRAX_PROCESSOR_XSLT));
        Processor.setPlatformDefaultProcessor(processorClassname);
        reporter.logTraceMsg(
            "Processor.setPlatformDefaultProcessor(processorClassname)");

        try
        {
            p = Processor.newInstance(XSLT);

            reporter.checkBool(
                (p != null), true,
                "newInstance(xslt)6 of default:Xalan 2.x but prop:null");
        }
        catch (ProcessorFactoryException pfe)
        {
            reporter.checkFail(
                "newInstance(xslt)6 of default:Xalan 2.x but prop:null threw: "
                + pfe.toString());
        }

        // Reset the property to what it was before the testCase started
        if (saveXSLTProp == null)
        {
            System.getProperties().remove(TRAX_PROCESSOR_XSLT);
        }
        else
        {
            System.getProperties().put(TRAX_PROCESSOR_XSLT, saveXSLTProp);
        }

        reporter.testCaseClose();

        return true;
    }

    /**
     * TRAX Processor: cover set/get instance methods.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase2()
    {

        reporter.testCaseInit(
            "TRAX Processor: cover set/get instance methods");
        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property is: "
                            + System.getProperty(TRAX_PROCESSOR_XSLT));
        Processor.setPlatformDefaultProcessor(processorClassname);
        reporter.logTraceMsg(
            "Processor.setPlatformDefaultProcessor(processorClassname)");

        Processor p = null;

        try
        {
            p = Processor.newInstance(XSLT);

            if (p == null)
            {
                reporter.checkFail(
                    "Processor is null; cannot continue testcase");

                return false;
            }
        }
        catch (ProcessorFactoryException pfe)
        {  // No-op
        }

        // Testing SAX get/set features and properties - both negative and positive tests
        boolean bit = true;

        try
        {
            p.setFeature(INVALID_NAME, true);
        }
        catch (Exception e)
        {
            reporter.checkPass("setFeature(" + INVALID_NAME
                               + ", true) properly threw: " + e.toString());

            bit = false;
        }

        if (bit)
            reporter.checkFail("setFeature(" + INVALID_NAME
                               + ", true) did not throw exception");

        bit = true;

        try
        {
            bit = p.getFeature(INVALID_NAME);
        }
        catch (Exception e)
        {
            reporter.checkPass("getFeature(" + INVALID_NAME
                               + ") properly threw: " + e.toString());

            bit = false;
        }

        if (bit)
            reporter.checkFail("getFeature(" + INVALID_NAME
                               + ") did not throw exception");

        bit = false;

        try
        {

            // We presume this is supported, since we use it later on
            bit = p.getFeature(FEATURE_DOM_INPUT);

            reporter.check(bit, true,
                           "getFeature(" + FEATURE_DOM_INPUT
                           + ") must be true for Xalan 2.x");
        }
        catch (Exception e)
        {
            reporter.checkFail("getFeature(" + FEATURE_DOM_INPUT
                               + ") threw: " + e.toString());
        }

        bit = false;

        try
        {

            // We presume this is supported, since we use it later on
            bit = p.getFeature(FEATURE_SAX_INPUT);

            reporter.check(bit, true,
                           "getFeature(" + FEATURE_SAX_INPUT
                           + ") must be true for Xalan 2.x");
        }
        catch (Exception e)
        {
            reporter.checkFail("getFeature(" + FEATURE_SAX_INPUT
                               + ") threw: " + e.toString());
        }

        // TODO investigate why setting this feature false throws SAXNotRecognizedException
        //      Maybe we can't set the native features?
        // p.setFeature(FEATURE_SAX_INPUT, false);
        // Test errorHandler set/get api and functionality
        LoggingSAXErrorHandler errHandler =
            new LoggingSAXErrorHandler(reporter);

        errHandler.setThrowWhen(errHandler.THROW_ON_FATAL);
        p.setErrorHandler(errHandler);
        reporter.checkObject(p.getErrorHandler(), errHandler,
                             "set/getErrorHandler API test");

        // See if we can get the errHandler to have something output
        try
        {
            Templates t = p.process(new InputSource(""));
        }
        catch (Exception e)
        {
            reporter.logStatusMsg("process(blank string) threw: "
                                  + e.toString());
        }

        reporter.logStatusMsg("process(blank string) errorHandler :"
                              + errHandler.getLastError() + ": "
                              + errHandler.getCounterString());

        try
        {
            Templates t = p.process(new InputSource(INVALID_NAME));
        }
        catch (Exception e)
        {
            reporter.logStatusMsg("process(" + INVALID_NAME + ") threw: "
                                  + e.toString());
        }

        reporter.logStatusMsg("process(" + INVALID_NAME + ") errorHandler :"
                              + errHandler.getLastError() + ": "
                              + errHandler.getCounterString());

        try
        {
            Templates t = p.process(new InputSource(simpleTest.inputName));

            // TODO: cause an error parsing the XML or something
        }
        catch (Exception e)
        {
            reporter.logStatusMsg("process(" + simpleTest.inputName
                                  + ") threw: " + e.toString());
        }

        reporter.logStatusMsg("process(" + simpleTest.inputName
                              + ") errorHandler :"
                              + errHandler.getLastError() + ": "
                              + errHandler.getCounterString());

        // Cheap set/get test for EntityResolver
        LoggingEntityResolver entRes = new LoggingEntityResolver(reporter);

        p.setEntityResolver(entRes);
        reporter.checkObject(p.getEntityResolver(), entRes,
                             "set/getEntityResolver API test");
        reporter.checkAmbiguous(
            "// TODO add validation for set/getEntityResolver");

        LoggingURIResolver URIRes = new LoggingURIResolver(reporter);

        p.setURIResolver(URIRes);
        reporter.checkObject(p.getURIResolver(), URIRes,
                             "set/getURIResolver API test");
        reporter.checkAmbiguous(
            "// TODO add validation for set/getURIResolver");

        try
        {
            XMLReader reader = XMLReaderFactory.createXMLReader();

            p.setXMLReader(reader);
            reporter.checkObject(p.getXMLReader(), reader,
                                 "set/getXMLReader API test");
            reporter.checkAmbiguous(
                "// TODO add validation for set/getXMLReader");
        }
        catch (SAXException se)
        {
            reporter.checkErr("set/getXMLReader API test: " + se.toString());
            reporter.logThrowable(reporter.STATUSMSG, se,
                                  "set/getXMLReader API test threw:");
        }

        reporter.testCaseClose();

        return true;
    }

    /**
     * TRAX Processor: cover process* instance methods.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase3()
    {

        reporter.testCaseInit(
            "TRAX Processor: cover process* instance methods");
        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property is: "
                            + System.getProperty(TRAX_PROCESSOR_XSLT));
        Processor.setPlatformDefaultProcessor(processorClassname);
        reporter.logTraceMsg(
            "Processor.setPlatformDefaultProcessor(processorClassname)");

        Processor p = null;

        try
        {
            p = Processor.newInstance(XSLT);

            if (p == null)
            {
                reporter.checkFail(
                    "Processor is null; cannot continue testcase");

                return false;
            }
        }
        catch (ProcessorFactoryException pfe)
        {  // No-op
        }

        // Processor.process() basic coverage test
        String messageStylesheet = inputDir + File.separator + XAPI
                                   + File.separator
                                   + "ProcessorAPIMessage.xsl";
        LoggingSAXErrorHandler errHandler;

        errHandler = new LoggingSAXErrorHandler(reporter);

        errHandler.setThrowWhen(errHandler.THROW_ON_FATAL);

        try
        {
            p.setErrorHandler(errHandler);

            Templates t = p.process(new InputSource(messageStylesheet));

            reporter.check((t != null), true,
                           "Created basic Templates object");

            Transformer f = t.newTransformer();

            reporter.check((f != null), true,
                           "Created basic Transformer object");
            f.transform(new InputSource(simpleTest.xmlName),
                        new Result(new FileWriter(outNames.nextName())));
            reporter.check(((new File(outNames.currentName())).exists()),
                           true, "transform() created an output file");
            reporter.logStatusMsg("messageStylesheet just created output:"
                                  + outNames.currentName());
        }
        catch (Exception e)
        {
            reporter.checkErr("process() basic test threw: " + e.toString());
            reporter.logThrowable(reporter.STATUSMSG, e,
                                  "process() basic test threw:");
        }

        reporter.logStatusMsg("process() basic test errorHandler :"
                              + errHandler.getLastError() + ": "
                              + errHandler.getCounterString());

        // Templates = Processor.processFromNode(Node) basic coverage test
        reporter.checkAmbiguous(
            "// TODO processFromNode(Node) copy from TraxMinitest");

        // Templates = Processor.processFromNode(Node, String) basic coverage test
        // TODO create stylesheet where String systemID is important 
        //      (does this need an import or include, or does other basic stuff matter?)
        reporter.checkAmbiguous(
            "// TODO processFromNode(Node, String) test needs stylesheet written");

        // Templates = Processor.processMultiple(InputSource[]) basic coverage test
        // InputSource[] = Processor.getAssociatedStylesheets(InputSource, media, title, charset) basic coverage test
        reporter.checkAmbiguous(
            "// TODO getAssociatedStylesheets test needs stylesheet written");

        // TemplatesBuilder = Processor.getTemplatesBuilder() basic coverage test
        reporter.checkAmbiguous("// TODO getTemplatesBuilder() ");
        reporter.testCaseClose();

        return true;
    }

    /**
     * Basic negative tests: various illegal stylesheets, etc..
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase4()
    {

        reporter.testCaseInit(
            "Basic negative tests: various illegal stylesheets, etc.");

        // Set the default, but leave the system property alone
        //  In theory, you could manually set the system property to some other 
        //  TRAX-conformant processor, and we'd test that instead of Xalan 2.x
        Processor.setPlatformDefaultProcessor(processorClassname);
        reporter.logTraceMsg(
            "Processor.setPlatformDefaultProcessor(processorClassname)");
        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property is: "
                            + System.getProperty(TRAX_PROCESSOR_XSLT));

        // Some invalid stylesheets to test for parsing/processing errors
        // use simpleTest.xmlName as an XML file without a xsl:stylesheet element
        Processor p = null;
        LoggingSAXErrorHandler errHandler;

        errHandler = new LoggingSAXErrorHandler(reporter);

        errHandler.setThrowWhen(errHandler.THROW_ON_FATAL);

        try
        {
            p = Processor.newInstance(XSLT);

            p.setErrorHandler(errHandler);

            Templates t = p.process(new InputSource(simpleTest.xmlName));
        }
        catch (Exception e)
        {
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "process of an xmlFile threw:");
        }

        String basePath = outputDir + File.separator + XAPI + File.separator;
        String illegalStylesheets[][] =
        {
            { basePath + "badXMLData.xsl", "This is not your parents XML!" },
            { basePath + "badClosingTag.xsl",
              "<?xml version=\"1.0\"?>\n<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">" },
            { basePath + "badNamespace.xsl",
              "<?xml version=\"1.0\"?>\n<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/blah/blah/blah\" version=\"1.0\">\n</xsl:stylesheet>" },

            /**
             * // For some reason, this still produces output
             *
             * {
             * basePath + "badVersion.xsl",
             * "<?xml version=\"1.0\"?>\n<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"abc\">\n</xsl:stylesheet>"
             * },
             */
            { basePath + "badTag.xsl",
              "<?xml version=\"1.0\"?>\n<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">\n<xsl:bad-tag/></xsl:stylesheet>" },
            { basePath + "badEntity.xsl",
              "<?xml version=\"1.0\"?>\n<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999XSL/Transform\" version=\"1.0\">\n&not-an-entity;</xsl:stylesheet>" }
        };

        // Cheap-o validation; should be improved to validate each case separately
        int numErrs = 0;

        for (int i = 0; i < illegalStylesheets.length; i++)
        {
            if (!writeFile(illegalStylesheets[i][0],
                           illegalStylesheets[i][1]))
                continue;

            errHandler = new LoggingSAXErrorHandler(reporter);

            errHandler.setThrowWhen(errHandler.THROW_ON_FATAL);

            String tmp = "(0)";  // Marker for when exception is thrown

            reporter.logTraceMsg("about to process: "
                                 + illegalStylesheets[i][0]);

            try
            {
                p = Processor.newInstance(XSLT);

                // Should we actually expect anything to get passed to the errHandler?
                p.setErrorHandler(errHandler);

                tmp = "(1)";

                Templates t =
                    p.process(new InputSource(illegalStylesheets[i][0]));

                tmp = "(2)";

                Transformer f = t.newTransformer();

                tmp = "(3)";

                f.transform(new InputSource(simpleTest.xmlName),
                            new Result(new FileWriter(outNames.nextName())));
                reporter.checkFail(illegalStylesheets[i][0]
                                   + " should not have created output:"
                                   + outNames.currentName());
            }
            catch (Exception e)
            {
                reporter.logThrowable(reporter.ERRORMSG, e,
                                      "process" + tmp + " of "
                                      + illegalStylesheets[i][0] + " threw:");

                numErrs++;
            }

            reporter.logStatusMsg("process(" + tmp + " of "
                                  + illegalStylesheets[i][0] + "):"
                                  + errHandler.getLastError() + ": "
                                  + errHandler.getCounterString());
        }  // end of for...

        reporter.check((numErrs > 0), true,
                       "Some bad stylesheets correctly threw exceptions");
        reporter.testCaseClose();

        return true;
    }

    /**
     * Cheap-o utility to write a test file; no validation performed.
     *
     * NEEDSDOC @param fileName
     * NEEDSDOC @param contents
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean writeFile(String fileName, String contents)
    {

        reporter.logTraceMsg("writeFile(" + fileName + ",...)");

        try
        {
            FileWriter fw = new FileWriter(fileName);

            fw.write(contents);
            fw.close();

            return true;
        }
        catch (IOException ioe)
        {
            reporter.logThrowable(reporter.ERRORMSG, ioe, "writeFile threw:");

            return false;
        }
    }

    /**
     * Convenience method to print out usage information - update if needed.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String usage()
    {

        return ("Common [optional] options supported by ProcessorAPITest:\n"
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

        ProcessorAPITest app = new ProcessorAPITest();

        app.doMain(args);
    }
}
