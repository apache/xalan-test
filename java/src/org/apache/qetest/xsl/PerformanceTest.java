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
 * PerformanceTest.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.*;
import org.apache.qetest.xslwrapper.*;

import java.io.File;
import java.io.FilenameFilter;

import java.util.Hashtable;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * New, Improved! PerformanceTest - benchmark repetitive timings of various processors.
 * <p>While the test harness does add a small amount of overhead to the benchmark
 * process, this makes reporting and running the test much easier.</p>
 * @author shane_curcuru@lotus.com
 * //@todo improve verification; make calling fileChecker conditional,
 * perhaps only every 10 iterations or something
 * @deprecated Please use StylesheetTestletDriver instead.
 * The preferred way to test trees of stylesheets (like the conf 
 * test) is to use the StylesheetTestletDriver to iterate over the 
 * testfiles, StylesheetTestlets to provide the testing algorithim, 
 * and TransformWrappers to interface with the XSLT processor.
 * @see org.apache.qetest.xsl.StylesheetTestletDriver
 * @see org.apache.qetest.xsl.PerformanceTestlet
 * @see org.apache.qetest.xsl.PerfEverythingTestlet
 * @see org.apache.qetest.xsl.PerfPreloadTestlet
 */
public class PerformanceTest extends XSLDirectoryIterator
{

    /**
     * Parameter: Should we preload a single process before timing iterations?
     * <p>Default: false. Format: -preload true|false</p>
     */
    public static final String OPT_PRELOAD = "preload";

    /** NEEDSDOC Field preload          */
    protected boolean preload = false;

    /**
     * Parameter: How many iterations should we make for each file?
     * <p>Default: 3. Format: -iterations int</p>
     */
    public static final String OPT_ITERATIONS = "iterations";

    /** NEEDSDOC Field iterations          */
    protected int iterations = 3;

    /** Markers for performance logging - Preload time. */
    public static final String PERF_PRELOAD = "UPre";

    /** Markers for performance logging - single iteration time. */
    public static final String PERF_ITERATION = "UItr";

    /** Markers for performance logging - average of iteration times. */
    public static final String PERF_AVERAGE = "UAvg";

    /** Markers for memory logging. */
    public static final String PERF_MEMORY = "UMem";

    /** Default ctor initializes test name, comment. */
    public PerformanceTest()
    {

        testName = "PerformanceTest";
        testComment =
            "Iterates over all perf test dirs and writes timing info";
    }

    /**
     * Initialize this test - setup description and createNewProcessor.  
     * Also explicitly initializes parameters related to PerformanceTest, 
     * like preload and iterations.
     *
     * @param p Props (unused currently)
     * @return False if we should abort
     */
    public boolean doTestFileInit(Properties p)
    {
        // Read in our additional options from properties block (XLTest should have set this)
        String tmp;

        tmp = testProps.getProperty(OPT_PRELOAD, null);

        if ((tmp != null) && tmp.equalsIgnoreCase("true"))
        {
            preload = true;
        }

        tmp = null;
        tmp = testProps.getProperty(OPT_ITERATIONS, null);

        try
        {
            iterations = Integer.parseInt(tmp);
        }
        catch (NumberFormatException numEx)
        {

            // no-op; leave as default
        }

        // Create a processor with our appropriate flavor, etc.
        // This is really only done to let XLDirectoryIterator later on 
        //  print out the flavor & version of the processor
        // Note: This processor will get destroyed later on anyway:
        //  for the performance test, we re-create for each file
        if (!createNewProcessor())
        {
            reporter.logErrorMsg(
                "Could not createNewProcessor before testing; it may not work!");
        }

        return true;
    }

