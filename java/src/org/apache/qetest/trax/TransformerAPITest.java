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

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.OutputKeys;  // Don't know why this needs explicit importing?!?!
import javax.xml.transform.stream.*;    // We assume Features.STREAM for some tests

// javax JAXP classes for parser pluggability
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

// java classes
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Basic API coverage test for the Transformer class of TRAX.
 * This test focuses on coverage testing for the API's, and 
 * very brief functional testing.  Also see tests in the 
 * trax\sax, trax\dom, and trax\stream directories for specific 
 * coverage of Transformer API's in those usage cases.
 * @author shane_curcuru@lotus.com
 */
public class TransformerAPITest extends XSLProcessorTestBase
{

    /** Cheap-o filename for various output files.  */
    protected OutputNameManager outNames;

    /** Cheap-o filename set for general API tests. */
    protected XSLTestfileInfo simpleTest = new XSLTestfileInfo();

    /** TransformerAPIParam.xsl used for set/getParameter related tests  */
    protected XSLTestfileInfo paramTest = new XSLTestfileInfo();

    /** Parameter names from TransformerAPIParam.xsl  */
    public static final String PARAM1S = "param1s";
    public static final String PARAM2S = "param2s";
    public static final String PARAM3S = "param3s";
    public static final String PARAM1N = "param1n";
    public static final String PARAM2N = "param2n";
    public static final String PARAM3N = "param3n";

    /** TransformerAPIOutputFormat.xsl used for set/getOutputFormat related tests  */
    protected XSLTestfileInfo outputFormatTest = new XSLTestfileInfo();

    /** Known outputFormat property name from outputFormatTest  */
    public static final String OUTPUT_FORMAT_NAME = "cdata-section-elements";

    /** Known outputFormat property value from outputFormatTest  */
    public static final String OUTPUT_FORMAT_VALUE = "cdataHere";

    /** Cache the relevant system property. */
    protected String saveXSLTProp = null;

    /** Allow user to override our default of Xalan 2.x processor classname. */
    public static final String XALAN_CLASSNAME =
        "org.apache.xalan.processor.TransformerFactoryImpl";

    /** 
     * Commandline/properties string to initialize a different 
     * TransformerFactory implementation - otherwise we default to 
     * Xalan 2.x org.apache.xalan.processor.TransformerFactoryImpl
     */
    protected String PROCESSOR_CLASSNAME = "processorClassname";

    /** NEEDSDOC Field processorClassname          */
    protected String processorClassname = XALAN_CLASSNAME;

    /** NEEDSDOC Field TRAX_PROCESSOR_XSLT          */
    public static final String TRAX_PROCESSOR_XSLT = "javax.xml.transform.TransformerFactory";

    /** Subdir name under test\tests\api for files.  */
    public static final String TRAX_SUBDIR = "trax";

    /** Default ctor initializes test name, comment, numTestCases. */
    public TransformerAPITest()
    {

        numTestCases = 3;  // REPLACE_num
        testName = "TransformerAPITest";
        testComment = "Basic API coverage test for the Transformer class";
    }

