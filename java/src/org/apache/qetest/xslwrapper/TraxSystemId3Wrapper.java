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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.qetest.xslwrapper;
import org.apache.qetest.QetestUtils;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.util.Hashtable;
import java.util.Properties;

/**
 * Implementation of TransformWrapper that uses the TrAX API and 
 * uses systemId URL's for it's sources; plus always transforms 
 * the xml file <b>three</b> times.
 *
 * This is the most common usage:
 * transformer = factory.newTransformer(new StreamSource(xslURL));
 * transformer.transform(new StreamSource(xmlURL), new StreamResult(resultFileName));
 *
 * <b>Important!</b>  The underlying System property of 
 * javax.xml.transform.TransformerFactory will determine the actual 
 * TrAX implementation used.  This value will be reported out in 
 * our getProcessorInfo() method.
 * 
 * @author Shane Curcuru
 * @version $Id$
 */
public class TraxSystemId3Wrapper extends TraxSystemIdWrapper
{

    /**
     * Get a general description of this wrapper itself.
     *
     * @return Uses TrAX to perform THREE transforms from StreamSource(systemId)
     */
    public String getDescription()
    {
        return "Uses TrAX to perform THREE transforms from StreamSource(systemId)";
    }


    /**
     * Get a specific description of the wrappered processor.  
     *
     * @return specific description of the underlying processor or 
     * transformer implementation: this should include both the 
     * general product name, as well as specific version info.  If 
     * possible, should be implemented without actively creating 
     * an underlying processor.
     */
    public Properties getProcessorInfo()
    {
        Properties p = TraxWrapperUtils.getTraxInfo();
        p.put("traxwrapper.method", "systemId3");
        p.put("traxwrapper.desc", getDescription());
        return p;
    }


