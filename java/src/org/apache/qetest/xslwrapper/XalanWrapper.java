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
 * XalanWrapper.java
 *
 */
package org.apache.qetest.xslwrapper;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Vector;

// The Xalan (apache) implementation
import org.apache.xalan.xslt.XSLTProcessor;
import org.apache.xalan.xslt.XSLTProcessorFactory;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTResultTarget;
 // XSLProcessorVersion is not present in the Xalan-J 2.x compatibility layer
 // import org.apache.xalan.xslt.XSLProcessorVersion;

/**
 * Implementation of a ProcessorWrapper for Xalan 1.x builds.
 * <p>See TraxWrapper for a Xalan 2.x wrapper solution.</p>
 * <p>Note: Updated to work with either Xalan-J 1.x or with 
 * Xalan-J 2.x's compatibility layer seamlessly.</p>
 * @author Shane Curcuru
 * @version $Id$
 */
public class XalanWrapper extends ProcessorWrapper
{

    /** No-op Ctor for the Xalan-J 1.x wrapper. */
    public XalanWrapper(){}

    /** Reference to current processor - Xalan flavor - convenience method. */
    protected org.apache.xalan.xslt.XSLTProcessor processor = null;

    /**
     * NEEDSDOC Method getXalanProcessor 
     *
     *
     * NEEDSDOC (getXalanProcessor) @return
     */
    public org.apache.xalan.xslt.XSLTProcessor getXalanProcessor()
    {
        return (processor);
    }

    /**
     * Construct a processor of the appropriate flavor, optionally specifying a liaison.
     * <p>May throw exceptions related to the creating of a new processor.</p>
     * <ul>Xalan supports two liaisons:
     * <li>org.apache.xalan.xpath.dtm.DTMLiaison</li>
     * <li>org.apache.xalan.xpath.xdom.XercesLiaison</li>
     * </ul>
     * @param liaisonClassName [optional] if non-null & non-blank, classname of an XML liaison
     * @return (Object)processor as a side effect; null if error
     * @exception Exception may be thrown by underlying operation
     *
     * @throws java.lang.Exception
     */
    public Object createNewProcessor(String liaisonClassName)
            throws java.lang.Exception  // Cover all exception cases
    {

        // Cleanup any prior objects 
        cleanup();

        if (liaisonClassName != null)
            processor =
                org.apache.xalan.xslt.XSLTProcessorFactory.getProcessorUsingLiaisonName(
                    liaisonClassName);
        else
            processor =
                org.apache.xalan.xslt.XSLTProcessorFactory.getProcessor();

        p = (Object) processor;

        // Return here; will be null if error or exception raised
        return (p);
    }

    /** FQCN of Xalan-J 1.x's version file.   */
    public static final String XALAN1_VERSION_CLASS = "org.apache.xalan.xslt.XSLProcessorVersion";

    /** FQCN of Xalan-J 2.x's version file, when using the compatibility layer.   */
    public static final String XALAN2_VERSION_CLASS = "org.apache.xalan.processor.XSLProcessorVersion";

    /** Marker string added to getDescription, when using the compatibility layer.   */
    public static final String XALAN2_MARKER = "-compat1";

    /** FQCN of Xerces-J 1.x's version file, for convenience.   */
    public static final String XERCES1_VERSION_CLASS = "org.apache.xerces.framework.Version";

    /**
     * Get a description of the wrappered processor.
     * @return info-string describing the processor and possibly it's common options
     */
    public String getDescription()
    {

        if (processor == null)
        {
            return ("ERROR: must call createNewProcessor first from: "
                    + getDescription());
        }
        else
        {
            StringBuffer buf = new StringBuffer("No Xalan version info found");
            String parserVersion = new String("no-parser-info-avail");
            Class clazz = null;
            Field f = null;

            // As a convenience, see if we can find the version 
            //  of the parser we're using as well
            try
            {
                // Currently, only check for Xerces versions
                clazz = Class.forName(XERCES1_VERSION_CLASS);
                // Found 1.x, grab it's version fields
                f = clazz.getField("fVersion");
                parserVersion = (String)f.get(null);
            }
            catch (Exception e2)
            {
                // no-op, leave value as-is
            }

            // Check for either 1.x or 2.x compatibility layer
            try
            {
                clazz = Class.forName(XALAN1_VERSION_CLASS);
                // Found 1.x, grab it's version fields
                buf = new StringBuffer();
                f = clazz.getField("PRODUCT");
                buf.append(f.get(null));
                buf.append(";");
                f = clazz.getField("LANGUAGE");
                buf.append(f.get(null));
                buf.append(";");
                f = clazz.getField("S_VERSION");
                buf.append(f.get(null));
                buf.append(";");
                buf.append(processor.getXMLProcessorLiaison());
                buf.append(";");
                buf.append(parserVersion);
            }
            catch (Exception e1)
            {
                // Can't find 1.x, look for 2.x compat layer
                try
                {
                    clazz = Class.forName(XALAN2_VERSION_CLASS);
                    // Found 2.x, grab it's version fields
                    buf = new StringBuffer();
                    f = clazz.getField("PRODUCT");
                    buf.append(f.get(null));
                    buf.append(XALAN2_MARKER);  // so user knows we're doing compatibility layer
                    buf.append(";");
                    f = clazz.getField("LANGUAGE");
                    buf.append(f.get(null));
                    buf.append(";");
                    f = clazz.getField("S_VERSION");
                    buf.append(f.get(null));
                    buf.append(";");
                    // Liaison info not applicable
                    buf.append(";");
                    buf.append(parserVersion);
                }
                catch (Exception e2)
                {
                    // Can't find 2.x either, just bail
                }
            }
            return buf.toString();
        }
    }

