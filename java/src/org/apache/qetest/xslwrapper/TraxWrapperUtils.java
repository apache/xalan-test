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

import javax.xml.transform.TransformerFactory;

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
     * Apply specific Attributes to a TransformerFactory.  
     *
     * Filters on hashkeys.startsWith("Processor.setAttribute.")
     * Exceptions thrown by underlying factory are ignored.
     *
     * @param factory TransformerFactory to call setAttributes on.
     * @param attrs Hashtable of potential attributes to set.
     */
    public static void setAttributes(TransformerFactory factory, 
                                     Hashtable attrs)
                                     throws IllegalArgumentException
    {
        if ((null == factory) || (null == attrs))
            return;

        Enumeration attrKeys = null;
        try
        {
            attrKeys = ((Properties)attrs).propertyNames();
        }
        catch (ClassCastException cce)
        {
            // Simply get as Hashtable instead
            attrKeys = attrs.keys();
        }

        while (attrKeys.hasMoreElements())
        {
            String key = (String) attrKeys.nextElement();
            // Only attempt to set the attr if it matches our marker
            if ((null != key)
                && (key.startsWith(TransformWrapper.SET_PROCESSOR_ATTRIBUTES)))
            {
                // Strip off our marker for the property name
                String processorKey = key.substring(TransformWrapper.SET_PROCESSOR_ATTRIBUTES.length());
                Object value = null;
                try
                {
                    value = ((Properties)attrs).getProperty(key);
                }
                catch (ClassCastException cce)
                {
                    // Simply get as Hashtable instead
                    value = attrs.get(key);
                }
                // Note: allow exceptions to propagate here
                factory.setAttribute(processorKey, value);
            }
        }

    }
}
