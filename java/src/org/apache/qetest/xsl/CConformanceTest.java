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
 * CConformanceTest.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.*;
import org.apache.qetest.xslwrapper.TransformWrapper;  // Merely for an error constant

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
 * New! CConformanceTest for Xalan-C's TestXSLT.exe program.
 * <p>Iterates over all conformance tests using common functionality,
 * then shells out a process to TestXSLT.exe for each test.
 * Automatically validates output files against golds, but may not
 * validate error or exception conditions yet.</p>
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class CConformanceTest extends XSLDirectoryIterator
{

    /**
     * Convenience method to print out usage information - update if needed.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String usage()
    {

        return ("Common [optional] options supported by CConformanceTest:\n"
                + "    -progName   <name.exe>\n"
                + "    -progPath   <d:\\path\\to\\prog>\n" + super.usage());
    }

    /** Array of 'command line' args for Process.main(args) calls. */
    String[] pargs = null;

    /**
     * Parameter: Actual name of external program to call.
     * <p>Default: TestXSLT</p>
     */
    String progName = "TestXSLT";

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
     * Parameter: -precompile If we should precompile (and
     * serialize) stylesheets separately first.
     * <p>Default: false.  Note: Likely not supported in early C++ releases.</p>
     */
    boolean precompile = false;

    /**
     * Default constructor - initialize testName, Comment.
     */
    public CConformanceTest()
    {

        testName = "CConformanceTest";
        testComment =
            "Iterates over all conf test dirs and validates outputs using Xalan-C";
    }

    /**
     * Initialize this test - process our input args and construct
     * array of command line args for processor.
     * @todo make this table-driven for the argument names
     * @todo update to include all supported args
     *
     * @param p Properties block of options to use - unused
     * @return true if OK, false if we should abort
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

        Vector vec = new Vector();

        // Set up pargs with global args from our inputs; used in every process
        // Standard args that XLTest supports:
        if ((liaison == null) || ("".equals(liaison)))
        {

            // no-op, don't use null or blank liaison
        }
        else
        {

            // setup args for liaison - note must be fully qualified classname
            vec.addElement("-PARSER");
            vec.addElement(liaison);
        }

        // Only pass the indent if it was explicitly set
        if (indentLevel > NO_INDENT)
        {
            vec.addElement("-INDENT");
            vec.addElement(Integer.toString(indentLevel));
        }

        // precompile is supported in processSingleFile
        // Unsupported: flavor - not very useful!
        // Unsupported: diagName
        // Unsupported: noReuse
        // Gadzillions of args that Process supports:
        setSingleArg("E", testProps, vec);  //"   [-E (Do not expand entity refs)]");
        setSingleArg("V", testProps, vec);  //"   [-V (Version info)]");
        setSingleArg("QC", testProps, vec);  //"   [-QC (Quiet Pattern Conflicts Warnings)]");
        setSingleArg("Q", testProps, vec);  //"   [-Q  (Quiet Mode)]");
        setSingleArg("LF", testProps, vec);  //"   [-LF (Use linefeeds only on output {default is CR/LF})]");
        setSingleArg("CR", testProps, vec);  //"   [-CR (Use carriage returns only on output {default is CR/LF})]");
        setSingleArg("TT", testProps, vec);  //"   [-TT (Trace the templates as they are being called.)]");
        setSingleArg("TG", testProps, vec);  //"   [-TG (Trace each generation event.)]");
        setSingleArg("TS", testProps, vec);  //"   [-TS (Trace each selection event.)]");
        setSingleArg("TTC", testProps, vec);  //"   [-TTC (Trace the template children as they are being processed.)]");
        setSingleArg("VALIDATE", testProps, vec);  //"   [-VALIDATE (Set whether validation occurs.  Validation is off by default.)]");
        setSingleArg("XML", testProps, vec);  //"   [-XML (Use XML formatter and add XML header.)]");
        setSingleArg("TEXT", testProps, vec);  //"   [-TEXT (Use simple Text formatter.)]");
        setSingleArg("HTML", testProps, vec);  //"   [-HTML (Use HTML formatter.)]");
        setSingleArg("SX", testProps, vec);  //"   [-SX (Xerces serializers)]");

        // FIXME: Args not yet tested    
        //"   [-ESCAPE (Which characters to escape {default is <>&\"\'\\r\\n}]");
        //"   [-TCLASS (TraceListener class for trace extensions.)]");
        //"   [-EDUMP {optional filename} (Do stackdump on error.)]");
        //"   [-PARAM name expression (Set a stylesheet parameter)]");
        // Copy the vector into array
        pargs = new String[vec.size()];

        vec.copyInto(pargs);
        reporter.logTraceMsg("Default arguments vector: " + vec.toString());

        return true;
    }

    /**
     * Worker method to add a single "-"arg if found in properties block.  
     *
     * @param name of argument, without the "-" dash
     * @param p Properties block to look in for arg
     * @param v Vector arg is added to, with an extra "-" dash
     */
    protected void setSingleArg(String name, Properties p, Vector v)
    {

        final String tmp = (String) p.get(name);

        if (tmp != null)  // Note: adds it even if it's blank!
        {
            v.addElement("-" + name);
        }
    }

    /**
     * Subclassed callback to provide info about the processor you're testing.
     *
     * @return ProcessorVersion;command line TestXSLT.exe:Xalan only
     */
    public String getDescription()
    {
        return "ProcessorVersion;command line TestXSLT.exe:Xalan only";
    }

    /**
     * Run through the directory given to us and run tests found in subdirs.
     *
     * @param p Properties block of options to use - unused
     * @return true
     */
    public boolean runTestCases(Properties p)
    {

        // Use all the default directory processing methods 
        // from our parent XLDirectoryIterator
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
     * @param XMLName local path/filename of .xml
     * @param XSLName local path/filename of .xsl
     * @param OutName  local path/filename of desired .out
     * @return true if OK, false if we should abort
     */
    public int processSingleFile(String XMLName, String XSLName,
                                 String OutName)
    {

        long fileTime = TransformWrapper.TIME_UNUSED;

        try
        {
            if (precompile)
            {
                reporter.checkFail(
                    "Sorry, precompile option not supported yet!");

                return UNEXPECTED_EXCEPTION;
            }  // of if (precompile)

            String args[] = new String[pargs.length + 7];  // progName -in XML -xsl XSL -out OUT

            if ((progPath != null) && (progPath.length() > 0))
            {
                args[0] = progPath + File.separator + progName;
            }
            else
            {

                // Pesume the program is on the PATH already...
                args[0] = progName;
            }

            args[1] = "-in";
            args[2] = translateInputName(XMLName);
            args[3] = "-xsl";
            args[4] = translateInputName(XSLName);
            args[5] = "-out";
            args[6] = translateOutputName(OutName);

            System.arraycopy(pargs, 0, args, 7, pargs.length);

            if (debug)
            {
                for (int x = 0; x < args.length; x++)
                {
                    reporter.logTraceMsg("arg[" + x + "]=" + args[x]);
                }
            }

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
            if (returnVal != TransformWrapper.TIME_UNUSED)
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

        // Catch any Throwable, check if they're expected, and restart
        catch (Throwable t)
        {
            reporter.logThrowable(Logger.ERRORMSG, t, 
                                  "processSingleFile(" + XSLName + ") threw");
            int retVal = checkExpectedException(t, XSLName, OutName);
            return retVal;
        }

        return PROCESS_OK;
    }

    /**
     * Worker method to translate filenames into appropriate format for C program.
     * <p>Always returns a string - will be blank if the file does not exist
     * or if we can't figure out the appropriate name to use</p>
     * <p><b>NOTE:</b> This should be delegated to 
     * QetestUtils.filenameToURL to put this code in a common 
     * place.  Also note that this may be incorrect on unix machines, 
     * since there should never be four slashes after the file: 
     * scheme name, only three.
     * @param name of file
     * @return appropriate translation into format that the .exe expects for -in, -xsl
     */
    public String translateInputName(String name)
    {

        StringBuffer ret = new StringBuffer("file:///");
        File f = new File(name);

        try
        {

            // This gives the best path, I think, but may throw exceptions
            ret.append(f.getCanonicalPath());
        }
        catch (Exception e)
        {

            // This should work in most cases
            ret.append(f.getAbsolutePath());
        }

        return ret.toString();
    }

    /**
     * Worker method to translate filenames into appropriate format for C program.
     * <p>Always returns a string - will be blank if the file does not exist
     * or if we can't figure out the appropriate name to use</p>
     * @param name of file
     * @return appropriate translation into format that the .exe expects for -out
     */
    public String translateOutputName(String name)
    {

        // Hopefully, we don't need translation on the output name
        return name;
    }

    /**
     * Simple child thread for reading an InputStream.
     * Used to capture the System.err and System.out streams
     * from the executed process - without hanging or blocking.
     */
    public class ThreadedStreamReader extends Thread
    {  // Begin of inner class

        /** BufferedReader to grab input.  */
        BufferedReader is = null;

        /** StringBuffer to hold input when done.  */
        StringBuffer sb = null;

        /**
         * Accessor method setInputStream 
         * @param set stream to set for is
         */
        public void setInputStream(BufferedReader set)
        {
            is = set;
        }

        /**
         * Run this thread; begins reading input stream until done.
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
         * Accessor method getBuffer 
         *
         * @return our StringBuffer, after we've run
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
     * @param environment passed as-is to Process.run
     * @return return value from program
     * @exception Exception may be thrown by Runtime.exec
     */
    public int execProcess(String[] cmdLine, String[] environment)
            throws Exception
    {

        // @todo check the logic here: will '-2' be a likely 
        //  return value from the process anyways?
        // this is needed in our caller to see if they should 
        //  use this process as part of timing data
        int retVal = (new Long(TransformWrapper.TIME_UNUSED)).intValue();

        if ((cmdLine == null) || (cmdLine.length < 1))
        {
            reporter.logErrorMsg(
                "execProcess called with null/blank arguments!");

            return retVal;
        }

        int bufSize = 2048; // Arbitrary bufSize seems to work well
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
            reporter.logThrowable(Logger.ERRORMSG, ie1, 
                                  "execProcess proc.waitFor() threw");
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
            reporter.logThrowable(Logger.ERRORMSG, ie2, 
                                  "Joining outReader threw");
        }

        try
        {
            errReader.join();

            sysErr = errReader.getBuffer().toString();
        }
        catch (InterruptedException ie3)
        {
            reporter.logThrowable(Logger.ERRORMSG, ie3, 
                                  "Joining errReader threw");
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
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        CConformanceTest app = new CConformanceTest();
        app.doMain(args);
    }
}  // end of class CConformanceTest