    /**
     * Run through the directory given to us and run tests found in subdirs.
     *
     * NEEDSDOC @param p
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean runTestCases(Properties p)
    {

        // The PerformanceTest simply uses all the default processing methods 
        // from our parent XLDirectoryIterator
        logMemory(true);  // dumps Runtime.freeMemory/totalMemory
        processTestDir();
        logMemory(true);  // dumps Runtime.freeMemory/totalMemory

        return true;
    }

    /**
     * Run one xsl/xml file pair through the processor; looping and capturing timing info.
     * @author Shane Curcuru
     *
     * NEEDSDOC @param XMLName
     * NEEDSDOC @param XSLName
     * NEEDSDOC @param OutName
     * @return int status - pass, fail, expected exception or unexpected exception
     * <p>processSingleFile should normally do all processing required to transform the XMLName,
     * via the XSLName, into an OutName file on disk.  It returns a status denoting
     * what happend during the processing.</p>
     * <p>For the PerformanceTest, we actually do a lot more:.</p>
     * <UL>
     * <LI>Create a new processorWrapper (and hence a new processor)</LI>
     * <LI>If preload, run one process through first and report timing</LI>
     * <LI>Iterate and perform a number of processes, timing for each one</LI>
     * <LI>Cleanup what we can to not interfere with other tests later</LI>
     * </UL>
     * <p>Futurework: should we strip off just the filename and output just that
     * in a specific format in the logPerfMsg calls to make it simpler to write reports?</p>
     */
    public int processSingleFile(String XMLName, String XSLName,
                                 String OutName)
    {

        long aggregate = 0L;
        long fileTime = ProcessorWrapper.ERROR;

        try
        {
                // Use this static convenience method; returns a URL; convert to String via toExternalForm()
                // Note: we should consider caching the original strings first, 
                //  in case we later on have a use for them instead of the URI'd format
                XMLName = QetestUtils.filenameToURL(XMLName);
                XSLName = QetestUtils.filenameToURL(XSLName);

                // Note: Currently 28-Jun-00, the output of files is handled differently, so 
                //  we do NOT want to convert those.  Subject to change, however.
                reporter.logTraceMsg("processSingleFile() useURI: "
                                     + XSLName);

            // TODO replicate ProcessorBenchmark.benchmarkPreprocess()
            // TODO cleanup outName - delete the file on disk
            File outFile = new File(OutName);

            try
            {
                boolean btmp = outFile.delete();

                reporter.logTraceMsg("Deleting OutFile of::" + OutName
                                     + " status: " + btmp);
            }
            catch (SecurityException se)
            {
                reporter.logWarningMsg("Deleting OutFile of::" + OutName
                                       + " threw: " + se.toString());

                // But continue anyways...
            }

            // Equivalent to ProcessorBenchmark.benchmark()
            // Create a new wrapper & processor each time; cleanup stuff
            processorW = null;

            logMemory(true);  // dumps Runtime.freeMemory/totalMemory

            // Ask our utility routine for a new specific wrapper
            //  (Will automatically create a new wrapper and appropriate processor
            if (!createNewProcessor())
            {
                reporter.logErrorMsg(
                    "ERROR: could not create processorWrapper, aborting.");

                return UNEXPECTED_EXCEPTION;
            }

            // Prime the pump, so to speak, if desired
            if (preload)
            {
                fileTime = processorW.processToFile(XMLName, XSLName,
                                                    OutName);

                if (fileTime == ProcessorWrapper.ERROR)
                {
                    reporter.logErrorMsg(
                        "ERROR: Preload process had a problem of::"
                        + XSLName);

                    return UNEXPECTED_EXCEPTION;
                }

                reporter.logPerfMsg(PERF_PRELOAD, fileTime,
                                    "Preload process of::" + XSLName);
            }

            logMemory(true);  // dumps Runtime.freeMemory/totalMemory

            int j;

            for (j = 1; j <= iterations; j++)
            {
                long retVal;

                // Note: We re-write the same output file each time, so 
                //       I suppose in theory that could affect future iterations
                retVal = processorW.processToFile(XMLName, XSLName, OutName);

                if (retVal == ProcessorWrapper.ERROR)
                {
                    reporter.logErrorMsg(
                        "ERROR: processToFile problem on iteration(" + j
                        + ") of::" + XSLName);

                    return UNEXPECTED_EXCEPTION;
                }

                aggregate += retVal;

                reporter.logPerfMsg(PERF_ITERATION, retVal,
                                    "processToFile(" + j + ") of::"
                                    + XSLName);
                logMemory(true);
            }

            reporter.logPerfMsg(PERF_AVERAGE, (aggregate / iterations),
                                "Average of (" + iterations
                                + ") iterations of::" + XSLName);
            // Log special performance element with our timing
            Hashtable attrs = new Hashtable();
            // idref is the individual filename
            attrs.put("idref", (new File(XSLName)).getName());
            // inputName is the actual name we gave to the processor
            attrs.put("inputName", XSLName);
            // preload.onetime is the one time it took in the preload stage
            if (preload)
                attrs.put("singletransform", new Long(fileTime));
            // process.avg is the average processing time...
            attrs.put("avgetoe", new Long(aggregate / iterations));
            // ... over a number of iterations
            attrs.put("iterations", new Integer(iterations));
            // The hackish buf at the end is simply a semicolon
            //  delimited list of individual timings, just for 
            //  fun - I'm not even sure we're going to use it
            logger.logElement(Logger.STATUSMSG, "perf", attrs, PERF_ITERATION);

        }

        // Catch any throwable and log an error
        catch (Throwable t)
        {
            reporter.logErrorMsg("processSingleFile of::" + XSLName
                                 + " threw: " + t.toString());

            return UNEXPECTED_EXCEPTION;
        }

        return PROCESS_OK;
    }


