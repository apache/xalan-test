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
 * DOMResultAPITest.java
 *
 */
package org.apache.qetest.trax.dom;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.qetest.FileBasedTest;
import org.apache.qetest.OutputNameManager;
import org.apache.qetest.QetestUtils;
import org.apache.qetest.xsl.XSLTestfileInfo;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

//-------------------------------------------------------------------------

/**
 * API Coverage test for the DOMResult class of TRAX.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class DOMResultAPITest extends FileBasedTest
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

    /** 
     * Information about an xsl/xml file pair for transforming with import/include.  
     */
    protected XSLTestfileInfo impInclFileInfo = new XSLTestfileInfo();

    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String TRAX_DOM_SUBDIR = "trax" + File.separator + "dom";


    /** Just initialize test name, comment, numTestCases. */
    public DOMResultAPITest()
    {
        numTestCases = 2;  // REPLACE_num
        testName = "DOMResultAPITest";
        testComment = "API Coverage test for the DOMResult class of TRAX";
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

        testFileInfo.inputName = QetestUtils.filenameToURL(testBasePath + "DOMTest.xsl");
        testFileInfo.xmlName = QetestUtils.filenameToURL(testBasePath + "DOMTest.xml");
        testFileInfo.goldName = goldBasePath + "DOMTest.out";

        impInclFileInfo.inputName = QetestUtils.filenameToURL(testBasePath + "DOMImpIncl.xsl");
        impInclFileInfo.xmlName = QetestUtils.filenameToURL(testBasePath + "DOMImpIncl.xml");
        impInclFileInfo.goldName = testBasePath + "DOMImpIncl.out";
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
        DOMResult defaultDOM = new DOMResult();
        reporter.checkObject(defaultDOM.getNode(), null, "Default DOMResult should have null Node");
        reporter.check(defaultDOM.getSystemId(), null, "Default DOMResult should have null SystemId");

        try
        {
            // ctor(Node) with a simple node
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            Node n = docBuilder.newDocument();
            DOMResult nodeDOM = new DOMResult(n);
            reporter.checkObject(nodeDOM.getNode(), n, "DOMResult(n) has Node: " + nodeDOM.getNode());
            reporter.check(nodeDOM.getSystemId(), null, "DOMResult(n) should have null SystemId");

            DOMResult nodeDOMid = new DOMResult(n, "this-is-system-id");
            reporter.checkObject(nodeDOMid.getNode(), n, "DOMResult(n,id) has Node: " + nodeDOMid.getNode());
            reporter.check(nodeDOMid.getSystemId(), "this-is-system-id", "DOMResult(n,id) has SystemId: " + nodeDOMid.getSystemId());

            DOMResult wackyDOM = new DOMResult();
            Node n2 = docBuilder.newDocument();
            wackyDOM.setNode(n2);
            reporter.checkObject(wackyDOM.getNode(), n2, "set/getNode API coverage");
            
            wackyDOM.setSystemId("another-system-id");
            reporter.checkObject(wackyDOM.getSystemId(), "another-system-id", "set/getSystemId API coverage");
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with DOMResult set/get API");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with DOMResult set/get API");
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Basic functionality of DOMResults.
     * Test 'blank' Result; reuse Results; swap Nodes; etc.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Basic functionality of DOMResults");

        DocumentBuilder docBuilder = null;
        TransformerFactory factory = null;
        Templates templates = null;
        Transformer transformer = null;
        Node xmlNode = null;
        Node xslNode = null;
        Node xslImpInclNode = null;
        Node xmlImpInclNode = null;
        try
        {
            factory = TransformerFactory.newInstance();
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setNamespaceAware(true);
            docBuilder = dfactory.newDocumentBuilder();
            reporter.logTraceMsg("parsing xml, xsl files");
            xslNode = docBuilder.parse(new InputSource(testFileInfo.inputName));
            xmlNode = docBuilder.parse(new InputSource(testFileInfo.xmlName));
            xslImpInclNode = docBuilder.parse(new InputSource(impInclFileInfo.inputName));
            xmlImpInclNode = docBuilder.parse(new InputSource(impInclFileInfo.xmlName));
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem creating factory; can't continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, t,
                                  "Problem creating factory; can't continue testcase");
            reporter.testCaseClose();
            return true;
        }
        try
        {
            // Try to get templates, transformer from node
            DOMSource xslSource = new DOMSource(xslNode);
            templates = factory.newTemplates(xslSource);
            DOMSource xmlSource = new DOMSource(xmlNode);
            
            // Transforming into a DOMResult with a node is already 
            //  well covered in DOMSourceAPITest and elsewhere
            // Verify a 'blank' Result object gets filled up properly
            DOMResult blankResult = new DOMResult();
            transformer = templates.newTransformer();
            transformer.transform(xmlSource, blankResult);
            reporter.logTraceMsg("blankResult is now: " + blankResult);
            Node blankNode = blankResult.getNode();
            if (blankNode != null)
            {
                serializeDOMAndCheck(blankNode, testFileInfo.goldName, "transform into blank DOMResult");
            }
            else
            {
                reporter.checkFail("transform into 'blank' DOMResult");
            }
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with blank results");
            reporter.logThrowable(reporter.ERRORMSG, t,
                                  "Problem with blank results");
        }
        boolean reusePass = false;
        try
        {
            DOMSource xmlSource = new DOMSource(xmlNode);
            DOMSource xslSource = new DOMSource(xslNode);
            templates = factory.newTemplates(xslSource);
            
            // Reuse the same result for multiple transforms
            DOMResult reuseResult = new DOMResult(docBuilder.newDocument());
            transformer = templates.newTransformer();
            transformer.transform(xmlSource, reuseResult);
            Node reuseNode = reuseResult.getNode();
            serializeDOMAndCheck(reuseNode, testFileInfo.goldName, "transform into reuseable1 DOMResult");
            
            // Get a new transformer just to avoid extra complexity
            reporter.logTraceMsg("About to re-use DOMResult from previous transform, should throw");
            transformer = templates.newTransformer();
            reusePass = true; // Next line should throw an exception
            transformer.transform(xmlSource, reuseResult); // SPR SCUU4RJKG4 throws DOM006
            reporter.checkFail("Re-using DOMResult should have thrown exception", "SCUU4RJKG4");
        }
        catch (Throwable t)
        {
            reporter.check(reusePass, true, "Re-using DOMResult throws exception properly");
            reporter.logThrowable(reporter.ERRORMSG, t,
                                  "Re-using DOMResult throws exception properly");
            reporter.logTraceMsg("@todo Should validate specific kind of error above");
        }
        try
        {
            DOMSource xmlSource = new DOMSource(xmlNode);
            DOMSource xslSource = new DOMSource(xslNode);
            templates = factory.newTemplates(xslSource);
            
            // Reuse the same result for multiple transforms, after resetting node
            DOMResult reuseResult = new DOMResult(docBuilder.newDocument());
            transformer = templates.newTransformer();
            transformer.transform(xmlSource, reuseResult);
            Node reuseNode = reuseResult.getNode();
            serializeDOMAndCheck(reuseNode, testFileInfo.goldName, "transform into reuseable2 DOMResult");
            
            // Get a new transformer just to avoid extra complexity
            reporter.logTraceMsg("About to re-use DOMResult from previous transform after setNode()");
            transformer = templates.newTransformer();
            reuseResult.setNode(docBuilder.newDocument());
            transformer.transform(xmlSource, reuseResult);
            reuseNode = reuseResult.getNode();
            serializeDOMAndCheck(reuseNode, testFileInfo.goldName, "transform into reused2 DOMResult");

            // Reuse again, with the same transformer
            reuseResult.setNode(docBuilder.newDocument());
            transformer.transform(xmlSource, reuseResult);
            reuseNode = reuseResult.getNode();
            serializeDOMAndCheck(reuseNode, testFileInfo.goldName, "transform into reused2 DOMResult again");
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with re-using results(2)");
            reporter.logThrowable(reporter.ERRORMSG, t,
                                  "Problem with re-using results(2)");
        }

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
                FileOutputStream fos = new FileOutputStream(outNames.nextName());
                StreamResult streamResult = new StreamResult(fos);
                DOMSource nodeSource = new DOMSource(dom);
                reporter.logTraceMsg("serializeDOMAndCheck() into " + outNames.currentName());
                identityTransformer.transform(nodeSource, streamResult);
                fos.close(); // must close ostreams we own
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
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by DOMResultAPITest:\n"
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

        DOMResultAPITest app = new DOMResultAPITest();

        app.doMain(args);
    }
}
