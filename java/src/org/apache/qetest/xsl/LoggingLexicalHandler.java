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
 * LoggingLexicalHandler.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.*;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * Cheap-o LexicalHandler for use by API tests.
 * <p>Implements LexicalHandler and dumps simplistic info 
 * everything to a Logger; a way to debug SAX stuff.</p>
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class LoggingLexicalHandler implements LexicalHandler
{

    /** No-op ctor seems useful. */
    public LoggingLexicalHandler(){}

    /**
     * Ctor that calls setLogger automatically.  
     *
     * @param l Logger we should log to
     */
    public LoggingLexicalHandler(Logger l)
    {
        setLogger(l);
    }

    /** 
     * Our Logger, who we tell all our secrets to.  
     */
    private Logger logger;

    /**
     * Accesor methods for our Logger.  
     * @param r Logger to set
     */
    public void setLogger(Logger l)
    {
        if (l != null)
            logger = l;
    }

    /**
     * Accesor methods for our Logger.  
     * @return Logger we use
     */
    public Logger getLogger()
    {
        return (logger);
    }

    /** Prefixed to all logger msg output.  */
    private final String prefix = "LLH:";

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

    /** What loggingLevel to use for logger.logMsg(). */
    private int level = Logger.DEFAULT_LOGGINGLEVEL;

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


    ////////////////// Implement LexicalHandler ////////////////// 
    public void startDTD (String name, String publicId, String systemId)
    	throws SAXException
    {
        // Note: this implies this class is !not! threadsafe
        setLastEvent("startDTD: " + name + ", " + publicId + ", " + systemId);
        logger.logMsg(level, getLastEvent());
    }

    public void endDTD ()
	    throws SAXException
    {
        setLastEvent("endDTD");
        logger.logMsg(level, getLastEvent());
    }

    public void startEntity (String name)
    	throws SAXException
    {
        setLastEvent("startEntity: " + name);
        logger.logMsg(level, getLastEvent());
    }

    public void endEntity (String name)
	    throws SAXException
    {
        setLastEvent("endEntity: " + name);
        logger.logMsg(level, getLastEvent());
    }

    public void startCDATA ()
    	throws SAXException
    {
        setLastEvent("startCDATA");
        logger.logMsg(level, getLastEvent());
    }

    public void endCDATA ()
	    throws SAXException
    {
        setLastEvent("endCDATA");
        logger.logMsg(level, getLastEvent());
    }

    public void comment (char ch[], int start, int length)
    	throws SAXException
    {
        StringBuffer buf = new StringBuffer("comment: ");
        buf.append(ch);
        buf.append(", ");
        buf.append(start);
        buf.append(", ");
        buf.append(length);

        setLastEvent(buf.toString());
        logger.logMsg(level, getLastEvent());
    }

}
