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

import org.apache.xalan.xsltc.cmdline.Compile;
import org.apache.xalan.xsltc.cmdline.Transform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Implementation of TransformWrapper that uses the command line 
 * wrappers of XSLTC - interim use only.
 *
 * <b>Important!</b> This wrapper is for temporary use only: we 
 * should really focus on getting XSLTC to use JAXP 1.1 as it's 
 * interface, and then (as needed) write another custom wrapper 
 * that calls XSLTC API's directly.
 * 
 * @author Shane Curcuru
 * @version $Id$
 */
public class XsltcMainWrapper extends TransformWrapperHelper
{

    protected static final String XSLTC_COMPILER_CLASS = "org.apache.xalan.xsltc.cmdline.Compile";
    protected static final String XSLTC_RUNTIME_CLASS = "org.apache.xalan.xsltc.cmdline.Transform";

    /**
     * Cached copy of newProcessor() Hashtable.
     */
    protected Hashtable newProcessorOpts = null;


    /**
     * Get a general description of this wrapper itself.
     *
     * @return Uses XSLTC command line to perform transforms
     */
    public String getDescription()
    {
        return "Uses XSLTC command line to perform transforms";
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
        p.put("traxwrapper.method", "streams");
        p.put("traxwrapper.desc", getDescription());
        return p;
    }


    /**
     * Actually create/initialize an underlying processor or factory.
     * 
     * Effectively a no-op; returns null.  Just forces a reset().
     *
     * @param options Hashtable of options, unused.
     *
     * @return (Object)getProcessor() as a side-effect, this will 
     * be null if there was any problem creating the processor OR 
     * if the underlying implementation doesn't use this
     *
     * @throws Exception covers any underlying exceptions thrown 
     * by the actual implementation
     */
    public Object newProcessor(Hashtable options) throws Exception
    {
        newProcessorOpts = options;
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
     * @param xmlName local path\filename of XML file to transform
     * @param xslName local path\filename of XSL stylesheet to use
     * @param resultName local path\filename to put result in
/* TWA - temp hack; use results dir to get path for to use for a dir to put
the translets
*/
     /*
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
        long startTime = 0;
        long xslBuild = 0;
        long transform = 0;

        // java org.apache.xalan.xlstc.cmdline.Compile play1.xsl
        // java org.apache.xalan.xlstc.cmdline.Transform play.xml play1 >stdout

        // Timed: compile stylesheet class from XSL file
//        String[] args1 = new String[2];
//        args1[0] = "-s"; // Don't allow System.exit
/* TWA - commented out the following for short-term
Problem when local path/file is being used, somewhere a file://// prefix is 
being appended to the filename and xsltc can't find the file even with the -u
So I strip off the protocol prefix and pass the local path/file
        args1[1] = "-u"; // Using URIs
        args1[2] = xslName;
*/
/* TWA - temporay hack to construct and pass a directory for translets */
        int last = resultName.lastIndexOf('/');
        String tdir = resultName.substring(0, last);
        int next = tdir.lastIndexOf('/');
        String transletsdirName = tdir.substring(0, next);

        String[] args1 = new String[4];
        args1[0] = "-s";
        args1[1] = "-d";
        args1[2] = transletsdirName;
        args1[3] = xslName;
        int idx = xslName.indexOf("file:////");
        if (idx != -1){
               xslName = new String(xslName.substring(8));
               args1[3] = xslName;
        }
        startTime = System.currentTimeMillis();
        /// Transformer transformer = factory.newTransformer(new StreamSource(xslName));
        Compile.main(args1);
        xslBuild = System.currentTimeMillis() - startTime;

        // Verify output file was created
        // WARNING: assumption of / here, which means we assume URI not local path - needs revisiting
        int nameStart = xslName.lastIndexOf('/') + 1;
        String baseName = xslName.substring(nameStart);
        int extStart = baseName.lastIndexOf('.');
        if (extStart > 0)
            baseName = baseName.substring(0, extStart);

        // Untimed: Apply any parameters needed
        // applyParameters(transformer);

        // Timed: read/build xml, transform, and write results

/* TWA - I don't see how this could have worked, there is no -s option in DefaultRun
so passing it in the args2 caused usuage messages to be output.
Also, we shouldn't use the -u option unless we are really using URLs, 
I'm just trying to get it to work with local path/files. With or without the 
-u option, the files were getting a file://// prefix with caused them to be not found
        String[] args2 = new String[3];
        args2[0] = "-s"; // Don't allow System.exit
        args2[1] = "-u"; // Using URIs
        args2[2] = xmlName;
        args2[3] = baseName;    // Just basename of the .class file, without the .class
                                // Note that . must be on CLASSPATH to work!
*/
        String[] args2 = new String[2];
        args2[0] = xmlName;
        int idx2 = xmlName.indexOf("file:////");
        if (idx2 != -1){
               args2[0] = new String(xmlName.substring(8));
        }
        args2[1] = baseName;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newSystemOut = new PrintStream(baos);
        PrintStream saveSystemOut = System.out;
        startTime = System.currentTimeMillis();
        // transformer.transform(new StreamSource(xmlName), new StreamResult(resultName));
        try
        {
            // Capture System.out into our byte array
            System.setOut(new PrintStream(baos));
            Transform.main(args2);
        }
        finally
        {
            // Be sure to restore System stuff!
            System.setOut(saveSystemOut);
        }
        // Writing data should really go in separate timing loop
        FileOutputStream fos = new FileOutputStream(resultName);
        fos.write(baos.toByteArray());
        fos.close();
        transform = System.currentTimeMillis() - startTime;

        File compiledXslClass = new File(baseName + ".class");
        //@todo WARNING! We REALLY need to clean up the name*.class files when we're done!!! -sc
        // TWA - we should probably use the -d option to put them in a desired directory first
        // I commented out the delete, to see if the translets were getting compiled
//        if (compiledXslClass.exists())
//             compiledXslClass.delete();

        long[] times = getTimeArray();
        times[IDX_OVERALL] = xslBuild + transform;
        times[IDX_XSLBUILD] = xslBuild;
        times[IDX_TRANSFORM] = transform;
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
        throw new RuntimeException("buildStylesheet not implemented yet!");
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
        if (!isStylesheetReady())
            throw new IllegalStateException("transformWithStylesheet() when isStylesheetReady() == false");

        throw new RuntimeException("transformWithStylesheet not implemented yet!");
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
        throw new RuntimeException("transformEmbedded not implemented yet!");
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
        // builtTemplates = null;
        if (newProcessor)
        {
            try
            {
                newProcessor(newProcessorOpts);
            }
            catch (Exception e)
            {
                //@todo Hmm: what should we do here?
            }
        }
    }


    /**
     * Apply a single parameter to a Transformer.
     *
     * WARNING: Not implemented! 27-Apr-01
     *
     * @param passThru to be passed to each applyParameter() method 
     * call - for TrAX, you might pass a Transformer object.
     * @param namespace for the parameter, may be null
     * @param name for the parameter, should not be null
     * @param value for the parameter, may be null
     */
    protected void applyParameter(Object passThru, String namespace, 
                                  String name, Object value)
    {
        /* WARNING: Not implemented! 27-Apr-01 */
    }
}
