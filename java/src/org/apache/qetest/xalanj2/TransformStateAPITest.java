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
 * TransformStateAPITest.java
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

// Needed Xalan-J 2.x specific classes
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformState;
import org.apache.xalan.transformer.TransformerClient;
import org.apache.xpath.XPath;
import org.apache.xml.utils.QName;

// Needed SAX, DOM, JAXP classes
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

// java classes
import java.io.File;
import java.util.Hashtable;
import java.util.Properties;
import java.lang.reflect.Method;

//-------------------------------------------------------------------------

/**
 * API coverage testing of TransformState interface.
 * Currently this focuses on dumping debug information about 
 * a TransformState while transforming several different 
 * stylesheets; manual validation of the output results 
 * to see what was traced) is expected.
 * Future work will add basic validation of 
 * various ExpectedTransformState objects, which will be 
 * keyed off of the ContentHandler event and the line/column 
 * information of the pertinent element or template. 
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class TransformStateAPITest extends XSLProcessorTestBase
        implements ContentHandler, TransformerClient

{
    /** Provides nextName(), currentName() functionality.  */
    protected OutputNameManager outNames;

    /** Identity transform - simple test.  */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();

    /**  RootTemplate: simple stylesheet with xsl:template select="/".  */
    protected XSLTestfileInfo testFileInfo2 = new XSLTestfileInfo();

    /**  Another simple test for manual debugging.  */
    protected XSLTestfileInfo testFileInfo3 = new XSLTestfileInfo();

    /**  Another simple test for manual debugging.  */
    protected XSLTestfileInfo testFileInfo4 = new XSLTestfileInfo();

    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String X2J_SUBDIR = "xalanj2";

    /** Level that various TransformState logging should use.  */
    protected int traceLoggingLevel = Logger.INFOMSG - 1;

    /** Just initialize test name, comment, numTestCases. */
    public TransformStateAPITest()
    {
        numTestCases = 4;  // REPLACE_num
        testName = "TransformStateAPITest";
        testComment = "API coverage testing of TransformState interface";
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

        testFileInfo.inputName = testBasePath + "identity.xsl";
        testFileInfo.xmlName = testBasePath + "identity.xml";

        testFileInfo3.inputName = testBasePath + "RootTemplate.xsl";
        testFileInfo3.xmlName = testBasePath + "RootTemplate.xml";

        testFileInfo2.inputName = testBasePath + "TransformStateAPITest.xsl";
        testFileInfo2.xmlName = testBasePath + "TransformStateAPITest.xml";

        testFileInfo4.inputName = testBasePath + "URIResolverTest.xsl";
        testFileInfo4.xmlName = testBasePath + "URIResolverTest.xml";

        return true;
    }


    /**
     * Quick smoketest of TransformState.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Quick smoketest of TransformState");
        reporter.logWarningMsg("Note: limited validation: partly just a crash test so far.");
        doTransform(QetestUtils.filenameToURL(testFileInfo.inputName), 
                    QetestUtils.filenameToURL(testFileInfo.xmlName), 
                    null);

        //@todo: add specific validation for selected trace elements in specific stylesheets
        reporter.checkPass("Crash test: we haven't crashed yet!");
        reporter.testCaseClose();
        return true;
    }

    /**
     * Quick smoketest of TransformState.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Quick smoketest of TransformState");
        reporter.logWarningMsg("Note: limited validation: partly just a crash test so far.");
        doTransform(QetestUtils.filenameToURL(testFileInfo2.inputName), 
                    QetestUtils.filenameToURL(testFileInfo2.xmlName), 
                    null);

        //@todo: add specific validation for selected trace elements in specific stylesheets
        reporter.checkPass("Crash test: we haven't crashed yet!");
        reporter.testCaseClose();
        return true;
    }

    /**
     * Quick smoketest of TransformState.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase3()
    {
        reporter.testCaseInit("Quick smoketest of TransformState");
        reporter.logWarningMsg("Note: limited validation: partly just a crash test so far.");
        doTransform(QetestUtils.filenameToURL(testFileInfo3.inputName), 
                    QetestUtils.filenameToURL(testFileInfo3.xmlName), 
                    null);

        //@todo: add specific validation for selected trace elements in specific stylesheets
        reporter.checkPass("Crash test: we haven't crashed yet!");
        reporter.testCaseClose();
        return true;
    }

    /**
     * Quick smoketest of TransformState.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase4()
    {
        reporter.testCaseInit("Quick smoketest of TransformState");
        reporter.logWarningMsg("Note: limited validation: partly just a crash test so far.");
        doTransform(QetestUtils.filenameToURL(testFileInfo4.inputName), 
                    QetestUtils.filenameToURL(testFileInfo4.xmlName), 
                    null);

        //@todo: add specific validation for selected trace elements in specific stylesheets
        reporter.checkPass("Crash test: we haven't crashed yet!");
        reporter.testCaseClose();
        return true;
    }

    /** Cheap-o worker method to do transform with us as output handler.  */
    protected void doTransform(String inputURL, String xmlURL, String options)
    {
        try
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            reporter.logInfoMsg("---- doTransform:" + options); // options otherwise currently unused
            reporter.logTraceMsg("---- About to newTransformer " + inputURL);
            Transformer transformer = factory.newTransformer(new StreamSource(inputURL));
            reporter.logTraceMsg("---- About to transform " + xmlURL + " into: SAXResult(this-no disk output)");
            transformer.transform(new StreamSource(xmlURL),
                                  new SAXResult(this)); // use us to handle result
            reporter.logInfoMsg("---- Afterwards, this.transformState=" + transformState);
            transformState = null; // just in case
            
        }
        catch (TransformerException te)
        {
            reporter.logThrowable(Logger.ERRORMSG, te, "doTransform threw: ");
            reporter.checkFail("doTransform threw: " + te.toString());
        }
    }

    ////////////////// partially Implement LoggingHandler ////////////////// 
    /** Cheap-o string representation of last event we got.  */
    protected String lastItem = LoggingHandler.NOTHING_HANDLED;


    /**
     * Accessor for string representation of last event we got.  
     * @param s string to set
     */
    protected void setLastItem(String s)
    {
        lastItem = s;
    }


    /**
     * Accessor for string representation of last event we got.  
     * @return last event string we had
     */
    public String getLast()
    {
        return lastItem;
    }

    /** 
     * Worker routine to validate a TransformState based on an event/value.  
     * Note: this may not be threadsafe!
     * //@todo actually add validation code - just logs out now
     * @param ts TransformState to validate, if null, just logs it
     * @param event our String constant of START_ELEMENT, etc.
     * @param value any String value of the current event 
     */
    protected void validateTransformState(TransformState ts, String event, String value)
    {
        if(null == transformState)
        {
            reporter.logTraceMsg("validateTransformState(ts-NULL!, " + event + ")=" + value);
            return;
        }
        reporter.logTraceMsg("validateTransformState(" + event + ")=" + value);
        logTransformStateDump(reporter, traceLoggingLevel, ts, event);
        //@todo: implement validation service for this stuff
        //  focus on what tooling/debugging clients will want to see
    }


    //-----------------------------------------------------------
    //---- Implement the TransformerClient interface
    //-----------------------------------------------------------
    /**
     * A TransformState object that we use to log state data.
     * This is the equivalent of the defaultHandler, even though 
     * that's not really the right metaphor.  This class could be 
     * upgraded to have both a default ContentHandler and a 
     * defaultTransformerClient in the future.
     */
    protected TransformState transformState = null;


    /**
     * Implement TransformerClient.setTransformState interface.  
     * Pass in a reference to a TransformState object, which
     * can be used during SAX ContentHandler events to obtain
     * information about he state of the transformation. This
     * method will be called before each startDocument event.
     *
     * @param ts A reference to a TransformState object
     */
    public void setTransformState(TransformState ts)
    {
        transformState = ts;
    }

    ////////////////// Utility methods for TransformState ////////////////// 
    /**
     * Utility method to dump data from TransformState.  
     * @return String describing various bits of the state
     */
    protected void logTransformStateDump(Reporter reporter, int traceLoggingLevel, 
            TransformState ts, String event)
    {
        String elemName = "transformStateDump";
        Hashtable attrs = new Hashtable();
        attrs.put("event", event);
        attrs.put("location", "L" + ts.getCurrentTemplate().getLineNumber()
                  + "C" + ts.getCurrentTemplate().getColumnNumber());

        StringBuffer buf = new StringBuffer();
        ElemTemplateElement elem = ts.getCurrentElement(); // may be actual or default template
        buf.append("  <currentElement>" 
                + XMLFileLogger.escapeString(XalanDumper.dump(elem, XalanDumper.DUMP_DEFAULT)) + "</currentElement>\n");

        ElemTemplate currentTempl = ts.getCurrentTemplate(); // Actual current template
        buf.append("  <currentTemplate>" 
                + XMLFileLogger.escapeString(XalanDumper.dump(currentTempl, XalanDumper.DUMP_DEFAULT)) + "</currentTemplate>\n");

        ElemTemplate matchTempl = ts.getMatchedTemplate(); // Actual matched template
        if (matchTempl != currentTempl)
            buf.append("  <matchedTemplate>" 
                + XMLFileLogger.escapeString(XalanDumper.dump(matchTempl, XalanDumper.DUMP_DEFAULT)) + "</matchedTemplate>\n");

        Node n = ts.getCurrentNode();   // current context node in source tree
        buf.append("  <currentNode>" 
                + XMLFileLogger.escapeString(XalanDumper.dump(n, XalanDumper.DUMP_DEFAULT)) + "</currentNode>\n");

        Node matchedNode = ts.getMatchedNode(); // node in source matched via getMatchedTemplate
        buf.append("  <matchedNode>" 
                + XMLFileLogger.escapeString(XalanDumper.dump(matchedNode, XalanDumper.DUMP_DEFAULT)) + "</matchedNode>\n");

        NodeIterator contextNodeList = ts.getContextNodeList(); // current context node list
        Node rootNode = contextNodeList.getRoot();
        buf.append("  <contextNodeListGetRoot>" 
                + XMLFileLogger.escapeString(XalanDumper.dump(rootNode, XalanDumper.DUMP_DEFAULT)) + "</contextNodeListGetRoot>\n");

        // Skip dumping Transformer info until we actually do something with it
        // Transformer transformer = ts.getTransformer(); // current transformer working
        // buf.append("getTransformer:" + transformer + "\n"); // TBD

        reporter.logElement(traceLoggingLevel, elemName, attrs, buf.toString());
    }


    //-----------------------------------------------------------
    //---- Implement the ContentHandler interface
    //-----------------------------------------------------------
    protected final String START_ELEMENT = "startElement:";
    protected final String END_ELEMENT = "endElement:";
    protected final String CHARACTERS = "characters:";

    // String Locator.getPublicId() null if none available
    // String Locator.getPublicId() null if none available
    // int Locator.getLineNumber() -1 if none available
    // int Locator.getColumnNumber() -1 if none available
    protected Locator ourLocator = null;
    
    /** 
     * Implement ContentHandler.setDocumentLocator.  
     * If available, this should always be called prior to a 
     * startDocument event.
     */
    public void setDocumentLocator (Locator locator)
    {
        // Note: this implies this class is !not! threadsafe
        ourLocator = locator; // future use
        if (null != locator)
            setLastItem("setDocumentLocator.getSystemId():" + locator.getSystemId());
        else
            setLastItem("setDocumentLocator:NULL");
        reporter.logMsg(traceLoggingLevel, getLast());
    }


    /** Cached TransformState object during lifetime startDocument -> endDocument.  */
    // Note: is this correct? Will it always be the same object?
    protected TransformState docCachedTransformState = null;
    /** Implement ContentHandler.startDocument.  */
    public void startDocument ()
        throws SAXException
    {
        setLastItem("startDocument");
        reporter.logMsg(traceLoggingLevel, getLast());
        // Comment out check call since the spec'd functionality
        //  is very likely to change to *not* be in startDocument 19-Jun-01 -sc 
        // reporter.check((null != transformState), true, "transformState non-null in startDocument");
        reporter.logStatusMsg("transformState in startDocument is: " + transformState);
        docCachedTransformState = transformState; // see endDocument
    }


    /** Implement ContentHandler.endDocument.  */
    public void endDocument()
        throws SAXException
    {
        setLastItem("endDocument");
        reporter.logMsg(traceLoggingLevel, getLast());
        // Comment out check call since the spec'd functionality
        //  is very likely to change to *not* be in startDocument 19-Jun-01 -sc 
        // reporter.checkObject(docCachedTransformState, transformState, 
        //               "transformState same in endDocument as startDocument"); // see startDocument
        reporter.logStatusMsg("transformState in endDocument is: " + transformState);
        docCachedTransformState = null;
    }


    /** Implement ContentHandler.startPrefixMapping.  */
    public void startPrefixMapping (String prefix, String uri)
        throws SAXException
    {
        setLastItem("startPrefixMapping: " + prefix + ", " + uri);
        reporter.logMsg(traceLoggingLevel, getLast());
    }


    /** Implement ContentHandler.endPrefixMapping.  */
    public void endPrefixMapping (String prefix)
        throws SAXException
    {
        setLastItem("endPrefixMapping: " + prefix);
        reporter.logMsg(traceLoggingLevel, getLast());
    }


    /** Implement ContentHandler.startElement.  */
    public void startElement (String namespaceURI, String localName,
                              String qName, Attributes atts)
        throws SAXException
    {
        StringBuffer buf = new StringBuffer();
        buf.append(namespaceURI + ", " 
                   + localName + ", " + qName + ";");
                   
        int n = atts.getLength();
        for(int i = 0; i < n; i++)
        {
            buf.append(", " + atts.getQName(i));
        }
        setLastItem(START_ELEMENT + buf.toString());

        validateTransformState(transformState, START_ELEMENT, buf.toString());
    }


    /** Implement ContentHandler.endElement.  */
    public void endElement (String namespaceURI, String localName, String qName)
        throws SAXException
    {
        setLastItem(END_ELEMENT + namespaceURI + ", " + localName + ", " + qName);

        validateTransformState(transformState, END_ELEMENT, null);
    }


    /** Implement ContentHandler.characters.  */
    public void characters (char ch[], int start, int length)
        throws SAXException
    {
        String s = new String(ch, start, length);
        setLastItem(CHARACTERS + "\"" + s + "\"");

        validateTransformState(transformState, CHARACTERS, s);
    }


    /** Implement ContentHandler.ignorableWhitespace.  */
    public void ignorableWhitespace (char ch[], int start, int length)
        throws SAXException
    {
        setLastItem("ignorableWhitespace: len " + length);
        reporter.logMsg(traceLoggingLevel, getLast());
    }


    /** Implement ContentHandler.processingInstruction.  */
    public void processingInstruction (String target, String data)
        throws SAXException
    {
        setLastItem("processingInstruction: " + target + ", " + data);
        reporter.logMsg(traceLoggingLevel, getLast());
    }


    /** Implement ContentHandler.skippedEntity.  */
    public void skippedEntity (String name)
        throws SAXException
    {
        setLastItem("skippedEntity: " + name);
        reporter.logMsg(traceLoggingLevel, getLast());
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
        return ("Common [optional] options supported by TransformStateAPITest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        TransformStateAPITest app = new TransformStateAPITest();
        app.doMain(args);
    }
}
