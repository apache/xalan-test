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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.qetest.xslwrapper;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.ErrorListener;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Cheap-o utilities for Trax*Wrapper implementations.
 *
 * @author Shane Curcuru
 * @version $Id$
 */
public abstract class TraxWrapperUtils
{

    /**
     * Get a generic description of the TrAX related info.  
     *
     * @return Properties block with basic info about any TrAX 
     * implementing processor, plus specific version information
     * about Xalan-J 2.x or Xerces-J 1.x if found
     */
    public static Properties getTraxInfo()
    {
        Properties p = new Properties();
        p.put("traxwrapper.api", "trax");
        p.put("traxwrapper.language", "java");
        try
        {
            Properties sysProps = System.getProperties();
            p.put("traxwrapper.jaxp.transform", 
                  sysProps.getProperty("javax.xml.transform.TransformerFactory", "unset"));
            p.put("traxwrapper.jaxp.parser.dom", 
                  sysProps.getProperty("javax.xml.parsers.DocumentBuilderFactory", "unset"));
            p.put("traxwrapper.jaxp.parser.sax", 
                  sysProps.getProperty("javax.xml.parsers.SAXParserFactory", "unset"));
        }
        // In case we're in an Applet
        catch (SecurityException se) { /* no-op, ignore */ }

        // Look for some Xalan/Xerces specific version info
        try
        {
            Class clazz = Class.forName("org.apache.xerces.framework.Version");
            // Found 1.x, grab it's version fields
            Field f = clazz.getField("fVersion");
            p.put("traxwrapper.xerces.version", (String)f.get(null));
        }
        catch (Exception e1) { /* no-op, ignore */ }

        try
        {
            Class clazz = Class.forName("org.apache.xalan.processor.XSLProcessorVersion");
            Field f = clazz.getField("S_VERSION");
            p.put("traxwrapper.xalan.version", (String)f.get(null));
        }
        catch (Exception e2) { /* no-op, ignore */ }

        return p;
    }


    /**
     * Apply specific Attributes to a TransformerFactory OR 
     * Transformer, OR call specific setFoo() API's on a 
     * TransformerFactory OR Transformer.  
     *
     * Filters on hashkeys.startsWith("Processor.setAttribute.")
     * Most Attributes are simply passed to factory.setAttribute(), 
     * however certain special cases are handled:
     * setURIResolver, setErrorListener.
     * Exceptions thrown by underlying transformer are propagated.
     * 
     * This takes an Object so that an underlying worker method can 
     * process either a TransformerFactory or a Transformer.
     *
     * @param factory TransformerFactory to call setAttributes on.
     * @param attrs Hashtable of potential attributes to set.
     */
    public static void setAttributes(Object setPropsOn, 
                                     Hashtable attrs)
                                     throws IllegalArgumentException
    {
        if ((null == setPropsOn) || (null == attrs))
            return;

        Enumeration attrKeys = null;
        try
        {
            // Attempt to use as a Properties block..
            attrKeys = ((Properties)attrs).propertyNames();
        }
        catch (ClassCastException cce)
        {
            // .. but fallback to get as Hashtable instead
            attrKeys = attrs.keys();
        }

        while (attrKeys.hasMoreElements())
        {
            String key = (String) attrKeys.nextElement();
            // Only attempt to set the attr if it matches our marker
            if ((null != key)
                && (key.startsWith(TransformWrapper.SET_PROCESSOR_ATTRIBUTES)))
            {
                Object value = null;
                try
                {
                    // Attempt to use as a Properties block..
                    value = ((Properties)attrs).getProperty(key);
                    // But, if null, then try getting as hash anyway
                    if (null == value)
                    {
                        value = attrs.get(key);
                    }
                }
                catch (ClassCastException cce)
                {
                    // .. but fallback to get as Hashtable instead
                    value = attrs.get(key);
                }
                // Strip off our marker for the property name
                String processorKey = key.substring(TransformWrapper.SET_PROCESSOR_ATTRIBUTES.length());
                // Ugly, but it works -sc
                if (setPropsOn instanceof TransformerFactory)
                    setAttribute((TransformerFactory)setPropsOn, processorKey, value);
                else if (setPropsOn instanceof Transformer)
                    setAttribute((Transformer)setPropsOn, processorKey, value);
                // else - ignore it, no-op    
            }
        }

    }


    /** Token specifying a call to setURIResolver.  */
    public static String SET_URI_RESOLVER = "setURIResolver";

    /** Token specifying a call to setErrorListener.  */
    public static String SET_ERROR_LISTENER = "setErrorListener";

    /**
     * Apply specific Attributes to a TransformerFactory OR call 
     * specific setFoo() API's on a TransformerFactory.  
     *
     * Filters on hashkeys.startsWith("Processor.setAttribute.")
     * Most Attributes are simply passed to factory.setAttribute(), 
     * however certain special cases are handled:
     * setURIResolver, setErrorListener.
     * Exceptions thrown by underlying transformer are propagated.
     *
     * @see setAttribute(Transformer, String, Object)
     * @param factory TransformerFactory to call setAttributes on.
     * @param key specific attribute or special case attr.
     * @param value to set for key.
     */
    private static void setAttribute(TransformerFactory factory, 
                                     String key, 
                                     Object value)
                                     throws IllegalArgumentException
    {
        if ((null == factory) || (null == key))
            return;

        // Note: allow exceptions to propagate here

        // Check if this is a special case to call a specific 
        //  API, or the general case to call setAttribute(key...)
        if (SET_URI_RESOLVER.equals(key))
        {
            factory.setURIResolver((URIResolver)value);
        }
        else if (SET_ERROR_LISTENER.equals(key))
        {
            factory.setErrorListener((ErrorListener)value);
        }
        else
        {
            // General case; just call setAttribute
            factory.setAttribute(key, value);
        }
    }


    /**
     * Apply specific Attributes to a Transformer OR call 
     * specific setFoo() API's on a Transformer.  
     *
     * Filters on hashkeys.startsWith("Processor.setAttribute.")
     * Only certain special cases are handled:
     * setURIResolver, setErrorListener.
     * Exceptions thrown by underlying transformer are propagated.
     *
     * @see setAttribute(TransformerFactory, String, Object)
     * @param factory TransformerFactory to call setAttributes on.
     * @param key specific attribute or special case attr.
     * @param value to set for key.
     */
    private static void setAttribute(Transformer transformer, 
                                     String key, 
                                     Object value)
                                     throws IllegalArgumentException
    {
        if ((null == transformer) || (null == key))
            return;

        // Note: allow exceptions to propagate here

        // Check if this is a special case to call a specific 
        //  API, or the general case to call setAttribute(key...)
        if (SET_URI_RESOLVER.equals(key))
        {
            transformer.setURIResolver((URIResolver)value);
        }
        else if (SET_ERROR_LISTENER.equals(key))
        {
            transformer.setErrorListener((ErrorListener)value);
        }
        else
        {
            // General case; no-op; no equivalent
        }
    }
}
