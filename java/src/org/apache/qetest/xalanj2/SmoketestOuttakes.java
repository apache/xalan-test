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
 * SmoketestOuttakes.java
 *
 */
package org.apache.qetest.xalanj2;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.trax.*;
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

// Use Xalan's own serializers for SAX ContentHandler output
import org.apache.xalan.serialize.SerializerFactory;
import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.templates.OutputProperties;

// Needed SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Parser;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.XMLReader;
import org.xml.sax.XMLFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.DeclHandler;

// Needed DOM classes
import org.w3c.dom.Node;

// java classes
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Individual test points taken out of other automation files.  
 * 
 * Although as a quality engineer I'm not sure I really like this 
 * idea, I'm temporarily moving test points with known and reported 
 * fail conditions out of a number of other automated tests into 
 * here.  In a distributed open source project like this, this 
 * should make it easier for developers to run a reliable smoketest 
 * before making any checkins (since the list of smoketest files 
 * will generally be kept to tests that we expect should pass; thus 
 * any fails when you run the smoketest when you run it are likely 
 * due to recent changes you have made).
 *
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class SmoketestOuttakes extends XSLProcessorTestBase
{

    /** Provides nextName(), currentName() functionality.  */
    protected OutputNameManager outNames;


    /** Just initialize test name, comment, numTestCases. */
    public SmoketestOuttakes()
    {
        numTestCases = 5;  // REPLACE_num
        testName = "SmoketestOuttakes";
        testComment = "Individual test points taken out of other automation files";
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
        File outSubDir = new File(outputDir + File.separator + "trax");
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + "trax" + File.separator
                                         + testName, ".out");

        return true;
    }


    /**
     * Recreate ExamplesTest.exampleContentHandlerToContentHandler.  
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Recreate ExamplesTest.exampleContentHandlerToContentHandler");

        try
        {
        String xslID = inputDir 
                              + File.separator 
                              + "trax"
                              + File.separator
                              + "xsl"
                              + File.separator
                              + "foo.xsl";
        String sourceID = inputDir 
                              + File.separator 
                              + "trax"
                              + File.separator
                              + "xml"
                              + File.separator
                              + "foo.xml";
        String goldName = goldDir 
                              + File.separator 
                              + "trax"
                              + File.separator
                              + "ExamplesTest_7.out";

        reporter.logTraceMsg("NOTE! This file is very sensitive to pathing issues!");
        
        TransformerFactory tfactory = TransformerFactory.newInstance();

        // Does this factory support SAX features?
        if (!tfactory.getFeature(SAXSource.FEATURE))
        {
            reporter.logErrorMsg("exampleContentHandlerToContentHandler:Processor does not support SAX");
            return true;
        }
          // If so, we can safely cast.
          SAXTransformerFactory stfactory = ((SAXTransformerFactory) tfactory);
          
          // A TransformerHandler is a ContentHandler that will listen for 
          // SAX events, and transform them to the result.
          reporter.logTraceMsg("newTransformerHandler(new StreamSource(" + QetestUtils.filenameToURL(xslID));
          TransformerHandler handler 
            = stfactory.newTransformerHandler(new StreamSource(QetestUtils.filenameToURL(xslID)));

          // Set the result handling to be a serialization to the file output stream.
          Serializer serializer = SerializerFactory.getSerializer
                                  (OutputProperties.getDefaultMethodProperties("xml"));
          FileOutputStream fos = new FileOutputStream(outNames.nextName());
          serializer.setOutputStream(fos);
          reporter.logStatusMsg("Test-output-to: new FileOutputStream(" + outNames.currentName());
          
          Result result = new SAXResult(serializer.asContentHandler());

          handler.setResult(result);
          
          // Create a reader, and set it's content handler to be the TransformerHandler.
          XMLReader reader=null;

          // Use JAXP1.1 ( if possible )
          try {
              javax.xml.parsers.SAXParserFactory factory=
                  javax.xml.parsers.SAXParserFactory.newInstance();
              factory.setNamespaceAware( true );
              javax.xml.parsers.SAXParser jaxpParser=
                  factory.newSAXParser();
              reader=jaxpParser.getXMLReader();
              
          } catch( javax.xml.parsers.ParserConfigurationException ex ) {
              throw new org.xml.sax.SAXException( ex );
          } catch( javax.xml.parsers.FactoryConfigurationError ex1 ) {
              throw new org.xml.sax.SAXException( ex1.toString() );
          } catch( NoSuchMethodError ex2 ) {
          }
          if( reader==null ) reader = getJAXPXMLReader();
          reader.setContentHandler(handler);
          
          // It's a good idea for the parser to send lexical events.
          // The TransformerHandler is also a LexicalHandler.
          reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
          
          // Parse the source XML, and send the parse events to the TransformerHandler.
          reporter.logTraceMsg("reader.parse(" + QetestUtils.filenameToURL(sourceID));
          reader.parse(QetestUtils.filenameToURL(sourceID));
          fos.close();

          reporter.logTraceMsg("Note: See SPR SCUU4RZT78 for discussion as to why this output is different than XMLReader/XMLFilter");
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(goldName),                
                          "exampleContentHandlerToContentHandler fileChecker of:" + outNames.currentName());
        
        
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with testCase1:");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with testCase1");
        }
        reporter.testCaseClose();
        return true;
    }



    /**
     * Recreate ExamplesTest.exampleContentHandlerToContentHandler.  
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Recreate ExamplesTest.exampleContentHandlerToContentHandler");

        String xslID = inputDir 
                              + File.separator 
                              + "trax"
                              + File.separator
                              + "xsl"
                              + File.separator
                              + "foo.xsl";
        String sourceID = inputDir 
                              + File.separator 
                              + "trax"
                              + File.separator
                              + "xml"
                              + File.separator
                              + "foo.xml";
        String goldName = goldDir 
                              + File.separator 
                              + "trax"
                              + File.separator
                              + "ExamplesTest_18.out";

        try
        {

            TransformerFactory tfactory = TransformerFactory.newInstance();

            // Make sure the transformer factory we obtained supports both
            // DOM and SAX.
            if (!(tfactory.getFeature(SAXSource.FEATURE)
                && tfactory.getFeature(DOMSource.FEATURE)))
            {
                reporter.logErrorMsg("exampleContentHandler2DOM:Processor does not support SAX/DOM");
                return true;
            }
              // We can now safely cast to a SAXTransformerFactory.
              SAXTransformerFactory sfactory = (SAXTransformerFactory) tfactory;
              
              // Create an Document node as the root for the output.
              DocumentBuilderFactory dfactory 
                = DocumentBuilderFactory.newInstance();
              DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
              org.w3c.dom.Document outNode = docBuilder.newDocument();
              
              // Create a ContentHandler that can liston to SAX events 
              // and transform the output to DOM nodes.
              reporter.logTraceMsg("newTransformerHandler(new StreamSource(" + QetestUtils.filenameToURL(xslID));
              TransformerHandler handler 
                = sfactory.newTransformerHandler(new StreamSource(QetestUtils.filenameToURL(xslID)));
              handler.setResult(new DOMResult(outNode));
              
              // Create a reader and set it's ContentHandler to be the 
              // transformer.
              XMLReader reader=null;

              // Use JAXP1.1 ( if possible )
              try {
                  javax.xml.parsers.SAXParserFactory factory=
                      javax.xml.parsers.SAXParserFactory.newInstance();
                  factory.setNamespaceAware( true );
                  javax.xml.parsers.SAXParser jaxpParser=
                      factory.newSAXParser();
                  reader=jaxpParser.getXMLReader();
                  
              } catch( javax.xml.parsers.ParserConfigurationException ex ) {
                  throw new org.xml.sax.SAXException( ex );
              } catch( javax.xml.parsers.FactoryConfigurationError ex1 ) {
                  throw new org.xml.sax.SAXException( ex1.toString() );
              } catch( NoSuchMethodError ex2 ) {
              }
              if( reader==null ) reader= getJAXPXMLReader();
              reader.setContentHandler(handler);
              reader.setProperty("http://xml.org/sax/properties/lexical-handler",
                                 handler);
              
              // Send the SAX events from the parser to the transformer,
              // and thus to the DOM tree.
              reporter.logTraceMsg("reader.parse(" + QetestUtils.filenameToURL(sourceID));
              reader.parse(QetestUtils.filenameToURL(sourceID));
              
              // Serialize the node for diagnosis.
              //    This serializes to outNames.nextName()
              exampleSerializeNode(outNode);

            fileChecker.check(reporter, new File(outNames.currentName()),
                              new File(goldName),                
                              "exampleContentHandler2DOM fileChecker of:" + outNames.currentName());

        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with testCase2:");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with testCase2");
        }
        reporter.testCaseClose();
        return true;
    }


    /**
    * Serialize a node to System.out; 
    * used in ExamplesTest; testCase1, testCase2 above
    */
    public void exampleSerializeNode(Node node)
        throws TransformerException, TransformerConfigurationException, 
        SAXException, IOException, ParserConfigurationException
    {
        TransformerFactory tfactory = TransformerFactory.newInstance(); 

        // This creates a transformer that does a simple identity transform, 
        // and thus can be used for all intents and purposes as a serializer.
        Transformer serializer = tfactory.newTransformer();

        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.transform(new DOMSource(node), 
                             new StreamResult(outNames.nextName()));
        reporter.logStatusMsg("Test-output-to: new StreamResult(" + outNames.currentName());
        // TEST UPDATE - Caller must validate outNames.currentName()
    }  


    /**
     * From ErrorListenerTest.java testCase2
     * Build a bad stylesheet/do a transform with SAX.
     * Verify that the ErrorListener is called properly.
     * Primarily using SAXSources.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase3()
    {
        reporter.testCaseInit("Build a bad stylesheet/do a transform with SAX");
        XSLTestfileInfo testFileInfo = new XSLTestfileInfo();
        testFileInfo.inputName = inputDir 
                              + File.separator 
                              + "err"
                              + File.separator + "ErrorListenerTest.xsl";
        testFileInfo.xmlName = inputDir 
                              + File.separator 
                              + "err"
                              + File.separator + "ErrorListenerTest.xml";
        testFileInfo.goldName = goldDir 
                              + File.separator 
                              + "err"
                              + File.separator + "ErrorListenerTest.out";
        int templatesExpectedType = LoggingErrorListener.TYPE_FATALERROR;
        String templatesExpectedValue = "decimal-format names must be unique. Name \"myminus\" has been duplicated";
        int transformExpectedType = LoggingErrorListener.TYPE_WARNING;
        String transformExpectedValue = "ExpectedMessage from:list1";
        
        
        LoggingErrorListener loggingErrorListener = new LoggingErrorListener(reporter);
        loggingErrorListener.setThrowWhen(LoggingErrorListener.THROW_NEVER);
        reporter.logTraceMsg("loggingErrorListener originally setup:" + loggingErrorListener.getQuickCounters());

        TransformerFactory factory = null;
        SAXTransformerFactory saxFactory = null;
        XMLReader reader = null;
        Templates templates = null;
        Transformer transformer = null;
        TransformerHandler handler = null;
        try
        {
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory; // assumes SAXSource.feature!

            // Set the errorListener and validate it
            saxFactory.setErrorListener(loggingErrorListener);
            reporter.check((saxFactory.getErrorListener() == loggingErrorListener),
                           true, "set/getErrorListener on saxFactory");

            // Use the JAXP way to get an XMLReader
            reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            InputSource is = new InputSource(QetestUtils.filenameToURL(testFileInfo.inputName));

            // Attempt to build templates from known-bad stylesheet
            // Validate known errors in stylesheet building 
            loggingErrorListener.setExpected(templatesExpectedType, 
                                             templatesExpectedValue);
            reporter.logTraceMsg("About to factory.newTransformerHandler(SAX:" + QetestUtils.filenameToURL(testFileInfo.inputName) + ")");
            handler = saxFactory.newTransformerHandler(new SAXSource(is));
            reporter.logTraceMsg("loggingErrorListener after newTransformerHandler:" + loggingErrorListener.getQuickCounters());
            // Clear out any setExpected or counters
            loggingErrorListener.reset();
            reporter.checkPass("set ErrorListener prevented any exceptions in newTransformerHandler()");

            // This stylesheet will still work, even though errors 
            //  were detected during it's building.  Note that 
            //  future versions of Xalan or other processors may 
            //  not be able to continue here...

            // Create a result and setup SAX parsing 'tree'
            Result result = new StreamResult(outNames.nextName());
            handler.setResult(result);
            reader.setContentHandler(handler);

            LoggingSAXErrorHandler loggingSAXErrorHandler = new LoggingSAXErrorHandler(reporter);
            loggingSAXErrorHandler.setThrowWhen(LoggingSAXErrorHandler.THROW_NEVER);
            reporter.logTraceMsg("LoggingSAXErrorHandler originally setup:" + loggingSAXErrorHandler.getQuickCounters());
            reader.setErrorHandler(loggingSAXErrorHandler);
            
            // Validate the first xsl:message call in the stylesheet
            loggingErrorListener.setExpected(transformExpectedType, 
                                             transformExpectedValue);
            reporter.logInfoMsg("about to parse/transform(" + QetestUtils.filenameToURL(testFileInfo.xmlName) + ")");
            reader.parse(QetestUtils.filenameToURL(testFileInfo.xmlName));
            reporter.logTraceMsg("LoggingSAXErrorHandler after parse:" + loggingSAXErrorHandler.getQuickCounters());
            // Clear out any setExpected or counters
            loggingErrorListener.reset();
            loggingSAXErrorHandler.reset();

            // Validate the actual output file as well: in this case, 
            //  the stylesheet should still work
            fileChecker.check(reporter, 
                    new File(outNames.currentName()), 
                    new File(testFileInfo.goldName), 
                    "Bugzilla#4044 SAX transform of error xsl into: " + outNames.currentName());
        }
        catch (Throwable t)
        {
            reporter.checkFail("errorListener-SAX unexpectedly threw: " + t.toString());
            reporter.logThrowable(Logger.ERRORMSG, t, "errorListener-SAX unexpectedly threw");
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * From ErrorListenerTest.java testCase3
     * Build a bad stylesheet/do a transform with DOMs.
     * Verify that the ErrorListener is called properly.
     * Primarily using DOMSources.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase4()
    {
        reporter.testCaseInit("Build a bad stylesheet/do a transform with DOMs");
        XSLTestfileInfo testFileInfo = new XSLTestfileInfo();
        testFileInfo.inputName = inputDir 
                              + File.separator 
                              + "err"
                              + File.separator + "ErrorListenerTest.xsl";
        testFileInfo.xmlName = inputDir 
                              + File.separator 
                              + "err"
                              + File.separator + "ErrorListenerTest.xml";
        testFileInfo.goldName = goldDir 
                              + File.separator 
                              + "err"
                              + File.separator + "ErrorListenerTest.out";
        int templatesExpectedType = LoggingErrorListener.TYPE_FATALERROR;
        String templatesExpectedValue = "decimal-format names must be unique. Name \"myminus\" has been duplicated";
        int transformExpectedType = LoggingErrorListener.TYPE_WARNING;
        String transformExpectedValue = "ExpectedMessage from:list1";

        LoggingErrorListener loggingErrorListener = new LoggingErrorListener(reporter);
        loggingErrorListener.setThrowWhen(LoggingErrorListener.THROW_NEVER);
        reporter.logTraceMsg("loggingErrorListener originally setup:" + loggingErrorListener.getQuickCounters());

        TransformerFactory factory = null;
        Templates templates = null;
        Transformer transformer = null;
        DocumentBuilderFactory dfactory = null;
        DocumentBuilder docBuilder = null;
        Node xmlNode = null;
        Node xslNode = null;
        try
        {
            // Startup a DOM factory, create some nodes/DOMs
            dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setNamespaceAware(true);
            docBuilder = dfactory.newDocumentBuilder();
            reporter.logInfoMsg("parsing xml, xsl files to DOMs");
            xslNode = docBuilder.parse(new InputSource(QetestUtils.filenameToURL(testFileInfo.inputName)));
            xmlNode = docBuilder.parse(new InputSource(QetestUtils.filenameToURL(testFileInfo.xmlName)));

            // Create a transformer factory with an error listener
            factory = TransformerFactory.newInstance();
            factory.setErrorListener(loggingErrorListener);

            // Attempt to build templates from known-bad stylesheet
            // Validate known errors in stylesheet building 
            loggingErrorListener.setExpected(templatesExpectedType, 
                                             templatesExpectedValue);
            reporter.logTraceMsg("About to factory.newTemplates(DOM:" + QetestUtils.filenameToURL(testFileInfo.inputName) + ")");
            templates = factory.newTemplates(new DOMSource(xslNode));
            reporter.logTraceMsg("loggingErrorListener after newTemplates:" + loggingErrorListener.getQuickCounters());
            // Clear out any setExpected or counters
            loggingErrorListener.reset();
            reporter.checkPass("set ErrorListener prevented any exceptions in newTemplates()");

            // This stylesheet will still work, even though errors 
            //  were detected during it's building.  Note that 
            //  future versions of Xalan or other processors may 
            //  not be able to continue here...
            reporter.logErrorMsg("Bugzilla#1062 throws NPE below at templates.newTransformer()");
            transformer = templates.newTransformer();

            reporter.logTraceMsg("default transformer's getErrorListener is: " + transformer.getErrorListener());
            // Set the errorListener and validate it
            transformer.setErrorListener(loggingErrorListener);
            reporter.check((transformer.getErrorListener() == loggingErrorListener),
                           true, "set/getErrorListener on transformer");

            // Validate the first xsl:message call in the stylesheet
            loggingErrorListener.setExpected(transformExpectedType, 
                                             transformExpectedValue);
            reporter.logInfoMsg("about to transform(DOM, StreamResult)");
            transformer.transform(new DOMSource(xmlNode), 
                                  new StreamResult(outNames.nextName()));
            reporter.logTraceMsg("after transform(...)");
            // Clear out any setExpected or counters
            loggingErrorListener.reset();

            // Validate the actual output file as well: in this case, 
            //  the stylesheet should still work
            fileChecker.check(reporter, 
                    new File(outNames.currentName()), 
                    new File(testFileInfo.goldName), 
                    "DOM transform of error xsl into: " + outNames.currentName());
            
        }
        catch (Throwable t)
        {
            reporter.checkFail("errorListener-DOM unexpectedly threw: " + t.toString());
            reporter.logThrowable(Logger.ERRORMSG, t, "errorListener-DOM unexpectedly threw");
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * From URIResolverTest.java testCase1
     * Build a stylesheet/do a transform with lots of URIs to resolve.
     * Verify that the URIResolver is called properly.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase5()
    {
        reporter.testCaseInit("Build a stylesheet/do a transform with lots of URIs to resolve");

        XSLTestfileInfo testFileInfo = new XSLTestfileInfo();
        testFileInfo.inputName = inputDir + File.separator + "trax" + File.separator + "URIResolverTest.xsl";
        testFileInfo.xmlName = inputDir + File.separator + "trax" + File.separator + "URIResolverTest.xml";
        testFileInfo.goldName = goldDir + File.separator + "trax" + File.separator + "URIResolverTest.out";

        TransformerFactory factory = null;
        Templates templates = null;
        Transformer transformer = null;
        try
        {
            factory = TransformerFactory.newInstance();
            // Set the URIResolver and validate it
            reporter.logInfoMsg("About to factory.newTemplates(" + QetestUtils.filenameToURL(testFileInfo.inputName) + ")");
            templates = factory.newTemplates(new StreamSource(QetestUtils.filenameToURL(testFileInfo.inputName)));
            transformer = templates.newTransformer();

            // Set the URIResolver and validate it
            LoggingURIResolver loggingURIResolver = new LoggingURIResolver((Logger)reporter);
            reporter.logTraceMsg("loggingURIResolver originally setup:" + loggingURIResolver.getQuickCounters());
            transformer.setURIResolver(loggingURIResolver);
            reporter.check((transformer.getURIResolver() == loggingURIResolver),
                           true, "set/getURIResolver on transformer"); 

            // Validate various URI's to be resolved during transform
            //  time with the loggingURIResolver
            reporter.logWarningMsg("Bugzilla#2425 every document() call is resolved twice twice - two fails caused below");
            String[] expectedXmlUris = 
            {
                "{" + QetestUtils.filenameToURL(testFileInfo.inputName) + "}" + "../impincl/SystemIdImport.xsl",
                "{" + QetestUtils.filenameToURL(testFileInfo.inputName) + "}" + "impincl/SystemIdImport.xsl",
                "{" + QetestUtils.filenameToURL(testFileInfo.inputName) + "}" + "systemid/impincl/SystemIdImport.xsl",
            };
            loggingURIResolver.setExpected(expectedXmlUris);
            reporter.logTraceMsg("about to transform(...)");
            transformer.transform(new StreamSource(QetestUtils.filenameToURL(testFileInfo.xmlName)), 
                                  new StreamResult(outNames.nextName()));
            reporter.logTraceMsg("after transform(...)");
        }
        catch (Throwable t)
        {
            reporter.checkFail("URIResolver test unexpectedly threw: " + t.toString());
            reporter.logThrowable(Logger.ERRORMSG, t, "URIResolver test unexpectedly threw");
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
        return ("Common [optional] options supported by SmoketestOuttakes:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        SmoketestOuttakes app = new SmoketestOuttakes();
        app.doMain(args);
    }
}
