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
 * SaxonWrapper.java
 *
 */
package org.apache.qetest.xslwrapper;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import java.io.PrintWriter;  // currently only used in unimplemented setDiagnosticsOutput
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;

// For utility method source
import java.net.URL;
import java.net.MalformedURLException;

// Needed SAX classes
import org.xml.sax.*;

// A Saxon-specific TRaX-like wrapper
// NOTE: package name subject to change!
import com.icl.saxon.*;
import com.icl.saxon.trax.*;
import com.icl.saxon.trax.serialize.*;  // Note: Saxon does not provide

// real implementations of serializers
//  so this needs work!
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// For the DOM examples we use Xerces
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.dom.DocumentImpl;

/**
 * Implementation of a ProcessorWrapper for the TRax interface of SAXON 5.5.1.
 * @todo share constants between TraxWrapper, SaxonWrapper
 * @todo document how we perform various types of transforms
 * @author Shane Curcuru
 * @version $Id$
 */
public class SaxonWrapper extends ProcessorWrapper
{

    /** No-op Ctor for the SAXON TRAX interface wrapper. */
    public SaxonWrapper(){}

    /** Reference to current processor - SAXON flavor - convenience method. */
    protected com.icl.saxon.trax.Processor processor = null;

    /**
     * NEEDSDOC Method getTraxProcessor 
     *
     *
     * NEEDSDOC (getTraxProcessor) @return
     */
    public com.icl.saxon.trax.Processor getTraxProcessor()
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

    /** Constants for system properties, etc.. */
    public static final String TRAX_PROCESSOR = "trax.processor";

    /** NEEDSDOC Field XSLT          */
    public static final String XSLT = "xslt";

    /** NEEDSDOC Field TRAX_PROCESSOR_XSLT          */
    public static final String TRAX_PROCESSOR_XSLT = TRAX_PROCESSOR + "."
                                                         + XSLT;

    /** NEEDSDOC Field ORG_XML_SAX_DRIVER          */
    public static final String ORG_XML_SAX_DRIVER = "org.xml.sax.driver";

    /** NEEDSDOC Field DEFAULT_PROCESSOR          */
    public static final String DEFAULT_PROCESSOR = "com.icl.saxon.StyleSheet";  // SAXON 5.5.1

    /** NEEDSDOC Field DEFAULT_PARSER          */
    public static final String DEFAULT_PARSER =
        "org.apache.xerces.parsers.SAXParser";  // TODO which parser to use?

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
     * @param liaisonClassName [optional] if non-null & non-blank,
     * classname of an XML liaison
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

        // Get a processor of 'xslt' stuff (i.e. Xalan)
        processor = Processor.newInstance(XSLT);
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
        long xmlTime = 0;
        long xslTime = 0;

        // Create trax-specific sources
        InputSource xsl = new InputSource(xslStylesheet);  // TODO change to be URI
        InputSource xml = new InputSource(xmlSource);  // TODO change to be URI

        // May throw IOException
        // Note: use OutputStream derivative, not Writer derivative, so that 
        //  the processor can properly control the output encoding!
        FileOutputStream resultStream = new FileOutputStream(resultFile);

        // Begin timing just the stylesheet creation
        startTime = System.currentTimeMillis();

        // Read and compile the stylesheet
        Templates templates = processor.process(xsl);

        xslTime = System.currentTimeMillis() - startTime;

        switch (transformType)
        {

        // Each case does timing just on the transformation
        case FILE_TO_FILE_TYPE :  // OK SAXON
            startTime = System.currentTimeMillis();

            Transformer transformer = templates.newTransformer();

            applyParams(transformer, params);
            transformer.transform(xml, new Result(resultStream));

            xmlTime = System.currentTimeMillis() - startTime;
            break;
        case DOM_TO_DOM_TYPE :
            xmlTime = transformDOM2DOM(templates, xml, resultStream);
            break;
        case SAX_TO_SAX_TYPE :
            xmlTime = transformSAX2SAX(templates, xml, resultStream);
            break;
        case SAX_TO_STREAM_TYPE :
            throw new java.lang.IllegalStateException("bad transformType("
                                                      + transformType
                                                      + ") for: "
                                                      + TRAX_WRAPPER_TYPE);

        // break;
        case DOM_TO_STREAM_TYPE :
            throw new java.lang.IllegalStateException("bad transformType("
                                                      + transformType
                                                      + ") for: "
                                                      + TRAX_WRAPPER_TYPE);

        // break;
        case STREAM_TO_DOM_TYPE :
            throw new java.lang.IllegalStateException("bad transformType("
                                                      + transformType
                                                      + ") for: "
                                                      + TRAX_WRAPPER_TYPE);

        // break;
        case AS_XML_FILTER_TYPE :
            xmlTime = transformAsXMLFilter(templates, xml, resultStream);
            break;
        default :
            throw new java.lang.IllegalStateException("bad transformType("
                                                      + transformType
                                                      + ") for: "
                                                      + TRAX_WRAPPER_TYPE);
        }

