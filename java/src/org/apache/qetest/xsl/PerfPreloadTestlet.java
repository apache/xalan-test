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
 * PerfPreloadTestlet.java
 *
 */
package org.apache.qetest.xsl;
import org.apache.qetest.*;
import org.apache.qetest.xslwrapper.ProcessorWrapper;

import java.io.File;
import java.util.Hashtable;

/**
 * Testlet to capture basic timing performance data using 
 * precompiled/prebuilt stylesheets.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class PerfPreloadTestlet extends TestletImpl
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.PerfPreloadTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new StylesheetDatalet(); }

    /**
     * Accesor method for a brief description of this test.  
     *
     * @return String describing what this PerfPreloadTestlet does.
     */
    public String getDescription()
    {
        return "PerfPreloadTestlet: preprocesses supplied file over multiple iterations and logs timing data";
    }


    /** Markers for performance logging - Preload time. */
    public static final String PERF_PRELOAD = "UPre;";

    /** Markers for performance logging - single iteration time. */
    public static final String PERF_ITERATION = "UItr;";

    /** Markers for performance logging - average of iteration times. */
    public static final String PERF_AVERAGE = "UAvg;";

    /** Markers for memory logging. */
    public static final String PERF_MEMORY = "UMem;";

    /**
     * Run this PerfPreloadTestlet: execute it's test and return.
     *
     * @param Datalet to use as data point for the test.
     */
    public void execute(Datalet d)
	{
        StylesheetDatalet datalet = null;
        try
        {
            datalet = (StylesheetDatalet)d;
            
        }
        catch (ClassCastException e)
        {
            logger.checkErr("Incorrect Datalet type provided, threw:" + e.toString());
            return;
        }
        
        // Cleanup outName - delete the file on disk
        try
        {
            File outFile = new File(datalet.outputName);
            boolean btmp = outFile.delete();
            logger.logMsg(Logger.TRACEMSG, "Deleting OutFile of::" + datalet.outputName
                                 + " status: " + btmp);
        }
        catch (SecurityException se)
        {
            logger.logMsg(Logger.WARNINGMSG, "Deleting OutFile of::" + datalet.outputName
                                   + " threw: " + se.toString());
            // But continue anyways...
        }

        // Go do performance stuff!
        try
        {
            // Save options from the datalet in convenience variables
            int iterations = 10;
            boolean preload = true;
            try
            {
                iterations = Integer.parseInt(datalet.options.getProperty("iterations"));
            }
            catch (Exception e) { /* no-op, leave as default */ }
            try
            {
                preload = (new Boolean(datalet.options.getProperty("preload"))).booleanValue();
            }
            catch (Exception e) { /* no-op, leave as default */ }

            // Store local copies of XSL, XML references to avoid 
            //  potential for changing datalet            
            String inputName = datalet.inputName;
            String xmlName = datalet.xmlName;

            // Create a new ProcessorWrapper of appropriate flavor
            ProcessorWrapper processorWrapper = ProcessorWrapper.getWrapper(datalet.flavor);
            if (null == processorWrapper.createNewProcessor(null))
            {
                logger.checkErr("ERROR: could not create processorWrapper, aborting.");
                return;
            }

            logger.logMsg(Logger.TRACEMSG, "executing with: inputName=" + inputName
                          + " xmlName=" + xmlName + " outputName=" + datalet.outputName
                          + " goldName=" + datalet.goldName + " flavor="  + datalet.flavor
                          + " iterations=" + iterations + " preload=" + preload);
            // Prime the pump, so to speak, if desired
            long preloadPreprocessTime = 0L;
            long preloadProcessTime = 0L;
            if (preload)
            {
                logMemory(true);  // dumps Runtime.freeMemory/totalMemory
                // Note: we don't support embedded files, obviously

                preloadPreprocessTime = processorWrapper.preProcessStylesheet(inputName);
                if (preloadPreprocessTime == ProcessorWrapper.ERROR)
                {
                    logger.checkFail("ERROR: Preload1 process had a problem of::"
                                     + datalet.inputName);
                    return;
                }

                preloadProcessTime = processorWrapper.processToFile(xmlName, datalet.outputName);
                if (preloadProcessTime == ProcessorWrapper.ERROR)
                {
                    logger.checkFail("ERROR: Preload2 process had a problem of::"
                                     + datalet.inputName);
                    return;
                }
                // @todo add verification of output file
            }

            logMemory(true);  // dumps Runtime.freeMemory/totalMemory

            // Save individual times in a buffer; I'm not sure 
            //  what this will be used for but it seems interesting
            StringBuffer buf = new StringBuffer();
            long aggregatePreprocess = 0L;
            long aggregateProcess = 0L;
            int ctr;
            for (ctr = 1; ctr <= iterations; ctr++)
            {
                // Note: We re-write the same output file each time, so 
                //       I suppose in theory that could affect future iterations
                long preprocessTime = processorWrapper.preProcessStylesheet(inputName);

                if (preprocessTime == ProcessorWrapper.ERROR)
                {
                    logger.checkFail("ERROR: PerfPreloadTestlet1 problem on iteration(" 
                                     + ctr + ") of::" + datalet.inputName);
                    return;
                }

                // Increment our overall counter
                aggregatePreprocess += preprocessTime;
                // Save individual timing info, semicolon delimited
                //  (no reason for semicolons, just picked on them)
                //  note that preprocess and process times are intermixed, 
                //  this is left as an exercise for the reader to write 
                //  some XSL to unmix them, if desired 8-)
                buf.append(preprocessTime);
                buf.append(';');

                long processTime = processorWrapper.processToFile(xmlName, datalet.outputName);

                if (processTime == ProcessorWrapper.ERROR)
                {
                    logger.checkFail("ERROR: PerfPreloadTestlet2 problem on iteration(" 
                                     + ctr + ") of::" + datalet.inputName);
                    return;
                }

                // Increment our overall counter
                aggregateProcess += processTime;
                buf.append(processTime);
                buf.append(';');

                // Should really make this optional
                logMemory(false);
            }

            // Log special performance element with our timing
            Hashtable attrs = new Hashtable();
            // desc is description of our algorithim
            attrs.put("desc", getDescription());
            // idref is the individual filename
            attrs.put("idref", (new File(datalet.inputName)).getName());
            // inputName is the actual name we gave to the processor
            attrs.put("inputName", inputName);
            // preload.* are the times it took in the preload stage
            if (preload)
            {
                attrs.put("preload.preprocess", new Long(preloadPreprocessTime));
                attrs.put("preload.process", new Long(preloadProcessTime));
            }
            // preprocess.avg is the average style sheet build processing time...
            attrs.put("preprocess.avg", new Long(aggregatePreprocess / iterations));
            // ... and process.avg is the average do transform processing time...
            attrs.put("process.avg", new Long(aggregateProcess / iterations));
            // ... over a number of iterations
            attrs.put("iterations", new Integer(iterations));
            // The hackish buf at the end is simply a semicolon
            //  delimited list of individual timings, just for 
            //  fun - I'm not even sure we're going to use it
            logger.logElement(Logger.STATUSMSG, "perf", attrs, PERF_ITERATION + buf.toString());
        }
        catch (Throwable t)
        {
            logger.checkFail("PerfPreloadTestlet of::" + datalet.inputName
                                 + " threw: " + t.toString());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            t.printStackTrace(pw);
            logger.logArbitrary(Logger.ERRORMSG, sw.toString());
            return;
        }

        // If we get here, attempt to validate the contents of 
        //  the last outputFile created
        CheckService fileChecker = new XHTFileCheckService();
        if (Logger.PASS_RESULT
            != fileChecker.check(logger,
                                 new File(datalet.outputName), 
                                 new File(datalet.goldName), 
                                 getDescription() + ", " + datalet.getDescription())
           )
            logger.logMsg(Logger.WARNINGMSG, "Failure reason: " + fileChecker.getExtendedInfo());
	}


    /**
     * Worker method: just reports Runtime.totalMemory/freeMemory.  
     */
    protected void logMemory(boolean doGC)
    {
        if (doGC)
        {
            Runtime.getRuntime().gc();
        }
        logger.logStatistic(Logger.STATUSMSG, Runtime.getRuntime().freeMemory(), 0, PERF_MEMORY + "freeMemory");
        logger.logStatistic(Logger.STATUSMSG, Runtime.getRuntime().totalMemory(), 0, PERF_MEMORY + "totalMemory");
    }

}  // end of class PerfPreloadTestlet

