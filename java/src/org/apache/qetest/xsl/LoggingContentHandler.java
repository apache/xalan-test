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
 * LoggingContentHandler.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.*;
import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Cheap-o ContentHandler for use by API tests.
 * <p>Implements ContentHandler and dumps simplistic info 
 * everything to a Logger; a way to debug SAX stuff.</p>
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class LoggingContentHandler extends LoggingHandler implements ContentHandler
{

    /** No-op sets logger to default.  */
    public LoggingContentHandler()
    {
        setLogger(getDefaultLogger());
    }

    /**
     * Ctor that calls setLogger automatically.  
     *
     * @param r Logger we should log to
     */
    public LoggingContentHandler(Logger l)
    {
        setLogger(l);
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


    /** Prefixed to all logger msg output.  */
    public final String prefix = "LCH:";


    /** Cheap-o string representation of last event we got.  */
    protected String lastItem = NOTHING_HANDLED;


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


    ////////////////// Implement ContentHandler ////////////////// 
    protected Locator ourLocator = null;
    
    public void setDocumentLocator (Locator locator)
    {
        // Note: this implies this class is !not! threadsafe
        setLastItem("setDocumentLocator");
        ourLocator = locator; // future use
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.setDocumentLocator(locator);
    }


    public void startDocument ()
        throws SAXException
    {
        setLastItem("startDocument");
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.startDocument();
    }


    public void endDocument()
        throws SAXException
    {
        setLastItem("endDocument");
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.endDocument();
    }


    public void startPrefixMapping (String prefix, String uri)
        throws SAXException
    {
        setLastItem("startPrefixMapping: " + prefix + ", " + uri);
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.startPrefixMapping(prefix, uri);
    }


    public void endPrefixMapping (String prefix)
        throws SAXException
    {
        setLastItem("endPrefixMapping: " + prefix);
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.endPrefixMapping(prefix);
    }


    public void startElement (String namespaceURI, String localName,
                                                        String qName, Attributes atts)
        throws SAXException
    {
        StringBuffer buf = new StringBuffer();
        buf.append("startElement: " + namespaceURI + ", " 
                   + namespaceURI + ", " + qName);
                   
        int n = atts.getLength();
        for(int i = 0; i < n; i++)
        {
            buf.append(", " + atts.getQName(i));
        }
        setLastItem(buf.toString());
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.startElement(namespaceURI, localName, qName, atts);
    }


    public void endElement (String namespaceURI, String localName, String qName)
        throws SAXException
    {
        setLastItem("endElement: " + namespaceURI + ", " + namespaceURI + ", " + qName);
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.endElement(namespaceURI, localName, qName);
    }


    public void characters (char ch[], int start, int length)
        throws SAXException
    {
        String s = new String(ch, start, (length > charLimit) ? charLimit : length);
        if(length > charLimit)
            setLastItem("characters: \"" + s + "\"...");
        else
            setLastItem("characters: \"" + s + "\"");
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.characters(ch, start, length);
    }


    public void ignorableWhitespace (char ch[], int start, int length)
        throws SAXException
    {
        setLastItem("ignorableWhitespace: len " + length);
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.ignorableWhitespace(ch, start, length);
    }


    public void processingInstruction (String target, String data)
        throws SAXException
    {
        setLastItem("processingInstruction: " + target + ", " + data);
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.processingInstruction(target, data);
    }


    public void skippedEntity (String name)
        throws SAXException
    {
        setLastItem("skippedEntity: " + name);
        logger.logMsg(level, prefix + getLast());
        if (null != defaultHandler)
            defaultHandler.skippedEntity(name);
    }

}
