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
 * TraxWrapper.java
 *
 */
package org.apache.qetest.xslwrapper;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import java.io.PrintWriter;  // currently only used in unimplemented setDiagnosticsOutput
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;

// Needed SAX and DOM classes
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;

// javax parsers and trax imports
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Implementation of a ProcessorWrapper for a TrAX-compilant XSLT processor.
 * <p>Currently defaults to the Xalan 2.x implementation, although
 * it should respect the trax.processor.xslt system property.</p>
 * @todo add better support for liaisons, or different processing
 * models (DOM, SAX, from files, whatever), etc.
 * @todo share constants between TraxWrapper, SaxonWrapper
 * @todo document how we perform various types of transforms
 * @author Shane Curcuru
 * @version $Id$
 */
public class TraxWrapper extends ProcessorWrapper
{

    /** No-op Ctor for the generic TRAX interface wrapper. */
    public TraxWrapper(){}

    /** Reference to current processor - TRaX flavor - convenience method. */
    protected javax.xml.transform.TransformerFactory processor = null;

    /**
     * @return reference to underlying TransformerFactory
     */
    public javax.xml.transform.TransformerFactory getTraxProcessor()
    {
        return (processor);
    }

    /** A preprocessed stylesheet that we're saving. */
    private Templates savedStylesheet = null;

    /** Whatever parameters the user has set. */
    private Hashtable params = null;

    /** Which type of transform we should perform. */
    protected int transformType = DEFAULT_TYPE;

    /** Constants for different types of transforms. */
    public static final String FILE_TO_FILE = "file-to-file";

    /** NEEDSDOC Field FILE_TO_FILE_TYPE          */
    public static final int FILE_TO_FILE_TYPE = 1;

    /** NEEDSDOC Field DOM_TO_DOM          */
    public static final String DOM_TO_DOM = "dom-to-dom";

    /** NEEDSDOC Field DOM_TO_DOM_TYPE          */
    public static final int DOM_TO_DOM_TYPE = 2;

    /** NEEDSDOC Field SAX_TO_SAX          */
    public static final String SAX_TO_SAX = "sax-to-sax";

    /** NEEDSDOC Field SAX_TO_SAX_TYPE          */
    public static final int SAX_TO_SAX_TYPE = 3;

    /** NEEDSDOC Field SAX_TO_STREAM          */
    public static final String SAX_TO_STREAM = "sax-to-stream";

    /** NEEDSDOC Field SAX_TO_STREAM_TYPE          */
    public static final int SAX_TO_STREAM_TYPE = 4;

    /** NEEDSDOC Field DOM_TO_STREAM          */
    public static final String DOM_TO_STREAM = "dom-to-stream";

    /** NEEDSDOC Field DOM_TO_STREAM_TYPE          */
    public static final int DOM_TO_STREAM_TYPE = 5;

    /** NEEDSDOC Field STREAM_TO_DOM          */
    public static final String STREAM_TO_DOM = "stream-to-dom";

    /** NEEDSDOC Field STREAM_TO_DOM_TYPE          */
    public static final int STREAM_TO_DOM_TYPE = 6;

    /** NEEDSDOC Field AS_XML_FILTER          */
    public static final String AS_XML_FILTER = "as-xml-filter";

    /** NEEDSDOC Field AS_XML_FILTER_TYPE          */
    public static final int AS_XML_FILTER_TYPE = 7;

    /** NEEDSDOC Field DEFAULT_TRANSFORM          */
    public static final String DEFAULT_TRANSFORM = FILE_TO_FILE;

    /** NEEDSDOC Field DEFAULT_TYPE          */
    public static final int DEFAULT_TYPE = FILE_TO_FILE_TYPE;

    /**
     * Mapping of transform types to integer constants.
     */
    protected static Hashtable typeMap = null;

    // Static class initializer for our typeMap
    static
    {
        typeMap = new Hashtable();

        typeMap.put(FILE_TO_FILE, new Integer(FILE_TO_FILE_TYPE));
        typeMap.put(DOM_TO_DOM, new Integer(DOM_TO_DOM_TYPE));
        typeMap.put(SAX_TO_SAX, new Integer(SAX_TO_SAX_TYPE));
        typeMap.put(SAX_TO_STREAM, new Integer(SAX_TO_STREAM_TYPE));
        typeMap.put(DOM_TO_STREAM, new Integer(DOM_TO_STREAM_TYPE));
        typeMap.put(STREAM_TO_DOM, new Integer(STREAM_TO_DOM_TYPE));
        typeMap.put(AS_XML_FILTER, new Integer(AS_XML_FILTER_TYPE));
    }
    ;

