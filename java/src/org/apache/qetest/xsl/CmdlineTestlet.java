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
 * originally based on software copyright (c) 2000, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/*
 *
 * CmdlineTestlet.java
 *
 */
package org.apache.qetest.xsl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.apache.qetest.Datalet;
import org.apache.qetest.Logger;
import org.apache.qetest.ThreadedStreamReader;
import org.apache.qetest.xslwrapper.TransformWrapper;

/**
 * Testlet for conformance testing of xsl stylesheet files using 
 * a command line interface instead of a TransformWrapper.
 *
 * This class provides a default algorithim for testing XSLT 
 * processsors from the command line.  Subclasses define the 
 * exact command line args, etc. used for different products.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class CmdlineTestlet extends StylesheetTestlet
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.CmdlineTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new StylesheetDatalet(); }

    /**
     * Accesor method for a brief description of this test.  
     *
     * @return String describing what this CmdlineTestlet does.
     */
    public String getDescription()
    {
        return "CmdlineTestlet";
    }

    /**
     * Parameter: Actual name of external program to call.  
     */
    public static final String OPT_PROGNAME = "progName";

    /**
     * Default Actual name of external program to call.  
     * @return TestXSLT, the Xalan-C command line.
     */
    public String getDefaultProgName()
    {
        return "TestXSLT";
    }

    /**
     * Path to external program to call; default is none.  
     */
    public static final String OPT_PROGPATH = "progPath";


    /** 
     * Worker method to actually perform the transform; 
     * overriden to use command line processing.  
     *
     * Logs out applicable info; attempts to perform transformation.
     *
     * @param datalet to test with
     * @param transformWrapper to have perform the transform
     * @throws allows any underlying exception to be thrown
     */
    protected void testDatalet(StylesheetDatalet datalet, TransformWrapper transformWrapper)
            throws Exception
    {
        String[] defaultArgs = new String[0];   // Currently unused
        String[] args = getProgramArguments(datalet, defaultArgs);
    
        StringBuffer argBuf = new StringBuffer();
        for (int i = 0; i < args.length; i++)
        {
            argBuf.append(args[i]);
            argBuf.append(" ");
        }
        //@todo Should we log a custom logElement here instead?
        logger.logMsg(Logger.TRACEMSG, "cmdline executing: " + argBuf.toString());

        // Declare variables ahead of time to minimize latency
        long startTime = 0;
        long overallTime = 0;

        // Use our worker method to execute the process, which 
        //  runs the test via the command line
        startTime = System.currentTimeMillis();
        execProcess(args, null);
        overallTime = System.currentTimeMillis() - startTime;
        
    }


    /**
     * Worker method to get list of arguments specific to this program.  
     * 
     * <p>Must be overridden for different processors, obviously.  
     * This implementation returns the args for Xalan-C TestXSLT</p>
     * 
     * @param program path\name of program to Runtime.exec()
     * @param defaultArgs any additional arguments to pass
     * @return String array of arguments suitable to pass to 
     * Runtime.exec()
     */
    public String[] getProgramArguments(StylesheetDatalet datalet, String[] defaultArgs)
    {
        final int NUMARGS = 7;
        String[] args = new String[defaultArgs.length + NUMARGS];
        String progName = datalet.options.getProperty(OPT_PROGNAME, getDefaultProgName());
        String progPath = datalet.options.getProperty(OPT_PROGPATH);
        if ((null != progPath) && (progPath.length() > 0))
        {
            args[0] = progPath + File.separator + progName;
        }
        else
        {
            // Pesume the program is on the PATH already...
            args[0] = progName;
        }
    
        // Default args for Xalan-C TestXSLT
        args[1] = "-in";
        args[2] = datalet.xmlName;
        args[3] = "-xsl";
        args[4] = datalet.inputName;
        args[5] = "-out";
        args[6] = datalet.outputName;

        if (defaultArgs.length > 0)
            System.arraycopy(defaultArgs, 0, args, NUMARGS, defaultArgs.length);

        return args;
    }


    /**
     * Worker method to shell out an external process.  
     * 
     * <p>Does a simple capturing of the out and err streams from
     * the process and logs them out.  Inherits the same environment 
     * that the current JVM is in.</p>
     * 
     * @param cmdline actual command line to run, including program name
     * @param environment passed as-is to Process.run
     * @return return value from program
     * @exception Exception may be thrown by Runtime.exec
     */
    public void execProcess(String[] cmdline, String[] environment)
            throws Exception
    {
        if ((cmdline == null) || (cmdline.length < 1))
        {
            logger.checkFail("execProcess called with null/blank arguments!");
            return;
        }

        int bufSize = 2048; // Arbitrary bufSize seems to work well
        ThreadedStreamReader outReader = new ThreadedStreamReader();
        ThreadedStreamReader errReader = new ThreadedStreamReader();
        Runtime r = Runtime.getRuntime();
        java.lang.Process proc = null;  // Fully declare to not conflict with org.apache.xalan.xslt.Process

        // Actually begin executing the program
        logger.logMsg(Logger.TRACEMSG, "execProcess starting " + cmdline[0]);

        proc = r.exec(cmdline, environment);

        // Immediately begin capturing any output therefrom
        outReader.setInputStream(
            new BufferedReader(
                new InputStreamReader(proc.getInputStream()), bufSize));
        errReader.setInputStream(
            new BufferedReader(
                new InputStreamReader(proc.getErrorStream()), bufSize));

        // Start two threads off on reading the System.out and System.err from proc
        outReader.start();
        errReader.start();
        int processReturnVal = -2; // HACK the default
        try
        {
            // Wait for the process to exit normally
            processReturnVal = proc.waitFor();
        }
        catch (InterruptedException ie1)
        {
            logger.logThrowable(Logger.ERRORMSG, ie1, 
                                  "execProcess proc.waitFor() threw");
        }

        // Now that we're done, presumably the Readers are also done
        StringBuffer sysOut = null;
        StringBuffer sysErr = null;
        try
        {
            outReader.join();
            sysOut = outReader.getBuffer();
        }
        catch (InterruptedException ie2)
        {
            logger.logThrowable(Logger.ERRORMSG, ie2, "Joining outReader threw");
        }

        try
        {
            errReader.join();
            sysErr = errReader.getBuffer();
        }
        catch (InterruptedException ie3)
        {
            logger.logThrowable(Logger.ERRORMSG, ie3, "Joining errReader threw");
        }

        checkOutputStreams(cmdline, sysOut, sysErr, processReturnVal);
    }


    /** 
     * Worker method to evaluate the System.out/.err streams of 
     * a particular processor.  
     *
     * @param cmdline that was used for execProcess
     * @param outBuf buffer from execProcess' System.out
     * @param errBuf buffer from execProcess' System.err
     * @param processReturnVal from execProcess
     */
    protected void checkOutputStreams(String[] cmdline, StringBuffer outBuf, 
            StringBuffer errBuf, int processReturnVal)
    {
        Hashtable attrs = new Hashtable();
        attrs.put("program", cmdline[0]);
        attrs.put("returnVal", String.valueOf(processReturnVal));

        StringBuffer buf = new StringBuffer();
        if ((null != errBuf) && (errBuf.length() > 0))
        {
            buf.append("<system-err>");
            buf.append(errBuf);
            buf.append("</system-err>\n");
        }
        if ((null != outBuf) && (outBuf.length() > 0))
        {
            buf.append("<system-out>");
            buf.append(outBuf);
            buf.append("</system-out>\n");
        }
        logger.logElement(Logger.INFOMSG, "checkOutputStreams", attrs, buf.toString());
        attrs = null;
        buf = null;
    }


    /** 
     * Worker method to get a TransformWrapper; overridden as no-op.  
     *
     * @param datalet to test with
     * @return null; CmdlineTestlet does not use this
     */
    protected TransformWrapper getTransformWrapper(StylesheetDatalet datalet)
    {
        return null;
    }
}  // end of class CmdlineTestlet

