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
 * PerformanceTestlet.java
 *
 */
package org.apache.qetest.xsl;
import org.apache.qetest.*;
import org.apache.qetest.xslwrapper.ProcessorWrapper;

import java.io.File;

/**
 * Testlet to capture basic timing performance data.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class PerformanceTestlet extends TestletImpl
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.PerformanceTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = new PerformanceDatalet(); }

    /**
     * Accesor method for a brief description of this test.  
     *
     * @return String describing what this PerformanceTestlet does.
     */
    public String getDescription()
    {
        return "PerformanceTestlet: processes supplied file over multiple iterations and logs timing data";
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
     * Run this PerformanceTestlet: execute it's test and return.
     *
     * @param Datalet to use as data point for the test.
     */
    public void execute(Datalet d)
	{
        PerformanceDatalet datalet = null;
        try
        {
            datalet = (PerformanceDatalet)d;
            
        }
        catch (ClassCastException e)
        {
            logger.checkErr("Datalet provided is not a PerformanceDatalet; cannot continue");
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
            // Create a new ProcessorWrapper of appropriate flavor
            ProcessorWrapper processorWrapper = ProcessorWrapper.getWrapper(datalet.flavor);
            if (null == processorWrapper.createNewProcessor(null))
            {
                logger.checkErr("ERROR: could not create processorWrapper, aborting.");
                return;
            }

            logger.logMsg(Logger.TRACEMSG, "executing with: inputName=" + datalet.inputName
                          + " xmlName=" + datalet.xmlName + " outputName=" + datalet.outputName
                          + " goldName=" + datalet.goldName + " flavor="  + datalet.flavor
                          + " iterations=" + datalet.iterations + " preload=" + datalet.preload);
            // Prime the pump, so to speak, if desired
            if (datalet.preload)
            {
                logMemory(true);  // dumps Runtime.freeMemory/totalMemory
                long preloadTime = processorWrapper.processToFile(datalet.xmlName, datalet.inputName,
                                                    datalet.outputName);

                if (preloadTime == ProcessorWrapper.ERROR)
                {
                    logger.checkFail("ERROR: Preload process had a problem of::"
                                     + datalet.inputName);
                    return;
                }

                logger.logStatistic(Logger.STATUSMSG, preloadTime, 0, 
                                    PERF_PRELOAD + "Preload process of::" + datalet.inputName);
                // @todo add verification of output file
            }

            logMemory(true);  // dumps Runtime.freeMemory/totalMemory

            long aggregate = 0L;
            int ctr;
            for (ctr = 1; ctr <= datalet.iterations; ctr++)
            {
                long retVal;

                // Note: We re-write the same output file each time, so 
                //       I suppose in theory that could affect future iterations
                retVal = processorWrapper.processToFile(datalet.xmlName, datalet.inputName, datalet.outputName);

                if (retVal == ProcessorWrapper.ERROR)
                {
                    logger.checkFail("ERROR: PerformanceTestlet problem on iteration(" 
                                     + ctr + ") of::" + datalet.inputName);

                    return;
                }

                // Increment our overall counter
                aggregate += retVal;
                // Log this particular iteration's time
                logger.logStatistic(Logger.STATUSMSG, retVal, 0,
                                    PERF_ITERATION + "processToFile(" + ctr + ") of::"
                                    + datalet.inputName);
                // Should really make this optional
                logMemory(false);
            }

            logger.logStatistic(Logger.STATUSMSG, (aggregate / datalet.iterations), 0, 
                                PERF_AVERAGE + "Average of (" + datalet.iterations
                                + ") iterations of::" + datalet.inputName);
                                
        }
        catch (Throwable t)
        {
            logger.checkFail("PerformanceTestlet of::" + datalet.inputName
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

}  // end of class PerformanceTestlet

