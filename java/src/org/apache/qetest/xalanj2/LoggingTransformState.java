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
 * LoggingTransformState.java
 *
 */
package org.apache.qetest.xalanj2;

import org.apache.qetest.*;
import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.transformer.TransformerClient;
import org.apache.xalan.transformer.TransformState;
import org.apache.xpath.XPath;
import org.w3c.dom.Node;

import java.lang.reflect.Method;


/**
 * Cheap-o ContentHandler that logs info about TransformState interface.
 * <p>Implements ContentHandler and dumps simplistic info 
 * everything to a Logger; a way to debug TransformState.</p>
 * <p>This class could use improvement, but currently serves both 
 * as a 'layer' for a ContentHandler (i.e. you can stick 
 * setDefaultHandler in which we'll call for you, thus actually 
 * getting output from your transform) as well as a logging 
 * service for the TransformState interface.  We dump to our 
 * Logger various interesting info from the TransformState 
 * object during each of our startElement(), endElement(), and 
 * characters() calls about both the source node being processed 
 * and about the xsl: element doing the processing.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class LoggingTransformState extends LoggingHandler 
       implements ContentHandler, TransformerClient
{

    /** No-op sets logger to default.  */
    public LoggingTransformState()
    {
        setLogger(getDefaultLogger());
    }

    /**
     * Ctor that calls setLogger automatically.  
     *
     * @param r Logger we should log to
     */
    public LoggingTransformState(Logger l)
    {
        setLogger(l);
    }


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


    /**
     * Accessor method for our TransformState object.
     *
     * @param TransformState object we are using
     */
    public TransformState getTransformState()
    {
        return transformState;
    }


    /**
     * Our default handler that we pass all events through to.
     */
    protected ContentHandler defaultHandler = null;


    /**
     * Set a default handler for us to wrapper.
     * Set a ContentHandler for us to use.
     *
     * @param default Object of the correct type to pass-through to;
     * throws IllegalArgumentException if null or incorrect type
     */
    public void setDefaultHandler(Object defaultC)
    {
        try
        {
            defaultHandler = (ContentHandler)defaultC;
        }
        catch (Throwable t)
        {
            throw new java.lang.IllegalArgumentException("setDefaultHandler illegal type: " + t.toString());
        }
    }


    /**
     * Accessor method for our default handler.
     *
     * @return default (Object) our default handler; null if unset
     */
    public Object getDefaultHandler()
    {
        return (Object)defaultHandler;
    }


    /** Prefixed to all logger msg output for ContentHandler.  */
    public final String prefix = "LTS:";


    /** Prefixed to all logger msg output for TransformState.  */
    public final String prefix2 = "LTS2:";


    /** Cheap-o string representation of last event we got.  */
    protected String lastItem = NOTHING_HANDLED;


    /** 
     * Cheap-o Verbosity flag: should we log all ContentHandler 
     * messages or not.  
     * //@todo should have accessors and be integrated better
     */
    public boolean verbose = false;


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


    /** setExpected, etc. not yet implemented.  */


    /** How many characters to report from characters event.  */
    private int charLimit = 30;


    /**
     * How many characters to report from characters event.  
     * @param l charLimit for us to use
     */
    public void setCharLimit(int l)
    {
        charLimit = l;
    }


    /**
     * How many characters to report from characters event.  
     * @return charLimit we use
     */
    public int getCharLimit()
    {
        return charLimit;
    }



    ////////////////// Utility methods for TransformState ////////////////// 
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
            buf.append("[" + x + "]");

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


    ////////////////// Implement ContentHandler ////////////////// 
    protected Locator ourLocator = null;
    
    /** Implement ContentHandler.setDocumentLocator.  */
    public void setDocumentLocator (Locator locator)
    {
        // Note: this implies this class is !not! threadsafe
        setLastItem("setDocumentLocator");
        ourLocator = locator; // future use
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.setDocumentLocator(locator);
    }


    /** Implement ContentHandler.startDocument.  */
    public void startDocument ()
        throws SAXException
    {
        setLastItem("startDocument");
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.startDocument();
    }


    /** Implement ContentHandler.endDocument.  */
    public void endDocument()
        throws SAXException
    {
        setLastItem("endDocument");
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.endDocument();
    }


    /** Implement ContentHandler.startPrefixMapping.  */
    public void startPrefixMapping (String prefix, String uri)
        throws SAXException
    {
        setLastItem("startPrefixMapping: " + prefix + ", " + uri);
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.startPrefixMapping(prefix, uri);
    }


    /** Implement ContentHandler.endPrefixMapping.  */
    public void endPrefixMapping (String prefix)
        throws SAXException
    {
        setLastItem("endPrefixMapping: " + prefix);
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.endPrefixMapping(prefix);
    }


    /** Implement ContentHandler.startElement.  */
    public void startElement (String namespaceURI, String localName,
                                                        String qName, Attributes atts)
        throws SAXException
    {
        final String START_ELEMENT = "startElement: ";
        StringBuffer buf = new StringBuffer();
        buf.append(namespaceURI + ", " 
                   + namespaceURI + ", " + qName);
                   
        int n = atts.getLength();
        for(int i = 0; i < n; i++)
        {
            buf.append(", " + atts.getQName(i));
        }
        setLastItem(START_ELEMENT + buf.toString());
        if (verbose)
            logger.logMsg(level, prefix + getLast());

        if (null != defaultHandler)
            defaultHandler.startElement(namespaceURI, localName, qName, atts);

        // Also handle TransformerState
        if(null != transformState)
        {
            logger.logMsg(level, prefix2 + START_ELEMENT 
                         + getCurrentElementInfo(transformState) + " is processing: " 
                         + getCurrentNodeInfo(transformState, buf.toString()));
        }
    }


    /** Implement ContentHandler.endElement.  */
    public void endElement (String namespaceURI, String localName, String qName)
        throws SAXException
    {
        final String END_ELEMENT = "endElement: ";
        setLastItem(END_ELEMENT + namespaceURI + ", " + namespaceURI + ", " + qName);
        if (verbose)
            logger.logMsg(level, prefix + getLast());

        if (null != defaultHandler)
            defaultHandler.endElement(namespaceURI, localName, qName);

        // Also handle TransformerState
        if(null != transformState)
        {
            logger.logMsg(level, prefix2 + END_ELEMENT 
                         + getCurrentElementInfo(transformState) + " is processing: " 
                         + getCurrentNodeInfo(transformState, null));
        }
    }


    /** Implement ContentHandler.characters.  */
    public void characters (char ch[], int start, int length)
        throws SAXException
    {
        final String CHARACTERS = "characters: ";
        String s = new String(ch, start, (length > charLimit) ? charLimit : length);
        String tmp = null;
        if(length > charLimit)
            tmp = "\"" + s + "\"...";
        else
            tmp = "\"" + s + "\"";

        setLastItem(CHARACTERS + tmp);
        if (verbose)
            logger.logMsg(level, prefix + getLast());

        if (null != defaultHandler)
            defaultHandler.characters(ch, start, length);

        // Also handle TransformerState
        if(null != transformState)
        {
            logger.logMsg(level, prefix2 + CHARACTERS 
                         + getCurrentElementInfo(transformState) + " is processing: " 
                         + getCurrentNodeInfo(transformState, tmp));
        }
    }


    /** Implement ContentHandler.ignorableWhitespace.  */
    public void ignorableWhitespace (char ch[], int start, int length)
        throws SAXException
    {
        setLastItem("ignorableWhitespace: len " + length);
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.ignorableWhitespace(ch, start, length);
    }


    /** Implement ContentHandler.processingInstruction.  */
    public void processingInstruction (String target, String data)
        throws SAXException
    {
        setLastItem("processingInstruction: " + target + ", " + data);
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.processingInstruction(target, data);
    }


    /** Implement ContentHandler.skippedEntity.  */
    public void skippedEntity (String name)
        throws SAXException
    {
        setLastItem("skippedEntity: " + name);
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.skippedEntity(name);
    }

}