        // Force output stream closed, just in case
        resultStream.close();

        // Return the sum of stylesheet create + transform time
        return (xslTime + xmlTime);
    }

    /**
     * Transform an xml document using SAX.
     *
     * NEEDSDOC @param templates
     * NEEDSDOC @param xml
     * NEEDSDOC @param out
     * @return milliseconds process time took
     *
     * @throws IOException
     * @throws SAXException
     * @throws TransformException
     */
    private long transformSAX2SAX(
            Templates templates, InputSource xml, OutputStream out)
                throws TransformException, SAXException, IOException
    {

        long startTime = 0;
        Transformer transformer = templates.newTransformer();

        applyParams(transformer, params);

        OutputFormat format = templates.getOutputFormat();
        Serializer serializer = SerializerFactory.getSerializer(format);

        serializer.setOutputStream(out);
        transformer.setContentHandler(serializer.asContentHandler());  // Use a TRaX serializer provided with Xalan

        //x transformer.setProperty("http://xml.apache.org/xslt/sourcebase", xml.getSystemId());
        //x XMLReader reader = XMLReaderFactory.createXMLReader();
        XMLReader reader = new com.icl.saxon.aelfred.SAXDriver();  // Use SAXON's favorite parser

        reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
                          true);

        //x reader.setFeature("http://apache.org/xml/features/validation/dynamic", true);
        ContentHandler chandler = transformer.getInputContentHandler();

        reader.setContentHandler(chandler);

        if (chandler instanceof org.xml.sax.ext.LexicalHandler)
            reader.setProperty(
                "http://xml.org/sax/properties/lexical-handler", chandler);
        else
            reader.setProperty(
                "http://xml.org/sax/properties/lexical-handler", null);

        // Only time the actual parsing (transforming)
        startTime = System.currentTimeMillis();

        reader.parse(xml);  // Or should we call transformer.parse(), as the SAXON example has?

        return (System.currentTimeMillis() - startTime);
    }

    /**
     * Transform an xml document using .
     *
     * NEEDSDOC @param templates
     * NEEDSDOC @param xml
     * NEEDSDOC @param out
     * @return milliseconds process time took
     *
     * @throws IOException
     * @throws SAXException
     * @throws TransformException
     */
    private long transformAsXMLFilter(
            Templates templates, InputSource xml, OutputStream out)
                throws TransformException, SAXException, IOException
    {

        long startTime = 0;
        Transformer transformer = templates.newTransformer();

        applyParams(transformer, params);

        // Set the result handling to be a serialization to out
        OutputFormat format = templates.getOutputFormat();
        Serializer serializer = SerializerFactory.getSerializer(format);

        serializer.setOutputStream(out);
        transformer.setContentHandler(serializer.asContentHandler());

        // The transformer will use a SAX parser as it's reader.        
        // XMLReader reader = XMLReaderFactory.createXMLReader();
        // transformer.setParent(reader);
        // Instead of the standard (xerces) parser, use SAXON's favorite parser
        transformer.setParent(new com.icl.saxon.aelfred.SAXDriver());

        // Now, when you call transformer.parse, it will set itself as 
        // the content handler for the parser object (it's "parent"), and 
        // will then call the parse method on the parser.
        // Only time the actual parsing (transforming)
        startTime = System.currentTimeMillis();

        transformer.parse(xml);

        return (System.currentTimeMillis() - startTime);
    }

    /**
     * Transform an xml document using DOMs.
     *
     * NEEDSDOC @param templates
     * NEEDSDOC @param xml
     * NEEDSDOC @param out
     * @return milliseconds process time took
     *
     * @throws Exception
     */
    private long transformDOM2DOM(
            Templates templates, InputSource xml, OutputStream out)
                throws Exception  // Just cover all cases, since we don't care which kind gets thrown
    {

        long startTime = 0;

        if (!processor.getFeature("http://xml.org/trax/features/dom/input"))
        {
            throw new org.xml.sax.SAXNotSupportedException(
                "DOM node processing not supported!");
        }

        DocumentBuilderFactory dfactory =
            DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dfactory.newDocumentBuilder();

        /**
         *   NOTE: this part should really be done as a DOM, but it doesn't
         *   fit with our model - a big TODO for later on
         * // Parse in the stylesheet
         * Node xslDoc = docBuilder.parse(new InputSource(xslID));
         *
         * // Create the template from the DOM of the stylesheet
         * Templates templates = processor.processFromNode(xslDoc);
         */

        // Here, time the parsing of the XML doc, the transformation, 
        //  and the serialization: this seems equivalent to what we 
        //  time in the other methods
        startTime = System.currentTimeMillis();

        // Parse the XML data document the same way
        Node xmlDoc = docBuilder.parse(xml);

        // Saxon example uses a slightly different way:
        // Use Xerces DOM
        //sx DOMParser parser = new DOMParser();
        //sx parser.parse(source(indir+"books.xml"));
        //sx Document xmlDoc = parser.getDocument();
        //sx Node outNode = new DocumentImpl();
        // Run the transformation from the DOM nodes
        org.w3c.dom.Document outNode = docBuilder.newDocument();
        Transformer transformer = templates.newTransformer();

        applyParams(transformer, params);
        transformer.transformNode(xmlDoc, new Result(outNode));

        // Use the serializers to output the result to disk
        OutputFormat format = templates.getOutputFormat();
        Serializer serializer = SerializerFactory.getSerializer(format);

        serializer.setOutputStream(out);
        serializer.asDOMSerializer().serialize(outNode);

        return (System.currentTimeMillis() - startTime);
    }

    /**
     * Preprocess a stylesheet and set it into the processor, based on string inputs.
     * @todo Does NOT respect the "trax.wrapper.type" System property yet.
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

        // Create trax-specific sources
        InputSource xsl = new InputSource(xslStylesheet);

        // Begin timing the whole process
        startTime = System.currentTimeMillis();

        // Read and compile the stylesheet
        savedStylesheet = processor.process(xsl);
        endTime = System.currentTimeMillis();
        stylesheetReady = true;

        return (endTime - startTime);
    }

    /**
     * Process the xmlSource using the xslStylesheet to produce the resultFile.
     * @todo Does NOT respect the "trax.wrapper.type" System property yet.
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

        // Declare variables ahead of time to minimize latency
        long startTime = 0;
        long endTime = 0;

        // Create trax-specific sources
        InputSource xml = new InputSource(xmlSource);

        // May throw IOException
        FileOutputStream resultStream = new FileOutputStream(resultFile);

        // Use the precompiled stylesheet
        Transformer transformer = savedStylesheet.newTransformer();

        applyParams(transformer, params);

        // Begin timing the whole process
        startTime = System.currentTimeMillis();

        // HACK: this should work off of transformType as well!
        transformer.transform(xml, new Result(resultStream));

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

        // Unimplemented; silently fail
        // processor.setDiagnosticsOutput(pw);
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

        // Unimplemented; silently fail
        // TODO: implement for Xalan/LotusXSL specific setFeature()
        // processor.getXMLProcessorLiaison().setIndent(i);
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
     * NEEDSDOC @param t
     * NEEDSDOC @param h
     */
    protected void applyParams(Transformer t, Hashtable h)
    {

        if (params == null)
            return;

        for (Enumeration enum = h.keys();
                enum.hasMoreElements(); /* no increment portion */ )
        {
            Object key = enum.nextElement();

            t.setParameter(key.toString(), null /* namespace TBD */,
                           h.get(key));
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

    /**
     * Create an InputSource that refers to a given File.
     * @author Michael.Kay@icl.com
     *
     * NEEDSDOC @param filename
     *
     * NEEDSDOC ($objectName$) @return
     */
    private static InputSource source(String filename)
    {

        File file = new File(filename);
        String path = file.getAbsolutePath();
        URL url = null;

        try
        {
            url = new URL(path);
        }
        catch (MalformedURLException ex)
        {
            try
            {

                // This is a bunch of weird code that is required to
                // make a valid URL on the Windows platform, due
                // to inconsistencies in what getAbsolutePath returns.
                String fs = System.getProperty("file.separator");

                if (fs.length() == 1)
                {
                    char sep = fs.charAt(0);

                    if (sep != '/')
                        path = path.replace(sep, '/');

                    if (path.charAt(0) != '/')
                        path = '/' + path;
                }

                path = "file://" + path;
                url = new URL(path);
            }
            catch (MalformedURLException e)
            {
                return null;
            }
        }

        return (new InputSource(url.toString()));
    }
}  // end of class SaxonWrapper

