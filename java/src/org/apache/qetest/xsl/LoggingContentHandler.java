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
 * everything to a Reporter; a way to debug SAX stuff.</p>
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class LoggingContentHandler implements ContentHandler
{

    /** No-op ctor seems useful. */
    public LoggingContentHandler(){}

    /**
     * Ctor that calls setReporter automatically.  
     *
     * @param r Reporter we should log to
     * @todo this should really be a Logger, not Reporter
     */
    public LoggingContentHandler(Reporter r)
    {
        setReporter(r);
    }

    /** 
     * Our Reporter, who we tell all our secrets to.  
     * Could/Should be switched to be a Logger.  
     */
    private Reporter reporter;

    /**
     * Accesor methods for our Reporter.  
     * @param r Reporter to set
     */
    public void setReporter(Reporter r)
    {
        if (r != null)
            reporter = r;
    }

    /**
     * Accesor methods for our Reporter.  
     * @return Reporter we use
     */
    public Reporter getReporter()
    {
        return (reporter);
    }

    /** Prefixed to all reporter msg output.  */
    private final String prefix = "LCH:";

    /** Cheap-o string representation of last event we got.  */
    private String lastEvent = null;

    /**
     * Method setLastEvent set our lastEvent field.
     * @param s string to set
     */
    protected void setLastEvent(String s)
    {
        lastEvent = s;
    }

    /**
     * Accessor for string representation of last event we got.  
     * @return last event string we had
     */
    public String getLastEvent()
    {
        return lastEvent;
    }

    /** What loggingLevel to use for reporter.logMsg(). */
    private int level = Reporter.DEFAULT_LOGGINGLEVEL;

    /**
     * Accesor methods; don't think it needs to be synchronized.  
     * @param l loggingLevel for us to use
     */
    public void setLoggingLevel(int l)
    {
        level = l;
    }

    /**
     * Accesor methods; don't think it needs to be synchronized.  
     * @return loggingLevel we use
     */
    public int getLoggingLevel()
    {
        return level;
    }

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
    public void setDocumentLocator (Locator locator)
    {
        // Note: this implies this class is !not! threadsafe
        setLastEvent("setDocumentLocator");
        reporter.logMsg(level, getLastEvent());
    }


    public void startDocument ()
        throws SAXException
    {
        setLastEvent("startDocument");
        reporter.logMsg(level, getLastEvent());
    }


    public void endDocument()
        throws SAXException
    {
        setLastEvent("endDocument");
        reporter.logMsg(level, getLastEvent());
    }


    public void startPrefixMapping (String prefix, String uri)
        throws SAXException
    {
        setLastEvent("startPrefixMapping: " + prefix + ", " + uri);
        reporter.logMsg(level, getLastEvent());
    }


    public void endPrefixMapping (String prefix)
        throws SAXException
    {
        setLastEvent("endPrefixMapping: " + prefix);
        reporter.logMsg(level, getLastEvent());
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
        setLastEvent(buf.toString());
        reporter.logMsg(level, getLastEvent());
    }


    public void endElement (String namespaceURI, String localName, String qName)
        throws SAXException
    {
        setLastEvent("endElement: " + namespaceURI + ", " + namespaceURI + ", " + qName);
        reporter.logMsg(level, getLastEvent());
    }


    public void characters (char ch[], int start, int length)
        throws SAXException
    {
        String s = new String(ch, start, (length > charLimit) ? charLimit : length);
        if(length > charLimit)
            setLastEvent("characters: \"" + s + "\"...");
        else
            setLastEvent("characters: \"" + s + "\"");
        reporter.logMsg(level, getLastEvent());
    }


    public void ignorableWhitespace (char ch[], int start, int length)
        throws SAXException
    {
        setLastEvent("ignorableWhitespace: len " + length);
        reporter.logMsg(level, getLastEvent());
    }


    public void processingInstruction (String target, String data)
        throws SAXException
    {
        setLastEvent("processingInstruction: " + target + ", " + data);
        reporter.logMsg(level, getLastEvent());
    }


    public void skippedEntity (String name)
        throws SAXException
    {
        setLastEvent("skippedEntity: " + name);
        reporter.logMsg(level, getLastEvent());
    }

}
