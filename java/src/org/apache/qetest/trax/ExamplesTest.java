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
 * ExamplesTest.java
 *
 */
package org.apache.qetest.trax;

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

// Needed JAXP classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.BufferedInputStream;    // dml
// java classes
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Test version of xml-xalan/java/samples/trax/Examples.java.
 * <p>This file is essentially copied from the Examples for TRAX, or 
 * javax.xml.transform; however this file actually validates most 
 * output and behavior for correctness.  Hopefully, we can get this 
 * file updated at the same time as Examples.java in the future.</p>
 * <p>In general, I merely copied each method from Examples.java
 * and made minor updates (try...catch within methods, call to 
 * reporter.logBlah to output messages, etc.) then added validation 
 * of actual output files.  Note that each method validates it's 
 * output by calling fileChecker.check(...) explicitly, so we can't 
 * change the input files without carefully changing the gold files 
 * for each area.</p>
 * <p>Note that some tests may use NOT_DEFINED for their gold file 
 * if we haven't yet validated what the 'correct' output should be 
 * for each case - these should be updated as time permits.</p>
 * @author shane_curcuru@lotus.com
 * @author scott_boag@lotus.com
 * @version $Id$
 */
public class ExamplesTest extends XSLProcessorTestBase
{

    /**
     * Provides nextName(), currentName() functionality for tests 
     * that may produce any number of output files.
     */
    protected OutputNameManager outNames;

    /** Sample test stylesheet to use for transformations, includes gold file.  */
    protected XSLTestfileInfo fooFile = new XSLTestfileInfo();

    /** Sample test stylesheet to use for transformations, includes gold file.  */
    protected XSLTestfileInfo bazFile = new XSLTestfileInfo();

    /** Sample test stylesheet name to use for multi-transformations.  */
    protected String foo2File;

    /** Sample test stylesheet name to use for multi-transformations.  */
    protected String foo3File;

    /** Sample gold files used for specific transforms - with params.  */
    protected String param1GoldName;

    /** Sample gold files used for specific transforms - with params and output format.  */
    protected String param2GoldName;

    /** Sample gold files used for specific transforms - with output format.  */
    protected String outputGoldName;

    /** Sample gold files used for specific transforms - XMLFilter/Reader.  */
    protected String saxGoldName;

    /** Gold file used for tests we haven't validated the correct results of yet.  */
    protected String NOT_DEFINED;


    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String TRAX_SUBDIR = "trax";


    /** Just initialize test name, comment, numTestCases. */
    public ExamplesTest()
    {
        numTestCases = 1;  // REPLACE_num
        testName = "ExamplesTest";
        testComment = "Test various combinations of Source and Result types";
    }


    /**
     * Initialize this test - Set names of xml/xsl test files,
     * REPLACE_other_test_file_init.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        // NOTE: 'reporter' variable is already initialized at this point

        // Used for all tests; just dump files in trax subdir
        File outSubDir = new File(outputDir + File.separator + TRAX_SUBDIR);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + TRAX_SUBDIR
                                         + File.separator + testName, ".out");

        String testBasePath = inputDir 
                              + File.separator 
                              + TRAX_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator 
                              + TRAX_SUBDIR
                              + File.separator;

        reporter.logTraceMsg("NOTE! This file is very sensitive to pathing issues!");
        fooFile.inputName = swapSlash(testBasePath + "xsl/foo.xsl");
        fooFile.xmlName = swapSlash(testBasePath + "xml/foo.xml");
        fooFile.goldName = goldBasePath + "foo.out";

        bazFile.xmlName = swapSlash(testBasePath + "xml/baz.xml");
        bazFile.goldName = goldBasePath + "baz.out";

        foo2File = swapSlash(testBasePath + "xsl/foo2.xsl");

        foo3File = swapSlash(testBasePath + "xsl/foo3.xsl");

        param1GoldName = goldBasePath + "param1.out";
        param2GoldName = goldBasePath + "param2.out";
        outputGoldName = goldBasePath + "output.out";
        saxGoldName = goldBasePath + "fooSAX.out";
        NOT_DEFINED = goldBasePath + "need-validated-output-file-here.out";
        return true;
    }


    /**
     * Worker method to swap / for \ in Strings.  
     */
    public String swapSlash(String s)
    {
        return (new String(s)).replace('\\', '/');
    }