    /** NEEDSDOC Field TRAX_PROCESSOR_XSLT          */
    public static final String TRAX_PROCESSOR_XSLT = "javax.xml.transform.TransformerFactory";

    /** NEEDSDOC Field ORG_XML_SAX_DRIVER          */
    public static final String ORG_XML_SAX_DRIVER = "org.xml.sax.driver";

    /** NEEDSDOC Field DEFAULT_PROCESSOR          */
    public static final String DEFAULT_PROCESSOR =
        "org.apache.xalan.processor.TransformerFactoryImpl";

    /** NEEDSDOC Field DEFAULT_PARSER          */
    public static final String DEFAULT_PARSER =
        "org.apache.xerces.parsers.SAXParser";

    /** NEEDSDOC Field TRAX_WRAPPER_TYPE          */
    public static final String TRAX_WRAPPER_TYPE = "trax.wrapper.type";

    /**
     * Construct a processor of the appropriate flavor, optionally specifying a liaison.
     * <p>May throw exceptions related to the creating of a new processor.</p>
     * <p>Note liaison support is not really implemented - a TODO item.</p>
     * <p>This method defaults the "trax.processor.xslt" system
     * property to the Xalan 2.x implementation.  It also reads
     * the "trax.wrapper.type" property to determine how we should
     * perform transformations: eg, SAX2SAX, DOM2DOM, FILE2FILE, etc..</p>
     *
     * @param liaisonClassName [optional] if non-null & non-blank,
     * classname of an XML liaison
     * @return (Object)processor as a side effect; null if error
     * @throws java.lang.Exception covers any underlying exceptions
     */
    public Object createNewProcessor(String liaisonClassName)
            throws java.lang.Exception  // Cover all exception cases
    {

        // Cleanup any prior objects 
        cleanup();

        // Default the System property trax.processor.xslt
        //      to the FQCN of the xalan implementation, if needed
        Properties props = System.getProperties();

        // Get, with default, then put back to install default
        String val = props.getProperty(TRAX_PROCESSOR_XSLT,
                                       DEFAULT_PROCESSOR);

        props.put(TRAX_PROCESSOR_XSLT, val);

        // Get, with default, then put back to install default
        val = props.getProperty(ORG_XML_SAX_DRIVER, DEFAULT_PARSER);

        props.put(ORG_XML_SAX_DRIVER, val);

        // Publish any changs.
        // Note that this call may throw SecurityException in 
        //  some cases, cf. if called from an applet
        System.setProperties(props);

        // Also get the 'type' of transformation we should perform
        //  with TRAX - using SAX, using files, DOMs, whatever
        try
        {
            Integer i =
                (Integer) typeMap.get(System.getProperty(TRAX_WRAPPER_TYPE,
                    DEFAULT_TRANSFORM));

            transformType = i.intValue();
        }
        catch (Exception e)
        {

            // Just set the default; ignore the exception
            transformType = DEFAULT_TYPE;
        }

        // Get a factory of 'xslt' stuff (i.e. Xalan)
        processor = TransformerFactory.newInstance();
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
            StringBuffer buf = new StringBuffer("TRaX");

            buf.append(";");
            buf.append("Java");
            buf.append(";");
            buf.append(
                System.getProperties().getProperty(TRAX_PROCESSOR_XSLT));  // TODO - improve this
            buf.append(";");
            buf.append(System.getProperties().getProperty(TRAX_WRAPPER_TYPE));  // TODO - improve this

            return buf.toString();
        }
    }

    /**
     * Process the xmlSource using the xslStylesheet to produce the resultFile.
     * <p>May throw exceptions related to asking the processor to perform the process.</p>
     * <p>Attempts to ask each processor to accomplish the task in the simplest
     * and most obvious manner.  Often copied from various processor's samples.</p>
     * <p>This also respects the "trax.wrapper.type" System property to
     * support different types of transforms.</p>
     *
     * @param xmlSource name of source XML file
     * @param xslStylesheet name of stylesheet XSL file
     * @param resultFile name of output file, presumably XML
     * @return milliseconds process time took
     * @throws java.lang.Exception covers any underlying exceptions
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
        long xmlTime = 0;

        // Create trax-specific sources - only as needed!

        // May throw IOException
        // Note: use OutputStream derivative, not Writer derivative, so that 
        //  the processor can properly control the output encoding!
        FileOutputStream resultStream = new FileOutputStream(resultFile);

        // Read and compile the stylesheet - only as needed!

        switch (transformType)
        {

        // Each case does timing just on the transformation
        // @todo check for ERROR return from underlying operations!
        case FILE_TO_FILE_TYPE :
            startTime = System.currentTimeMillis();

            // Default method of building the stylesheet
            Transformer transformer = processor.newTransformer(new StreamSource(xslStylesheet));
            // Apply any parameters needed (note: may affect timing 
            //  data slightly with method call and Properties lookup)
            applyParams(transformer, params);
            // Transform the XML document into the output stream
            transformer.transform(new StreamSource(xmlSource), new StreamResult(resultStream));

            xmlTime = System.currentTimeMillis() - startTime;
            break;

        case DOM_TO_DOM_TYPE :
            xmlTime = processDOMToDOM(xmlSource, xslStylesheet, resultStream);
            break;

        default :
            throw new java.lang.IllegalStateException("bad/unimplemented transformType("
                                                      + transformType
                                                      + ") for: "
                                                      + TRAX_WRAPPER_TYPE);
        }

        // Force output stream closed, just in case
        resultStream.close();

        // Return the timing data
        return (xmlTime);
    }


    /**
     * Perform the transform from a DOM to a DOM (then serialize).
     *
     * //@todo EVALUATE TIMING: right now, we time everything, 
     * all DOM building and transforms (but not serialization)
     * @param xmlSource name of source XML file
     * @param xslStylesheet name of stylesheet XSL file
     * @param resultFile name of output file, presumably XML
     * @return milliseconds process time took
     * @throws java.lang.Exception covers any underlying exceptions
     */
    protected long processDOMToDOM(
            String xmlSource, String xslStylesheet, OutputStream resultStream)
                throws java.lang.Exception  // Cover all exception cases
    {
        if (!(processor.getFeature(DOMSource.FEATURE) 
              && processor.getFeature(DOMResult.FEATURE)))
        {
            // If DOMs are not supported in either input (Sources)
            //  or output (Results), then bail
            return ERROR;
        }
        long endTime = 0;
        long startTime = System.currentTimeMillis();

        // Parse in the stylesheet into a DOM
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
        Node xslDoc = docBuilder.parse(new InputSource(xslStylesheet));

        // Create a DOMSource to encapsulate the xsl DOM
        DOMSource dsource = new DOMSource(xslDoc);
        // If we don't do this, the transformer won't know how to 
        // resolve relative URLs in the stylesheet.
        dsource.setSystemId(xslStylesheet);

        // Build a stylesheet from the DOMSource
        Templates templates = processor.newTemplates(dsource);
        Transformer transformer = templates.newTransformer();

        // Parse in the xml data into a DOM
        dfactory = DocumentBuilderFactory.newInstance();
        docBuilder = dfactory.newDocumentBuilder();
        Node xmlDoc = docBuilder.parse(new InputSource(xmlSource));

        // Prepare a result and transform it into a DOM
        org.w3c.dom.Document outNode = docBuilder.newDocument();
        applyParams(transformer, params);
        transformer.transform(new DOMSource(xmlDoc), 
                              new DOMResult(outNode));
        // Stop timing now
        endTime = System.currentTimeMillis();

        // Now serialize output to disk with identity transformer
        Transformer serializer = processor.newTransformer();
        serializer.transform(new DOMSource(outNode), 
                             new StreamResult(resultStream));

        return (endTime - startTime);
    }


    /**
     * Preprocess a stylesheet and set it into the processor, based on string inputs.
     *
     * //@todo Does NOT respect the "trax.wrapper.type" System property yet.
     * @param xslStylesheet name of stylesheet XSL file
     * @return milliseconds process time took or ProcessorWrapper.ERROR
     * @throws java.lang.Exception covers any underlying exceptions
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

        // Create trax-specific sources
        Source xsl = new StreamSource(xslStylesheet);

        // Begin timing the whole process
        startTime = System.currentTimeMillis();

        // Read and compile the stylesheet
        savedStylesheet = processor.newTemplates(xsl);
        endTime = System.currentTimeMillis();
        stylesheetReady = true;

        return (endTime - startTime);
    }

    /**
     * Process the xmlSource using the xslStylesheet to produce the resultFile.
     *
     * //@todo Does NOT respect the "trax.wrapper.type" System property yet.
     * @param xmlSource name of source XML file
     * @param resultFile name of output file, presumably XML
     * @return milliseconds process time took or ProcessorWrapper.ERROR
     * @throws java.lang.Exception covers any underlying exceptions
     */
    public long processToFile(String xmlSource, String resultFile)
            throws java.lang.Exception  // should cover all exception cases
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        // Declare variables ahead of time to minimize latency
        long startTime = 0;
        long endTime = 0;

        // May throw IOException
        FileOutputStream resultStream = new FileOutputStream(resultFile);

        // Use the precompiled stylesheet
        Transformer transformer = savedStylesheet.newTransformer();

        applyParams(transformer, params);

        // Begin timing the whole process
        startTime = System.currentTimeMillis();

        // HACK: this should work off of transformType as well!
        transformer.transform(new StreamSource(xmlSource), new StreamResult(resultStream));

        endTime = System.currentTimeMillis();

        // Force output stream closed, just in case
        resultStream.close();

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
        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        // Declare variables ahead of time to minimize latency
        long startTime = 0;
        long endTime = 0;
        String media= null;     // currently ignored
        String title = null;    // currently ignored
        String charset = null;  // currently ignored

        // May throw IOException
        FileOutputStream resultStream = new FileOutputStream(resultFile);

        // Begin timing the whole process
        startTime = System.currentTimeMillis();

        // Get the xml-stylesheet and process it
        Source stylesheetSource = processor.getAssociatedStylesheet(new StreamSource(xmlSource), 
                                                              media, title, charset);

        Transformer transformer = processor.newTransformer(stylesheetSource);
          
        applyParams(transformer, params);

        transformer.transform(new StreamSource(xmlSource), new StreamResult(resultStream));

        endTime = System.currentTimeMillis();

        // Force output stream closed, just in case
        resultStream.close();

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

        // Does not appear to be applicable for TRaX?
        stylesheetReady = false;
        savedStylesheet = null;
        params = null;
    }

    /**
     * Set diagnostics output PrintWriter - not implemented.  
     *
     * @param pw ignored
     */
    public void setDiagnosticsOutput(java.io.PrintWriter pw)
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        // Unimplemented; silently fail
        // processor.setDiagnosticsOutput(pw);
    }

    /**
     * Set the indent level of the processor - not implemented.  
     *
     * @param i ignored
     */
    public void setIndent(int i)
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        // Unimplemented; silently fail
        // TODO: implement for Xalan/LotusXSL specific setFeature()
        // processor.getXMLProcessorLiaison().setIndent(i);
    }

    /**
     * Set a String name=value param in the processor, if applicable.  
     *
     * @param key name of the parameter, encoded for TRAX
     * @param expression value of the parameter
     */
    public void setStylesheetParam(String key, String expression)
    {

        // Ensure we (apparently) have some processor
        if (processor == null)
            throw new java.lang.IllegalStateException(
                "You must call createNewProcessor first!");

        if (params == null)
            params = new Hashtable();

        // Just put the param in our hash; individual methods will 
        //  make use of these later when needed
        params.put(key, expression);
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

        if (params == null)
            params = new Hashtable();

        // Just put the param in our hash; individual methods will 
        //  make use of these later when needed
        params.put(key, expression);
    }

    /**
     * Apply our set of parameters to a transformer.  
     *
     * @todo update to munge TRAX-encoded {namespace}'s as needed
     * @todo should we call t.setParameters(Properties) instead?
     * @param t transformer to set parameters on
     * @param h hash of paramName=paramVal objects
     */
    protected void applyParams(Transformer t, Hashtable h)
    {

        if (params == null)
            return;

        for (Enumeration enum = h.keys();
                enum.hasMoreElements(); /* no increment portion */ )
        {
            Object key = enum.nextElement();

            // @todo update to munge TRAX-encoded {namespace}'s as needed
            t.setParameter(key.toString(), h.get(key));
        }
    }

    /** Worker method to cleanup any internal state. */
    private void cleanup()
    {

        processor = null;
        p = null;
        stylesheetReady = false;
        savedStylesheet = null;
        params = null;
    }
}  // end of class TraxWrapper

