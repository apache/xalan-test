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

import org.apache.xalan.xslt.Process;

import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Implementation of TransformWrapper that calls Xalan's default 
 * command line class Process.main.
 *
 * This is similar to the common usage:
 * java org.apache.xalan.xslt.Process -in foo.xml -xsl foo.xsl ...
 *
 * See OPT_PREFIX for how to pass miscellaneous cmdline args.
 *
 * //@todo support precompiled/serialized stylesheets
 *
 * @author Shane_Curcuru@us.ibm.com
 * @version $Id$
 */
public class XalanProcessWrapper extends TransformWrapperHelper
{

    /**
     * Marker for cmdline items to add to optionArgs.  
     */
    public static final String OPT_PREFIX = TransformWrapper.SET_PROCESSOR_ATTRIBUTES + "cmdline";


    /**
     * Array of additional or optional args for Process.main() calls.  
     */
    protected String[] optionArgs = new String[0];


    /**
     * Cached copy of newProcessor() Hashtable.
     */
    protected Hashtable newProcessorOpts = null;


    /**
     * Get a general description of this wrapper itself.
     *
     * @return Calls org.apache.xalan.xslt.Process -in foo.xml -xsl foo.xsl ...
     */
    public String getDescription()
    {
        return "Calls org.apache.xalan.xslt.Process -in foo.xml -xsl foo.xsl ...";
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
        p.put("traxwrapper.method", "xalanProcess");
        p.put("traxwrapper.desc", getDescription());
        return p;
    }


    /**
     * Actually create/initialize an underlying processor or factory.
     * 
     * No-op, since we always construct a new Process instance 
     * for every transformation.
     *
     * @param options Hashtable of options, unused.
     *
     * @return null, we don't use this
     *
     * @throws Exception covers any underlying exceptions thrown 
     * by the actual implementation
     */
    public Object newProcessor(Hashtable options) throws Exception
    {
        newProcessorOpts = options;
        
        // semi-HACK: set any additional cmdline options from user
        String extraOpts = null;
        try
        {
            // Attempt to use as a Properties block..
            extraOpts = ((Properties)options).getProperty(OPT_PREFIX);
            // But, if null, then try getting as hash anyway
            if (null == extraOpts)
            {
                extraOpts = (String)options.get(OPT_PREFIX);
            }
        }
        catch (ClassCastException cce)
        {
            // .. but fallback to get as Hashtable instead
            extraOpts = (String)options.get(OPT_PREFIX);
        }

        if ((null != extraOpts) && (extraOpts.length() > 0))
        {
            Vector v = new Vector();
            StringTokenizer st = new StringTokenizer(extraOpts, " ");
            while (st.hasMoreTokens())
            {
                v.addElement(st.nextToken());
            }
            optionArgs = new String[v.size()];
            v.copyInto(optionArgs);
        }
        //@todo do we need to do any other cleanup?
        reset(false);
        return null;
    }


    /**
     * Transform supplied xmlName file with the stylesheet in the 
     * xslName file into a resultName file.
     *
     * Names are assumed to be local path\filename references, and 
     * will be converted to URLs as needed for any underlying 
     * processor implementation.
     *
     * //@todo attempt to capture any System.out/err output 
     * from this command line class for logging
     *
     * @param xmlName local path\filename of XML file to transform
     * @param xslName local path\filename of XSL stylesheet to use
     * @param resultName local path\filename to put result in
     *
     * @return array of longs denoting timing of only these parts of 
     * our operation: IDX_OVERALL
     *
     * @throws Exception any underlying exceptions from the 
     * wrappered processor are simply allowed to propagate; throws 
     * a RuntimeException if any other problems prevent us from 
     * actually completing the operation
     */
    public long[] transform(String xmlName, String xslName, String resultName)
        throws Exception
    {
        long startTime = 0;
        long overall = 0;
        
        // Untimed: Apply any parameters needed
        applyParameters(null);

        // Untimed: Construct command line array
        final int NUM_FARGS = 6; // Need 6 extra slots for 
                                     // -in XML -xsl XSL -out OUT
        String args[] = new String[optionArgs.length + NUM_FARGS];

        args[0] = "-in";
        args[1] = QetestUtils.filenameToURL(xmlName);
        args[2] = "-xsl";
        args[3] = QetestUtils.filenameToURL(xslName);
        args[4] = "-out";
        args[5] = resultName;
        System.arraycopy(optionArgs, 0, args, NUM_FARGS, optionArgs.length);

        // Timed: entire operation
        //@todo attempt to capture any System.out/err output 
        //  from this command line class for logging
        startTime = System.currentTimeMillis();
        org.apache.xalan.xslt.Process.main(args);
        overall = System.currentTimeMillis() - startTime;

        long[] times = getTimeArray();
        times[IDX_OVERALL] = overall;
        return times;
    }


    /**
     * Pre-build/pre-compile a stylesheet.
     *
     * Although the actual mechanics are implementation-dependent, 
     * most processors have some method of pre-setting up the data 
     * needed by the stylesheet itself for later use in transforms.
     * In TrAX/javax.xml.transform, this equates to creating a 
     * Templates object.
     * 
     * Sets isStylesheetReady() to true if it succeeds.  Users can 
     * then call transformWithStylesheet(xmlName, resultName) to 
     * actually perform a transformation with this pre-built 
     * stylesheet.
     *
     * @param xslName local path\filename of XSL stylesheet to use
     *
     * @return array of longs denoting timing of only these parts of 
     * our operation: IDX_OVERALL, IDX_XSLBUILD
     *
     * @throws Exception any underlying exceptions from the 
     * wrappered processor are simply allowed to propagate; throws 
     * a RuntimeException if any other problems prevent us from 
     * actually completing the operation
     *
     * @see #transformWithStylesheet(String xmlName, String resultName)
     */
    public long[] buildStylesheet(String xslName) throws Exception
    {
        throw new IllegalStateException("XalanProcessWrapper.transformWithStylesheet not implemented!");
    }


    /**
     * Transform supplied xmlName file with a pre-built/pre-compiled 
     * stylesheet into a resultName file.  
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
        throw new IllegalStateException("XalanProcessWrapper.transformWithStylesheet not implemented!");
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
        throw new IllegalStateException("XalanProcessWrapper.transformWithStylesheet not implemented!");
    }


    /**
     * Reset our parameters and wrapper state, and optionally 
     * force creation of a new underlying processor implementation.
     *
     * This always clears our built stylesheet and any parameters 
     * that have been set.  If newProcessor is true, also forces a 
     * re-creation of our underlying processor as if by calling 
     * newProcessor().
     *
     * @param newProcessor if we should reset our underlying 
     * processor implementation as well
     */
    public void reset(boolean newProcessor)
    {
        super.reset(newProcessor); // clears indent and parameters
        m_stylesheetReady = false;
    }


    /**
     * Apply a single parameter to a Transformer.
     *
     * No-op, we don't yet support parameters.
     *
     * @param passThru to be passed to each applyParameter() method 
     * @param namespace for the parameter, may be null
     * @param name for the parameter, should not be null
     * @param value for the parameter, may be null
     */
    protected void applyParameter(Object passThru, String namespace, 
                                  String name, Object value)
    {
        return;
    }


}
