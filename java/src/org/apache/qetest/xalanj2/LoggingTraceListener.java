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
 * LoggingTraceListener.java
 *
 */
package org.apache.qetest.xalanj2;
import org.apache.qetest.*;

import java.io.IOException;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import org.apache.xalan.trace.TraceListener;
import org.apache.xalan.trace.GenerateEvent;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.trace.TracerEvent;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.templates.Constants;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.apache.xml.utils.QName;
import org.apache.xpath.axes.ContextNodeList;
import org.apache.xpath.XPath;

/**
 * Logging TraceListener interface.
 * Implementation of the TraceListener interface that
 * prints each event to our logger as it occurs.
 * Future improvements: allow you to specify a set of 
 * expected events to validate.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class LoggingTraceListener extends LoggingHandler 
       implements TraceListener
{

    /**
     * Accesor method for a brief description of this service.  
     * @return String "LoggingTraceListener: logs and counts trace events"
     */
    public String getDescription()
    {
        return "LoggingTraceListener: logs and counts trace events";
    }


    /** No-op sets logger to default.  */
    public LoggingTraceListener()
    {
        setLogger(getDefaultLogger());
    }

    /**
     * Ctor that calls setLogger automatically.  
     *
     * @param r Logger we should log to
     */
    public LoggingTraceListener(Logger l)
    {
        setLogger(l);
    }


    /**
     * Set a default handler for us to wrapper - no-op.
     * Since you can add multiple TraceListeners, there's no sense 
     * in us wrappering another one.
     * @param default Object unused
     */
    public void setDefaultHandler(Object noop)
    {
        /* no-op */
    }


    /**
     * Accessor method for our default handler - no-op.
     * @return null
     */
    public Object getDefaultHandler()
    {
        return null;
    }


    /** Prefixed to all logger msg output for TraceListener.  */
    public final String prefix = "LTL:";


    /** Cheap-o string representation of last event we got.  */
    protected String lastItem = NOTHING_HANDLED;


    /**
     * Accessor for string representation of last trace event.  
     * @param s string to set
     */
    protected void setLastItem(String s)
    {
        lastItem = s;
    }


    /**
     * Accessor for string representation of last trace event.  
     * @return last event string we had
     */
    public String getLast()
    {
        return lastItem;
    }


    /** Constant for getCounters()[]: trace events.  */
    public static final int TYPE_TRACE = 0;

    /** Constant for getCounters()[]: generated events.  */
    public static final int TYPE_GENERATED = 1;

    /** Constant for getCounters()[]: selected events.  */
    public static final int TYPE_SELECTED = 2;

    /** 
     * Counters for how many events we've handled.  
     * Index into array are the TYPE_* constants.
     */
    protected int[] counters = 
    {
        0, /* trace */
        0, /* generated */
        0  /* selected */
    };


    /**
     * Get a list of counters of all items we've logged.
     * Returned as trace, generated, selected
     * Index into array are the TYPE_* constants.
     *
     * @return array of int counters for each item we log
     */
    public int[] getCounters()
    {
        return counters;
    }

    /**
     * Reset all items or counters we've handled.  
     */
    public void reset()
    {
        setLastItem(NOTHING_HANDLED);
        for (int i = 0; i < counters.length; i++)
        {
            counters[i] = 0;
        }
    }

    /** setExpected, etc. not yet implemented.  */

    ////////////////// Implement TraceListener ////////////////// 
    /**
     * Logging implementation of TraceListener method.
     * Method that is called when a trace event occurs.
     * The method is blocking.  It must return before processing continues.
     *
     * @param tracerEvent the trace event.
     */
    public void trace(TracerEvent tracerEvent)
    {
        counters[TYPE_TRACE]++;

        StringBuffer buf = new StringBuffer("trace:");
        int dumpLevel = XalanDumper.DUMP_DEFAULT;
        if (null != tracerEvent.m_mode) // not terribly elegant way to do it
            dumpLevel = XalanDumper.DUMP_NOCLOSE;
        switch (tracerEvent.m_styleNode.getXSLToken())
        {
            // Specific handling for most common 'interesting' items
            case Constants.ELEMNAME_TEXTLITERALRESULT :
                buf.append(XalanDumper.dump((ElemTextLiteral) tracerEvent.m_styleNode, dumpLevel));
                break;

            case Constants.ELEMNAME_TEMPLATE :
                buf.append(XalanDumper.dump((ElemTemplate) tracerEvent.m_styleNode, dumpLevel));
                break;

            default :
                buf.append(XalanDumper.dump((ElemTemplateElement) tracerEvent.m_styleNode, dumpLevel));
        }
        if (null != tracerEvent.m_mode)
            buf.append(XalanDumper.SEP + "m_mode=" + tracerEvent.m_mode + XalanDumper.RBRACKET);

        setLastItem(buf.toString());
        logger.logMsg(level, prefix + getLast());
    }

    /**
     * Logging implementation of TraceListener method.
     * Method that is called just after the formatter listener is called.
     *
     * @param selectionEvent the selected event.
     * @throws javax.xml.transform.TransformerException never thrown
     */
    public void selected(SelectionEvent selectionEvent) 
            throws javax.xml.transform.TransformerException
    {
        counters[TYPE_SELECTED]++;

        StringBuffer buf = new StringBuffer("selected:");
        ElemTemplateElement styleNodeElem = (ElemTemplateElement) selectionEvent.m_styleNode;
        ElemTemplateElement parent = (ElemTemplateElement) styleNodeElem.getParentNode();
        if (parent == styleNodeElem.getStylesheetRoot().getDefaultRootRule())
        {
            buf.append("[default-root-rule]");
        }
        else if (parent == styleNodeElem.getStylesheetRoot().getDefaultTextRule())
        {
            buf.append("[default-text-rule]");
        }
        else if (parent == styleNodeElem.getStylesheetRoot().getDefaultRule())
        {
            buf.append("[default-rule]");
        }
        else
            buf.append(XalanDumper.dump(styleNodeElem, XalanDumper.DUMP_NOCLOSE));

        buf.append(selectionEvent.m_attributeName + "="
                   + selectionEvent.m_xpath.getPatternString() + ";");

        if (selectionEvent.m_selection.getType() == selectionEvent.m_selection.CLASS_NODESET)
        {
            // Must create as DTMNodeIterator for DTM_EXP merge 13-Jun-01
            NodeIterator nl = selectionEvent.m_selection.nodeset();

            if (nl instanceof ContextNodeList)
            {
                try
                {
                    nl = ((ContextNodeList)nl).cloneWithReset();
                }
                catch(CloneNotSupportedException cnse)
                {
                    buf.append("[Can't trace nodelist, threw: CloneNotSupportedException]");
                }
                Node pos = nl.nextNode();

                if (null == pos)
                {
                    buf.append("[empty node list]");
                }
                else // (null == pos)
                {
                    while (null != pos)
                    {
                        buf.append(" " + pos);
                        pos = nl.nextNode();
                    }
                }
            }
            else // (nl instanceof ContextNodeList)
            {
                buf.append("[Can't trace nodelist: it isn't a ContextNodeList]");
            }
        }
        else // (selectionEvent.m_selection.getType() == selectionEvent.m_selection.CLASS_NODESET)
        {
            buf.append("[" + selectionEvent.m_selection.str() +"]");
        }
        buf.append(XalanDumper.RBRACKET);   // Since we said DUMP_NOCLOSE above
        setLastItem(buf.toString());
        logger.logMsg(level, prefix + getLast());
    }

    /**
     * Logging implementation of TraceListener method.
     * Method that is called just after the formatter listener is called.
     *
     * @param generateEvent the generate event.
     */
    public void generated(GenerateEvent generateEvent)
    {
        counters[TYPE_GENERATED]++;

        StringBuffer buf = new StringBuffer("generated:");
        switch (generateEvent.m_eventtype)
        {
            case GenerateEvent.EVENTTYPE_STARTDOCUMENT :
                buf.append("STARTDOCUMENT");
            break;

            case GenerateEvent.EVENTTYPE_ENDDOCUMENT :
                buf.append("ENDDOCUMENT");
            break;

            case GenerateEvent.EVENTTYPE_STARTELEMENT :
                buf.append("STARTELEMENT[" + generateEvent.m_name + "]"); // just hardcode [ LBRACKET ] RBRACKET here
            break;

            case GenerateEvent.EVENTTYPE_ENDELEMENT :
                buf.append("ENDELEMENT[" + generateEvent.m_name + "]");
            break;

            case GenerateEvent.EVENTTYPE_CHARACTERS :
                String chars1 = new String(generateEvent.m_characters, generateEvent.m_start, generateEvent.m_length);
                buf.append("CHARACTERS[" + chars1 + "]");
            break;

            case GenerateEvent.EVENTTYPE_CDATA :
                String chars2 = new String(generateEvent.m_characters, generateEvent.m_start, generateEvent.m_length);
                buf.append("CDATA[" + chars2 + "]");
            break;

            case GenerateEvent.EVENTTYPE_COMMENT :
                buf.append("COMMENT[" + generateEvent.m_data + "]");
            break;

            case GenerateEvent.EVENTTYPE_PI :
                buf.append("PI[" + generateEvent.m_name + ", " + generateEvent.m_data + "]");
            break;

            case GenerateEvent.EVENTTYPE_ENTITYREF :
                buf.append("ENTITYREF[" + generateEvent.m_name + "]");
            break;

            case GenerateEvent.EVENTTYPE_IGNORABLEWHITESPACE :
                buf.append("IGNORABLEWHITESPACE");
            break;
        }
        setLastItem(buf.toString());
        logger.logMsg(level, prefix + getLast());
    }
}