    /**
     * Initialize this test - Set names of xml/xsl test files, cache system property.  
     *
     * @param p Properties to initialize with (may be unused)
     * @return false if test should be aborted, true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {

        // Used for all tests; just dump files in trax subdir
        File outSubDir = new File(outputDir + File.separator + TRAX_SUBDIR);

        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: "
                                   + outSubDir);

        outNames = new OutputNameManager(outputDir + File.separator + TRAX_SUBDIR
                                         + File.separator + testName, ".out");

        // We assume inputDir=...tests\api, and use the trax subdir
        //  also assume inputDir, etc. exist already
        String testBasePath = inputDir + File.separator + TRAX_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir + File.separator + TRAX_SUBDIR
                              + File.separator;

        simpleTest.xmlName = testBasePath + "TransformerAPIParam.xml";
        simpleTest.inputName = testBasePath + "TransformerAPIParam.xsl";
        simpleTest.goldName = goldBasePath + "TransformerAPIParam.out";

        paramTest.xmlName = testBasePath + "TransformerAPIParam.xml";
        paramTest.inputName = testBasePath + "TransformerAPIParam.xsl";
        paramTest.goldName = goldBasePath + "TransformerAPIParam.out";
        
        outputFormatTest.xmlName = testBasePath + "TransformerAPIOutputFormat.xml";
        outputFormatTest.inputName = testBasePath + "TransformerAPIOutputFormat.xsl";
        outputFormatTest.goldName = goldBasePath + "TransformerAPIOutputFormat.out";

        // Cache trax system property
        saveXSLTProp = System.getProperty(TRAX_PROCESSOR_XSLT);

        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property is: "
                            + saveXSLTProp);

        // Check if user wants to use a processor other than Xalan 2.x
        processorClassname = testProps.getProperty(PROCESSOR_CLASSNAME,
                                                   XALAN_CLASSNAME);
        // @todo fix: user should be able to specify -processorClassname 
        //  on the command line to override the system properties

        reporter.logInfoMsg(PROCESSOR_CLASSNAME + " property is: "
                            + processorClassname);
        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property is: "
                            + System.getProperty(TRAX_PROCESSOR_XSLT));

        try
        {
            TransformerFactory tf = TransformerFactory.newInstance();
            if (!tf.getFeature(Features.STREAM))
            {   // The rest of this test relies on Streams only
                reporter.logWarningMsg("Features.STREAM not supported! Some tests may be invalid!");
            }
        }
        catch (Exception e)
        {
            reporter.checkFail(
                "Problem creating Templates; cannot continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "Problem creating Templates");
            return true;
        }

        return true;
    }

    /**
     * Cleanup this test - reset the cached system property trax.processor.xslt.  
     *
     * @param p Properties to initialize with (may be unused)
     * @return false if test should be aborted, true otherwise
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
     * TRAX Transformer: cover basic get/setParameter(s) APIs.
     * See {@link ParamTest ParamTest to be written} for more 
     * functional test coverage on setting different kinds 
     * and types of parameters, etc.
     * 
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase1()
    {

        reporter.testCaseInit(
            "TRAX Transformer: cover basic get/setParameter(s) APIs");

        TransformerFactory factory = null;
        Templates templates = null;
        Transformer transformer = null;
        Transformer identityTransformer = null;
        try
        {
            factory = TransformerFactory.newInstance();
            identityTransformer = factory.newTransformer();
            templates = factory.newTemplates(new StreamSource(paramTest.inputName));
        }
        catch (Exception e)
        {
            reporter.checkFail("Problem creating Templates; cannot continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, e, 
                                  "Problem creating Templates; cannot continue testcase");
            return true;
        }
        // Note: large number of try...catch blocks so that early 
        // exceptions won't blow out the whole testCase
        try
        {
            // See what the default 'identity' transform has by default
            Object tmp = identityTransformer.getParameter("This-param-does-not-exist");
            reporter.checkObject(tmp, null, "This-param-does-not-exist is null by default identityTransformer");
            // Can you set properties on this transformer?
            identityTransformer.setParameter("foo", "bar");
            tmp = identityTransformer.getParameter("foo");
            if (tmp == null)
            {
                reporter.checkAmbiguous("@todo set/getParameter on identity transform returns null, what should it do?");
            }
            else
            {
                reporter.checkString((String)tmp, "bar", "identityTransformer set/getParameter");
            }
        } 
        catch (Exception e)
        {
            reporter.checkFail("Problem with identity parameters");
            reporter.logThrowable(reporter.ERRORMSG, e, "Problem with identity parameters");
        }

        try
        {
            transformer = templates.newTransformer(); // may throw TransformerConfigurationException
            // Default Transformer should not have any parameters..
            Object tmp = transformer.getParameter("This-param-does-not-exist");
            reporter.checkObject(tmp, null, "This-param-does-not-exist is null by default");
            //  .. including params in the stylesheet
            tmp = transformer.getParameter(PARAM1S);
            if (tmp == null)
            {   // @todo should use checkObject instead of this if... construct
                reporter.checkPass(PARAM1S + " is null by default");
            }
            else
            {
                reporter.checkFail(PARAM1S + " is " + tmp + " by default");
            }

            // Verify simple set/get of a single parameter
            transformer.setParameter(PARAM1S, "new value1s");
            reporter.logTraceMsg("Just reset " + PARAM1S + " to new value1s");
            tmp = transformer.getParameter(PARAM1S);    // SPR SCUU4QWTVZ - returns an XString
            if (tmp == null)
            {
                reporter.checkFail(PARAM1S + " is still set to null!");
            }
            else
            {
                reporter.logTraceMsg(PARAM1S + " is now ?" + tmp + "?, isa " + tmp.getClass().getName());
                reporter.checkString((String)tmp, "new value1s", PARAM1S + " is now set to ?" + tmp + "?");
            }
        } 
        catch (Exception e)
        {
            reporter.checkFail("Problem set/getParameter testing");
            reporter.logThrowable(reporter.ERRORMSG, e, "Problem set/getParameter testing");
        }

        try
        {
            transformer = templates.newTransformer();
            // Verify simple set/get of a single parameter
            transformer.setParameter(PARAM1S, new Integer(1234));
            reporter.logTraceMsg("Just set " + PARAM1S + " to Integer(1234)");
            Object tmp = transformer.getParameter(PARAM1S);    // SPR SCUU4QWTVZ - returns an XObject
            // @todo need to type-check maybe?
            reporter.logTraceMsg(PARAM1S + " is now ?" + tmp + "?, isa " + tmp.getClass().getName());
            reporter.checkObject(tmp, new Integer(1234), PARAM1S + " is now set to ?" + tmp + "?");

            transformer.setParameter(PARAM3S, "new value3s");
            reporter.logTraceMsg("Just reset " + PARAM3S + " to new value3s");
            tmp = transformer.getParameter(PARAM3S);
            reporter.checkString((String)tmp, "new value3s", PARAM3S + " is now set to ?" + tmp + "?");

            Properties setParams = new Properties();
            setParams.put(PARAM1S, "another value1s");
            setParams.put(PARAM2S, "yet another value2s");

            // Verify setting Properties full of params works - feature removed from product 13-Nov-00
        } 
        catch (Exception e)
        {
            reporter.checkFail("Problem set/getParameters testing");
            reporter.logThrowable(reporter.ERRORMSG, e, "Problem set/getParameters testing");
        }

        try
        {
            transformer = templates.newTransformer();
            transformer.setParameter(PARAM1S, "test-param-1s");
            transformer.setParameter(PARAM1N, "test-param-1n");
            // Verify basic params actually affect transformation
            if (doTransform(templates.newTransformer(), 
                            new StreamSource(paramTest.xmlName), 
                            new StreamResult(new FileOutputStream(outNames.nextName()))))
            {
                // @todo should update goldFile!
                fileChecker.check(reporter, 
                                  new File(outNames.currentName()), 
                                  new File(paramTest.goldName), 
                                  "transform with param1s,param1n into: " + outNames.currentName());
            }
            reporter.checkAmbiguous("@todo verify if params are still set after transform");
        } 
        catch (Exception e)
        {
            reporter.checkFail("Problem with parameter transform");
            reporter.logThrowable(reporter.ERRORMSG, e, "Problem with parameter transform");
        }

        reporter.testCaseClose();

        return true;
    }


    /**
     * TRAX Transformer: cover basic get/setOutputFormat APIs.
     * See {@link OutputFormatTest} for more coverage on setting 
     * different kinds of outputs, etc.
     * 
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase2()
    {

        reporter.testCaseInit(
            "TRAX Transformer: cover basic get/setOutputFormat APIs");

        TransformerFactory factory = null;
        Templates templates = null;
        Transformer transformer = null;
        Transformer identityTransformer = null;
        try
        {
            factory = TransformerFactory.newInstance();
            identityTransformer = factory.newTransformer();
            templates = factory.newTemplates(new StreamSource(outputFormatTest.inputName));
        }
        catch (Exception e)
        {
            reporter.checkFail("Problem creating Templates; cannot continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, e, 
                                  "Problem creating Templates; cannot continue testcase");
            return true;
        }

        try
        {
            // See what the default 'identity' transform has by default
            Properties identityProps = identityTransformer.getOutputProperties();
            reporter.logHashtable(reporter.STATUSMSG, identityProps, 
                                  "default identityTransformer.getOutputProperties()");

            // Can you set properties on this transformer?
            identityTransformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "cdata-sections");
            String tmp = identityTransformer.getOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS);
            reporter.check(tmp, "cdata-sections", "identityTransformer set/getOutputProperty");
        } 
        catch (Exception e)
        {
            reporter.checkFail("Problem with identity output properties");
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "Problem with identity output properties");
        }

        try
        {
            // See what our templates parent has
            Properties tmpltProps = templates.getOutputProperties();
            reporter.logHashtable(reporter.STATUSMSG, tmpltProps, 
                                  "default templates.getOutputProperties()");

            // See what we have by default, from our testfile
            transformer = templates.newTransformer();
            Properties outProps = transformer.getOutputProperties();
            reporter.logHashtable(reporter.STATUSMSG, outProps, 
                                  "default transformer.getOutputProperties()");
            
            reporter.checkAmbiguous("@todo validate transformer, templates have same default output formats");

            // @todo validate some known output property from our testfile
            String tmp = transformer.getOutputProperty(OUTPUT_FORMAT_NAME);
            reporter.check(tmp, OUTPUT_FORMAT_VALUE, "transformer get known OutputProperty value ?" + tmp + "?");

            // Simple set/getOutputProperty
            tmp = transformer.getOutputProperty(OutputKeys.OMIT_XML_DECLARATION);
            reporter.logTraceMsg(OutputKeys.OMIT_XML_DECLARATION + " is currently: " + tmp);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            tmp = transformer.getOutputProperty(OutputKeys.OMIT_XML_DECLARATION);
            reporter.check(tmp, "yes", "transformer set/getOutputProperty value to ?" + tmp + "?");

            // setOutputProperties(Properties oformat)
            // Properties getOutputProperties()
            // setOutputProperty(String name, String value) throws TransformerException
            // String getOutputProperty(String name) throws TransformerException;
            // Only with StreamResult, obviously
            // OutputKeys.METHOD = xml|html|text|qname-but-not-ncname
            // OutputKeys.VERSION = number
            // OutputKeys.ENCODING = string
            // OutputKeys.OMIT_XML_DECLARATION = yes|no
            // OutputKeys.STANDALONE = yes|no
            // OutputKeys.DOCTYPE_PUBLIC = string
            // OutputKeys.DOCTYPE_SYSTEM = string
            // OutputKeys.CDATA_SECTION_ELEMENTS = qnames
            // OutputKeys.INDENT = qnames
            // OutputKeys.MEDIA_TYPE = qnames
            // OutputKeys.CDATA_SECTION_ELEMENTS = qnames

            reporter.checkAmbiguous("@todo Cover set/getOutputProperty/ies()");
        } 
        catch (Exception e)
        {
            reporter.checkFail("Problem with set/get output properties");
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "Problem with set/get output properties");
        }

        reporter.testCaseClose();

        return true;
    }

    /**
     * TRAX Transformer: cover transform() API and basic 
     * functionality; plus set/getURIResolver() API.
     *
     * Note: These are simply coverage tests for the 
     * transform() API - for more general testing, 
     * see TraxWrapper.java and use ConformanceTest or 
     * another suitable test driver.
     * 
     * @todo should the Features.SAX and Features.DOM tests be in 
     * this file, or should they be in sax/dom subdirectory tests?
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase3()
    {
        reporter.testCaseInit(
            "TRAX Transformer: cover transform() and set/getURIResolver API and functionality");
        TransformerFactory factory = null;
        try
        {
            // Grab a stylesheet to use 
            factory = TransformerFactory.newInstance();
            Templates templates = factory.newTemplates(new StreamSource(simpleTest.inputName));

            Transformer transformer = templates.newTransformer();
            // URIResolver should be null by default; try to set/get one
            reporter.checkObject(transformer.getURIResolver(), null, "getURIResolver is null by default");
            LoggingURIResolver myURIResolver = new LoggingURIResolver(reporter);
            transformer.setURIResolver(myURIResolver);
            reporter.checkObject(transformer.getURIResolver(), myURIResolver, "set/getURIResolver test");
            reporter.logTraceMsg("myURIres.getCounterString = " + myURIResolver.getCounterString());

            // Assumes we support Streams
            if (doTransform(transformer, 
                            new StreamSource(simpleTest.xmlName), 
                            new StreamResult(new FileOutputStream(outNames.nextName()))))
            {
                fileChecker.check(reporter, 
                                  new File(outNames.currentName()), 
                                  new File(simpleTest.goldName), 
                                  "transform(Stream, Stream) into: " + outNames.currentName());
            }
            reporter.logTraceMsg("myURIres.getCounterString = " + myURIResolver.getCounterString());

            reporter.checkAmbiguous("@todo basic URIResolver functionality test (i.e. does it get used in a transform)");
        }
        catch (Exception e)
        {
            reporter.checkFail("TestCase threw: " + e.toString());
            reporter.logThrowable(reporter.ERRORMSG, e, "TestCase threw:");
        }

        try
        {
            if (factory.getFeature(Features.DOM))
            {
                // @todo should they be in sax/dom subdirectory tests?
                reporter.checkAmbiguous("@todo transform(DOM, DOM)");
                reporter.checkAmbiguous("@todo transform(DOM, Stream)");
                reporter.checkAmbiguous("@todo transform(Stream, DOM)");
            }
            else
            {
                reporter.logWarningMsg("Skipping transform(DOM*) tests, "
                                       + Features.DOM
                                       + " not supported");
            }

            if (factory.getFeature(Features.SAX))
            {
                // @todo should they be in sax/dom subdirectory tests?
                reporter.checkAmbiguous("@todo transform(SAX, SAX)");
                reporter.checkAmbiguous("@todo transform(SAX, Stream)");
                reporter.checkAmbiguous("@todo transform(Stream, SAX)");
            }
            else
            {
                reporter.logWarningMsg("Skipping transform(SAX*) tests, "
                                       + Features.SAX
                                       + " not supported");
            }
            if (factory.getFeature(Features.SAX) && factory.getFeature(Features.DOM))
            {
                // @todo should they be in sax/dom subdirectory tests?
                reporter.checkAmbiguous("@todo transform(SAX, DOM)");
                reporter.checkAmbiguous("@todo transform(DOM, SAX)");
            }
            else
            {
                reporter.logWarningMsg("Skipping transform(SAX/DOM) tests, one of them not supported.");
            }
        }
        catch (Exception e)
        {
            reporter.checkFail("TestCase threw: " + e.toString());
            reporter.logThrowable(reporter.ERRORMSG, e, "TestCase threw:");
        }
        reporter.testCaseClose();

        return true;
    }


    /**
     * Worker method performs transforms (and catches exceptions, etc.)
     * Side effect: checkFail() if exception thrown
     *
     * @param Transformer to use
     * @param Source to pull in XML from
     * @param Result to put output in; may be modified
     * @return false if exception thrown, true otherwise
     */
    public boolean doTransform(Transformer t, Source s, Result r)
    {
        try
        {
            t.transform(s, r);
            return true;
            
        } 
        catch (TransformerException e)
        {
            reporter.checkFail("doTransform threw: " + e.toString());
            reporter.logThrowable(reporter.ERRORMSG, e, "doTransform threw:");
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
        return ("Common [optional] options supported by TransformerAPITest:\n"
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

        TransformerAPITest app = new TransformerAPITest();

        app.doMain(args);
    }
}
