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
 * ResultAPITest.java
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
import org.xml.sax.ContentHandler;

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
 * Basic API coverage test for the Result class of TRAX.
 * @author shane_curcuru@lotus.com
 */
public class ResultAPITest extends XSLProcessorTestBase
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
    public ResultAPITest()
    {

        numTestCases = 2;  // REPLACE_num
        testName = "ResultAPITest";
        testComment = "Basic API coverage test for the Result class of TRAX";
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
     * TRAX Result: cover APIs.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase1()
    {

        reporter.testCaseInit("TRAX Result: cover APIs");

        // Ctor(OutputStream)
        // set/getByteStream (OutputStream)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Result resultBytes = new Result(baos);

        reporter.checkObject(resultBytes.getByteStream(), baos,
                             "ctor()/getByteStream()");

        // Verify other items are not set
        if ((resultBytes.getCharacterStream() == null)
                && (resultBytes.getNode() == null))
        {
            reporter.checkPass("resultBytes should not have Writer/Node");
        }
        else
        {
            reporter.checkFail("resultBytes should not have Writer/Node");
        }

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

        resultBytes.setByteStream(baos2);
        reporter.checkObject(resultBytes.getByteStream(), baos2,
                             "set/getByteStream()");

        // Ctor(Writer)
        // set/getCharacterStream (Writer)
        StringWriter sw = new StringWriter();
        Result resultWriter = new Result(sw);

        reporter.checkObject(resultWriter.getCharacterStream(), sw,
                             "ctor()/getCharacterStream()");

        // Verify other items are not set
        if ((resultWriter.getByteStream() == null)
                && (resultWriter.getNode() == null))
        {
            reporter.checkPass(
                "resultWriter should not have ByteStream/Node");
        }
        else
        {
            reporter.checkFail(
                "resultWriter should not have ByteStream/Node");
        }

        StringWriter sw2 = new StringWriter();

        resultWriter.setCharacterStream(sw2);
        reporter.checkObject(resultWriter.getCharacterStream(), sw2,
                             "set/getCharacterStream()");

        // Ctor(Node)
        // set/getNode (Node)
        // What's the easiest way to just create a Node?
        try
        {
            DocumentBuilderFactory dfactory =
                DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            Node n = docBuilder.newDocument();
            Result resultNode = new Result(n);

            reporter.checkObject(resultNode.getNode(), n, "ctor()/getNode()");

            // Verify other items are not set
            if ((resultNode.getByteStream() == null)
                    && (resultNode.getCharacterStream() == null))
            {
                reporter.checkPass(
                    "resultNode should not have ByteStream/CharacterStream");
            }
            else
            {
                reporter.checkFail(
                    "resultNode should not have ByteStream/CharacterStream");
            }

            Node n2 = docBuilder.newDocument();

            resultNode.setNode(n2);
            reporter.checkObject(resultNode.getNode(), n2, "set/getNode()");
        }
        catch (ParserConfigurationException pce)
        {
            reporter.checkFail("Creating Node threw: " + pce.toString());
            reporter.logThrowable(reporter.ERRORMSG, pce,
                                  "Creating Node threw:");
        }

        reporter.testCaseClose();

        return true;
    }

    /**
     * TRAX Result: validate basic Result functionality.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase2()
    {

        reporter.testCaseInit(
            "TRAX Result: validate basic Results functionality");

        // Actually use each type of Results to get some output
        reporter.logInfoMsg(TRAX_PROCESSOR_XSLT + " property is: "
                            + System.getProperty(TRAX_PROCESSOR_XSLT));
        Processor.setPlatformDefaultProcessor(processorClassname);
        reporter.logTraceMsg(
            "Processor.setPlatformDefaultProcessor(processorClassname)");

        // Setup inputs and get a stylesheet for use by all tests
        InputSource xsl = new InputSource(simpleTest.inputName);
        InputSource xml = new InputSource(simpleTest.xmlName);
        Templates templates = null;
        Transformer transformer = null;

        try
        {
            Processor p = Processor.newInstance(XSLT);

            // Setup inputs and get a stylesheet for use by all tests
            templates = p.process(xsl);
        }
        catch (Exception e)
        {
            reporter.checkFail(
                "Problem creating Templates; cannot continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "Problem creating Templates");

            return true;
        }

        try
        {

            // Test Results(ByteStream)
            FileOutputStream fos = new FileOutputStream(outNames.nextName());
            Result resultBytes = new Result(fos);

            transformer = templates.newTransformer();

            transformer.transform(xml, resultBytes);

            // Ensure the stream is flushed and closed
            fos.close();
            fileChecker.check(reporter, new File(outNames.currentName()),
                              new File(simpleTest.goldName),
                              "Result(ByteStream) into "
                              + outNames.currentName());
        }
        catch (Exception e)
        {
            reporter.checkFail("Testing Results(ByteStream) threw: "
                               + e.toString());
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "Testing Results(ByteStream) threw:");
        }

        try
        {

            // Test Results(CharacterStream)
            FileWriter fw = new FileWriter(outNames.nextName());
            Result resultWriter = new Result(fw);

            transformer = templates.newTransformer();

            transformer.transform(xml, resultWriter);

            // Ensure the stream is flushed and closed
            fw.close();
            fileChecker.check(reporter, new File(outNames.currentName()),
                              new File(simpleTest.goldName),
                              "Result(CharacterStream) into "
                              + outNames.currentName());
        }
        catch (Exception e)
        {
            reporter.checkFail("Testing Results(CharacterStream) threw: "
                               + e.toString());
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "Testing Results(CharacterStream) threw:");
        }

        try
        {

            // Test Result(Node)
            DocumentBuilderFactory dfactory =
                DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            Document node = docBuilder.newDocument();
            Result resultNode = new Result(node);

            transformer = templates.newTransformer();

            transformer.transform(xml, resultNode);

            // Use the serializers to output the result to disk
            OutputFormat format = templates.getOutputFormat();
            Serializer serializer = SerializerFactory.getSerializer(format);

            serializer.setOutputStream(
                new FileOutputStream(outNames.nextName()));
            serializer.asDOMSerializer().serialize(node);
            fileChecker.check(reporter, new File(outNames.currentName()),
                              new File(simpleTest.goldName),
                              "Result(Node) into " + outNames.currentName());
        }
        catch (Exception e)
        {
            reporter.checkFail("Testing Results(Node) threw: "
                               + e.toString());
            reporter.logThrowable(reporter.ERRORMSG, e,
                                  "Testing Results(Node) threw:");
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

        return ("Common [optional] options supported by ResultAPITest:\n"
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

        ResultAPITest app = new ResultAPITest();

        app.doMain(args);
    }
}
