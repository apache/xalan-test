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

package org.apache.qetest.xsl;

import org.apache.qetest.Datalet;
import org.apache.qetest.Logger;
import org.apache.qetest.xslwrapper.TransformWrapper;
import org.apache.qetest.xslwrapper.TransformWrapperHelper;

import java.io.File;
import java.util.Hashtable;

/**
 * Testlet to capture specific timing performance data.
 * This Testlet is a cut-down version of PerfEverythingTestlet.
 *
 * We log out XalanC-like overall performance numbers, as well 
 * as logging out any available data from the times array returned 
 * by our transformWrapper.  Note that different flavors of 
 * transformWrapper will return different sets of timings.
 *
 * @author Shane_Curcuru@us.ibm.com
 * @version $Id$
 */
public class PerfTestlet extends StylesheetTestlet
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.PerfTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new StylesheetDatalet(); }

    /**
     * Accesor method for a brief description of this test.  
     * @return String describing what this PerfTestlet does.
     */
    public String getDescription()
    {
        return "PerfTestlet";
    }


    /** 
     * Worker method to actually perform the transform; 
     * overriden to use command line processing.  
     *
     * Logs out applicable info; attempts to perform transformation 
     * and also prints out a &lt;perf&gt; element.
     *
     * @param datalet to test with
     * @param transformWrapper to have perform the transform
     * @throws allows any underlying exception to be thrown
     */
    protected void testDatalet(StylesheetDatalet datalet, TransformWrapper transformWrapper)
            throws Exception
    {
        // Setup: Save options from the datalet in convenience variables
        int iterations = 10;
        boolean runtimeGC = false;
        try
        {
            iterations = Integer.parseInt(datalet.options.getProperty("iterations"));
        }
        catch (Exception e) { /* no-op, leave as default */ }
        try
        {
            runtimeGC = (new Boolean(datalet.options.getProperty("runtimeGC"))).booleanValue();
        }
        catch (Exception e) { /* no-op, leave as default */ }

        // Setup: store various XalanC-like timing data in convenience variables
        long singletransform = 0L;  // Very first Preload end-to-end transform
        long etoe = 0L;     // First end-to-end transform during iterations
        long avgetoe = 0L;  // Average of end-to-end transforms during iterations
        long parsexsl = 0L;     // First stylesheet preprocess during iterations
        long avgparsexsl = 0L;  // Average of stylesheet preprocess during iterations
        long unparsedxml = 0L;   // First stylesheet process during iterations
        long avgunparsedxml = 0L;// Average of stylesheet process during iterations

        logger.logMsg(Logger.TRACEMSG, "executing with: inputName=" + datalet.inputName
                      + " xmlName=" + datalet.xmlName + " outputName=" + datalet.outputName
                      + " goldName=" + datalet.goldName + " flavor="  + datalet.flavor
                      + " iterations=" + iterations 
                      + " algorithim=" + getDescription());

        //@todo make various logMemory calls optional
        logMemory(runtimeGC, true);

        // Measure(singletransform): Very first Preload end-to-end transform
        long[] times = null;
        times = transformWrapper.transform(datalet.xmlName, datalet.inputName,
                                            datalet.outputName);
        singletransform = times[TransformWrapper.IDX_OVERALL];
        logMemory(runtimeGC, false);

        // Measure(parsexsl): once: first preprocess
        times = transformWrapper.buildStylesheet(datalet.inputName);
        parsexsl = times[TransformWrapper.IDX_OVERALL];
        logMemory(runtimeGC, false);

        // Measure(unparsedxml): once: first process
        times = transformWrapper.transformWithStylesheet(datalet.xmlName, datalet.outputName);
        unparsedxml = times[TransformWrapper.IDX_OVERALL];
        logMemory(runtimeGC, false);

        for (int ctr = 1; ctr <= iterations; ctr++)
        {
            // Measure(avgparsexsl): average preprocess
            times = transformWrapper.buildStylesheet(datalet.inputName);
            avgparsexsl += times[TransformWrapper.IDX_OVERALL];
            logMemory(runtimeGC, false);

            // Measure(avgunparsedxml): average process
            times = transformWrapper.transformWithStylesheet(datalet.xmlName, datalet.outputName);
            avgunparsedxml += times[TransformWrapper.IDX_OVERALL];
            logMemory(runtimeGC, false);
        }

        // Measure(etoe): once: first full process
        times = transformWrapper.transform(datalet.xmlName, datalet.inputName, datalet.outputName);
        etoe = times[TransformWrapper.IDX_OVERALL];
        logMemory(runtimeGC, true);

        // Aggregate all specific timing data returned by TransformWrappers
        //  note that different flavors of wrappers will be able 
        //  to report different kinds of times
        // This data is separate from the XalanC-like data above
        //@todo determine which data is best to store
        long[] storedTimes = TransformWrapperHelper.getTimeArray();
        long[] storedCounts = TransformWrapperHelper.getTimeArray();
        for (int ctr = 1; ctr <= iterations; ctr++)
        {
            // Measure(avgetoe): average full process
            times = transformWrapper.transform(datalet.xmlName, datalet.inputName, datalet.outputName);
            avgetoe += times[TransformWrapper.IDX_OVERALL];
            aggregateTimes(times, storedTimes, storedCounts);
            logMemory(runtimeGC, false);
        }

        Hashtable attrs = new Hashtable();
        // BOTH: Log all specific timing data returned by TransformWrappers
        logAggregateTimes(attrs, storedTimes, storedCounts);

        // ASLO: Log special XalanC-like performance element with our timing
        // UniqRunid is an Id that our TestDriver normally sets 
        //  with some unique code, so that results analysis 
        //  stylesheets can compare different test runs
        attrs.put("UniqRunid", datalet.options.getProperty("runId", "runId;none"));
        // processor is the 'flavor' of processor we're testing
        attrs.put("processor", transformWrapper.getDescription());
        // idref is the individual filename
        attrs.put("idref", (new File(datalet.inputName)).getName());
        // inputName is the actual name we gave to the processor
        attrs.put("inputName", datalet.inputName);
        attrs.put("iterations", new Integer(iterations));
        attrs.put("singletransform", new Long(singletransform)); // Very first Preload end-to-end transform
        attrs.put("etoe", new Long(etoe)); // First end-to-end transform during iterations
        // Note that avgetoe should match logTimes()'s OVERALL value
        attrs.put("avgetoe", new Long(avgetoe / iterations)); // Average of end-to-end transforms during iterations
        attrs.put("parsexsl", new Long(parsexsl)); // First stylesheet preprocess during iterations
        attrs.put("avgparsexsl", new Long(avgparsexsl / iterations)); // Average of stylesheet preprocess during iterations
        attrs.put("unparsedxml", new Long(unparsedxml)); // First stylesheet process during iterations
        attrs.put("avgunparsedxml", new Long(avgunparsedxml / iterations)); // Average of stylesheet process during iterations

        logger.logElement(Logger.STATUSMSG, "perf", attrs, "PItr;");
    }

    /**
     * Worker method: optionally reports Runtime.totalMemory/freeMemory; 
     * optionally first calls .gc() to force garbage collection.  
     * @param doGC: call .gc() or not first
     * @param doLog: log out memory stats or not
     */
    protected void logMemory(boolean doGC, boolean doLog)
    {
        if (doGC)
        {
            Runtime.getRuntime().gc();
        }
        if (doLog)
        {
            logger.logStatistic(Logger.STATUSMSG, Runtime.getRuntime().freeMemory(), 0, "UMem;freeMemory");
            logger.logStatistic(Logger.STATUSMSG, Runtime.getRuntime().totalMemory(), 0, "UMem;totalMemory");
        }
    }

    /**
     * Worker method: aggregate timing arrays and keep counters.  
     * @param newTimes new timing data to add to storedTimes
     * @param storedTimes incremented from newTimes
     * @param countTimes number of time slots actually incremented; 
     * i.e. TIME_UNUSED ones are not incremented
     */
    protected void aggregateTimes(long[] newTimes, long[] storedTimes, long[] countTimes)
    {
        // Note assumption in this class that all are same len
        for (int i = 0; i < storedTimes.length; i++)
        {
            // Only aggregate items that have actual timing data
            if (TransformWrapper.TIME_UNUSED != newTimes[i])
            {
                // Be sure to start from 0 if we haven't been hit before
                if (TransformWrapper.TIME_UNUSED == storedTimes[i])
                {
                    // Start counting the time data for this slot
                    storedTimes[i] = newTimes[i];
                    // Start counter for this slot
                    countTimes[i] = 1;
                }
                else
                {
                    // Aggregate the time data for this slot
                    storedTimes[i] += newTimes[i];
                    // Increment counter for this slot
                    countTimes[i]++;
                }
            }
        }
    }

    /**
     * Worker method: log aggregate timing arrays and keep counters.  
     * @param storedTimes to log out
     * @param countTimes number of time slots actually incremented
     */
    protected void logAggregateTimes(Hashtable attrs, long[] storedTimes, long[] countTimes)
    {
        for (int i = 0; i < storedTimes.length; i++)
        {
            // Only log items that have actual timing data
            if (TransformWrapper.TIME_UNUSED != storedTimes[i])
            {
                attrs.put(TransformWrapperHelper.getTimeArrayDesc(i), 
                          new Long(storedTimes[i] / countTimes[i]));
            }
        }
    }
}  // end of class PerfTestlet

