/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights 
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
 * TemplatesHandlerAPITest.java
 *
 */
package org.apache.qetest.trax.sax;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

// Needed SAX, DOM, JAXP classes
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.w3c.dom.Node;

// java classes
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * API Coverage test for the TemplatesHandler class of TRAX.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class TemplatesHandlerAPITest extends FileBasedTest
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
    public TemplatesHandlerAPITest()
    {
        numTestCases = 2;  // REPLACE_num
        testName = "TemplatesHandlerAPITest";
        testComment = "API Coverage test for the TemplatesHandler class of TRAX";
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
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;

            // Basic construction
            TemplatesHandler tHandler = saxFactory.newTemplatesHandler();
            reporter.check((tHandler != null), true, "newTemplatesHandler() returns non-null");

            // getTemplates API coverage - simple
            Templates templates = tHandler.getTemplates();
            reporter.checkObject(templates, null, "getTemplates() is null on new TemplatesHandler");

            // set/getSystemId API coverage
            tHandler.setSystemId(NONSENSE_SYSTEMID);
            reporter.checkObject(tHandler.getSystemId(), NONSENSE_SYSTEMID, "set/getSystemId API coverage");
            tHandler.setSystemId(null);
            reporter.checkObject(tHandler.getSystemId(), null, "set/getSystemId API coverage to null");

        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with TemplatesHandler set/get API");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with TemplatesHandler set/get API");
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Basic functionality of TemplatesHandler.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Basic functionality of TemplatesHandler");
        // Provide local copies of URLized filenames, so that we can
        //  later run tests with either Strings or URLs
        String xslURI = QetestUtils.filenameToURL(testFileInfo.inputName);
        String xmlURI = QetestUtils.filenameToURL(testFileInfo.xmlName);
        String xslImpInclURI = QetestUtils.filenameToURL(impInclFileInfo.inputName);
        String xmlImpInclURI = QetestUtils.filenameToURL(impInclFileInfo.xmlName);

        TransformerFactory factory = null;
        SAXTransformerFactory saxFactory = null;
        TemplatesHandler templatesHandler = null;
        Templates templates = null;
        Transformer transformer = null;
        try
        {
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;

            // Validate a templatesHandler can create a valid stylesheet
            templatesHandler = saxFactory.newTemplatesHandler();

            XMLReader reader = getJAXPXMLReader();
            reader.setContentHandler(templatesHandler);

            // Parse the stylesheet, which means we should be able to getTemplates()
            reader.parse(xslURI);

            //Get the Templates object from the ContentHandler
            templates = templatesHandler.getTemplates();
            reporter.check((templates != null), true, "getTemplates() returns non-null with valid stylesheet");
            Properties xslOutProps = templates.getOutputProperties();
            reporter.check((xslOutProps != null), true, "getTemplates().getOutputProperties() returns non-null with valid stylesheet");
            //@todo validate a specific output property
            transformer = templates.newTransformer();
            reporter.check((transformer != null), true, "getTemplates().newTransformer() returns non-null with valid stylesheet");

            // Validate that this transformer actually works
            FileOutputStream fos = new FileOutputStream(outNames.nextName());
            Result result = new StreamResult(fos);
            Source xmlSource = new StreamSource(xmlURI);
            transformer.transform(xmlSource, result);
            fos.close(); // must close ostreams we own
            if (Logger.PASS_RESULT
                != fileChecker.check(reporter, 
                        new File(outNames.currentName()), 
                        new File(testFileInfo.goldName), 
                        "SAX-built simple transform into: " + outNames.currentName())
                )
                 reporter.logInfoMsg("SAX-built simple transform failure reason:" + fileChecker.getExtendedInfo());
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem TemplatesHandler(1)");
            reporter.logThrowable(reporter.ERRORMSG, t,"Problem TemplatesHandler(1)");
        }
        try
        {
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;

            // Validate a templatesHandler can create a stylesheet 
            //  with imports/includes, with the default systemId
            templatesHandler = saxFactory.newTemplatesHandler();

            XMLReader reader = getJAXPXMLReader();
            reader.setContentHandler(templatesHandler);

            // Parse the stylesheet, which means we should be able to getTemplates()
            reader.parse(xslImpInclURI);

            //Get the Templates object from the ContentHandler
            templates = templatesHandler.getTemplates();
            reporter.check((templates != null), true, "getTemplates() returns non-null with impincl stylesheet");
            Properties xslOutProps = templates.getOutputProperties();
            reporter.check((xslOutProps != null), true, "getTemplates().getOutputProperties() returns non-null with impincl stylesheet");
            //@todo validate a specific output property
            transformer = templates.newTransformer();
            reporter.check((transformer != null), true, "getTemplates().newTransformer() returns non-null with impincl stylesheet");

            // Validate that this transformer actually works
            FileOutputStream fos = new FileOutputStream(outNames.nextName());
            Result result = new StreamResult(fos);
            Source xmlSource = new StreamSource(xmlImpInclURI);
            transformer.transform(xmlSource, result);
            fos.close(); // must close ostreams we own
            if (Logger.PASS_RESULT
                != fileChecker.check(reporter, 
                        new File(outNames.currentName()), 
                        new File(impInclFileInfo.goldName), 
                        "SAX-built impincl transform into: " + outNames.currentName())
                )
                 reporter.logInfoMsg("SAX-built impincl transform failure reason:" + fileChecker.getExtendedInfo());
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem TemplatesHandler(2)");
            reporter.logThrowable(reporter.ERRORMSG, t,"Problem TemplatesHandler(2)");
        }
        try
        {
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;

            // Validate a templatesHandler with an incorrect 
            //  systemId reports an error nicely
            templatesHandler = saxFactory.newTemplatesHandler();

            // Set the base systemId for the handler to a bogus one
            templatesHandler.setSystemId(NONSENSE_SYSTEMID);

            XMLReader reader = getJAXPXMLReader();
            reader.setContentHandler(templatesHandler);

            // Parse the stylesheet, which should throw some 
            //  exception, since the imports/includes won't be 
            //  found at the bogus systemId
            reader.parse(xslImpInclURI);

            // This line will only get run if above didn't throw an exception
            reporter.checkFail("No exception when expected: parsing stylesheet with bad systemId");

        }
        catch (Throwable t)
        {
            String msg = t.toString();
            if (msg != null)
            {
                // Note: 'impincl/SimpleImport.xsl' comes from the stylesheet itself,
                //  so to reduce dependencies, only validate that portion of the msg
                reporter.check((msg.indexOf("impincl/SimpleImport.xsl") > 0), true, 
                               "Expected Exception has proper message for bad systemId");
            }
            else
            {
                reporter.checkFail("Expected Exception has proper message for bad systemId");
            }
            reporter.logThrowable(reporter.STATUSMSG, t,"(potentially) Expected Exception");
        }
        finally
        {
            // Validate the templatesHandler still has systemId set
            reporter.check(templatesHandler.getSystemId(), NONSENSE_SYSTEMID, 
                           "templatesHandler still has systemId set");
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Worker method to get an XMLReader.
     *
     * Not the most efficient of methods, but makes the code simpler.
     *
     * @return a new XMLReader for use, with setNamespaceAware(true)
     */
    protected XMLReader getJAXPXMLReader()
            throws Exception
    {
        // Be sure to use the JAXP methods only!
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser saxParser = factory.newSAXParser();
        return saxParser.getXMLReader();
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by TemplatesHandlerAPITest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        TemplatesHandlerAPITest app = new TemplatesHandlerAPITest();
        app.doMain(args);
    }
}
