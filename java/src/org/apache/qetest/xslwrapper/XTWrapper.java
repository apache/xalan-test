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
 * XTWrapper.java
 *
 */
package org.apache.qetest.xslwrapper;

import java.io.File;
import java.io.PrintWriter;

import java.net.URL;

// The XT (James Clark) implementation
import org.xml.sax.InputSource;
import org.xml.sax.Parser;

import com.jclark.xsl.sax.XMLProcessorEx;
import com.jclark.xsl.sax.XSLProcessorImpl;
import com.jclark.xsl.sax.OutputMethodHandlerImpl;
import com.jclark.xsl.sax.FileDestination;

/**
 * Implementation of a ProcessorWrapper for XT.
 * @author Shane Curcuru
 * @version $Id$
 */
public class XTWrapper extends ProcessorWrapper
{

    /** No-op Ctor for the Xalan-J 1.x wrapper. */
    public XTWrapper(){}

    /** Reference to current processor - XT flavor - convenience method. */
    protected com.jclark.xsl.sax.XSLProcessorImpl processor = null;

    /**
     * NEEDSDOC Method getXTProcessor 
     *
     *
     * NEEDSDOC (getXTProcessor) @return
     */
    public com.jclark.xsl.sax.XSLProcessorImpl getXTProcessor()
    {
        return (processor);
    }

    /**
     * Construct a processor of the appropriate flavor, optionally specifying a liaison.
     * <p>May throw exceptions related to the creating of a new processor.</p>
     * <ul>XT supports ??? liaisons:
     * <li>com.jclark.xml.sax.CommentDriver (default)</li>
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

        processor = new com.jclark.xsl.sax.XSLProcessorImpl();

        if (liaisonClassName == null)
            liaisonClassName = "com.jclark.xml.sax.CommentDriver";  // default

        try
        {
            Object parserObj = Class.forName(liaisonClassName).newInstance();

            if (parserObj instanceof XMLProcessorEx)
                processor.setParser((XMLProcessorEx) parserObj);
            else
                processor.setParser((org.xml.sax.Parser) parserObj);
        }
        catch (Exception e)
        {
            System.err.println("createNewProcesor(xt) threw: "
                               + e.toString());
            e.printStackTrace();

            processor = null;
        }

        p = (Object) processor;

        // Return here; will be null if error or exception raised
        return (p);
    }

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
            StringBuffer buf = new StringBuffer("XT");

            buf.append(";");
            buf.append("JAVA");
            buf.append(";");
            buf.append("unknown_version");
            buf.append(";");
            buf.append("unknown_liaison");

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

        // Create XT-specific sources
        OutputMethodHandlerImpl outHandler =
            new OutputMethodHandlerImpl(processor);

        outHandler.setDestination(new FileDestination(new File(resultFile)));

        InputSource xmlIS = xtInputSourceFromString(xmlSource);
        InputSource xslIS = xtInputSourceFromString(xslStylesheet);

        // Begin timing the process: stylesheet, output, and process
        startTime = System.currentTimeMillis();

        processor.loadStylesheet(xslIS);
        processor.setOutputMethodHandler(outHandler);
        processor.parse(xmlIS);

        endTime = System.currentTimeMillis();

        return (endTime - startTime);
    }

    /**
     * Worker method for using XT to process.  
     *
     * NEEDSDOC @param name
     *
     * NEEDSDOC ($objectName$) @return
     */
    private InputSource xtInputSourceFromString(String name)
    {

        File file = new File(name);
        String path = file.getAbsolutePath();

        // Add absolute / to beginning if needed
        if (path.charAt(0) != '/')
            path = '/' + path;

        try
        {
            java.net.URL temp = new URL("file", "", path);

            return (new InputSource(temp.toString()));
        }
        catch (Exception e)
        {
            System.err.println("xtInputSourceFromString(xt) of: " + name
                               + " threw: " + e.toString());
            e.printStackTrace();

            return (null);
        }
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

        // Create XT-specific source
        InputSource xslIS = xtInputSourceFromString(xslStylesheet);

        // Begin timing loading the stylesheet
        startTime = System.currentTimeMillis();

        processor.loadStylesheet(xslIS);  // side effect: also sets the stylesheet

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

        // Create XT-specific sources
        OutputMethodHandlerImpl outHandler =
            new OutputMethodHandlerImpl(processor);

        outHandler.setDestination(new FileDestination(new File(resultFile)));

        InputSource xmlIS = xtInputSourceFromString(xmlSource);

        // Begin timing the process: stylesheet, output, and process
        startTime = System.currentTimeMillis();

        processor.setOutputMethodHandler(outHandler);
        processor.parse(xmlIS);

        endTime = System.currentTimeMillis();

        return (endTime - startTime);
    }

    /**
     * Reset the state.
     */
    public void reset()
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        // processor.reset();  // FIXME: no applicable API?
        stylesheetReady = false;
    }

    /**
     * Set diagnostics output PrintWriter:unimplemented.  
     *
     * NEEDSDOC @param pw
     */
    public void setDiagnosticsOutput(java.io.PrintWriter pw)
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        //processor.setDiagnosticsOutput(pw);  // FIXME: no applicable API?
    }

    /**
     * Set the indent level of the processor:unimplemented.  
     *
     * NEEDSDOC @param i
     */
    public void setIndent(int i)
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        // processor.getXMLProcessorLiaison().setIndent(i); // FIXME: needs to be implemented
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

        // NEEDSWORK: ensure XT is expecting the same kind of expression as other processors
        processor.setParameter(key, (Object) expression);
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

        // NEEDSWORK: ensure XT is expecting the same kind of expression as other processors
        processor.setParameter(key, (Object) expression);
    }

    /** Worker method to cleanup any internal state. */
    private void cleanup()
    {

        processor = null;
        p = null;
        stylesheetReady = false;
    }
}  // end of class XTWrapper

