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
 * SAXResultAPITest.java
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
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.w3c.dom.Node;

// Xalan-J 2.x serializers (sould be changed to use just 
//  identity transformer for this functionality)
import org.apache.xalan.serialize.SerializerFactory;
import org.apache.xalan.serialize.Serializer;

// java classes
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * API Coverage test for the SAXResult class of TRAX.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class SAXResultAPITest extends XSLProcessorTestBase
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
    public static final String NONSENSE_SYSTEMID = "file:///nonsense-system-id";

    /** Just initialize test name, comment, numTestCases. */
    public SAXResultAPITest()
    {
        numTestCases = 2;  // REPLACE_num
        testName = "SAXResultAPITest";
        testComment = "API Coverage test for the SAXResult class of TRAX";
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
        impInclFileInfo.goldName = testBasePath + "SAXImpIncl.out";
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
     * Basic API coverage, constructor and set/get methods.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Basic API coverage, constructor and set/get methods");

        // Default no-arg ctor sets nothing 
        SAXResult defaultSAX = new SAXResult();
        reporter.checkObject(defaultSAX.getHandler(), null, "Default SAXResult should have null Handler");
        reporter.checkObject(defaultSAX.getLexicalHandler(), null, "Default SAXResult should have null LexicalHandler");
        reporter.checkObject(defaultSAX.getSystemId(), null, "Default SAXResult should have null SystemId");

        try
        {
            TransformerFactory factory = null;
            SAXTransformerFactory saxFactory = null;
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;

            // ctor(Handler) with identity transformer, which is both
            //  a ContentHandler and LexicalHandler
            TransformerHandler tHandler = saxFactory.newTransformerHandler();  // identity transformer
            ContentHandler handler = (ContentHandler)tHandler;
            SAXResult handlerSAX = new SAXResult(handler);
            reporter.checkObject(handlerSAX.getHandler(), handler, "SAXResult(handler) has Handler: " + handlerSAX.getHandler());
            reporter.checkObject(handlerSAX.getLexicalHandler(), null, "SAXResult(handler) should have null LexicalHandler");
            reporter.checkObject(handlerSAX.getSystemId(), null, "SAXResult(handler) should have null SystemId");

            // ctor(Handler) with LoggingContentHandler, which not
            //  a LexicalHandler, so it can't be cast
            ContentHandler nonLexHandler = new LoggingContentHandler(reporter);
            SAXResult otherHandlerSAX = new SAXResult(nonLexHandler);
            reporter.checkObject(otherHandlerSAX.getHandler(), nonLexHandler, "SAXResult(non-lexhandler) has Handler: " + otherHandlerSAX.getHandler());
            reporter.checkObject(otherHandlerSAX.getLexicalHandler(), null, "SAXResult(non-lexhandler) should have null LexicalHandler when ContentHandler!=LexicalHandler");
            reporter.checkObject(otherHandlerSAX.getSystemId(), null, "SAXResult(non-lexhandler) should have null SystemId");

            // Note the Javadoc in SAXResult which talks about 
            //  automatically casting the ContentHandler into 
            //  a LexicalHandler: this cannot be tested alone 
            //  here, since it's the Transformer that does that 
            //  internally if necessary, and it may not get set 
            //  back into the SAXResult object itself

            // set/getHandler API coverage
            SAXResult wackySAX = new SAXResult();
            wackySAX.setHandler(handler); // isa LexicalHandler also
            reporter.checkObject(wackySAX.getHandler(), handler, "set/getHandler API coverage");
            reporter.checkObject(wackySAX.getLexicalHandler(), null, "getLexicalHandler after set/getHandler");

            // set/getLexicalHandler API coverage
            LexicalHandler lexHandler = new LoggingLexicalHandler(reporter);
            reporter.logTraceMsg("lexHandler is " + lexHandler);
            wackySAX.setLexicalHandler(lexHandler); // SCUU4SPPMV - does not work
            LexicalHandler gotLH = wackySAX.getLexicalHandler();
            if (gotLH == lexHandler)
            {
                reporter.checkPass("set/getLexicalHandler API coverage is: " + wackySAX.getLexicalHandler(), "SCUU4SPPMV");
            }
            else
            {
                reporter.checkFail("set/getLexicalHandler API coverage is: " + wackySAX.getLexicalHandler(), "SCUU4SPPMV");
            }
            reporter.checkObject(wackySAX.getHandler(), handler, "set/getLexicalHandler API coverage, does not affect ContentHandler");

            // set/getHandler API coverage, setting to null, which 
            //  should work here but can't be used legally
            wackySAX.setHandler(null);
            reporter.checkObject(wackySAX.getHandler(), null, "set/getHandler API coverage to null (possibly illegal)");
            gotLH = wackySAX.getLexicalHandler();
            if (gotLH == lexHandler)
            {
                reporter.checkPass("getLexicalHandler unaffected by setHandler(null), is: " + wackySAX.getLexicalHandler(), "SCUU4SPPMV");
            }
            else
            {
                reporter.checkFail("getLexicalHandler unaffected by setHandler(null), is: " + wackySAX.getLexicalHandler(), "SCUU4SPPMV");
            }
            wackySAX.setLexicalHandler(null);
            reporter.checkObject(wackySAX.getLexicalHandler(), null, "set/getHandler API coverage to null (possibly illegal)");

            // set/getSystemId API coverage
            wackySAX.setSystemId(NONSENSE_SYSTEMID);
            reporter.checkObject(wackySAX.getSystemId(), NONSENSE_SYSTEMID, "set/getSystemId API coverage");
            wackySAX.setSystemId(null);
            reporter.checkObject(wackySAX.getSystemId(), null, "set/getSystemId API coverage to null");
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with SAXResult set/get API");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with SAXResult set/get API");
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Basic functionality of SAXResults.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Basic functionality of SAXResults");
        // Provide local copies of URLized filenames, so that we can
        //  later run tests with either Strings or URLs
        String xslURI = filenameToURL(testFileInfo.inputName);
        String xmlURI = filenameToURL(testFileInfo.xmlName);
        String xslImpInclURI = filenameToURL(impInclFileInfo.inputName);
        String xmlImpInclURI = filenameToURL(impInclFileInfo.xmlName);

        TransformerFactory factory = null;
        SAXTransformerFactory saxFactory = null;
        Templates streamTemplates;
        try
        {
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;
            // Process a simple stylesheet for use later
            reporter.logTraceMsg("factory.newTemplates(new StreamSource(" + xslURI + "))");
            streamTemplates = factory.newTemplates(new StreamSource(xslURI));
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem creating factory; can't continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, t,"Problem creating factory; can't continue testcase");
            return true;
        }
        try
        {
            // Verify very simple use of just a SAXResult
            // Use simple Xalan serializer for disk output, setting 
            //  the stylesheet's output properties into it
            Properties outProps = streamTemplates.getOutputProperties();
            Serializer serializer = SerializerFactory.getSerializer(outProps);
            FileOutputStream fos = new FileOutputStream(outNames.nextName());
            reporter.logTraceMsg("serializer.setOutputStream(new FileOutputStream(" + outNames.currentName() + ")");
            serializer.setOutputStream(fos);
            SAXResult saxResult = new SAXResult(serializer.asContentHandler());
            
            // Just do a normal transform to this result
            Transformer transformer = streamTemplates.newTransformer();
            reporter.logTraceMsg("transform(new StreamSource(" + xmlURI + "), saxResult)");
            transformer.transform(new StreamSource(xmlURI), saxResult);
            fos.close(); // must close ostreams we own
            fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "simple transform into SAXResult into: " + outNames.currentName());

        }
        catch (Throwable t)
        {
            reporter.checkFail("Basic functionality of SAXResults threw: " + t.toString());
            reporter.logThrowable(reporter.ERRORMSG, t, "Basic functionality of SAXResults");
        }

        try
        {
            // Negative test: SAXResult without a handler should throw
            SAXResult saxResult = new SAXResult();
            
            // Just do a normal transform to this result
            Transformer transformer = streamTemplates.newTransformer();
            reporter.logTraceMsg("transform(..., nullsaxResult)");
            transformer.transform(new StreamSource(xmlURI), saxResult);
            reporter.checkFail("transform(..., nullsaxResult) should have thrown exception");
        }
        catch (IllegalArgumentException iae)
        {
            // This is the exception we expect, so pass (and don't 
            //  bother displaying the full logThrowable)
            reporter.checkPass("transform(..., nullsaxResult) properly threw: " + iae.toString());
        }
        catch (Throwable t)
        {
            reporter.checkFail("transform(..., nullsaxResult) unexpectedly threw: " + t.toString());
            reporter.logThrowable(reporter.ERRORMSG, t, "transform(..., nullsaxResult) threw");
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
        return ("Common [optional] options supported by SAXResultAPITest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        SAXResultAPITest app = new SAXResultAPITest();
        app.doMain(args);
    }
}
