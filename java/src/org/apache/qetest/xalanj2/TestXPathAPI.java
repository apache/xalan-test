/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
// This file uses 4 space indents, no tabs.

/*
 *
 * TestXPathAPI.java
 *
 */
package org.apache.qetest.xalanj2;

// import common testing framework stuff
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// java classes
import java.io.File;
import java.util.Hashtable;
import java.util.Properties;
import java.lang.reflect.Method;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.DOMBuilder;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.objects.XObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

// Imported JAVA API for XML Parsing 1.0 classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException; 

// Imported Serializer classes
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

/**
 *  Very basic utility for applying an XPath epxression to an xml file and printing information
 /  about the execution of the XPath object and the nodes it finds.
 *  Takes 2 arguments:
 *     (1) an xml filename
 *     (2) an XPath expression to apply to the file
 *  Examples:
 *     java ApplyXPath foo.xml /
 *     java ApplyXPath foo.xml /doc/name[1]/@last
 * @see XPathAPI
 */
public class TestXPathAPI extends XSLProcessorTestBase
{
  protected String filename = null;
  protected String[] xpath;
  
  protected XSLTestfileInfo testFileInfo1 = new XSLTestfileInfo();
  protected XSLTestfileInfo testFileInfo4 = new XSLTestfileInfo();
  protected XSLTestfileInfo testFileInfo2 = new XSLTestfileInfo();
  protected XSLTestfileInfo testFileInfo3 = new XSLTestfileInfo();

  /** Provides nextName(), currentName() functionality.  */
  protected OutputNameManager outNames; 

  /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String X2J_SUBDIR = "xalanj2";

    /** Level that various TransformState logging should use.  */
    protected int traceLoggingLevel = Logger.INFOMSG - 1;
    
    private int numxpath;

    /** Just initialize test name, comment, numTestCases. */
    public TestXPathAPI()
    {
        numTestCases = 4;  // REPLACE_num
        testName = "XPathAPITest";
        testComment = "API coverage testing of XPathAPI";
    }
    
    /**
     * Initialize this test - Set names of xml/xsl test files etc.
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        // NOTE: 'reporter' variable is already initialized at this point

        // Used for all tests; just dump files in trax subdir
        File outSubDir = new File(outputDir + File.separator + X2J_SUBDIR);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + X2J_SUBDIR
                                         + File.separator + testName, ".out");
       
        String testBasePath = inputDir 
                              + File.separator 
                              + X2J_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator 
                              + X2J_SUBDIR
                              + File.separator;

        testFileInfo1.xmlName = testBasePath + "testXPath.xml";
        testFileInfo1.goldName = goldBasePath;

        testFileInfo3.xmlName = testBasePath + "testXPath.xml";
        testFileInfo3.goldName = goldBasePath;

        testFileInfo2.xmlName = testBasePath + "testXPath.xml";
        testFileInfo2.goldName = goldBasePath;

        //testFileInfo4.inputName = testBasePath + "test2.xsl";
        testFileInfo4.xmlName = testBasePath + "testXPath.xml";
        testFileInfo4.goldName = goldBasePath;
        
        numxpath = 25;
        xpath = new String[numxpath];
        xpath[0] = "/doc/a/@test";
        xpath[1] = "//."; 
        xpath[2] = "/doc"; 
        xpath[3] = "/doc/a"; 
        xpath[4] = "//@*";
        xpath[5] = ".";
        xpath[6] = "//ancestor-or-self::*";
        xpath[7] = "./child::*[1]";
        xpath[8] = "//descendant-or-self::*/@*[1]";
        xpath[9] = "//@* | * | node()";
        xpath[10] = "//*";
        xpath[11] = "/doc/namespace::*";
        xpath[12] = "//descendant::comment()";        
        xpath[13] = "//*[local-name()='a']";
        xpath[14] = "//*[current()]/@*";
        xpath[15] = "//*[last()]";
        xpath[16] = "doc/*[last()]";
        xpath[17] = "/doc/a/*[current()]/@*";
        xpath[18] = "doc/descendant::node()";
        xpath[19] = "doc/a/@*";
        xpath[20] = "doc/b/a/ancestor-or-self::*";
        xpath[21] = "doc/b/a/preceding::*";
        xpath[22] = "doc/a/following::*";
        xpath[23] = "/doc/b/preceding-sibling::*";
        xpath[24] = "/doc/a/following-sibling::*";

