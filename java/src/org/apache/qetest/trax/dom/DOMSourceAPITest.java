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
 * DOMSourceAPITest.java
 *
 */
package org.apache.qetest.trax.dom;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// Needed SAX, DOM, JAXP classes
import org.xml.sax.InputSource;
import org.w3c.dom.Node;

// java classes
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * API Coverage test for the DOMSource class of TRAX.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class DOMSourceAPITest extends XSLProcessorTestBase
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
    public static final String TRAX_DOM_SUBDIR = "trax" + File.separator + "dom";


    /** Just initialize test name, comment, numTestCases. */
    public DOMSourceAPITest()
    {
        numTestCases = 2;  // REPLACE_num
        testName = "DOMSourceAPITest";
        testComment = "API Coverage test for the DOMSource class of TRAX";
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
        File outSubDir = new File(outputDir + File.separator + TRAX_DOM_SUBDIR);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + TRAX_DOM_SUBDIR
                                         + File.separator + testName, ".out");

        String testBasePath = inputDir 
                              + File.separator 
                              + TRAX_DOM_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator 
                              + TRAX_DOM_SUBDIR
                              + File.separator;

        testFileInfo.inputName = testBasePath + "DOMTest.xsl";
        testFileInfo.xmlName = testBasePath + "DOMTest.xml";
        testFileInfo.goldName = goldBasePath + "DOMTest.out";
        try
        {
            TransformerFactory tf = TransformerFactory.newInstance();
            if (!(tf.getFeature(DOMSource.FEATURE)
                  && tf.getFeature(DOMResult.FEATURE)))
            {   // The rest of this test relies on DOM
                reporter.logErrorMsg("DOM*.FEATURE not supported! Some tests may be invalid!");
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
     * Basic API coverage, constructor and set/get methods.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Basic API coverage, constructor and set/get methods");

        // Default no-arg ctor sets nothing (but needs special test for 
        //  creating new doc when being transformed)
        DOMSource defaultDOM = new DOMSource();
        reporter.checkObject(defaultDOM.getNode(), null, "Default DOMSource should have null Node");
        reporter.check(defaultDOM.getSystemId(), null, "Default DOMSource should have null SystemId");

        try
        {
            // ctor(Node) with a simple node
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            Node n = docBuilder.newDocument();
            DOMSource nodeDOM = new DOMSource(n);
            reporter.checkObject(nodeDOM.getNode(), n, "DOMSource(n) has Node: " + nodeDOM.getNode());
            reporter.check(nodeDOM.getSystemId(), null, "DOMSource(n) should have null SystemId");

            DOMSource nodeDOMid = new DOMSource(n, "this-is-system-id");
            reporter.checkObject(nodeDOMid.getNode(), n, "DOMSource(n,id) has Node: " + nodeDOMid.getNode());
            reporter.check(nodeDOMid.getSystemId(), "this-is-system-id", "DOMSource(n,id) has SystemId: " + nodeDOMid.getSystemId());

            DOMSource wackyDOM = new DOMSource();
            Node n2 = docBuilder.newDocument();
            wackyDOM.setNode(n2);
            reporter.checkObject(wackyDOM.getNode(), n2, "set/getNode API coverage");
            
            wackyDOM.setSystemId("another-system-id");
            reporter.checkObject(wackyDOM.getSystemId(), "another-system-id", "set/getSystemId API coverage");
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with DOMSource set/get API");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with DOMSource set/get API");
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Basic functionality of DOMSources.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Basic functionality of DOMSources");

        TransformerFactory factory = null;
        Templates templates = null;
        Transformer transformerXSL = null;
        DocumentBuilder docBuilder = null;
        Node xmlNode = null;
        Node xslNode = null;
        try
        {
            // Startup a factory, create some nodes/DOMs
            factory = TransformerFactory.newInstance();
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setNamespaceAware(true);
            docBuilder = dfactory.newDocumentBuilder();
            reporter.logTraceMsg("parsing xml, xsl files");
            xslNode = docBuilder.parse(new InputSource(testFileInfo.inputName));
            xmlNode = docBuilder.parse(new InputSource(testFileInfo.xmlName));
        }
        catch (Throwable t)
        {
            reporter.checkErr("Problem with factory; testcase may not work");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with factory; testcase may not work");
        }
        try
        {
            // A blank DOM as an input stylesheet - what should happen?
            DOMSource blankXSLDOM = new DOMSource();
            reporter.logTraceMsg("About to newTemplates(blankXSLDOM)");
            Templates blankTemplates = factory.newTemplates(blankXSLDOM); // SPR SCUU4R5JYZ throws npe
            reporter.check((blankTemplates != null), true, "factory.newTemplates(blankXSLDOM) is non-null");
            reporter.checkObject(blankXSLDOM.getNode(), null, "blankXSLDOM is still empty");
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with blankXSLDOM(1)");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with blankXSLDOM(1)");
        }
        try
        {
            // A blank DOM as an input stylesheet - what should happen?
            DOMSource blankXSLDOM = new DOMSource();
            reporter.logTraceMsg("About to newTransformer(blankXSLDOM)");
            Transformer blankTransformer = factory.newTransformer(blankXSLDOM); // SPR SCUU4R5JYZ throws npe
            reporter.check((blankTransformer != null), true, "factory.newTransformer(blankXSLDOM) is non-null");
            reporter.checkObject(blankXSLDOM.getNode(), null, "blankXSLDOM is still empty");
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with blankXSLDOM(2)");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with blankXSLDOM(2)");
        }

        try
        {
            // Try to get templates, transformerXSL from node
            DOMSource xslDOM = new DOMSource(xslNode);
            templates = factory.newTemplates(xslDOM);
            reporter.check((templates != null), true, "factory.newTemplates(DOMSource) is non-null");
            transformerXSL = factory.newTransformer(xslDOM);
            reporter.check((transformerXSL != null), true, "factory.newTransformer(DOMSource) is non-null");
            
            // A simple DOM-DOM-DOM transform
            DOMSource xmlDOM = new DOMSource(xmlNode);
            Node outNode = docBuilder.newDocument();
            DOMResult outDOM = new DOMResult(outNode);
            transformerXSL.transform(xmlDOM, outDOM);
            Node gotNode = outDOM.getNode();
            reporter.check((gotNode != null), true, "transform(xmlDOM, outDOM) has non-null outNode");
            serializeDOMAndCheck(gotNode, testFileInfo.goldName, "transform(xmlDOM, outDOM)");
            reporter.logTraceMsg("@todo validate the dom in memory as well");

            // A blank DOM as source doc of the transform - should 
            //  auto-create a source Document
            DOMSource blankSource = new DOMSource();
            Node emptyNode = docBuilder.newDocument();
            DOMResult emptyNodeDOM = new DOMResult(emptyNode);
            reporter.logTraceMsg("About to transform(blankSource, emptyNodeDOM)");
            transformerXSL.transform(blankSource, emptyNodeDOM); // SPR SCUU4R5KLL throws TransformerException
            Node tmpNode = blankSource.getNode();
            reporter.check((tmpNode != null), true, "transform(blankSource, emptyNodeDOM) has non-null node");
            serializeDOMAndCheck(gotNode, testFileInfo.goldName, "transform(blankSource, emptyNodeDOM) HACK: needs new gold file");
            reporter.checkAmbiguous("validate contents of emptyNodeDOM");            
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with tests(1)");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with tests(1)");
        }
        try
        {
            // A blank DOM as an output of the transform - should 
            //  auto-create a source Document
            DOMSource xmlDOM = new DOMSource(xmlNode);
            DOMResult emptyResult = new DOMResult();
            reporter.logTraceMsg("About to transform(xmlDOM, emptyResult)");
            transformerXSL.transform(xmlDOM, emptyResult);
            Node outNode = emptyResult.getNode();
            reporter.check((outNode != null), true, "transform(xmlDOM, emptyResult) has non-null node");
            serializeDOMAndCheck(outNode, testFileInfo.goldName, "transform(xmlDOM, emptyResult)");
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with tests(2)");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with tests(2)");
        }

        reporter.checkAmbiguous("@todo setSystemId functionality");

        reporter.testCaseClose();
        return true;
    }


    /**
     * Worker method to serialize DOM and fileChecker.check().  
     * @return true if pass, false otherwise
     */
    public boolean serializeDOMAndCheck(Node dom, String goldFileName, String comment)
    {
        if ((dom == null) || (goldFileName == null))
        {
            reporter.logWarningMsg("serializeDOMAndCheck of null dom or goldFileName!");
            return false;
        }
        try
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            if (factory.getFeature(StreamResult.FEATURE))
            {
                // Use identity transformer to serialize
                Transformer identityTransformer = factory.newTransformer();
                StreamResult streamResult = new StreamResult(new FileOutputStream(outNames.nextName()));
                DOMSource nodeSource = new DOMSource(dom);
                reporter.logTraceMsg("serializeDOMAndCheck() into " + outNames.currentName());
                identityTransformer.transform(nodeSource, streamResult);
                fileChecker.check(reporter, 
                                  new File(outNames.currentName()), 
                                  new File(goldFileName), 
                                  comment + " into " + outNames.currentName());
                return true;    // Note: should check return from fileChecker.check!
            }
            else
            {   // We should try another method to serialize the data
                reporter.logWarningMsg("getFeature(StreamResult.FEATURE), can't validate serialized data");
                return false;
            }
            
        }
        catch (Throwable t)
        {
            reporter.checkFail("serializeDOMAndCheckFile threw: " + t.toString());
            reporter.logThrowable(reporter.ERRORMSG, t, "serializeDOMAndCheckFile threw:");
            return false;
        }
    }
    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by DOMSourceAPITest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + "REPLACE_any_new_test_arguments\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {

        DOMSourceAPITest app = new DOMSourceAPITest();

        app.doMain(args);
    }
}
