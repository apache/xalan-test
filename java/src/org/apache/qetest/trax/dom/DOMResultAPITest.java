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
 * DOMResultAPITest.java
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
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * API Coverage test for the DOMResult class of TRAX.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class DOMResultAPITest extends XSLProcessorTestBase
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
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Basic functionality of DOMResults");

        TransformerFactory factory = null;
        Templates templates = null;
        Transformer transformer = null;
        Node xmlNode = null;
        Node xslNode = null;
        try
        {
            factory = TransformerFactory.newInstance();
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            reporter.logTraceMsg("parsing xml, xsl files");
            xslNode = docBuilder.parse(new InputSource(testFileInfo.inputName));
            xmlNode = docBuilder.parse(new InputSource(testFileInfo.xmlName));
            
            // Try to get templates, transformer from node
            DOMSource xslDOM = new DOMSource(xslNode);
            templates = factory.newTemplates(xslDOM);
            DOMSource xmlDOM = new DOMSource(xmlNode);
            
            DOMResult blankDOM = new DOMResult();
            transformer = templates.newTransformer();
            transformer.transform(xmlDOM, blankDOM);
            reporter.logTraceMsg("blankDOM is now: " + blankDOM);
            reporter.checkAmbiguous("More tests to be added!");            
            
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem creating factory; can't continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, t,
                                  "Problem creating factory; can't continue testcase");
            return true;
        }

        reporter.testCaseClose();
        return true;
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
