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
 * ProcessorWrapper.java
 *
 */
package org.apache.qetest.xslwrapper;

import java.io.InputStream;
import java.io.IOException;

import java.util.Properties;

/**
 * Helper class to adapt or wrap instances of xsl processors.
 * <p>Abstract class defining basic processor actions and a
 * static method to create specific flavors of wrappers.  This
 * serves as a simple way to abstract the act of processing an XML
 * data file with an XSL stylesheet into an output file.  Thus
 * test classes can be written that can operate on any type of
 * wrappered processor.</p>
 * <p>Basic API's for processing also return a long denoting the
 * number of milliseconds taken during the 'interesting portions'
 * of that operation.  Note that it is <b>very</b> important that
 * implementing classes clearly document exactly what is being
 * timed in their implementations, and the ways that they are
 * implementing the process (SAX, DOM, etc.)</p>
 * @author Shane Curcuru
 * @version $Id$
 */
public abstract class ProcessorWrapper
{

    //-----------------------------------------------------
    //-------- Worker methods implemented by subclassed wrappers --------
    //-----------------------------------------------------

    /**
     * Construct a processor of the appropriate flavor, optionally
     * specifying a liaison or other option.
     * <p>May throw exceptions related to the creating of a new processor.
     * Subclasses must set the value of (Object)p in this method and return it.</p>
     * <p>Note that not all processors may use liaisons in the same manner.</p>
     * //@todo revisit: see if liaisonClassName should be genericized
     *
     * @param liaisonClassName [optional] if non-null & non-blank,
     * classname of an XML liaison
     * @return (Object)processor as a side effect; null if error
     * @throws java.lang.Exception covers any underlying exceptions
     */
    public abstract Object createNewProcessor(String liaisonClassName)
        throws java.lang.Exception;  // Cover all exception cases

    /**
     * Get a description of the wrappered processor.
     * @return info-string describing the processor
     */
    public abstract String getDescription();

    /** Reference to actual current processor object. */
    protected Object p = null;

    /**
     * Reference to actual current processor object.  
     * @return underlying (Object)processor object
     */
    public Object getProcessor()
    {
        return p;
    }

    /** If we think the current processor has a preprocessed stylesheet ready. */
    protected boolean stylesheetReady = false;

    /**
     * If we think the current processor has a preprocessed stylesheet ready.  
     * @return true if ready to call processToFile(String xmlSource, 
     * String resultFile)
     */
    public boolean getStylesheetReady()
    {
        return stylesheetReady;
    }

    /** 
     * Return value when an error occours from process* methods. 
     * Most methods return either a long representing the milliseconds 
     * the 'interesting' portions of the operation took, or this 
     * ERROR constant if something went wrong.
     */
    public static final long ERROR = -1L;

    //-----------------------------------------------------
    //-------- Processing methods implemented by subclassed wrappers --------
    //-----------------------------------------------------

    /**
     * Process xmlSource using xslStylesheet to produce resultFile.
     * <p>Will throw any exceptions from the underlying processor 
     * as-is, so callers probably need to catch Exception, and then 
     * check whichever specific types of Exception they expect.</p>
     * <p>Attempts to ask each processor to accomplish the task 
     * in the simplest and most obvious manner.  Often copied from 
     * various processor's samples. Should only time data related 
     * to the basic act of processing the file, not time setting up 
     * local variables or processing the filenames. <b>Note:</b> 
     * the format of the filenames (absolute, relative, local, URI)
     * is not necessarily defined.</p>
     *
     * @param xmlSource file name of source XML file
     * @param xslStylesheet file name of stylesheet XSL file
     * @param resultFile file name of output file
     * @return milliseconds process time took or ProcessorWrapper.ERROR
     * @throws java.lang.Exception covers any underlying exceptions
     */
    public abstract long processToFile(
        String xmlSource, String xslStylesheet, String resultFile)
            throws java.lang.Exception;  // Cover all exception cases

    /**
     * Preprocess a stylesheet and set it into the processor.
     * <p>Most XSLT processors offer some feature allowing you to 
     * parse and pre-process a stylesheet into a compact memory 
     * representation, which may then be used later to perform a 
     * process or transformation. Normal usage would be to call this 
     * method, then call processToFile(String xmlSource, 
     * String resultFile), and then validate the resultFile.</p>
     *
     * @param xslStylesheet file name of stylesheet XSL file
     * @return milliseconds process time took or ProcessorWrapper.ERROR
     * @throws java.lang.Exception covers any underlying exceptions
     */
    public abstract long preProcessStylesheet(String xslStylesheet)
        throws java.lang.Exception;  // should cover all exception cases

    /**
     * Process xmlSource using a preProcessStylesheet to produce resultFile.
     * <p>Must call preProcessStylesheet(String xslStylesheet) 
     * before calling this method.</p>
     *
     * @param xmlSource file name of source XML file
     * @param resultFile file name of output file
     * @return milliseconds process time took or ProcessorWrapper.ERROR
     * @throws java.lang.Exception covers any underlying exceptions
     */
    public abstract long processToFile(String xmlSource, String resultFile)
        throws java.lang.Exception;  // should cover all exception cases

    /**
     * Process xmlSource with embedded stylesheet to produce resultFile.
     * <p>Wrappers will ask their processor to parse an XML file 
     * that presumably has an &lt;?xml-stylesheet element in it.</p>
     *
     * @param xmlSource file name of source XML file
     * @param resultFile file name of output file
     * @return milliseconds process time took or ProcessorWrapper.ERROR
     * @throws java.lang.Exception covers any underlying exceptions
     */
    public abstract long processEmbeddedToFile(String xmlSource, String resultFile)
        throws java.lang.Exception;  // should cover all exception cases

