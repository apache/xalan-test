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
 * SAXSourceAPITest.java
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
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.w3c.dom.Node;

// java classes
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * API Coverage test for the SAXSource class of TRAX.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class SAXSourceAPITest extends FileBasedTest
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
    public SAXSourceAPITest()
    {
        numTestCases = 2;  // REPLACE_num
        testName = "SAXSourceAPITest";
        testComment = "API Coverage test for the SAXSource class of TRAX";
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
            reporter.checkErr("Problem creating factory; Some tests may be invalid!");
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
        SAXSource defaultSAX = new SAXSource();
        reporter.checkObject(defaultSAX.getInputSource(), null, "Default SAXSource should have null InputSource");
        reporter.checkObject(defaultSAX.getXMLReader(), null, "Default SAXSource should have null XMLReader");
        reporter.check(defaultSAX.getSystemId(), null, "Default SAXSource should have null SystemId");

        try
        {
            // ctor(InputSource) with an InputSource()
            InputSource srcNoID = new InputSource();
            SAXSource saxSrcNoID = new SAXSource(srcNoID);
            reporter.checkObject(saxSrcNoID.getInputSource(), srcNoID, "SAXSource(new InputSource()) has InputSource: " + saxSrcNoID.getInputSource());
            reporter.checkObject(saxSrcNoID.getXMLReader(), null, "SAXSource(new InputSource()) should have null XMLReader");
            reporter.check(saxSrcNoID.getSystemId(), null, "SAXSource(new InputSource()) should have null SystemId");

            // ctor(InputSource) with an InputSource("sysId")
            InputSource srcWithID = new InputSource(NONSENSE_SYSTEMID);
            SAXSource saxSrcWithID = new SAXSource(srcWithID);
            reporter.checkObject(saxSrcWithID.getInputSource(), srcWithID, "SAXSource(new InputSource(sysId)) has InputSource: " + saxSrcWithID.getInputSource());
            reporter.checkObject(saxSrcWithID.getXMLReader(), null, "SAXSource(new InputSource(sysId)) should have null XMLReader");
            reporter.check(saxSrcWithID.getSystemId(), NONSENSE_SYSTEMID, "SAXSource(new InputSource(sysId)) has SystemId: " + saxSrcWithID.getSystemId());

            // ctor(XMLReader, InputSource) 
            reporter.logTraceMsg("API coverage of ctor(XMLReader, InputSource)...");
            reporter.logTraceMsg("JAXP way:reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader()");
            XMLReader reader2 = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            SAXSource saxSrcReaderID2 = new SAXSource(reader2, srcWithID);
            reporter.checkObject(saxSrcReaderID2.getInputSource(), srcWithID, "SAXSource(reader, new InputSource(sysId)) has InputSource: " + saxSrcReaderID2.getInputSource());
            reporter.checkObject(saxSrcReaderID2.getXMLReader(), reader2, "SAXSource(reader, new InputSource(sysId)) has XMLReader: " + saxSrcReaderID2.getXMLReader());
            reporter.check(saxSrcReaderID2.getSystemId(), NONSENSE_SYSTEMID, "SAXSource(reader, new InputSource(sysId)) has SystemId: " + saxSrcReaderID2.getSystemId());

            // ctor(XMLReader, InputSource) 
            reporter.logTraceMsg("SAX way:reader = SAXParser.getXMLReader()");
            // Be sure to use the JAXP methods only!
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser saxParser = factory.newSAXParser();
            XMLReader reader = saxParser.getXMLReader();
            SAXSource saxSrcReaderID = new SAXSource(reader, srcWithID);
            reporter.checkObject(saxSrcReaderID.getInputSource(), srcWithID, "SAXSource(reader, new InputSource(sysId)) has InputSource: " + saxSrcReaderID.getInputSource());
            reporter.checkObject(saxSrcReaderID.getXMLReader(), reader, "SAXSource(reader, new InputSource(sysId)) has XMLReader: " + saxSrcReaderID.getXMLReader());
            reporter.check(saxSrcReaderID.getSystemId(), NONSENSE_SYSTEMID, "SAXSource(reader, new InputSource(sysId)) has SystemId: " + saxSrcReaderID.getSystemId());

            // ctor(null InputSource) - note it won't actually 
            //  be able to be used as a Source in real life
            SAXSource saxNullSrc = new SAXSource(null);
            reporter.checkObject(saxNullSrc.getInputSource(), null, "SAXSource(null InputSource) has null InputSource");
            reporter.checkObject(saxNullSrc.getXMLReader(), null, "SAXSource(null InputSource) has null XMLReader");
            reporter.check(saxNullSrc.getSystemId(), null, "SAXSource(null InputSource) has null SystemId");

            // ctor(null Reader, null InputSource)
            SAXSource saxNullSrc2 = new SAXSource(null, null);
            reporter.checkObject(saxNullSrc2.getInputSource(), null, "SAXSource(null XMLReader, null InputSource) has null InputSource");
            reporter.checkObject(saxNullSrc2.getXMLReader(), null, "SAXSource(null XMLReader, null InputSource) has null XMLReader");
            reporter.check(saxNullSrc2.getSystemId(), null, "SAXSource(null XMLReader, null InputSource) has null SystemId");


            // Validate various simple set/get methods
            SAXSource wackySAX = new SAXSource();
            // Validate setting systemId auto-creates InputSource 
            //  with that systemId
            wackySAX.setSystemId(NONSENSE_SYSTEMID);
            reporter.checkObject(wackySAX.getSystemId(), NONSENSE_SYSTEMID, "set/getSystemId API coverage - after autocreate InputSource");
            reporter.check((wackySAX.getInputSource() != null), true, "setSystemId autocreates an InputSource");
            InputSource newIS = wackySAX.getInputSource();
            reporter.check(newIS.getSystemId(), NONSENSE_SYSTEMID, "autocreated InputSource has correct systemId");

            // API Coverage set/getSystemId
            wackySAX.setSystemId("another-system-id");
            reporter.checkObject(wackySAX.getSystemId(), "another-system-id", "set/getSystemId API coverage");
            InputSource gotIS = wackySAX.getInputSource();
            reporter.check(gotIS.getSystemId(), "another-system-id", "Changing SAXSource systemId changes InputSource's");
            // setting to null explicitly
            wackySAX.setSystemId(null);
            reporter.checkObject(wackySAX.getSystemId(), null, "set/getSystemId API coverage with null");
            reporter.checkObject(wackySAX.getInputSource().getSystemId(), null, "InputSource follows setSystemId(null) of parent source");

            // API Coverage set/getInputSource
            InputSource anotherIS = new InputSource(NONSENSE_SYSTEMID);
            wackySAX.setInputSource(anotherIS);
            reporter.checkObject(wackySAX.getInputSource(), anotherIS, "set/getInputSource API coverage");
            reporter.check(wackySAX.getSystemId(), NONSENSE_SYSTEMID, "setInputSource sets our systemId");


            // API Coverage set/getXMLReader
            reporter.checkObject(wackySAX.getXMLReader(), null, "wackySAX still does not have an XMLReader");
            // Be sure to use the JAXP methods only!
            saxParser = factory.newSAXParser();
            XMLReader wackyReader = saxParser.getXMLReader();
            wackySAX.setXMLReader(wackyReader);
            reporter.checkObject(wackySAX.getXMLReader(), wackyReader, "set/getXMLReader API coverage");
            wackySAX.setXMLReader(null);
            reporter.checkObject(wackySAX.getXMLReader(), null, "set/getXMLReader API coverage with null");
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with SAXSource set/get API");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with SAXSource set/get API");
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Basic functionality of SAXSources.
     * Use them in simple transforms, with/without systemId set.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Basic functionality of SAXSources");
        // Provide local copies of URLized filenames, so that we can
        //  later run tests with either Strings or URLs
        String xslURI = QetestUtils.filenameToURL(testFileInfo.inputName);
        String xmlURI = QetestUtils.filenameToURL(testFileInfo.xmlName);
        String xslImpInclURI = QetestUtils.filenameToURL(impInclFileInfo.inputName);
        String xmlImpInclURI = QetestUtils.filenameToURL(impInclFileInfo.xmlName);

        TransformerFactory factory = null;
        SAXTransformerFactory saxFactory = null;
        try
        {
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem creating factory; can't continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, t,"Problem creating factory; can't continue testcase");
            return true;
        }

        try
        {
            // Validate process of a stylesheet using a simple SAXSource with a URL
            reporter.logTraceMsg("Create templates/transformer from SAXSource(new InputSource(URL))");
            SAXSource xslSAXSrc = new SAXSource(new InputSource(xslURI));
            Templates templates = factory.newTemplates(xslSAXSrc);
            reporter.check((templates != null), true, "Create templates from SAXSource(new InputSource(URL))");
            
            xslSAXSrc = new SAXSource(new InputSource(xslURI));
            Transformer transformer1 = factory.newTransformer(xslSAXSrc);
            reporter.check((transformer1 != null), true, "Create transformer from SAXSource(new InputSource(URL))");

            Transformer transformer2 = templates.newTransformer();
            reporter.check((transformer2 != null), true, "Create transformer from earlier templates");

            reporter.logTraceMsg("Validate transform of SAXSource(XML) using above transformers");
            SAXSource xmlSAXSrc = new SAXSource(new InputSource(xmlURI));

            reporter.logTraceMsg("Create FileOutputStream to " + outNames.nextName());
            transformer1.transform(xmlSAXSrc, new StreamResult(outNames.currentName()));
            fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "transform of SAXSource(URL) using newTransformer transformer into: " + outNames.currentName());

            reporter.logTraceMsg("Create FileOutputStream to " + outNames.nextName());
            transformer2.transform(xmlSAXSrc, new StreamResult(outNames.currentName()));
            fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "transform of SAXSource(URL) using templates.getTransformer into: " + outNames.currentName());


            // Validate process of a stylesheet using a simple SAXSource with an InputStream
            //  Note setting systemId is not necessary with this stylesheet
            reporter.logTraceMsg("Create templates/transformer from SAXSource(...new InputStream(" + testFileInfo.inputName + ")))");
            SAXSource xslSAXSrcStream = new SAXSource(new InputSource(new FileInputStream(testFileInfo.inputName)));
            Templates templatesStream = factory.newTemplates(xslSAXSrc);
            reporter.check((templatesStream != null), true, "Create templates from SAXSource(FileInputStream())");
            Transformer transformerStream = templatesStream.newTransformer();
            reporter.check((transformerStream != null), true, "Create transformer from templates");
            
            reporter.logTraceMsg("Validate transform of SAXSource(...new InputStream(" + testFileInfo.xmlName + " )) using above transformers");
            SAXSource xmlSAXSrcStream = new SAXSource(new InputSource(new FileInputStream(testFileInfo.xmlName)));

            reporter.logTraceMsg("Create FileOutputStream to " + outNames.nextName());
            transformerStream.transform(xmlSAXSrcStream, new StreamResult(outNames.currentName()));
            fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "transform of SAXSource(FileInputStreams) into: " + outNames.currentName());
            
        reporter.logTraceMsg("@todo: add more systemId tests of various types here");

        }
        catch (Throwable t)
        {
            reporter.checkFail("simple SAXSources threw: " + t.toString());
            reporter.logThrowable(reporter.ERRORMSG, t, "simple SAXSources threw");
        }

        try
        {
            // Validate process of a stylesheet using a simple SAXSource with imports/includes
            // Since we're providing the InputSource with a URL, we
            //  don't also need to setSystemId
            reporter.logTraceMsg("Create templates/transformer from SAXSource(new InputSource(" + xslImpInclURI + "))");
            SAXSource xslSAXSrc = new SAXSource(new InputSource(xslImpInclURI));
            Templates templates = factory.newTemplates(xslSAXSrc);
            reporter.check((templates != null), true, "Create templates from SAXSource(new InputSource(" + xslImpInclURI + "))");
            
            xslSAXSrc = new SAXSource(new InputSource(xslImpInclURI));
            Transformer transformer1 = factory.newTransformer(xslSAXSrc);
            reporter.check((transformer1 != null), true, "Create transformer from SAXSource(new InputSource(" + xslImpInclURI + "))");

            Transformer transformer2 = factory.newTransformer(xslSAXSrc);
            reporter.check((transformer2 != null), true, "Create transformer from earlier templates");

            reporter.logTraceMsg("Validate transform of SAXSource(" + xmlImpInclURI + ") using above transformers");
            SAXSource xmlSAXSrc = new SAXSource(new InputSource(xmlImpInclURI));

            reporter.logTraceMsg("Create FileOutputStream to " + outNames.nextName());
            transformer1.transform(xmlSAXSrc, new StreamResult(outNames.currentName()));
            fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(impInclFileInfo.goldName), 
                              "transform of SAXSource(impinclURLXML) using newTransformer transformer into: " + outNames.currentName());

            reporter.logTraceMsg("Create FileOutputStream to " + outNames.nextName());
            xmlSAXSrc = new SAXSource(new InputSource(xmlImpInclURI));
            xmlSAXSrc.setSystemId(xmlImpInclURI); // see if setting it affects anything
            transformer2.transform(xmlSAXSrc, new StreamResult(outNames.currentName()));
            fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(impInclFileInfo.goldName), 
                              "transform of SAXSource(impinclURLXML) using templates.getTransformer into: " + outNames.currentName());

            reporter.logTraceMsg("@todo: add systemId tests, etc. for various kinds of InputSources");

        }
        catch (Throwable t)
        {
            reporter.checkFail("impinclURL SAXSources threw: " + t.toString());
            reporter.logThrowable(reporter.ERRORMSG, t, "impinclURL SAXSources threw");
        }

        reporter.logTraceMsg("@todo: add SAXSource(Reader, InputSource) tests");

        reporter.logTraceMsg("@todo: add wacky tests: reuse SAXSource, use then set/get then reuse, etc.");

        reporter.logTraceMsg("@todo: test static sourceToInputSource() method");

        reporter.testCaseClose();
        return true;
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by SAXSourceAPITest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        SAXSourceAPITest app = new SAXSourceAPITest();
        app.doMain(args);
    }
}
