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
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

// Use Xalan's own serializers for SAX ContentHandler output
import org.apache.xalan.serialize.SerializerFactory;
import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.templates.OutputProperties;

// Needed SAX, DOM, JAXP classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
// Needed SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Parser;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.XMLReaderFactory;
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
        numTestCases = 2;  // REPLACE_num
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
          if( reader==null ) reader = XMLReaderFactory.createXMLReader();
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
              if( reader==null ) reader= XMLReaderFactory.createXMLReader();
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
    * Serialize a node to System.out.
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