        return true;
    }


  
  /** Process input args and execute the XPath.  */
  public boolean testCase1()
    throws Exception
  {        
    filename = testFileInfo1.xmlName;

    if ((filename != null) && (filename.length() > 0)
        && (xpath != null) && (xpath.length > 0))
    {
      reporter.testCaseInit("Quick smoketest of XPathAPI");
        
      // Tell that we're loading classes and parsing, so the time it 
      // takes to do this doesn't get confused with the time to do 
      // the actual query and serialization.
      reporter.logInfoMsg("Loading classes, parsing "+filename);
      
      // Set up a DOM tree to query.
      InputSource in = new InputSource(new FileInputStream(filename));
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      Document doc = dfactory.newDocumentBuilder().parse(in);
      
      // Set up an identity transformer to use as serializer.
      Transformer serializer = TransformerFactory.newInstance().newTransformer();
      serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      
      for (int i=0;i<numxpath; i++) 
      {
        // Use the simple XPath API to select a nodeIterator.
        reporter.logWarningMsg("Querying DOM using "+xpath[i]);
        NodeIterator nl = XPathAPI.selectNodeIterator(doc, xpath[i]);
        
        // Serialize the found nodes to System.out.
        
        Node n;
        
        while ((n = nl.nextNode())!= null)
        { 
          serializer.transform(new DOMSource(n), new StreamResult(outNames.nextName()));
          File f = new File(outNames.currentName()); 
          int result = fileChecker.check(reporter, 
                                           f, 
                                           new File(testFileInfo1.goldName + f.getName()), 
                                          "(1)transform into " + outNames.currentName());
          if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("(1)TestXPathAPI failure reason:" + fileChecker.getExtendedInfo());

          reporter.logTraceMsg(outNames.currentName() + n);
        }
      }
      reporter.testCaseClose();
    }
    else
    {
      reporter.logWarningMsg("Bad input args: " + filename + ", " + xpath);
    }
    
    return true;
   
  }
  
   /** Process input args and execute the XPath.  */
  public boolean testCase2()
    throws Exception
  {        
    filename = testFileInfo1.xmlName;

    if ((filename != null) && (filename.length() > 0)
        && (xpath != null) && (xpath.length > 0))
    {
       reporter.testCaseInit("Quick smoketest of XPathAPI");
      // Tell that we're loading classes and parsing, so the time it 
      // takes to do this doesn't get confused with the time to do 
      // the actual query and serialization.
      reporter.logInfoMsg("Loading classes, parsing "+filename);
      
      // Set up a DOM tree to query.
      InputSource in = new InputSource(new FileInputStream(filename));
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      Document doc = dfactory.newDocumentBuilder().parse(in);
      
      // Set up an identity transformer to use as serializer.
      Transformer serializer = TransformerFactory.newInstance().newTransformer();
      serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

      for (int i=0;i<numxpath; i++) 
      {
        // Use the simple XPath API to select a nodeIterator.
        System.out.println("Querying DOM using "+xpath[i]);
        NodeList nl = XPathAPI.selectNodeList(doc, xpath[i]);
        
        // Serialize the found nodes to System.out.
        
        Node n;
        int j = 0;
        while (j < nl.getLength())
        {        
          n = nl.item(j++);
          serializer.transform(new DOMSource(n), new StreamResult(outNames.nextName()));
          File f = new File(outNames.currentName()); 
          int result = fileChecker.check(reporter, 
                                           f, 
                                           new File(testFileInfo2.goldName + f.getName()), 
                                          "(1)transform into " + outNames.currentName());
          if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("(1)TestXPathAPI failure reason:" + fileChecker.getExtendedInfo());

          reporter.logTraceMsg(outNames.currentName());
        }
      }
      reporter.testCaseClose();
    }
    else
    {
      reporter.logWarningMsg("Bad input args: " + filename + ", " + xpath);
    }
    
    return true;
    
  }
  
   /** Process input args and execute the XPath.  */
  public boolean testCase3()
    throws Exception
  {        
    filename = testFileInfo1.xmlName;

    if ((filename != null) && (filename.length() > 0)
        && (xpath != null) && (xpath.length > 0))
    {
       reporter.testCaseInit("Quick smoketest of XPathAPI");
      // Tell that we're loading classes and parsing, so the time it 
      // takes to do this doesn't get confused with the time to do 
      // the actual query and serialization.
      reporter.logInfoMsg("Loading classes, parsing "+filename);
      
      // Set up a DOM tree to query.
      InputSource in = new InputSource(new FileInputStream(filename));
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      Document doc = dfactory.newDocumentBuilder().parse(in);
      
      // Set up an identity transformer to use as serializer.
      Transformer serializer = TransformerFactory.newInstance().newTransformer();
      serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      
      for (int i=0;i<numxpath; i++) 
      {
        // Use the simple XPath API to select a nodeIterator.
        reporter.logWarningMsg("Querying DOM using "+xpath[i]);
        Node n = XPathAPI.selectSingleNode(doc, xpath[i]);
        
        // Serialize the found nodes to System.out.
        if (n != null)
        {
          serializer.transform(new DOMSource(n), new StreamResult(outNames.nextName()));
          File f = new File(outNames.currentName()); 
          int result = fileChecker.check(reporter, 
                                           f, 
                                           new File(testFileInfo3.goldName + f.getName()), 
                                          "(1)transform into " + outNames.currentName());
          if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("(1)TestXPathAPI failure reason:" + fileChecker.getExtendedInfo());

          reporter.logTraceMsg(outNames.currentName());
        }
      }
      
       reporter.testCaseClose();
    }
    else
    {
      reporter.logWarningMsg("Bad input args: " + filename + ", " + xpath);
    }
    
    return true;
   
  }
  
  
   /** Process input args and execute the XPath.  */
  public boolean testCase4()
    throws Exception
  {        
    filename = testFileInfo1.xmlName;

    if ((filename != null) && (filename.length() > 0)
        && (xpath != null) && (xpath.length > 0))
    {
       reporter.testCaseInit("Quick smoketest of XPathAPI");
      // Tell that we're loading classes and parsing, so the time it 
      // takes to do this doesn't get confused with the time to do 
      // the actual query and serialization.
      reporter.logInfoMsg("Loading classes, parsing "+filename);
      
      // Set up a DOM tree to query.
      InputSource in = new InputSource(new FileInputStream(filename));
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      Document doc = dfactory.newDocumentBuilder().parse(in);
      
      // Set up an identity transformer to use as serializer.
      Transformer serializer = TransformerFactory.newInstance().newTransformer();
      serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      
      //Document d1 =(Document) doc.getDocumentElement();
     // Node d = (doc.getNodeType() == Node.DOCUMENT_NODE)
     // ? (Document) doc.getDocumentElement() : doc;
      PrefixResolverDefault prefixResolver = 
                 new PrefixResolverDefault(doc.getDocumentElement());

      for (int i=0;i<numxpath; i++) 
      {
        // Use the simple XPath API to select a nodeIterator.
        reporter.logWarningMsg("Querying DOM using "+xpath[i]);
        XObject list = XPathAPI.eval(doc, xpath[i], prefixResolver);
        
        // Serialize the found nodes to System.out.
        int n;
        
        DTMIterator nl = list.iter();
        DTMManager dtmManager = nl.getDTMManager();
        
        while ((n = nl.nextNode())!= DTM.NULL)
        { 
          Node node = dtmManager.getDTM(n).getNode(n);
          serializer.transform(new DOMSource(node), new StreamResult(outNames.nextName()));
          File f = new File(outNames.currentName()); 
          int result = fileChecker.check(reporter, 
                                           f, 
                                           new File(testFileInfo4.goldName + f.getName()), 
                                          "(1)transform into " + outNames.currentName());
          if (result == Logger.FAIL_RESULT)
                reporter.logInfoMsg("(1)TestXPathAPI failure reason:" + fileChecker.getExtendedInfo());

          reporter.logTraceMsg(outNames.currentName());
        }
        
        
      }
       reporter.testCaseClose();
    }
    else
    {
      reporter.logWarningMsg("Bad input args: " + filename + ", " + xpath);
    }
   
    return true;
  }
  
   //-----------------------------------------------------------
  //---- Basic XSLProcessorTestBase utility methods
  //-----------------------------------------------------------
  /**
   * Convenience method to print out usage information - update if needed.  
   * @return String denoting usage of this test class
   */
  public String usage()
  {
    return ("Common [optional] options supported by TestXPathAPI:\n"
            + "(Note: assumes inputDir=.\\tests\\api)\n"
            + super.usage());   // Grab our parent classes usage as well
  }
  
  
  /** Main method to run from the command line.    */
  public static void main (String[] args)
  {
    TestXPathAPI app = new TestXPathAPI();
    app.doMain(args);
  }	
  
} // end of class ApplyXPath