    /**
     * Set our instance variables from a Properties file.
     * Calls super.initializeFromProperties() to get defaults.
     * This is needed to explicitly initialize properties 
     * that are custom to this file, like iterations and 
     * preload.
     * //@todo Note that this needs some redesign, since it 
     * means we have to duplicate too much initialization code 
     * in different places.
     *
     * @param props Properties block to set name=value pairs from
     * @return status - true if OK, false if error.
     */
    public boolean initializeFromProperties(Properties props)
    {
        // Be sure to get our parents to initialize everything first!
        boolean b = super.initializeFromProperties(props);

        // Read in our additional options from properties block (XLTest should have set this)
        String tmp;
        tmp = props.getProperty(OPT_PRELOAD, null);

        if ((tmp != null) && tmp.equalsIgnoreCase("true"))
        {
            testProps.put(OPT_PRELOAD, "true");
            preload = true;
        }

        tmp = null;
        tmp = props.getProperty(OPT_ITERATIONS, null);

        try
        {
            iterations = Integer.parseInt(tmp);
            testProps.put(OPT_ITERATIONS, tmp);
        }
        catch (NumberFormatException numEx)
        {

            // no-op; leave as default
        }

        return b;
    }


    /**
     * Cheap-o memory logger - just reports Runtime.totalMemory/freeMemory.  
     *
     * NEEDSDOC @param total
     */
    protected void logMemory(boolean total)
    {

        Runtime r = Runtime.getRuntime();

        r.gc();
        reporter.logPerfMsg(PERF_MEMORY, r.freeMemory(), "freeMemory");

        if (total)
        {
            reporter.logPerfMsg(PERF_MEMORY, r.totalMemory(), "totalMemory");
        }
    }

    /**
     * Convenience method to print out usage information - update if needed.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String usage()
    {

        return ("Common [optional] options supported by PerformanceTest:\n"
                + "    -" + OPT_PRELOAD
                + "   run an extra process first, before looping\n" + "    -"
                + OPT_ITERATIONS
                + "   how many times to loop over each file\n"
                + super.usage());
    }

    /**
     * Main method to run test from the command line - can be left alone.  
     *
     * NEEDSDOC @param args
     */
    public static void main(String[] args)
    {

        PerformanceTest app = new PerformanceTest();

        app.doMain(args);
    }
}  // end of class PerformanceTest

