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

package org.apache.qetest.xalanj2;
import org.apache.qetest.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import org.apache.xalan.trace.GenerateEvent;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.trace.TracerEvent;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.lang.reflect.Method;

/**
 * Static utility for dumping info about common Xalan objects.
 * Cheap-o string representations of some common properties 
 * of various objects; supports some formatting and encapsulation 
 * but could use improvements.
 * Note: currently purposefully outputs plain strings, not 
 * any XML-like elements, so it's easier for other XML-like 
 * logging utilities to output our data without escaping, etc.
 * 
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public abstract class XalanDumper 
{
    // abstract class cannot be instantiated

    /** Simple text constants: for items that are null.  */
    public static final String NULL = "NULL";
    /** Simple text constants: separator between items.  */
    public static final String SEP = ";";
    /** Simple text constants: beginning a block of items.  */
    public static final String LBRACKET = "[";
    /** Simple text constants: ending a block of items.  */
    public static final String RBRACKET = "]";
    /** Simple text constants: line number.  */
    public static final String LNUM = "L";
    /** Simple text constants: column number.  */
    public static final String CNUM = "C";

    /** Simple output formats: default behavior.  */
    public static final int DUMP_DEFAULT = 0;
    /** Simple output formats: verbose: extra output.  */
    public static final int DUMP_VERBOSE = 1;
    /** Simple output formats: a contained object.  */
    public static final int DUMP_CONTAINED = 2;
    /** Simple output formats: don't close block.  */
    public static final int DUMP_NOCLOSE = 4;
    /** Simple output formats: don't include id's or other items likely to change.  */
    public static final int DUMP_NOIDS = 8;

    /** Cheap-o recursion marker: already recursing in Nodes/NodeLists.  */
    public static final int DUMP_NODE_RECURSION = 16;

    /**
     * Return String describing an ElemTemplateElement.
     *
     * @param elem the ElemTemplateElement to print info of
     * @param dumpLevel what format/how much to dump
     */
    public static String dump(ElemTemplateElement elem, int dumpLevel)
    {
        StringBuffer buf = new StringBuffer("ElemTemplateElement" + LBRACKET);
        if (null == elem)
            return buf.toString() + NULL + RBRACKET;

        // Note for user if it's an LRE or an xsl element
        if(elem instanceof ElemLiteralResult)
            buf.append("LRE:");
        else
            buf.append("xsl:");

        buf.append(elem.getNodeName());
        buf.append(SEP + LNUM + elem.getLineNumber());
        buf.append(SEP + CNUM + elem.getColumnNumber());
        buf.append(SEP + "getLength=" + elem.getLength());
        if (DUMP_VERBOSE == (dumpLevel & DUMP_VERBOSE))
        {
            // Only include systemIds (which are long) if verbose
            buf.append(SEP + "getSystemId=" + elem.getSystemId());
            buf.append(SEP + "getStylesheet=" + elem.getStylesheet().getSystemId());
        }
        try
        {
            Class cl = ((Object)elem).getClass();
            Method getSelect = cl.getMethod("getSelect", null);
            if(null != getSelect)
            {
                buf.append(SEP + "select=");
                XPath xpath = (XPath)getSelect.invoke(elem, null);
                buf.append(xpath.getPatternString());
            }
        }
        catch(Exception e)
        {
            // no-op: just don't put in the select info for these items
        }
        if (DUMP_NOCLOSE == (dumpLevel & DUMP_NOCLOSE))
            return buf.toString();
        else
            return buf.toString() + RBRACKET;
    }


    /**
     * Return String describing an ElemTextLiteral.
     *
     * @param elem the ElemTextLiteral to print info of
     * @param dumpLevel what format/how much to dump
     */
    public static String dump(ElemTextLiteral elem, int dumpLevel)
    {
        StringBuffer buf = new StringBuffer("ElemTextLiteral" + LBRACKET);
        if (null == elem)
            return buf.toString() + NULL + RBRACKET;

        buf.append(elem.getNodeName()); // I don't think this ever changes from #Text?
        buf.append(SEP + LNUM + elem.getLineNumber());
        buf.append(SEP + CNUM + elem.getColumnNumber());

        String chars = new String(elem.getChars(), 0, elem.getChars().length);
        buf.append(SEP + "chars=" + chars.trim());

        if (DUMP_NOCLOSE == (dumpLevel & DUMP_NOCLOSE))
            return buf.toString();
        else
            return buf.toString() + RBRACKET;
    }

    /**
     * Return String describing an ElemTemplate.
     *
     * @param elem the ElemTemplate to print info of
     * @param dumpLevel what format/how much to dump
     */
    public static String dump(ElemTemplate elem, int dumpLevel)
    {
        StringBuffer buf = new StringBuffer("ElemTemplate" + LBRACKET);
        if (null == elem)
            return buf.toString() + NULL + RBRACKET;

        buf.append("xsl:" + elem.getNodeName());
        buf.append(SEP + LNUM + elem.getLineNumber());
        buf.append(SEP + CNUM + elem.getColumnNumber());
        if (DUMP_VERBOSE == (dumpLevel & DUMP_VERBOSE))
        {
            // Only include systemIds (which are long) if verbose
            buf.append(SEP + "getSystemId=" + elem.getSystemId());
            buf.append(SEP + "getStylesheet=" + elem.getStylesheet().getSystemId());
        }
        try
        {
            Class cl = ((Object)elem).getClass();
            Method getSelect = cl.getMethod("getSelect", null);
            if(null != getSelect)
            {
                buf.append(SEP + "select=");
                XPath xpath = (XPath)getSelect.invoke(elem, null);
                buf.append(xpath.getPatternString());
            }
        }
        catch(Exception e)
        {
            // no-op: just don't put in the select info for these items
        }
        if (null != elem.getMatch())
            buf.append(SEP + "match=" + elem.getMatch().getPatternString());

        if (null != elem.getName())
            buf.append(SEP + "name=" + elem.getName());

        if (null != elem.getMode())
            buf.append(SEP + "mode=" + elem.getMode());

        buf.append(SEP + "priority=" + elem.getPriority());

        if (DUMP_NOCLOSE == (dumpLevel & DUMP_NOCLOSE))
            return buf.toString();
        else
            return buf.toString() + RBRACKET;
    }


    /**
     * Return String describing a Transformer.
     * Currently just returns info about a get selected public 
     * getter methods from a Transformer.
     * Only really useful when it can do instanceof TransformerImpl 
     * to return custom info about Xalan
     *
     * @param t the Transformer to print info of
     * @param dumpLevel what format/how much to dump
     */
    public static String dump(Transformer trans, int dumpLevel)
    {
        if (null == trans)
            return "Transformer" + LBRACKET + NULL + RBRACKET;

        StringBuffer buf = new StringBuffer();

        StringWriter sw = new StringWriter();
        Properties p = trans.getOutputProperties();
        if (null != p)
        {
            p.list(new PrintWriter(sw));
            buf.append("getOutputProperties{" + sw.toString() + "}");
        }

        if (trans instanceof TransformerImpl)
        {
            final TransformerImpl timpl = (TransformerImpl)trans;
            // We have a Xalan-J 2.x basic transformer
            buf.append("getBaseURLOfSource=" + timpl.getBaseURLOfSource() + SEP);
            // Result getOutputTarget()
            // ContentHandler getInputContentHandler(boolean doDocFrag)
            // DeclHandler getInputDeclHandler()
            // LexicalHandler getInputLexicalHandler()
            // OutputProperties getOutputFormat()
            // Serializer getSerializer()
            // ElemTemplateElement getCurrentElement()
            // int getCurrentNode()
            // ElemTemplate getCurrentTemplate()
            // ElemTemplate getMatchedTemplate()
            // int getMatchedNode()
            // DTMIterator getContextNodeList()
            // StylesheetRoot getStylesheet()
            // int getRecursionLimit()
            buf.append("getMode=" + timpl.getMode() + SEP);
        }

        return "Transformer" + LBRACKET 
            + buf.toString() + RBRACKET;
    }


    /**
     * Return String describing a Node.
     * Currently just returns TracerEvent.printNode(n)
     *
     * @param n the Node to print info of
     * @param dumpLevel what format/how much to dump
     */
    public static String dump(Node n, int dumpLevel)
    {
        if (null == n)
            return "Node" + LBRACKET + NULL + RBRACKET;

        // Copied but modified from TracerEvent; ditch hashCode
        StringBuffer buf = new StringBuffer();

        if (n instanceof Element)
        {
            buf.append(n.getNodeName());

            Node c = n.getFirstChild();

            while (null != c)
            {
                if (c instanceof Attr)
                {
                    buf.append(dump(c, dumpLevel | DUMP_NODE_RECURSION) + " ");
                }
                c = c.getNextSibling();
            }
        }
        else
        {
            if (n instanceof Attr)
            {
                buf.append(n.getNodeName() + "=" + n.getNodeValue());
            }
            else
            {
                buf.append(n.getNodeName());
            }
        }

            
        // If we're already recursing, don't bother printing out 'Node' again
        if (DUMP_NODE_RECURSION == (dumpLevel & DUMP_NODE_RECURSION))
            return LBRACKET + buf.toString() + RBRACKET;
        else
            return "Node" + LBRACKET + buf.toString() + RBRACKET;
    }

    /**
     * Return String describing a DTMNodeProxy.
     * This is the Xalan-J 2.x internal wrapper for Nodes.
     *
     * @param n the DTMNodeProxy to print info of
     * @param dumpLevel what format/how much to dump
     */
    public static String dump(DTMNodeProxy n, int dumpLevel)
    {
        if (null == n)
            return "DTMNodeProxy" + LBRACKET + NULL + RBRACKET;

        // Copied but modified from TracerEvent; ditch hashCode
        StringBuffer buf = new StringBuffer();

        if (DUMP_NOIDS != (dumpLevel & DUMP_NOIDS))
        {
            // Only include the DTM node number if asked
            buf.append(n.getDTMNodeNumber());
        }
        
        if (n instanceof Element)
        {
            buf.append(n.getNodeName());
            // Also output first x chars of value
            buf.append(substr(n.getNodeValue()));

            DTMNodeProxy c = (DTMNodeProxy)n.getFirstChild();

            while (null != c)
            {
                buf.append(dump(c, dumpLevel | DUMP_NODE_RECURSION) + " ");
                c = (DTMNodeProxy)c.getNextSibling();
            }
        }
        else
        {
            if (n instanceof Attr)
            {
                buf.append(n.getNodeName() + "=" + n.getNodeValue());
            }
            else
            {
                buf.append(n.getNodeName());
                // Also output first x chars of value
                buf.append(substr(n.getNodeValue()));
            }
        }

            
        // If we're already recursing, don't bother printing out 'Node' again
        if (DUMP_NODE_RECURSION == (dumpLevel & DUMP_NODE_RECURSION))
            return LBRACKET + buf.toString() + RBRACKET;
        else
            return "DTMNodeProxy" + LBRACKET + buf.toString() + RBRACKET;
    }

    /** Cheap-o worker method to substring a string.  */
    public static int MAX_SUBSTR = 8;

    /** Cheap-o worker method to substring a string.  */
    public static String SUBSTR_PREFIX = ":";

    /** Cheap-o worker method to substring a string.  */
    protected static String substr(String s)
    {
        if (null == s)
            return "";
        return SUBSTR_PREFIX + s.substring(0, Math.min(s.length(), MAX_SUBSTR));
    }

    /**
     * Return String describing a NodeList.
     * Currently just returns TracerEvent.printNode(n)
     *
     * @param nl the NodeList to print info of
     * @param dumpLevel what format/how much to dump
     */
    public static String dump(NodeList nl, int dumpLevel)
    {
        if (null == nl)
            return "NodeList" + LBRACKET + NULL + RBRACKET;

        StringBuffer buf = new StringBuffer();

        int len = nl.getLength() - 1;
        int i = 0;
        while (i < len)
        {
            Node n = nl.item(i);
            if (null != n)
            {
                buf.append(dump(n, dumpLevel) + ", ");
            }
            ++i;
        }

        if (i == len)
        {
            Node n = nl.item(len);
            if (null != n)
            {
                buf.append(dump(n, dumpLevel));
            }
        }
        return "NodeList" + LBRACKET 
            + buf.toString() + RBRACKET;
    }


    /**
     * Print String type of node.  
     * @param n Node to report type of 
     * @return String type name
     */
    public static String dumpNodeType(Node n)
    {
        if (null == n)
            return NULL;
        switch (n.getNodeType())
        {
        case Node.DOCUMENT_NODE :
            return "DOCUMENT_NODE";

        case Node.ELEMENT_NODE :
            return "ELEMENT_NODE";

        case Node.CDATA_SECTION_NODE :
            return "CDATA_SECTION_NODE";

        case Node.ENTITY_REFERENCE_NODE :
            return "ENTITY_REFERENCE_NODE";

        case Node.ATTRIBUTE_NODE :
            return "ATTRIBUTE_NODE";

        case Node.COMMENT_NODE :
            return "COMMENT_NODE";

        case Node.ENTITY_NODE :
            return "ENTITY_NODE";

        case Node.NOTATION_NODE :
            return "NOTATION_NODE";

        case Node.PROCESSING_INSTRUCTION_NODE :
            return "PROCESSING_INSTRUCTION_NODE";

        case Node.TEXT_NODE :
            return "TEXT_NODE";

        default :
            return "UNKNOWN_NODE";
        }
    }  // end of dumpNodeType()

}