    /**
     * Set a String name=value param in the processor, if applicable.
     * <p>The implementation is wrapper dependent, but generally 
     * wrappers should set any Params given immediately before 
     * performing any processing operations. Does not necessarily 
     * handle namespaces, nor non-String valued values.</p>
     *
     * @param key name of the param
     * @param expression value of the param, as a String
     */
    public abstract void setStylesheetParam(String key, String expression);

    /**
     * Set a String namespace:name=value param in the processor, if applicable.
     * <p>The implementation is wrapper dependent, but generally 
     * wrappers should set any Params given immediately before 
     * performing any processing operations. Does not handle 
     * non-String valued values; if the processor does not obviously 
     * support namespaces, the namespace is ignored.</p>
     *
     * @param namespace of the param
     * @param key name of the param
     * @param expression value of the param
     */
    public abstract void setStylesheetParam(String namespace, String key,
                                            String expression);

    /** 
     * Reset the state of the processor, if applicable. 
     * <p>This implementation is wrapper dependent, but generally 
     * should be called before re-using the same wrapper to 
     * process additional files.</p>
     */
    public abstract void reset();

    /**
     * Set a diagnostics output PrintWriter, if applicable.
     * <p>For processors that offer a simple diagnostics stream, 
     * this is passed in.  Not (currently) used for tracing or 
     * advanced diagnostics features in some processors.</p>
     * @param pw PrintWriter to dump diagnostic or error output
     */
    public abstract void setDiagnosticsOutput(java.io.PrintWriter pw);

    /**
     * Set the indent level of the processor, if applicable.
     * <p>Note that the default is to let the processor decide the indent
     * level (and/or if it should indent or not).  If you set it explicitly,
     * then 0 == no indent or equivalent, >=1 == indent to that level.</p>
     * @param i indent level
     */
    public abstract void setIndent(int i);

    //-----------------------------------------------------
    //-------- Factory static class methods --------
    //-----------------------------------------------------

    /**
     * Currently known 'good' list of implemented wrappers.
     * <p>Allows specification of a simple name for each wrapper,
     * so clients don't necessarily have to know the full classname.</p>
     * <ul>Where:
     * <li>key = String simple name = xalan|trax|etc...</li>
     * <li>Supports: See ProcessorWrapper.properties</li>
     * <li>Or:</li>
     * <li>value = String FQCN of wrapper implementation</li>
     * <li>Or, optionally:</li>
     * <li>value = String full classname;property=value</li>
     * <li>Supports: See ProcessorWrapper.properties</li>
     * </ul>
     * <p>As a hack, some values can be 'overloaded' by specifying
     * an optional system property name = value pair after a semicolon
     * in the value.  This will be stripped off and set into the System
     * properties before creating the wrapper.</p>
     */
    protected static Properties wrapperMapper = null;

    /** Static initializer for wrapperMapper. */
    static
    {
        wrapperMapper = new Properties();

        try
        {
            InputStream is = ProcessorWrapper.class.getResourceAsStream(
                "ProcessorWrapper.properties");

            wrapperMapper.load(is);
            is.close();
        }
        catch (IOException ioe)
        {
            System.err.println("Loading ProcessorWrapper.properties threw: "
                               + ioe.toString());
            ioe.printStackTrace();
        }
    }
    ;

    /**
     *  Accessor for our wrapperMapper.  
     *
     * @return Properties block of wrapper implementations 
     * that we implicitly know about
     */
    public static final Properties getDescriptions()
    {
        return wrapperMapper;
    }

    /** Separators for specially wrapped classes and their properties. */
    public static final String WRAPPER_SEPARATOR = ";";

    /** Separators for specially wrapped classes and their properties. */
    public static final String PROPERTY_SEPARATOR = "=";

    /**
     * Create a wrapper of a specific flavor.
     * <p>When using a 'mapped' flavor, will also set the requested
     * System property before creating the wrapper class itself.</p>
     * @param flavor name of a wrapper class; simple names or FQCN's
     * @return a ProcessorWrapper of the desired type; null if error
     */
    public static final ProcessorWrapper getWrapper(String flavor)
    {

        // Attempt to lookup the flavor: if found, use the 
        //  value we got, otherwise default to the same value
        String className = null;

        className = wrapperMapper.getProperty(flavor, flavor);

        // Strip off any optional properties and set them
        int pos = className.indexOf(WRAPPER_SEPARATOR);

        if (pos > 0)
        {

            // Rip off the property part, and save it
            String prop = className.substring(pos + 1);

            className = className.substring(0, pos);

            // Separate into property=value
            int pos2 = prop.indexOf(PROPERTY_SEPARATOR);
            String val = "";  // default to blank string

            if (pos2 > 0)
            {
                val = prop.substring(pos2 + 1);
                prop = prop.substring(0, pos2);
            }

            // Set the property into the system
            // This will presumably be used by the specific 
            //  wrapper class later for some purpose
            System.getProperties().put(prop, val);  // Applet may throw SecurityException
        }

        // Constuct one of the asked-for Wrappers
        Class wClass = null;
        ProcessorWrapper wrapper = null;

        try
        {
            wClass = Class.forName(className);
            wrapper = (ProcessorWrapper) wClass.newInstance();
        }
        catch (Exception e)
        {
            System.err.println("Exception creating flavor(" + className
                               + ") threw: " + e.toString());
            e.printStackTrace();
        }

        return wrapper;
    }
}  // end of class ProcessorWrapper

