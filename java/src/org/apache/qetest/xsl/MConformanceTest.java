/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2001 The Apache Software Foundation.  All rights 
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
 * MConformanceTest.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.*;
import org.apache.qetest.xslwrapper.ProcessorWrapper;  // Merely for the ERROR constant

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.Properties;
import java.util.Vector;

//-------------------------------------------------------------------------

/**
 * New! MConformanceTest for alternate command line program.
 * <p>Iterates over all conformance tests using common functionality,
 * then shells out a process to TestXSLT.exe for each test.
 * Automatically validates output files against golds, but may not
 * validate error or exception conditions yet.</p>
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class MConformanceTest extends XSLDirectoryIterator
{

    /**
     * Convenience method to print out usage information - update if needed.  
     * @return basic usage description
     */
    public String usage()
    {

        return ("Common [optional] options supported by MConformanceTest:\n"
                + "    -progName   <name.exe>\n"
                + "    -progPath   <d:\\path\\to\\prog>\n" + super.usage());
    }

    /** Array of 'command line' args for Process.main(args) calls. */
    String[] pargs = null;

    /**
     * Parameter: Actual name of external program to call.
     * <p>Default: msxsl</p>
     */
    String progName = "msxsl";

    /**
     * Parameter: Actual name of external program to call.
     * <p>Default: TestXSLT</p>
     */
    public static final String OPT_PROGNAME = "progName";

    /**
     * Path to external program to call.
     * <p>Default: blank string</p>
     */
    String progPath = "";

    /**
     * Path to external program to call.
     * <p>Default: blank string</p>
     */
    public static final String OPT_PROGPATH = "progPath";

    /**
     * Default constructor - initialize testName, Comment.
     */
    public MConformanceTest()
    {

        testName = "MConformanceTest";
        testComment =
            "Iterates over all conf test dirs and validates outputs using cmdline processor";
    }

    /**
     * Initialize this test - process our input args and construct
     * array of command line args for processor.
     * //@todo make this table-driven for the argument names
     * //@todo update to include all supported args
     *
     * @param p unused
     * @return true, or false if error
     */
    public boolean doTestFileInit(Properties p)
    {

        // Validate/setup progName, progPath
        progName = testProps.getProperty(OPT_PROGNAME, progName);
        progPath = testProps.getProperty(OPT_PROGPATH, progPath);

        reporter.logTraceMsg("progPath\\progName= " + progPath
                             + File.separator + progName);

        // Attempt to check if it exists (but only in certain cases)
        if ((progPath != null) && (progPath.length() > 0))
        {
            File progF = new File(progPath + File.separator + progName);

            if (!progF.exists())
            {
                reporter.logErrorMsg("Program may not exist! " + progPath
                                     + File.separator + progName);
            }
        }
        else
        {

            // No-op: can't easily validate programs (presumably) found on the PATH
        }
        reporter.logTraceMsg("Sorry, no additional arguments to cmdline prog supported!");

        return true;
    }


    /**
     * Subclassed callback to provide info about the processor you're testing.
     *
     * @return brief description of processor used
     */
    public String getDescription()
    {
        return "ProcessorVersion;command line msxsl.exe:Windows only";
    }

    /**
     * Run through the directory given to us and run tests found in subdirs.
     *
     * @param p unused
     * @return true
     */
    public boolean runTestCases(Properties p)
    {
        processTestDir();
        return true;
    }

    /**
     * Run one xsl/xml file pair through the processor.
     * <p>We simply call Process.main() with a constructed series
     * of args[] using Runtime.exec().</p>
     * <p>Note that timings are in no way comparable to the normal
     * ConformanceTest since this is C++, not Java, and since we have
     * the overhead of shelling out the process and processing
     * the command line.</p>
     *
     * @param XMLName path\filename to XML file
     * @param XSLName path\filename to stylesheet file
     * @param OutName path\filename to outputfile
     *
     * @return UNEXPECTED_EXCEPTION or PROCESS_OK; currently does 
     * not check for expectedExceptions
     */
    public int processSingleFile(String XMLName, String XSLName,
                                 String OutName)
    {
        long fileTime = ProcessorWrapper.ERROR;
        try
        {
            String args[] = new String[6];  // progName XML XSL -o OUT -t
            if ((progPath != null) && (progPath.length() > 0))
            {
                args[0] = progPath + File.separator + progName;
            }
            else
            {
                // Pesume the program is on the PATH already...
                args[0] = progName;
            }

            args[1] = XMLName;
            args[2] = XSLName;
            args[3] = "-o"; // flag for outname
            args[4] = OutName; 
            args[5] = "-t"; // print timings

            // Note: no other args currently supported!
            //System.arraycopy(pargs, 0, args, 7, pargs.length);

            // Declare variables ahead of time to minimize latency
            long startTime = 0;
            long endTime = 0;
            int returnVal = 0;

            startTime = System.currentTimeMillis();

            // Use our worker method to execute the process
            returnVal = execProcess(args, null);
            endTime = System.currentTimeMillis();
            fileTime = endTime - startTime;

            // if the execution of the process was basically OK, 
            //  then add this file to overall timing info
            if (returnVal != ProcessorWrapper.ERROR)
            {
                dirTime += fileTime;
                dirFilesProcessed++;
                reporter.logTraceMsg("processSingleFile(" + XSLName
                                     + ") no exceptions; time " + fileTime);
                reporter.logInfoMsg("processSingleFile(" + XSLName
                                    + ") returnValue =  " + returnVal);
            }
            else
            {
                // Do not increment performance counters if there's an error
                // Note: can this code ever really be executed?
                reporter.logWarningMsg("processSingleFile(" + XSLName
                                       + ") returned ERROR code!");
            }
        }

        // Catch general Exceptions, check if they're expected, and restart
        catch (Exception e)
        {
            reporter.logStatusMsg("processSingleFile(" + XSLName
                                  + ") threw: " + e.toString());
            int retVal = checkExpectedException(e, XSLName, OutName);
            return retVal;
        }

        // Catch any Throwable, check if they're expected, and restart
        catch (Throwable t)
        {
            reporter.logStatusMsg("processSingleFile(" + XSLName
                                  + ") threw: " + t.toString());
            int retVal = checkExpectedException(t, XSLName, OutName);
            return retVal;
        }
        return PROCESS_OK;
    }


    /**
     * Simple child thread for reading an InputStream.
     * Used to capture the System.err and System.out streams
     * from the executed process - without hanging or blocking.
     */
    public class ThreadedStreamReader extends Thread
    {  // Begin of inner class

        /** NEEDSDOC Field is          */
        BufferedReader is = null;

        /** NEEDSDOC Field sb          */
        StringBuffer sb = null;

        /**
         * NEEDSDOC Method setInputStream 
         *
         *
         * NEEDSDOC @param set
         */
        public void setInputStream(BufferedReader set)
        {
            is = set;
        }

        /**
         * NEEDSDOC Method run 
         *
         */
        public void run()
        {

            sb = new StringBuffer();

            // Note that reporters may not be threadsafe, so we should avoid there use herein
            if (is == null)
            {
                sb.append(
                    "ERROR! ThreadedStreamReader.run() with setInputStream(null)");

                return;
            }

            sb.append("<tsrbuf>");

            String i = null;

            try
            {
                i = is.readLine();
            }
            catch (IOException ioe1)
            {  // Presumably the stream is bad, so just bag out
                i = null;
            }

            while (i != null)
            {
                sb.append(i);

                try
                {
                    i = is.readLine();
                }
                catch (IOException ioe2)
                {  // Presumably the stream is bad, so just bag out
                    i = null;
                }
            }

            sb.append("</tsrbuf>");
        }

        /**
         * NEEDSDOC Method getBuffer 
         *
         *
         * NEEDSDOC (getBuffer) @return
         */
        public StringBuffer getBuffer()
        {
            return sb;
        }
    }
    ;  // End of inner class

    /**
     * Worker method to shell out an external process.
     * <p>Does a simple capturing of the out and err streams from
     * the process.  Inherits the same environment that the current
     * JVM is in.</p>
     * @param cmdLine actual command line to run, including program name
     * NEEDSDOC @param environment
     * @return return value from program
     * @exception Exception may be thrown by Runtime.exec
     */
    public int execProcess(String[] cmdLine, String[] environment)
            throws Exception
    {

        // @todo check the logic here: will '-1' be a likely 
        //  return value from the process anyways?
        // this is needed in our caller to see if they should 
        //  use this process as part of timing data
        int retVal = (new Long(ProcessorWrapper.ERROR)).intValue();

        if ((cmdLine == null) || (cmdLine.length < 1))
        {
            reporter.logErrorMsg(
                "execProcess called with null/blank arguments!");

            return retVal;
        }

        int bufSize = 2048;
        ThreadedStreamReader outReader = new ThreadedStreamReader();
        ThreadedStreamReader errReader = new ThreadedStreamReader();
        Runtime r = Runtime.getRuntime();
        java.lang.Process proc = null;  // Fully declare to not conflict with org.apache.xalan.xslt.Process

        // Actually begin executing the program
        reporter.logTraceMsg("execProcess starting " + cmdLine[0]);

        proc = r.exec(cmdLine, environment);

        // Immediately begin capturing any output therefrom
        outReader.setInputStream(
            new BufferedReader(
                new InputStreamReader(proc.getInputStream()), bufSize));
        errReader.setInputStream(
            new BufferedReader(
                new InputStreamReader(proc.getErrorStream()), bufSize));

        // Start two thread off on reading the System.out and System.err from proc
        outReader.start();
        errReader.start();

        try
        {

            // Wait for the process to exit normally
            retVal = proc.waitFor();
        }
        catch (InterruptedException ie1)
        {
            reporter.logWarningMsg("execProcess proc.waitFor() threw: "
                                   + ie1.toString());
        }

        // Now that we're done, presumably the Readers are also done
        String sysOut = "No System.out captured";
        String sysErr = "No System.err captured";

        try
        {
            outReader.join();

            sysOut = outReader.getBuffer().toString();
        }
        catch (InterruptedException ie2)
        {
            reporter.logWarningMsg("Joining outReader threw: "
                                   + ie2.toString());
        }

        try
        {
            errReader.join();

            sysErr = errReader.getBuffer().toString();
        }
        catch (InterruptedException ie3)
        {
            reporter.logWarningMsg("Joining errReader threw: "
                                   + ie3.toString());
        }

        reporter.logArbitrary(reporter.INFOMSG,
                              "proc.System.out was: " + sysOut);
        reporter.logArbitrary(reporter.INFOMSG,
                              "proc.System.err was: " + sysErr);
        reporter.logTraceMsg("execProcess exitVal=" + retVal);

        return retVal;
    }

    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line args
     */
    public static void main(String[] args)
    {
        MConformanceTest app = new MConformanceTest();
        app.doMain(args);
    }
}  // end of class MConformanceTest

