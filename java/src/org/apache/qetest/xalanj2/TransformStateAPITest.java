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
import java.util.Properties;
import java.lang.reflect.Method;

//-------------------------------------------------------------------------

/**
 * API coverage testing of TransformState interface.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class TransformStateAPITest extends XSLProcessorTestBase
        implements ContentHandler, TransformerClient

{
    /**
     * Provides nextName(), currentName() functionality for tests 
     * that may produce any number of output files.
     */
    protected OutputNameManager outNames;

    /** 
     * Identity transform - simple test.  
     */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();

    /** 
     * Another simple test.  
     */
    protected XSLTestfileInfo testFileInfo2 = new XSLTestfileInfo();


    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String X2J_SUBDIR = "xalanj2";

    /** Level that various TransformState logging should use.  */
    protected int traceLoggingLevel = Logger.INFOMSG;

    /** Just initialize test name, comment, numTestCases. */
    public TransformStateAPITest()
    {
        numTestCases = 1;  // REPLACE_num
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
        testFileInfo.goldName = goldBasePath + "identity.out";
        testFileInfo2.inputName = testBasePath + "URIResolverTest.xsl";
        testFileInfo2.xmlName = testBasePath + "URIResolverTest.xml";
        testFileInfo2.goldName = goldBasePath + "URIResolverTest.out";

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

        try
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(QetestUtils.filenameToURL(testFileInfo.inputName)));
            reporter.logWarningMsg("---- About to process " + QetestUtils.filenameToURL(testFileInfo.inputName));
            transformer.transform(new StreamSource(QetestUtils.filenameToURL(testFileInfo.xmlName)),
                                  new SAXResult(this)); // use us to handle result
            Transformer transformer2 = factory.newTransformer(new StreamSource(QetestUtils.filenameToURL(testFileInfo2.inputName)));
            reporter.logWarningMsg("---- About to process " + QetestUtils.filenameToURL(testFileInfo2.inputName));
            transformer2.transform(new StreamSource(QetestUtils.filenameToURL(testFileInfo2.xmlName)),
                                  new SAXResult(this)); // use us to handle result
            //@todo: add specific validation for selected trace elements in specific stylesheets
        }
        catch (Throwable t)
        {
            reporter.checkFail("testCase1 threw: " + t.toString());
            reporter.logThrowable(Logger.ERRORMSG, t, "testCase1 threw: ");
        }

        reporter.testCaseClose();
        return true;
    }


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

    protected void validateTransformState(TransformState ts, String event, String value)
    {
        if(null == transformState)
        {
            reporter.logTraceMsg("TS null for:" + event + value);
            return;
        }
        // Experimenting: which info is reported where? What's most useful to validate on?
        reporter.logMsg(traceLoggingLevel, event 
                     + getCurrentElementInfo(ts) + " is processing: " 
                     + getCurrentNodeInfo(ts, value));
        reporter.logArbitrary(traceLoggingLevel, event 
                     + getTransformStateDump(ts));
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
    protected String getTransformStateDump(TransformState ts)
    {
        StringBuffer buf = new StringBuffer();
        ElemTemplateElement elem = ts.getCurrentElement(); // may be actual or default template
        buf.append("getCurrentElement:" + getElemTemplateElementDump(elem) + "\n");

        ElemTemplate currentTempl = ts.getCurrentTemplate(); // Actual current template
        buf.append("getCurrentTemplate:" + getElemTemplateDump(currentTempl) + "\n");

        ElemTemplate matchTempl = ts.getMatchedTemplate(); // Actual matched template
        buf.append("getMatchedTemplate:" + getElemTemplateDump(matchTempl) + "\n");

        Node n = ts.getCurrentNode();   // current context node in source tree
        buf.append("getCurrentNode:" + n + "\n"); // TBD

        Node matchedNode = ts.getMatchedNode(); // node in source matched via getMatchedTemplate
        buf.append("getMatchedNode:" + matchedNode + "\n"); // TBD

        NodeIterator contextNodeList = ts.getContextNodeList(); // current context node list
        buf.append("getContextNodeList:" + contextNodeList + "\n"); // TBD

        Transformer transformer = ts.getTransformer(); // current transformer working
        buf.append("getTransformer:" + transformer + "\n"); // TBD
        return buf.toString();
    }

    /**
     * Utility method to dump data from an ElemTemplate.  
     * @return String describing various bits of the state
     */
    protected String getElemTemplateDump(ElemTemplate et)
    {
        StringBuffer buf = new StringBuffer("ElemTemplateDump;");
        if (null == et)
            return buf.toString();
        buf.append("\n    getNodeName:" + et.getNodeName() + ";");
        buf.append("\n    getStylesheet:" + et.getStylesheet() + ";");
        buf.append("\n    getSystemId:" + et.getSystemId() + ";");
        buf.append("\n    getPublicId:" + et.getPublicId() + ";");
        QName qname = et.getName();
        if (null != qname)
            buf.append("\n    getName:" + qname.toString() + ";");
        else
            buf.append("\n    getName:NULL;");
        qname = et.getMode();
        if (null != qname)
            buf.append("\n    getMode:" + qname.toString() + ";");
        else
            buf.append("\n    getMode:NULL;");

        XPath xpath = et.getMatch();
        if (null != xpath)
            buf.append("\n    getMatch:" + xpath.getPatternString() + ";");
        else
            buf.append("\n    getMatch:NULL;");

        buf.append("\n    getPriority:" + et.getPriority() + ";");
        return buf.toString();
    }
    /**
     * Utility method to dump data from an ElemTemplateElement.  
     * @return String describing various bits of the state
     */
    protected String getElemTemplateElementDump(ElemTemplateElement et)
    {
        StringBuffer buf = new StringBuffer("ElemTemplateElementDump;");
        if (null == et)
            return buf.toString();
        buf.append("\n    getNodeName:" + et.getNodeName() + ";");
        buf.append("\n    getStylesheet:" + et.getStylesheet() + ";");
        buf.append("\n    getSystemId:" + et.getSystemId() + ";");
        buf.append("\n    getPublicId:" + et.getPublicId() + ";");
        buf.append("\n    getLength:" + et.getLength() + ";");
        buf.append("\n    getLineNumber:" + et.getLineNumber() + ";");
        buf.append("\n    getColumnNumber:" + et.getColumnNumber() + ";");

        return buf.toString();
    }

    /**
     * Utility method to gather data about current node.  
     * @return String describing node
     */
    protected String getCurrentNodeInfo(TransformState ts, String x)
    {
        StringBuffer buf = new StringBuffer();
        Node n = ts.getCurrentNode();
        if(null != n)
        {
            buf.append(n.getNodeName());
            if(Node.TEXT_NODE == n.getNodeType())
            {
                buf.append("[");
                buf.append(n.getNodeValue());
                buf.append("]");
            }
        }
        else
            buf.append("[NULL-NODE]");

        if (null != x)            
            buf.append("[value:" + x + "]");

        return buf.toString();
    }

    /**
     * Utility method to gather data about current element in xsl.  
     * @return String describing element
     */
    protected String getCurrentElementInfo(TransformState ts)
    {
        StringBuffer buf = new StringBuffer();
        ElemTemplateElement templ = ts.getCurrentElement();

        if(null != templ)
        {
            // Note for user if it's an LRE or an xsl element
            if(templ instanceof ElemLiteralResult)
                buf.append("LRE:");
            else
                buf.append("xsl:");

            buf.append(templ.getNodeName());
            buf.append(", line# "+templ.getLineNumber());
            buf.append(", col# "+templ.getColumnNumber());
            try
            {
                Class cl = ((Object)templ).getClass();
                Method getSelect = cl.getMethod("getSelect", null);
                if(null != getSelect)
                {
                    buf.append(", select='");
                    XPath xpath = (XPath)getSelect.invoke(templ, null);
                    buf.append(xpath.getPatternString()+"'");
                }
            }
            catch(java.lang.reflect.InvocationTargetException ite)
            {
                // no-op: just don't put in the select info for these items
                // buf.append("(threw: InvocationTargetException)");
            }
            catch(IllegalAccessException iae)
            {
                // no-op
            }
            catch(NoSuchMethodException nsme)
            {
                // no-op
            }
        }
        else
            buf.append("[NULL-ELEMENT]");

        return buf.toString();
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
        setLastItem("setDocumentLocator");
        ourLocator = locator; // future use
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
        reporter.check((null != transformState), true, "transformState non-null in startDocument");
        docCachedTransformState = transformState; // see endDocument
    }


    /** Implement ContentHandler.endDocument.  */
    public void endDocument()
        throws SAXException
    {
        setLastItem("endDocument");
        reporter.logMsg(traceLoggingLevel, getLast());
        reporter.checkObject(docCachedTransformState, transformState, 
                       "transformState same in endDocument as startDocument"); // see startDocument
        docCachedTransformState = null;
    }


    /** Implement ContentHandler.startPrefixMapping.  */
    public void startPrefixMapping (String prefix, String uri)
        throws SAXException
    {
        setLastItem("startPrefixMapping: " + ", " + uri);
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
                   + namespaceURI + ", " + qName);
                   
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
        setLastItem(END_ELEMENT + namespaceURI + ", " + namespaceURI + ", " + qName);

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