    /**
     * Process the xmlSource using the xslStylesheet to produce the resultFile.
     * <p>May throw exceptions related to asking the processor to perform the process.</p>
     * <p>Attempts to ask each processor to accomplish the task in the simplest
     * and most obvious manner.  Often copied from various processor's samples.</p>
     * @param xmlSource name of source XML file
     * @param xslStylesheet name of stylesheet XSL file
     * @param resultFile name of output file, presumably XML
     * @return milliseconds process time took
     * @exception Exception may be thrown by underlying operation
     *
     * @throws java.lang.Exception
     */
    public long processToFile(
            String xmlSource, String xslStylesheet, String resultFile)
                throws java.lang.Exception  // Cover all exception cases
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        // Declare variables ahead of time to minimize latency
        long startTime = 0;
        long endTime = 0;

        // Create xalan-specific sources
        org.apache.xalan.xslt.XSLTInputSource xml =
            new org.apache.xalan.xslt.XSLTInputSource(xmlSource);
        org.apache.xalan.xslt.XSLTInputSource xsl =
            new org.apache.xalan.xslt.XSLTInputSource(xslStylesheet);
        org.apache.xalan.xslt.XSLTResultTarget result =
            new org.apache.xalan.xslt.XSLTResultTarget(resultFile);

        // Begin timing the whole process
        startTime = System.currentTimeMillis();

        processor.process(xml, xsl, result);

        endTime = System.currentTimeMillis();

        return (endTime - startTime);
    }

    /**
     * Preprocess a stylesheet and set it into the processor, based on string inputs.
     * @param xslStylesheet name of stylesheet XSL file
     * @return milliseconds process time took or ProcessorWrapper.ERROR
     * @exception Exception may be thrown by underlying operation
     *
     * @throws java.lang.Exception
     */
    public long preProcessStylesheet(String xslStylesheet)
            throws java.lang.Exception  // should cover all exception cases
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        // Declare variables ahead of time to minimize latency
        long startTime = 0;
        long endTime = 0;

        // Create xalan-specific sources
        org.apache.xalan.xslt.StylesheetRoot sRoot;
        org.apache.xalan.xslt.XSLTInputSource xsl =
            new org.apache.xalan.xslt.XSLTInputSource(xslStylesheet);

        // Begin timing loading the stylesheet
        startTime = System.currentTimeMillis();
        sRoot = processor.processStylesheet(xsl);  // side effect: also sets the stylesheet
        endTime = System.currentTimeMillis();
        stylesheetReady = true;

        return (endTime - startTime);
    }

    /**
     * Process the xmlSource using the xslStylesheet to produce the resultFile.
     * @param xmlSource name of source XML file
     * @param resultFile name of output file, presumably XML
     * @return milliseconds process time took or ProcessorWrapper.ERROR
     * @exception Exception may be thrown by underlying operation
     *
     * @throws java.lang.Exception
     */
    public long processToFile(String xmlSource, String resultFile)
            throws java.lang.Exception  // should cover all exception cases
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        // Ensure we (apparently) have already processed a stylesheet
        if (!stylesheetReady)
            throw new java.lang.IllegalStateException(
                "You must call preProcessStylesheet first!");

        // Declare variables ahead of time to minimize latency
        long startTime = 0;
        long endTime = 0;

        // Create xalan-specific sources
        org.apache.xalan.xslt.XSLTInputSource xml =
            new org.apache.xalan.xslt.XSLTInputSource(xmlSource);
        org.apache.xalan.xslt.XSLTResultTarget result =
            new org.apache.xalan.xslt.XSLTResultTarget(resultFile);

        // Begin timing the whole process
        startTime = System.currentTimeMillis();

        processor.process(xml, null, result);

        endTime = System.currentTimeMillis();

        return (endTime - startTime);
    }


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
    public long processEmbeddedToFile(String xmlSource, String resultFile)
        throws java.lang.Exception  // should cover all exception cases
    {
        throw new RuntimeException("processEmbeddedToFile() not yet implemented!");
    }


    /**
     * Reset the state.
     * <p>This needs to be called after a process() call is invoked,
     * if the processor is to be used again.</p>
     */
    public void reset()
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        processor.reset();

        stylesheetReady = false;
    }

    /**
     * Set diagnostics output PrintWriter.  
     *
     * NEEDSDOC @param pw
     */
    public void setDiagnosticsOutput(java.io.PrintWriter pw)
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        processor.setDiagnosticsOutput(pw);
    }

    /**
     * Set the indent level of the processor.  
     *
     * NEEDSDOC @param i
     */
    public void setIndent(int i)
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        // HACK: functionality not present in 2.x compat, 
        //  just comment out for now: we should really try 
        //  to dynamically load this for 1.x and/or replace the 
        //  functionality for 2.x later on
        //processor.getXMLProcessorLiaison().setIndent(i);
    }

    /**
     * Set a String name=value param in the processor, if applicable.  
     *
     * NEEDSDOC @param key
     * NEEDSDOC @param expression
     */
    public void setStylesheetParam(String key, String expression)
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        processor.setStylesheetParam(key, expression);
    }

    /**
     * Set a String namespace:name=value param in the processor, if applicable.
     * @todo Needs Implementation: namespace is currently <b>ignored!</b>
     * @param namespace of the param
     * @param key name of the param
     * @param expression value of the param
     */
    public void setStylesheetParam(String namespace, String key,
                                   String expression)
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        processor.setStylesheetParam(key, expression);
    }

    /** Worker method to cleanup any internal state. */
    private void cleanup()
    {

        processor = null;
        p = null;
        stylesheetReady = false;
    }
}  // end of class XalanWrapper

