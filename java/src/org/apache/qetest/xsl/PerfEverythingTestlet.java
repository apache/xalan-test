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
 * PerfEverythingTestlet.java
 *
 */
package org.apache.qetest.xsl;
import org.apache.qetest.*;
import org.apache.qetest.xslwrapper.ProcessorWrapper;

import java.io.File;
import java.util.Hashtable;

/**
 * Testlet to capture specific timing performance data.
 * This Testlet attempts to mirror what one of our Xalan-C 
 * performance tests does, at least as well as we can compare 
 * Java processing to the C processing model.
 * Note that as a standalone Testlet, this class does waaaay too 
 * much stuff: Testlets should really be smaller test algorithims
 * than here.  However for performance measurements and reporting 
 * purposes, we really need to do it all here.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class PerfEverythingTestlet extends TestletImpl
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.PerfEverythingTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new PerformanceDatalet(); }

    /**
     * Accesor method for a brief description of this test.  
     *
     * @return String describing what this PerfEverythingTestlet does.
     */
    public String getDescription()
    {
        return "PerfEverythingTestlet: processes supplied file in multiple ways over multiple iterations and logs timing data";
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
     * Run this PerfEverythingTestlet: execute it's test and return.
     * This algorithim processes the supplied file in several 
     * different ways:
     * <ul>
     * <li>Run one full end-to-end transform and log timing 
     * (a 'preload' process: note that results may vary for this 
     * timing depending on whether you run this Testlet standalone 
     * or if you're using StylesheetTestletDriver to run it, due at 
     * least to classloading issues, if nothing else)</li>
     * <li>Preprocess the stylesheet once and log timing</li>
     * <li>Process that preprocessed stylesheet once and log timing</li>
     * <li>Loop iterations times and preprocess the stylesheet, 
     * then do a transform with it, and log each time</li>
     * <li>Loop iterations times and do a full end-to-end transform
     * and log each time</li>
     * </ul>
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
            logger.checkErr("Datalet provided is not a StylesheetDatalet; cannot continue");
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

        // Setup: Save options from the datalet in convenience variables
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

        // Setup: store various timing data in convenience variables
        long singletransform = 0L;  // Very first Preload end-to-end transform
        long etoe = 0L;     // First end-to-end transform during iterations
        long avgetoe = 0L;  // Average of end-to-end transforms during iterations
        long parsexsl = 0L;     // First stylesheet preprocess during iterations
        long avgparsexsl = 0L;  // Average of stylesheet preprocess during iterations
        long unparsedxml = 0L;   // First stylesheet process during iterations
        long avgunparsedxml = 0L;// Average of stylesheet process during iterations

        // Setup: Store local copies of XSL, XML references for 
        //  potential change to URLs            
        String inputName = datalet.inputName;
        String xmlName = datalet.xmlName;
        if (datalet.useURL)
        {
            // inputName may not exist if it's an embedded test
            if (null != inputName)
                inputName = QetestUtils.filenameToURL(inputName);
            xmlName = QetestUtils.filenameToURL(xmlName);
        }
        // Go do performance stuff!
        try
        {
            // Setup: Create a new ProcessorWrapper of appropriate flavor
            ProcessorWrapper processorWrapper = ProcessorWrapper.getWrapper(datalet.flavor);
            if (null == processorWrapper.createNewProcessor(null))
            {
                logger.checkErr("ERROR: could not create processorWrapper, aborting.");
                return;
            }
            logger.logMsg(Logger.TRACEMSG, "executing with: inputName=" + inputName
                          + " xmlName=" + xmlName + " outputName=" + datalet.outputName
                          + " goldName=" + datalet.goldName + " flavor="  + datalet.flavor
                          + " iterations=" + iterations + " preload=" + preload
                          + " algorithim=" + getDescription());
            //@todo make various logMemory calls optional
            logMemory(true);

            // Measure(singletransform): Very first Preload end-to-end transform
            singletransform = processorWrapper.processToFile(xmlName, inputName,
                                                datalet.outputName);
            if (singletransform == ProcessorWrapper.ERROR)
            {
                logger.checkFail("ERROR: Preload0 error with:" + datalet.inputName);
                return;
            }

            logMemory(true);
            // Measure(parsexsl): once: first preprocess
            parsexsl = processorWrapper.preProcessStylesheet(inputName);
            if (parsexsl == ProcessorWrapper.ERROR)
            {
                logger.checkFail("ERROR: Preload1 error with:" + datalet.inputName);
                return;
            }
            // Measure(unparsedxml): once: first process
            unparsedxml = processorWrapper.processToFile(xmlName, datalet.outputName);
            if (unparsedxml == ProcessorWrapper.ERROR)
            {
                logger.checkFail("ERROR: Preload2 error with:" + datalet.inputName);
                return;
            }

            for (int ctr = 1; ctr <= iterations; ctr++)
            {
                // Measure(avgparsexsl): average preprocess
                long preprocessTime = processorWrapper.preProcessStylesheet(inputName);
                if (preprocessTime == ProcessorWrapper.ERROR)
                {
                    logger.checkFail("ERROR: PreprocessLoop1 error iter(" + ctr + ") with:" + datalet.inputName);
                    return;
                }
                avgparsexsl += preprocessTime;

                // Measure(avgunparsedxml): average process
                long processTime = processorWrapper.processToFile(xmlName, datalet.outputName);
                if (processTime == ProcessorWrapper.ERROR)
                {
                    logger.checkFail("ERROR: PreprocessLoop2 error iter(" + ctr + ") with:" + datalet.inputName);
                    return;
                }
                avgunparsedxml += processTime;
                logMemory(false);
            }

            // Measure(etoe): once: first full process
            etoe = processorWrapper.processToFile(xmlName, inputName, datalet.outputName);
            if (etoe == ProcessorWrapper.ERROR)
            {
                logger.checkFail("ERROR: Process error with:" + datalet.inputName);
                return;
            }
            for (int ctr = 1; ctr <= iterations; ctr++)
            {
                // Measure(avgetoe): average full process
                long retVal = processorWrapper.processToFile(xmlName, inputName, datalet.outputName);
                if (retVal == ProcessorWrapper.ERROR)
                {
                    logger.checkFail("ERROR: ProcessLoop error iter(" + ctr + ") with:" + datalet.inputName);
                    return;
                }
                avgetoe += retVal;
                logMemory(false);
            }

            // Log special performance element with our timing
            Hashtable attrs = new Hashtable();
            // idref is the individual filename
            attrs.put("idref", (new File(datalet.inputName)).getName());
            // inputName is the actual name we gave to the processor
            attrs.put("inputName", inputName);

            attrs.put("singletransform", new Long(singletransform)); // Very first Preload end-to-end transform
            attrs.put("etoe", new Long(etoe)); // First end-to-end transform during iterations
            attrs.put("avgetoe", new Long(avgetoe / iterations)); // Average of end-to-end transforms during iterations
            attrs.put("parsexsl", new Long(parsexsl)); // First stylesheet preprocess during iterations
            attrs.put("avgparsexsl", new Long(avgparsexsl / iterations)); // Average of stylesheet preprocess during iterations
            attrs.put("unparsedxml", new Long(unparsedxml)); // First stylesheet process during iterations
            attrs.put("avgunparsedxml", new Long(avgunparsedxml / iterations)); // Average of stylesheet process during iterations

            logger.logElement(Logger.STATUSMSG, "perf", attrs, PERF_ITERATION);
        }
        catch (Throwable t)
        {
            logger.checkFail("PerfEverythingTestlet with:" + datalet.inputName
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

}  // end of class PerfEverythingTestlet