    /**
     * Call each of the methods found in Examples.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Call each of the methods found in Examples");

        // Note: the tests must be used with the same input files, 
        //  since they hard-code the gold files within the methods
        reporter.logTraceMsg("exampleSimple1(" + fooFile.xmlName + ", " + fooFile.inputName + ")");
        exampleSimple1(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleSimple2(" + fooFile.xmlName + ", " + fooFile.inputName + ")");
        exampleSimple2(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleFromStream(" + fooFile.xmlName + ", " + fooFile.inputName + ")");
        exampleFromStream(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleFromReader(" + fooFile.xmlName + ", " + fooFile.inputName + ")");
        exampleFromReader(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleUseTemplatesObj(" + fooFile.xmlName + ", " + bazFile.xmlName + ", " + fooFile.inputName + ")");
        exampleUseTemplatesObj(fooFile.xmlName, bazFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleContentHandlerToContentHandler(fooFile.xmlName, fooFile.inputName)");
        exampleContentHandlerToContentHandler(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleXMLReader(fooFile.xmlName, fooFile.inputName)");
        exampleXMLReader(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleXMLFilter(fooFile.xmlName, fooFile.inputName)");
        exampleXMLFilter(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleXMLFilterChain(fooFile.xmlName, fooFile.inputName, foo2File, foo3File)");
        exampleXMLFilterChain(fooFile.xmlName, fooFile.inputName, foo2File, foo3File);

        reporter.logTraceMsg("exampleDOM2DOM(" + fooFile.xmlName + ", " + fooFile.inputName + ")");
        exampleDOM2DOM(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleParam(" + fooFile.xmlName + ", " + fooFile.inputName + ")");
        exampleParam(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleTransformerReuse(" + fooFile.xmlName + ", " + fooFile.inputName + ")");
        exampleTransformerReuse(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleOutputProperties(" + fooFile.xmlName + ", " + fooFile.inputName + ")");
        exampleOutputProperties(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleUseAssociated(" + fooFile.xmlName +")");
        exampleUseAssociated(fooFile.xmlName);

        reporter.logTraceMsg("exampleContentHandler2DOM(" + fooFile.xmlName + ", " + fooFile.inputName + ")");
        exampleContentHandler2DOM(fooFile.xmlName, fooFile.inputName);

        reporter.logTraceMsg("exampleAsSerializer(" + fooFile.xmlName + ", " + fooFile.inputName + ")");
        exampleAsSerializer(fooFile.xmlName, fooFile.inputName);

        reporter.testCaseClose();
        return true;
    }

  /**
   * Show the simplest possible transformation from system id 
   * to output stream.
   */
  public void exampleSimple1(String sourceID, String xslID)
  {
    try
    {
        // Create a transform factory instance.
        TransformerFactory tfactory = TransformerFactory.newInstance();
    
        // Create a transformer for the stylesheet.
        Transformer transformer 
          = tfactory.newTransformer(new StreamSource(xslID));
        // No need to setSystemId, the transformer can get it from the URL
    
        // Transform the source XML to System.out.
        transformer.transform( new StreamSource(sourceID),
                               new StreamResult(outNames.nextName()));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(fooFile.goldName),
                          "exampleSimple1 fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleSimple1 threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleSimple1 threw");
    }
  }
  
  /**
   * Show the simplest possible transformation from File 
   * to a File.
   */
  public void exampleSimple2(String sourceID, String xslID)
  {
    try
    {
        // Create a transform factory instance.
        TransformerFactory tfactory = TransformerFactory.newInstance();
    
        // Create a transformer for the stylesheet.
        Transformer transformer 
          = tfactory.newTransformer(new StreamSource(new File(xslID)));
        // No need to setSystemId, the transformer can get it from the File
    
        // Transform the source XML to System.out.
        transformer.transform( new StreamSource(new File(sourceID)),
                               new StreamResult(new File(outNames.nextName())));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(fooFile.goldName),
                          "exampleSimple2 fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleSimple2 threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleSimple2 threw");
    }
  }

  
  /**
   * Show simple transformation from input stream to output stream.
   */
  public void exampleFromStream(String sourceID, String xslID)
 {
    try
    {
        // Create a transform factory instance.
        TransformerFactory tfactory = TransformerFactory.newInstance();

        InputStream xslIS = new BufferedInputStream(new FileInputStream(xslID));
        StreamSource xslSource = new StreamSource(xslIS);
        // Note that if we don't do this, relative URLs can not be resolved correctly!
        xslSource.setSystemId(xslID);

        // Create a transformer for the stylesheet.
        Transformer transformer = tfactory.newTransformer(xslSource);
    
        InputStream xmlIS = new BufferedInputStream(new FileInputStream(sourceID));
        StreamSource xmlSource = new StreamSource(xmlIS);
        // Note that if we don't do this, relative URLs can not be resolved correctly!
        xmlSource.setSystemId(sourceID);
    
        // Transform the source XML to System.out.
        transformer.transform( xmlSource, new StreamResult(outNames.nextName()));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(fooFile.goldName),
                          "exampleFromStream fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleFromStream threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleFromStream threw");
    }
  }
  
  /**
   * Show simple transformation from reader to output stream.  In general 
   * this use case is discouraged, since the XML encoding can not be 
   * processed.
   */
  public void exampleFromReader(String sourceID, String xslID)
  {
    try
    {
        // Create a transform factory instance.
        TransformerFactory tfactory = TransformerFactory.newInstance();

        // Note that in this case the XML encoding can not be processed!
        Reader xslReader = new BufferedReader(new FileReader(xslID));
        StreamSource xslSource = new StreamSource(xslReader);
        // Note that if we don't do this, relative URLs can not be resolved correctly!
        xslSource.setSystemId(xslID);

        // Create a transformer for the stylesheet.
        Transformer transformer = tfactory.newTransformer(xslSource);
    
        // Note that in this case the XML encoding can not be processed!
        Reader xmlReader = new BufferedReader(new FileReader(sourceID));
        StreamSource xmlSource = new StreamSource(xmlReader);
        // Note that if we don't do this, relative URLs can not be resolved correctly!
        xmlSource.setSystemId(sourceID);
    
        // Transform the source XML to System.out.
        transformer.transform( xmlSource, new StreamResult(outNames.nextName()));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(fooFile.goldName),
                          "exampleFromReader fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleFromReader threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleFromReader threw");
    }
  }


 
  /**
   * Show the simplest possible transformation from system id to output stream.
   */
  public void exampleUseTemplatesObj(String sourceID1, 
                                    String sourceID2, 
                                    String xslID)
  {
    try
    {
        TransformerFactory tfactory = TransformerFactory.newInstance();
    
        // Create a templates object, which is the processed, 
        // thread-safe representation of the stylesheet.
        Templates templates = tfactory.newTemplates(new StreamSource(xslID));

        // Illustrate the fact that you can make multiple transformers 
        // from the same template.
        Transformer transformer1 = templates.newTransformer();
        Transformer transformer2 = templates.newTransformer();
    
        transformer1.transform(new StreamSource(sourceID1),
                              new StreamResult(outNames.nextName()));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(fooFile.goldName),
                          "exampleUseTemplatesObj(1) fileChecker of:" + outNames.currentName());
    
        transformer2.transform(new StreamSource(sourceID2),
                              new StreamResult(outNames.nextName()));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(bazFile.goldName),
                          "exampleUseTemplatesObj(2) fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleUseTemplatesObj threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleUseTemplatesObj threw");
    }
  }
  

  /**
   * Show the Transformer using SAX events in and SAX events out.
   */
  public void exampleContentHandlerToContentHandler(String sourceID, 
                                                           String xslID)
  {
    try
    {
        TransformerFactory tfactory = TransformerFactory.newInstance();

        // Does this factory support SAX features?
        if (!tfactory.getFeature(SAXSource.FEATURE))
        {
            reporter.logErrorMsg("exampleContentHandlerToContentHandler:Processor does not support SAX");
            return;
        }
          // If so, we can safely cast.
          SAXTransformerFactory stfactory = ((SAXTransformerFactory) tfactory);
          
          // A TransformerHandler is a ContentHandler that will listen for 
          // SAX events, and transform them to the result.
          reporter.logTraceMsg("newTransformerHandler..." + xslID);
          TransformerHandler handler 
            = stfactory.newTransformerHandler(new StreamSource(xslID));

          // Set the result handling to be a serialization to the file output stream.
          Serializer serializer = SerializerFactory.getSerializer
                                  (OutputProperties.getDefaultMethodProperties("xml"));
          serializer.setOutputStream(new FileOutputStream(outNames.nextName()));
      
          
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
          reporter.logTraceMsg("reader.parse " + sourceID);
          reader.parse(sourceID);

          reporter.logTraceMsg("Note: I have not double-checked that we're comparing against the correct gold file! 14-Dec-00");
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(saxGoldName),
                          "exampleContentHandlerToContentHandler fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleContentHandlerToContentHandler threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleContentHandlerToContentHandler threw");
    }

  }
  
  /**
   * Show the Transformer as a SAX2 XMLReader.  An XMLFilter obtained 
   * from newXMLFilter should act as a transforming XMLReader if setParent is not
   * called.  Internally, an XMLReader is created as the parent for the XMLFilter.
   */
  public void exampleXMLReader(String sourceID, String xslID)
  {
    try
    {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        if(!tfactory.getFeature(SAXSource.FEATURE))
        {
            reporter.logErrorMsg("exampleXMLReader:Processor does not support SAX");
            return;
        }
          reporter.logTraceMsg("newXMLFilter..." + xslID);
          XMLReader reader 
            = ((SAXTransformerFactory) tfactory).newXMLFilter(new StreamSource(xslID));
          
          // Set the result handling to be a serialization to the file output stream.
          Serializer serializer = SerializerFactory.getSerializer
                                  (OutputProperties.getDefaultMethodProperties("xml"));
          serializer.setOutputStream(new FileOutputStream(outNames.nextName()));
      
          reader.setContentHandler(serializer.asContentHandler());

          reporter.logTraceMsg("reader.parse " + sourceID);
          reader.parse(new InputSource(sourceID));

        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(saxGoldName),
                          "exampleXMLReader fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleXMLReader threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleXMLReader threw");
    }

  }

  /**
   * Show the Transformer as a simple XMLFilter.  This is pretty similar
   * to exampleXMLReader, except that here the parent XMLReader is created 
   * by the caller, instead of automatically within the XMLFilter.  This 
   * gives the caller more direct control over the parent reader.
   */
  public void exampleXMLFilter(String sourceID, String xslID)
  {
    try
    {
        TransformerFactory tfactory = TransformerFactory.newInstance();

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

          // Set the result handling to be a serialization to the file output stream.
          Serializer serializer = SerializerFactory.getSerializer
                                  (OutputProperties.getDefaultMethodProperties("xml"));
          serializer.setOutputStream(new FileOutputStream(outNames.nextName()));
          reader.setContentHandler(serializer.asContentHandler());

        try
        {
          reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
                            true);
          reader.setFeature("http://apache.org/xml/features/validation/dynamic",
                            true);
        }
        catch (SAXException se)
        {
            reporter.logErrorMsg("exampleXMLFilter: reader threw :" + se.toString());
          // What can we do?
          // TODO: User diagnostics.
        }

        reporter.logTraceMsg("newXMLFilter..." + xslID);
        XMLFilter filter 
          = ((SAXTransformerFactory) tfactory).newXMLFilter(new StreamSource(xslID));

        filter.setParent(reader);

        // Now, when you call transformer.parse, it will set itself as 
        // the content handler for the parser object (it's "parent"), and 
        // will then call the parse method on the parser.
          reporter.logTraceMsg("filter.parse " + sourceID);
        filter.parse(new InputSource(sourceID));

        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(saxGoldName),
                          "exampleXMLFilter fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleXMLFilter threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleXMLFilter threw");
    }
  }

  /**
   * This example shows how to chain events from one Transformer
   * to another transformer, using the Transformer as a
   * SAX2 XMLFilter/XMLReader.
   */
  public void exampleXMLFilterChain(String sourceID, String xslID_1, 
                                    String xslID_2, String xslID_3)
  {
    try
    {
        TransformerFactory tfactory = TransformerFactory.newInstance();
    
        Templates stylesheet1 = tfactory.newTemplates(new StreamSource(xslID_1));
        Transformer transformer1 = stylesheet1.newTransformer();
    
         // If one success, assume all will succeed.
        if (!tfactory.getFeature(SAXSource.FEATURE))
        {
            reporter.logErrorMsg("exampleXMLFilterChain:Processor does not support SAX");
            return;
        }
          SAXTransformerFactory stf = (SAXTransformerFactory)tfactory;
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

          reporter.logTraceMsg("newXMLFilter..." + xslID_1);
          XMLFilter filter1 = stf.newXMLFilter(new StreamSource(xslID_1));
          reporter.logTraceMsg("newXMLFilter..." + xslID_2);
          XMLFilter filter2 = stf.newXMLFilter(new StreamSource(xslID_2));
          reporter.logTraceMsg("newXMLFilter..." + xslID_3);
          XMLFilter filter3 = stf.newXMLFilter(new StreamSource(xslID_3));

          if (null == filter1) // If one success, assume all were success.
          {
              reporter.checkFail("exampleXMLFilterChain: filter is null");
              return;
          }

            // transformer1 will use a SAX parser as it's reader.    
            filter1.setParent(reader);

            // transformer2 will use transformer1 as it's reader.
            filter2.setParent(filter1);

            // transform3 will use transform2 as it's reader.
            filter3.setParent(filter2);

          // Set the result handling to be a serialization to the file output stream.
          Serializer serializer = SerializerFactory.getSerializer
                                  (OutputProperties.getDefaultMethodProperties("xml"));
          serializer.setOutputStream(new FileOutputStream(outNames.nextName()));
          filter3.setContentHandler(serializer.asContentHandler());

            // Now, when you call transformer3 to parse, it will set  
            // itself as the ContentHandler for transform2, and 
            // call transform2.parse, which will set itself as the 
            // content handler for transform1, and call transform1.parse, 
            // which will set itself as the content listener for the 
            // SAX parser, and call parser.parse(new InputSource(fooFile.xmlName)).
          reporter.logTraceMsg("filter3.parse " + sourceID);
            filter3.parse(new InputSource(sourceID));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(NOT_DEFINED),
                          "exampleXMLFilterChain fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleXMLFilterChain threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleXMLFilterChain threw");
    }
  }

  /**
   * Show how to transform a DOM tree into another DOM tree.
   * This uses the javax.xml.parsers to parse an XML file into a
   * DOM, and create an output DOM.
   */
  public Node exampleDOM2DOM(String sourceID, String xslID)
  {
    try
    {
        TransformerFactory tfactory = TransformerFactory.newInstance();

        if (!tfactory.getFeature(DOMSource.FEATURE))
        {
            reporter.logErrorMsg("exampleDOM2DOM:Processor does not support SAX");
            return null;
        }

          Templates templates;

          {
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            org.w3c.dom.Document outNode = docBuilder.newDocument();
            Node doc = docBuilder.parse(new InputSource(xslID));
     
            DOMSource dsource = new DOMSource(doc);
            // If we don't do this, the transformer won't know how to 
            // resolve relative URLs in the stylesheet.
            dsource.setSystemId(xslID);

            templates = tfactory.newTemplates(dsource);
          }

          Transformer transformer = templates.newTransformer();
          DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
          org.w3c.dom.Document outNode = docBuilder.newDocument();
          Node doc = docBuilder.parse(new InputSource(sourceID));

          transformer.transform(new DOMSource(doc), new DOMResult(outNode));
          
          Transformer serializer = tfactory.newTransformer();
          serializer.transform(new DOMSource(outNode), new StreamResult(outNames.nextName()));

        reporter.logCriticalMsg("@todo TEST UPDATE validate this output:" + outNames.currentName());
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(NOT_DEFINED),    // @todo validate the output
                          "exampleDOM2DOM fileChecker of:" + outNames.currentName());
          return outNode;
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleDOM2DOM threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleDOM2DOM threw");
        return null;
        
    }
  } 

  /**
   * This shows how to set a parameter for use by the templates. Use 
   * two transformers to show that different parameters may be set 
   * on different transformers.
   */
  public void exampleParam(String sourceID, String xslID)
  {
    try
    {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Templates templates = tfactory.newTemplates(new StreamSource(xslID));
        Transformer transformer1 = templates.newTransformer();
        Transformer transformer2 = templates.newTransformer();

        transformer1.setParameter("a-param",
                                  "hello to you!");
        transformer1.transform(new StreamSource(sourceID),
                               new StreamResult(outNames.nextName()));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(param1GoldName),
                          "exampleParam(1) fileChecker of:" + outNames.currentName());
    
    
        transformer2.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer2.transform(new StreamSource(sourceID),
                               new StreamResult(outNames.nextName()));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(outputGoldName),
                          "exampleParam(2) fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleParam threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleParam threw");
    }
  }
  
  /**
   * Show the that a transformer can be reused, and show resetting 
   * a parameter on the transformer.
   */
  public void exampleTransformerReuse(String sourceID, String xslID)
  {
    try
    {
        // Create a transform factory instance.
        TransformerFactory tfactory = TransformerFactory.newInstance();
    
        // Create a transformer for the stylesheet.
        Transformer transformer 
          = tfactory.newTransformer(new StreamSource(xslID));
    
        transformer.setParameter("a-param",
                                  "hello to you!");
    
        // Transform the source XML to System.out.
        transformer.transform( new StreamSource(sourceID),
                               new StreamResult(outNames.nextName()));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(param1GoldName),
                          "exampleTransformerReuse(1) fileChecker of:" + outNames.currentName());

        transformer.setParameter("a-param",
                                  "hello to me!");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        // Transform the source XML to System.out.
        transformer.transform( new StreamSource(sourceID),
                               new StreamResult(outNames.nextName()));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(param2GoldName),
                          "exampleTransformerReuse(2) fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleTransformerReuse threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleTransformerReuse threw");
    }
  }

  /**
   * Show how to override output properties.
   */
  public void exampleOutputProperties(String sourceID, String xslID)
  {
    try
    {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Templates templates = tfactory.newTemplates(new StreamSource(xslID));
        Properties oprops = templates.getOutputProperties();

        oprops.put(OutputKeys.INDENT, "yes");

        Transformer transformer = templates.newTransformer();

        transformer.setOutputProperties(oprops);
        transformer.transform(new StreamSource(sourceID),
                               new StreamResult(outNames.nextName()));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(outputGoldName),
                          "exampleOutputProperties fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleOutputProperties threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleOutputProperties threw");
    }
  }

  /**
   * Show how to get stylesheets that are associated with a given
   * xml document via the xml-stylesheet PI (see http://www.w3.org/TR/xml-stylesheet/).
   */
  public void exampleUseAssociated(String sourceID)
  {
    try
    {
        TransformerFactory tfactory = TransformerFactory.newInstance();

        // The DOM tfactory will have it's own way, based on DOM2, 
        // of getting associated stylesheets.
        if (!(tfactory instanceof SAXTransformerFactory))
        {
            reporter.logErrorMsg("exampleUseAssociated:Processor does not support SAX");
            return;
        }
          SAXTransformerFactory stf = ((SAXTransformerFactory) tfactory);
          Source sources =
            stf.getAssociatedStylesheet(new StreamSource(sourceID),
              null, null, null);

          if(null == sources)
          {
            reporter.checkFail("exampleUseAssociated:problem with source objects");
            return;
          }
            Transformer transformer = tfactory.newTransformer(sources);

            transformer.transform(new StreamSource(sourceID),
                               new StreamResult(outNames.nextName()));
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(fooFile.goldName),
                          "exampleUseAssociated fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleUseAssociated threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleUseAssociated threw");
    }
  }
  
  /**
   * Show the Transformer using SAX events in and DOM nodes out.
   */
  public void exampleContentHandler2DOM(String sourceID, String xslID)
  {
    try
    {
        TransformerFactory tfactory = TransformerFactory.newInstance();

        // Make sure the transformer factory we obtained supports both
        // DOM and SAX.
        if (!(tfactory.getFeature(SAXSource.FEATURE)
            && tfactory.getFeature(DOMSource.FEATURE)))
        {
            reporter.logErrorMsg("exampleContentHandler2DOM:Processor does not support SAX/DOM");
            return;
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
          TransformerHandler handler 
            = sfactory.newTransformerHandler(new StreamSource(xslID));
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
          reader.parse(sourceID);
          
          // Serialize the node for diagnosis.
          //    This serializes to outNames.nextName()
          exampleSerializeNode(outNode);

        reporter.logCriticalMsg("@todo TEST UPDATE validate this output:" + outNames.currentName());
        // @todo TEST UPDATE validate this output
        //  Note: 05-Dec-00 output seems to be bad: has 
        //  duplicate xmlns:foo="http://apache.org/foo" decls 
        // in the foo:document element!
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(NOT_DEFINED),
                          "exampleContentHandler2DOM fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleContentHandler2DOM threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleContentHandler2DOM threw");
    }
  }
  
  /**
   * Serialize a node to System.out.
   */
  public void exampleSerializeNode(Node node)
    throws TransformerException, TransformerConfigurationException, SAXException, IOException,
    ParserConfigurationException
  {
    TransformerFactory tfactory = TransformerFactory.newInstance(); 
    
    // This creates a transformer that does a simple identity transform, 
    // and thus can be used for all intents and purposes as a serializer.
    Transformer serializer = tfactory.newTransformer();
    
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    serializer.transform(new DOMSource(node), 
                         new StreamResult(outNames.nextName()));
                    // TEST UPDATE - Caller must validate outNames.currentName()
  }  
  
  /**
   * A fuller example showing how the TrAX interface can be used 
   * to serialize a DOM tree.
   */
  public void exampleAsSerializer(String sourceID, String xslID)
  {
    try
    {
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
        org.w3c.dom.Document outNode = docBuilder.newDocument();
        Node doc = docBuilder.parse(new InputSource(sourceID));

        TransformerFactory tfactory = TransformerFactory.newInstance(); 
    
        // This creates a transformer that does a simple identity transform, 
        // and thus can be used for all intents and purposes as a serializer.
        Transformer serializer = tfactory.newTransformer();
    
        Properties oprops = new Properties();
        oprops.put("method", "html");
        oprops.put("indent-amount", "2");
        serializer.setOutputProperties(oprops);
        serializer.transform(new DOMSource(doc), 
                             new StreamResult(outNames.nextName()));
        reporter.logCriticalMsg("@todo TEST UPDATE validate this output:" + outNames.currentName());
        fileChecker.check(reporter, new File(outNames.currentName()),
                          new File(NOT_DEFINED),
                          "exampleAsSerializer fileChecker of:" + outNames.currentName());
    } 
    catch (Throwable t)
    {
        reporter.checkFail("exampleAsSerializer threw: " + t.toString());
        reporter.logThrowable(reporter.ERRORMSG, t, "exampleAsSerializer threw");
    }
  }
  




    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by ExamplesTest:\n"
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

        ExamplesTest app = new ExamplesTest();

        app.doMain(args);
    }
}