    /**
     * Transform supplied xmlName file with the stylesheet in the 
     * xslName file into a resultName file <b>three</b> times.
     *
     * Names are assumed to be local path\filename references, and 
     * will be converted to URLs as needed for any underlying 
     * processor implementation.
     *
     * @param xmlName local path\filename of XML file to transform
     * @param xslName local path\filename of XSL stylesheet to use
     * @param resultName local path\filename to put result in
     *
     * @return array of longs denoting timing of only these parts of 
     * our operation: IDX_OVERALL, IDX_XSLBUILD, IDX_TRANSFORM
     *
     * @throws Exception any underlying exceptions from the 
     * wrappered processor are simply allowed to propagate; throws 
     * a RuntimeException if any other problems prevent us from 
     * actually completing the operation
     */
    public long[] transform(String xmlName, String xslName, String resultName)
        throws Exception
    {
        preventFootShooting();
        long startTime = 0;
        long xslBuild = 0;
        long transform = 0;
        
        // Timed: read/build xsl from a URL
        startTime = System.currentTimeMillis();
        Transformer transformer = factory.newTransformer(
                new StreamSource(QetestUtils.filenameToURL(xslName)));
        xslBuild = System.currentTimeMillis() - startTime;

        // Untimed: Set any of our options as Attributes on the transformer
        TraxWrapperUtils.setAttributes(transformer, newProcessorOpts);

        // Untimed: Apply any parameters needed
        applyParameters(transformer);

        // Timed: read/build xml, transform, and write results
        startTime = System.currentTimeMillis();
        transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlName)), 
                              new StreamResult(resultName));
        transform = System.currentTimeMillis() - startTime;

        // Only time the first transform, but do two more
        // This is to test transformer re-use
        transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlName)), 
                      new StreamResult(resultName));

        transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlName)), 
                      new StreamResult(resultName));

        long[] times = getTimeArray();
        times[IDX_OVERALL] = xslBuild + transform;
        times[IDX_XSLBUILD] = xslBuild;
        times[IDX_TRANSFORM] = transform;
        return times;
    }


    /**
     * Transform supplied xmlName file with a pre-built/pre-compiled 
     * stylesheet into a resultName file <b>three</b> times.  
     *
     * User must have called buildStylesheet(xslName) beforehand,
     * obviously.
     * Names are assumed to be local path\filename references, and 
     * will be converted to URLs as needed.
     *
     * @param xmlName local path\filename of XML file to transform
     * @param resultName local path\filename to put result in
     *
     * @return array of longs denoting timing of only these parts of 
     * our operation: IDX_OVERALL, IDX_XSLBUILD, IDX_TRANSFORM
     *
     * @throws Exception any underlying exceptions from the 
     * wrappered processor are simply allowed to propagate; throws 
     * a RuntimeException if any other problems prevent us from 
     * actually completing the operation; throws an 
     * IllegalStateException if isStylesheetReady() == false.
     *
     * @see #buildStylesheet(String xslName)
     */
    public long[] transformWithStylesheet(String xmlName, String resultName)
        throws Exception
    {
        if (!isStylesheetReady())
            throw new IllegalStateException("transformWithStylesheet() when isStylesheetReady() == false");

        preventFootShooting();
        long startTime = 0;
        long transform = 0;
        
        // UNTimed: get Transformer from Templates
        Transformer transformer = builtTemplates.newTransformer();

        // Untimed: Set any of our options as Attributes on the transformer
        TraxWrapperUtils.setAttributes(transformer, newProcessorOpts);

        // Untimed: Apply any parameters needed
        applyParameters(transformer);

        // Timed: read/build xml, transform, and write results
        startTime = System.currentTimeMillis();
        transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlName)), 
                              new StreamResult(resultName));
        transform = System.currentTimeMillis() - startTime;

        // Only time the first transform, but do two more
        // This is to test transformer re-use
        transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlName)), 
                              new StreamResult(resultName));

        transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlName)), 
                              new StreamResult(resultName));

        long[] times = getTimeArray();
        times[IDX_OVERALL] = transform;
        times[IDX_TRANSFORM] = transform;
        return times;
    }


    /**
     * Transform supplied xmlName file with a stylesheet found in an 
     * xml-stylesheet PI into a resultName file.
     *
     * Names are assumed to be local path\filename references, and 
     * will be converted to URLs as needed.  Implementations will 
     * use whatever facilities exist in their wrappered processor 
     * to fetch and build the stylesheet to use for the transform.
     *
     * @param xmlName local path\filename of XML file to transform
     * @param resultName local path\filename to put result in
     *
     * @return array of longs denoting timing of only these parts of 
     * our operation: IDX_OVERALL, IDX_XSLREAD (time to find XSL
     * reference from the xml-stylesheet PI), IDX_XSLBUILD, (time 
     * to then build the Transformer therefrom), IDX_TRANSFORM
     *
     * @throws Exception any underlying exceptions from the 
     * wrappered processor are simply allowed to propagate; throws 
     * a RuntimeException if any other problems prevent us from 
     * actually completing the operation
     */
    public long[] transformEmbedded(String xmlName, String resultName)
        throws Exception
    {
        preventFootShooting();
        long startTime = 0;
        long xslRead = 0;
        long xslBuild = 0;
        long transform = 0;
        
        // Timed: readxsl from the xml document
        startTime = System.currentTimeMillis();
        Source xslSource = factory.getAssociatedStylesheet(new StreamSource(QetestUtils.filenameToURL(xmlName)), 
                                                              null, null, null);
        xslRead = System.currentTimeMillis() - startTime;

        // Timed: build xsl from a URL
        startTime = System.currentTimeMillis();
        Transformer transformer = factory.newTransformer(xslSource);
        xslBuild = System.currentTimeMillis() - startTime;

        // Untimed: Set any of our options as Attributes on the transformer
        TraxWrapperUtils.setAttributes(transformer, newProcessorOpts);

        // Untimed: Apply any parameters needed
        applyParameters(transformer);

        // Timed: read/build xml, transform, and write results
        startTime = System.currentTimeMillis();
        transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlName)), 
                              new StreamResult(resultName));
        transform = System.currentTimeMillis() - startTime;

        // Only time the first transform, but do two more
        // This is to test transformer re-use
        transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlName)), 
                              new StreamResult(resultName));

        transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlName)), 
                              new StreamResult(resultName));

        long[] times = getTimeArray();
        times[IDX_OVERALL] = xslRead + xslBuild + transform;
        times[IDX_XSLREAD] = xslRead;
        times[IDX_XSLBUILD] = xslBuild;
        times[IDX_TRANSFORM] = transform;
        return times;
    }
}
