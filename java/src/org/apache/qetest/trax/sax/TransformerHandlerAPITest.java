/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
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
 * TransformerHandlerAPITest.java
 *
 */
package org.apache.qetest.trax.sax;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

// Needed SAX, DOM, JAXP classes
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.w3c.dom.Node;

// java classes
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * API Coverage test for the TransformerHandler class of TRAX.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class TransformerHandlerAPITest extends XSLProcessorTestBase
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
    public static final String TRAX_SAX_SUBDIR = "trax" + File.separator + "sax";

    /** Nonsense systemId for various tests.  */
    public static final String NONSENSE_SYSTEMID = "file:///nonsense/system/id/";

    /** Just initialize test name, comment, numTestCases. */
    public TransformerHandlerAPITest()
    {
        numTestCases = 2;  // REPLACE_num
        testName = "TransformerHandlerAPITest";
        testComment = "API Coverage test for the TransformerHandler class of TRAX";
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
        File outSubDir = new File(outputDir + File.separator + TRAX_SAX_SUBDIR);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + TRAX_SAX_SUBDIR
                                         + File.separator + testName, ".out");

        String testBasePath = inputDir 
                              + File.separator 
                              + TRAX_SAX_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator 
                              + TRAX_SAX_SUBDIR
                              + File.separator;

        // Note these are initialized as strings denoting filenames,
        //  and *not* as URL/URI's
        testFileInfo.inputName = testBasePath + "SAXTest.xsl";
        testFileInfo.xmlName = testBasePath + "SAXTest.xml";
        testFileInfo.goldName = goldBasePath + "SAXTest.out";

        impInclFileInfo.inputName = testBasePath + "SAXImpIncl.xsl";
        impInclFileInfo.xmlName = testBasePath + "SAXImpIncl.xml";
        impInclFileInfo.goldName = goldBasePath + "SAXImpIncl.out";
        try
        {
            TransformerFactory tf = TransformerFactory.newInstance();
            if (!(tf.getFeature(SAXSource.FEATURE)
                  && tf.getFeature(SAXResult.FEATURE)))
            {   // The rest of this test relies on SAX
                reporter.logErrorMsg("SAX*.FEATURE not supported! Some tests may be invalid!");
            }
        }
        catch (Throwable t)
        {
            reporter.checkFail(
                "Problem creating factory; Some tests may be invalid!");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem creating factory; Some tests may be invalid!");
        }

        return true;
    }


    /**
     * Basic API coverage of set/get methods.
     * Note that most of the functionality of this class goes 
     * far beyond what we test in this testCase.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Basic API coverage of set/get methods");

        // No public constructor available: you must always ask 
        //  a SAXTransformerFactory to give you one
        TransformerFactory factory = null;
        SAXTransformerFactory saxFactory = null;

        try
        {
            // Validate API's for an identity transformer
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;

            // Basic construction of identity transformer
            TransformerHandler tHandler = saxFactory.newTransformerHandler();
            reporter.check((tHandler != null), true, "newTransformerHandler() returns non-null");

            // getTemplates API coverage - simple
            Transformer transformer = tHandler.getTransformer();
            reporter.check((transformer != null), true, "getTransformer() is non-null on new identity TransformerHandler");

            // set/getSystemId API coverage
            tHandler.setSystemId(NONSENSE_SYSTEMID);
            reporter.checkObject(tHandler.getSystemId(), NONSENSE_SYSTEMID, "identityTransformer.set/getSystemId API coverage");
            tHandler.setSystemId(null);
            reporter.checkObject(tHandler.getSystemId(), null, "identityTransformer.set/getSystemId API coverage to null");

            // setResult API coverage
            Result unusedResult = new StreamResult(outNames.currentName()); // currentName is probably _0
            tHandler.setResult(unusedResult);
            reporter.checkPass("Crash test: identityTransformer.setResult appears to have worked");
            reporter.logStatusMsg("Note that we can't verify setResult since there's no getResult!");
            try
            {
                tHandler.setResult(null);
                reporter.checkFail("identityTransformer.setResult(null) did not throw an exception");            
            }
            catch (IllegalArgumentException iae)
            {
                reporter.checkPass("identityTransformer.setResult(null) properly threw: " + iae.toString());
            }
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with TransformerHandler set/get API");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with TransformerHandler set/get API");
        }

        try
        {
            // Validate API's for a 'real' transformer, which is different code
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;

            // Basic construction of identity transformer
            TransformerHandler tHandler = saxFactory.newTransformerHandler(new StreamSource(filenameToURL(testFileInfo.inputName)));
            reporter.check((tHandler != null), true, "newTransformerHandler(.." + filenameToURL(testFileInfo.inputName) + ")) returns non-null");

            // getTemplates API coverage - simple
            Transformer transformer = tHandler.getTransformer();
            reporter.check((transformer != null), true, "realTransformer.getTransformer() is non-null");

            // set/getSystemId API coverage
            tHandler.setSystemId(NONSENSE_SYSTEMID);
            reporter.checkObject(tHandler.getSystemId(), NONSENSE_SYSTEMID, "realTransformer.set/getSystemId API coverage");
            tHandler.setSystemId(null);
            reporter.checkObject(tHandler.getSystemId(), null, "realTransformer.set/getSystemId API coverage to null");

            // setResult API coverage
            Result unusedResult = new StreamResult(outNames.nextName()); // use nextName() instead of currentName()
            reporter.logInfoMsg("new StreamResult(" + outNames.currentName() + ")");
            tHandler.setResult(unusedResult);
            reporter.checkPass("Crash test: realTransformer.setResult appears to have worked");
            reporter.logStatusMsg("Note that we can't verify setResult since there's no getResult!");
            try
            {
                tHandler.setResult(null);
                reporter.checkFail("realTransformer.setResult(null) did not throw an exception");            
            }
            catch (IllegalArgumentException iae)
            {
                reporter.checkPass("realTransformer.setResult(null) properly threw: " + iae.toString());
            }
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with TransformerHandler set/get API");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with TransformerHandler set/get API");
        }
        reporter.testCaseClose();
        return true;
    }


    /**
     * Basic functionality of TransformerHandler.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Basic functionality of TransformerHandler");
        // Provide local copies of URLized filenames, so that we can
        //  later run tests with either Strings or URLs
        String xslURI = filenameToURL(testFileInfo.inputName);
        String xmlURI = filenameToURL(testFileInfo.xmlName);
        String xslImpInclURI = filenameToURL(impInclFileInfo.inputName);
        String xmlImpInclURI = filenameToURL(impInclFileInfo.xmlName);

        TransformerFactory factory = null;
        SAXTransformerFactory saxFactory = null;
        TransformerHandler transformerHandler = null;
        Transformer transformer = null;
        try
        {
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;

            // Validate an identity transformerHandler is valid 
            //  and performs as an identity stylesheet
            transformerHandler = saxFactory.newTransformerHandler();
            transformer = transformerHandler.getTransformer();
            reporter.check((transformer != null), true, "identity newTransformerHandler is non-null");
            transformer.transform(new StreamSource(xmlURI), new StreamResult(outNames.nextName()));
            int res = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.xmlName), 
                              "identity newTransformerHandler transform into: " + outNames.currentName());
            if (res == reporter.FAIL_RESULT)
                reporter.logInfoMsg("identity newTransformerHandler transform failure reason:" + fileChecker.getExtendedInfo());

        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem TransformerHandler(1)");
            reporter.logThrowable(reporter.ERRORMSG, t,"Problem TransformerHandler(1)");
        }

        try
        {
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;

            // Validate newTransformerHandler(Source) works
            Source xslSource = new StreamSource(xslURI);
            transformerHandler = saxFactory.newTransformerHandler(xslSource);
            transformer = transformerHandler.getTransformer();
            reporter.check((transformer != null), true, "newTransformerHandler(Source) is non-null");
            transformer.transform(new StreamSource(xmlURI), new StreamResult(outNames.nextName()));
            int res = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "newTransformerHandler(Source) transform into: " + outNames.currentName());
            if (res == reporter.FAIL_RESULT)
                reporter.logInfoMsg("newTransformerHandler(Source) transform failure reason:" + fileChecker.getExtendedInfo());

        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem TransformerHandler(2)");
            reporter.logThrowable(reporter.ERRORMSG, t,"Problem TransformerHandler(2)");
        }

        try
        {
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;

            // Validate newTransformerHandler(Templates) works
            Source xslSource = new StreamSource(xslURI);
            Templates otherTemplates = factory.newTemplates(xslSource);
            transformerHandler = saxFactory.newTransformerHandler(otherTemplates);
            transformer = transformerHandler.getTransformer();
            reporter.check((transformer != null), true, "newTransformerHandler(Templates) is non-null");
            transformer.transform(new StreamSource(xmlURI), new StreamResult(outNames.nextName()));
            int res = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "newTransformerHandler(Templates) transform into: " + outNames.currentName());
            if (res == reporter.FAIL_RESULT)
                reporter.logInfoMsg("newTransformerHandler(Templates) transform failure reason:" + fileChecker.getExtendedInfo());

        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem TransformerHandler(3)");
            reporter.logThrowable(reporter.ERRORMSG, t,"Problem TransformerHandler(3)");
        }
        reporter.logTraceMsg("//@todo validate newTransformerHandler.setResult functionality");
        reporter.logTraceMsg("//@todo validate newTransformerHandler.set/getSystemId functionality");

        reporter.testCaseClose();
        return true;
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by TransformerHandlerAPITest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        TransformerHandlerAPITest app = new TransformerHandlerAPITest();
        app.doMain(args);
    }
}
