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
 * LoggingTraceListenerEx.java
 *
 */
package org.apache.qetest.xalanj2;
import org.apache.qetest.*;

import java.io.IOException;
import java.util.Hashtable;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import org.apache.xalan.trace.TraceListener;
import org.apache.xalan.trace.TraceListenerEx;
import org.apache.xalan.trace.GenerateEvent;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.trace.EndSelectionEvent;
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
 * Logging TraceListenerEx interface.
 * Implementation of the TraceListenerEx interface that
 * prints each event to our logger as it occurs; simply adds 
 * impl of new selectedEnd event to LoggingTraceListener.
 * Future improvements: allow you to specify a set of 
 * expected events to validate.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class LoggingTraceListenerEx extends LoggingTraceListener 
       implements TraceListenerEx
{

    /**
     * Accesor method for a brief description of this service.  
     * @return String "LoggingTraceListenerEx: logs and counts trace and end events"
     */
    public String getDescription()
    {
        return "LoggingTraceListenerEx: logs and counts trace and end events";
    }


    /** No-op sets logger to default.  */
    public LoggingTraceListenerEx()
    {
        setLogger(getDefaultLogger());
    }

    /**
     * Ctor that calls setLogger automatically.  
     *
     * @param r Logger we should log to
     */
    public LoggingTraceListenerEx(Logger l)
    {
        setLogger(l);
    }


    /** Constant for getCounters()[]: selected events.  */
    public static final int TYPE_SELECTED_END = 3;

    /** 
     * Counters for how many events we've handled.  
     * Index into array are the TYPE_* constants.
     */
    protected int[] counters = 
    {
        0, /* trace */
        0, /* generated */
        0, /* selected */
        0  /* selectedEnd */
    };

    public int getCounterEx()
    {
        return counters[TYPE_SELECTED_END];
        
    }
    /** setExpected, etc. not yet implemented.  */

    ////////////////// Implement TraceListenerEx ////////////////// 

    /**
     * Method that is called after an xsl:apply-templates or 
     * xsl:for-each selection occurs.
     *
     * @param ev the generate event.
     *
     * @throws javax.xml.transform.TransformerException
     */
    public void selectEnd(EndSelectionEvent endSelectionEvent) 
          throws javax.xml.transform.TransformerException
    {
        counters[TYPE_SELECTED_END]++;
        Hashtable attrs = new Hashtable();
        attrs.put("event", "selectEnd");
        attrs.put("location", "L" + endSelectionEvent.m_styleNode.getLineNumber()
                  + "C" + endSelectionEvent.m_styleNode.getColumnNumber());

        StringBuffer buf = new StringBuffer("  <styleNode>");
        ElemTemplateElement styleNodeElem = (ElemTemplateElement) endSelectionEvent.m_styleNode;
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
            buf.append(XalanDumper.dump(styleNodeElem, XalanDumper.DUMP_DEFAULT));
            
        buf.append("  </styleNode>\n");

        buf.append("  <m_xpath>" + endSelectionEvent.m_attributeName + "="
                   + endSelectionEvent.m_xpath.getPatternString() + "</m_xpath>\n");

        buf.append("  <m_selection>");
        if (endSelectionEvent.m_selection.getType() == endSelectionEvent.m_selection.CLASS_NODESET)
        {
            NodeIterator nl = endSelectionEvent.m_selection.nodeset();

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
            buf.append("[" + endSelectionEvent.m_selection.str() +"]");
        }
        buf.append("</m_selection>\n");

        buf.append("  <m_sourceNode>" + XalanDumper.dump(endSelectionEvent.m_sourceNode, XalanDumper.DUMP_DEFAULT) + "</m_sourceNode>\n");

        setLastItem(buf.toString());
        logger.logElement(level, TRACE_LISTENER_DUMP, attrs, buf.toString());
    }
}
