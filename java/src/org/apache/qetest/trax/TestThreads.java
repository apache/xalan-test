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
 * TestThreads.java
 *
 */
package org.apache.qetest.trax;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.Properties;
import java.util.StringTokenizer;

// Needed SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// For optional URI/URLs instead of string filenames
import java.net.URL;
import java.net.MalformedURLException;

// Note that not all imports are listed here
import org.apache.trax.*;  // TRAX package name will change

//-------------------------------------------------------------------------

/**
 * Testing multiple simultaneous processors on different threads with TRAX.
 * <p>No validation of output files is currently done!  You must manually
 * inspect any logfiles.  Most options can be passed in with a Properties file.</p>
 * @author shane_curcuru@lotus.com
 */
public class TestThreads
{

    /**
     * Convenience method to print out usage information.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public static String usage()
    {

        return ("Usage: TestThreads file.properties :\n"
                + "    where the properties file can set:,\n"
                + "    testDir=e:\\builds\\xsl-test\n"
                + "    outDir=e:\\builds\\xsl-test\\results\n"
                + "    logFile=e:\\builds\\xsl-test\\results\\TestThreads.xml\n"
                + "    numRunners=5\n" + "    numRunnerCalls=10\n"
                + "    setOneFile=bool01\n" + "    setTwoFile=expr01\n"
                + "    setThreeFile=numb01\n" + "    paramName=SomeParam\n"
                + "    paramVal=TheValue\n");
    }

    /** NEEDSDOC Field debug          */
    public boolean debug = true;  // for adhoc debugging

    /**
     * Number of sets of worker threads to create and loops per runner.
     * <p>'numRunners=xx', default is 10; 'numRunnerCalls=xx', default is 50.</p>
     */
    protected int numRunners = 10;

    /** NEEDSDOC Field numRunnerCalls          */
    protected int numRunnerCalls = 50;

    /**
     * Root input filenames that certain runners should use, in the testDir.
     * <p>'setOneFile=File'; 'setTwoFile=File'; 'setThreeFile=File'
     * in .prop file to set; default is TestThreads1, TestThreads2, TestThreads3.</p>
     * <p>Files are found in 'testDir=c:\bar\baz' from .prop file.</p>
     */
    protected String testDir = null;

    /** NEEDSDOC Field setOneFilenameRoot          */
    protected String setOneFilenameRoot = "TestThreads1";

    /** NEEDSDOC Field setTwoFilenameRoot          */
    protected String setTwoFilenameRoot = "TestThreads2";

    /** NEEDSDOC Field setThreeFilenameRoot          */
    protected String setThreeFilenameRoot = "TestThreads3";

    /**
     * All output logs and files get put in the outDir.
     */
    protected String outDir = null;

    /**
     * Sample PARAM name that certain runners should use.
     * <p>Use 'paramName=xx' in .prop file to set, default is test1.</p>
     */
    protected String paramName = "test1";

    /**
     * Sample PARAM value that certain runners should use.
     * <p>Use 'paramVal=xx' in .prop file to set, default is bar.</p>
     */
    protected String paramVal = "bar";

    /**
     * liaisonClassName that just the *second* set of runners should use.
     * <p>Use 'liaison=xx' in .prop file to set, default is null (whatever the processor's default is).</p>
     */
    protected String liaison = null;  // TRAX unused

    // Used to pass info to runners; simpler to update than changing ctors

    /** NEEDSDOC Field ID          */
    public static final int ID = 0;

    /** NEEDSDOC Field XMLNAME          */
    public static final int XMLNAME = 1;

    /** NEEDSDOC Field XSLNAME          */
    public static final int XSLNAME = 2;

    /** NEEDSDOC Field OUTNAME          */
    public static final int OUTNAME = 3;

    /** NEEDSDOC Field PARAMNAME          */
    public static final int PARAMNAME = 4;

    /** NEEDSDOC Field PARAMVAL          */
    public static final int PARAMVAL = 5;

    /** NEEDSDOC Field OPTIONS          */
    public static final int OPTIONS = 6;

    /** NEEDSDOC Field LIAISON          */
    public static final int LIAISON = 7;

    /** NEEDSDOC Field FUTUREUSE          */
    public static final int FUTUREUSE = 8;

    /**
     * Name of main file's output logging; each runner also has separate output.
     */
    protected String logFileName = "TestThreads.xml";

    /**
     * Construct multiple threads with processors and run them all.
     * @author Shane Curcuru & Scott Boag
     * <p>Preprocesses some stylesheets, then creates lots of worker threads.</p>
     */
    public void runTest()
    {

        // Prepare a log file and dump out some basic info
        createLogFile(logFileName);
        println("<?xml version=\"1.0\"?>");
        println("<resultsfile fileName=\"" + logFileName + "\">");
        println("<message desc=\"threads=" + (3 * numRunners)
                + " iterations=" + numRunnerCalls + "\"/>");
        println("<message desc=\"oneF=" + setOneFilenameRoot + " twof="
                + setTwoFilenameRoot + " threef=" + setThreeFilenameRoot
                + "\"/>");
        println("<message desc=\"param=" + paramName + " val=" + paramVal
                + " liaison=" + liaison + "\"/>");

        // Preprocess some stylesheets for use by the runners
        String errStr = "Create processor threw: ";
        Templates stylesheet1, stylesheet2, stylesheet3;

        try
        {
            String setOneURL =
                getURLFromString(testDir + setOneFilenameRoot + ".xsl",
                                 null).toExternalForm();
            String setTwoURL =
                getURLFromString(testDir + setTwoFilenameRoot + ".xsl",
                                 null).toExternalForm();
            String setThreeURL =
                getURLFromString(testDir + setThreeFilenameRoot + ".xsl",
                                 null).toExternalForm();
            Processor stylesheetProcessor = Processor.newInstance("xslt");

            errStr = "Processing stylesheet1 threw: ";
            stylesheet1 =
                stylesheetProcessor.process(new InputSource(setOneURL));
            errStr = "Processing stylesheet2 threw: ";
            stylesheet2 =
                stylesheetProcessor.process(new InputSource(setTwoURL));
            errStr = "Processing stylesheet3 threw: ";
            stylesheet3 =
                stylesheetProcessor.process(new InputSource(setThreeURL));
        }
        catch (Exception e)
        {
            println("<arbitrary desc=\"" + errStr + e.toString() + "\">");

            if (pWriter != null)
            {
                e.printStackTrace(pWriter);
            }

            e.printStackTrace();
            println("</arbitrary>");

            return;
        }

        errStr = "PreCreating runners threw: ";

        try
        {
            String[] rValues = new String[FUTUREUSE];

            // Create a whole bunch of worker threads and run them
            for (int i = 0; i < numRunners; i++)
            {
                TestThreadsRunner r1, r2, r3;
                Thread t1, t2, t3;

                // First set of runners reports on memory usage periodically
                rValues[ID] = "one-" + i;
                rValues[XMLNAME] = "file:" + testDir + setOneFilenameRoot
                                   + ".xml";
                rValues[XSLNAME] = testDir + setOneFilenameRoot + ".xsl";
                rValues[OUTNAME] = outDir + setOneFilenameRoot + "r" + i;
                rValues[PARAMNAME] = paramName;
                rValues[PARAMVAL] = paramVal;
                rValues[OPTIONS] = "memory;param";
                errStr = "Creating runnerone-" + i + " threw: ";
                r1 = new TestThreadsRunner(rValues, stylesheet1,
                                           numRunnerCalls);
                t1 = new Thread(r1);

                t1.start();

                // Second set of runners is polite; uses optional liaison
                rValues[ID] = "two-" + i;
                rValues[XMLNAME] = "file:" + testDir + setTwoFilenameRoot
                                   + ".xml";
                rValues[XSLNAME] = testDir + setTwoFilenameRoot + ".xsl";
                rValues[OUTNAME] = outDir + setTwoFilenameRoot + "r" + i;
                rValues[PARAMNAME] = paramName;
                rValues[PARAMVAL] = paramVal;
                rValues[OPTIONS] = "polite;param";

                if ((liaison != null) &&!(liaison.equals("")))
                    rValues[LIAISON] = liaison;

                errStr = "Creating runnertwo-" + i + " threw: ";
                r2 = new TestThreadsRunner(rValues, stylesheet2,
                                           numRunnerCalls);
                t2 = new Thread(r2);

                t2.start();

                rValues[LIAISON] = null;

                // Third set of runners will recreate it's processor each time
                // and report memory usage; but not set the param
                // Note: this causes lots of calls to System.gc
                rValues[ID] = "thr-" + i;
                rValues[XMLNAME] = "file:" + testDir + setThreeFilenameRoot
                                   + ".xml";
                rValues[XSLNAME] = testDir + setThreeFilenameRoot + ".xsl";
                rValues[OUTNAME] = outDir + setThreeFilenameRoot + "r" + i;
                rValues[PARAMNAME] = paramName;
                rValues[PARAMVAL] = paramVal;
                rValues[OPTIONS] = "recreate;memory";
                errStr = "Creating runnerthree-" + i + " threw: ";
                r3 = new TestThreadsRunner(rValues, stylesheet3,
                                           numRunnerCalls);
                t3 = new Thread(r3);

                t3.start();
                println("<message desc=\"Created " + i
                        + "th set of runners.\"/>");
            }
        }
        catch (Exception e)
        {
            println("<arbitrary desc=\"" + errStr + e.toString() + "\">");

            if (pWriter != null)
            {
                e.printStackTrace(pWriter);
            }

            e.printStackTrace();
            println("</arbitrary>");
        }

        // Clean up our own references, just for completeness
        stylesheet1 = null;
        stylesheet2 = null;
        stylesheet3 = null;
        errStr = null;

        println("<message desc=\"Created all our runners!\"/>");
        println("<message desc=\"TestThreads main thread now complete\"/>");
        println("</resultsfile>");

        if (pWriter != null)
            pWriter.flush();
    }

    /**
     * Read in properties file and set instance variables.  
     *
     * NEEDSDOC @param fName
     *
     * NEEDSDOC ($objectName$) @return
     */
    protected boolean initPropFile(String fName)
    {

        Properties p = new Properties();

        try
        {

            // Load named file into our properties block
            FileInputStream fIS = new FileInputStream(fName);

            p.load(fIS);

            // Parse out any values that match our internal convenience variables
            outDir = p.getProperty("outputDir", outDir);

            // Validate the outDir and use it to reset the logFileName
            File oDir = new File(outDir);

            if (!oDir.exists())
            {
                if (!oDir.mkdirs())
                {

                    // Error, we can't create the outDir, default to current dir
                    println("<message desc=\"outputDir(" + outDir
                            + ") does not exist, defaulting to .\"/>");

                    outDir = ".";
                }
            }

            // Verify testDir as well
            testDir = p.getProperty("inputDir", testDir);

            File tDir = new File(testDir);

            if (!tDir.exists())
            {
                if (!tDir.mkdirs())
                {

                    // Error, we can't create the testDir, abort
                    println("<message desc=\"inputDir(" + testDir
                            + ") does not exist, terminating test\"/>");

                    return false;
                }
            }

            // Add on separators
            testDir += File.separator;
            outDir += File.separator;

            // Each defaults to variable initializers            
            logFileName = p.getProperty("logFile", logFileName);
            setOneFilenameRoot = p.getProperty("setOneFile",
                                               setOneFilenameRoot);
            setTwoFilenameRoot = p.getProperty("setTwoFile",
                                               setTwoFilenameRoot);
            setThreeFilenameRoot = p.getProperty("setThreeFile",
                                                 setThreeFilenameRoot);
            paramName = p.getProperty("paramName", paramName);
            paramVal = p.getProperty("paramVal", paramVal);
            liaison = p.getProperty("liaison", liaison);

            String numb;

            numb = p.getProperty("numRunners");

            if (numb != null)
            {
                try
                {
                    numRunners = Integer.parseInt(numb);
                }
                catch (NumberFormatException numEx)
                {

                    // no-op, leave set as default
                    println("<message desc=\"numRunners threw: "
                            + numEx.toString() + "\"/>");
                }
            }

            numb = p.getProperty("numRunnerCalls");

            if (numb != null)
            {
                try
                {
                    numRunnerCalls = Integer.parseInt(numb);
                }
                catch (NumberFormatException numEx)
                {

                    // no-op, leave set as default
                    println("<message desc=\"numRunnerCalls threw: "
                            + numEx.toString() + "\"/>");
                }
            }
        }
        catch (Exception e)
        {
            println("<arbitrary=\"initPropFile: " + fName + " threw: "
                    + e.toString() + "\">");

            if (pWriter != null)
            {
                e.printStackTrace(pWriter);
            }

            e.printStackTrace();
            println("</arbitrary>");

            return false;
        }

        return true;
    }

    /**
     * Bottleneck output; goes to System.out and main's pWriter.  
     *
     * NEEDSDOC @param s
     */
    protected void println(String s)
    {

        System.out.println(s);

        if (pWriter != null)
            pWriter.println(s);
    }

    /** A simple log output file for the main thread; each runner also has it's own. */
    protected PrintWriter pWriter = null;

    /**
     * Worker method to setup a simple log output file.  
     *
     * NEEDSDOC @param n
     */
    protected void createLogFile(String n)
    {

        try
        {
            pWriter = new PrintWriter(new FileWriter(n, true));
        }
        catch (Exception e)
        {
            System.err.println("<message desc=\"createLogFile threw: "
                               + e.toString() + "\"/>");
            e.printStackTrace();
        }
    }

    /**
     * Startup the test from the command line.  
     *
     * NEEDSDOC @param args
     */
    public static void main(String[] args)
    {

        if (args.length != 1)
        {
            System.err.println("ERROR! Must have one argument\n" + usage());

            return;  // Don't System.exit, it's not polite
        }

        TestThreads app = new TestThreads();

        if (!app.initPropFile(args[0]))  // Side effect: creates pWriter for logging
        {
            System.err.println("ERROR! Could not read properties file: "
                               + args[0]);

            return;
        }

        app.runTest();
    }

    // /////////////////// HACK - added from Xalan1 org.apache.xalan.xslt.Process /////////////////////

    /**
     * Take a user string and try and parse XML, and also return the url.
     *
     * NEEDSDOC @param urlString
     * NEEDSDOC @param base
     *
     * NEEDSDOC ($objectName$) @return
     * @exception SAXException thrown if we really really can't create the URL
     */
    public static URL getURLFromString(String urlString, String base)
            throws SAXException
    {

        String origURLString = urlString;
        String origBase = base;

        // System.out.println("getURLFromString - urlString: "+urlString+", base: "+base);
        Object doc;
        URL url = null;
        int fileStartType = 0;

        try
        {
            if (null != base)
            {
                if (base.toLowerCase().startsWith("file:/"))
                {
                    fileStartType = 1;
                }
                else if (base.toLowerCase().startsWith("file:"))
                {
                    fileStartType = 2;
                }
            }

            boolean isAbsoluteURL;

            // From http://www.ics.uci.edu/pub/ietf/uri/rfc1630.txt
            // A partial form can be distinguished from an absolute form in that the
            // latter must have a colon and that colon must occur before any slash
            // characters. Systems not requiring partial forms should not use any
            // unencoded slashes in their naming schemes.  If they do, absolute URIs
            // will still work, but confusion may result.
            int indexOfColon = urlString.indexOf(':');
            int indexOfSlash = urlString.indexOf('/');

            if ((indexOfColon != -1) && (indexOfSlash != -1)
                    && (indexOfColon < indexOfSlash))
            {

                // The url (or filename, for that matter) is absolute.
                isAbsoluteURL = true;
            }
            else
            {
                isAbsoluteURL = false;
            }

            if (isAbsoluteURL || (null == base) || (base.length() == 0))
            {
                try
                {
                    url = new URL(urlString);
                }
                catch (MalformedURLException e){}
            }

            // The Java URL handling doesn't seem to handle relative file names.
            else if (!((urlString.charAt(0) == '.') || (fileStartType > 0)))
            {
                try
                {
                    URL baseUrl = new URL(base);

                    url = new URL(baseUrl, urlString);
                }
                catch (MalformedURLException e){}
            }

            if (null == url)
            {

                // Then we're going to try and make a file URL below, so strip 
                // off the protocol header.
                if (urlString.toLowerCase().startsWith("file:/"))
                {
                    urlString = urlString.substring(6);
                }
                else if (urlString.toLowerCase().startsWith("file:"))
                {
                    urlString = urlString.substring(5);
                }
            }

            if ((null == url) && ((null == base) || (fileStartType > 0)))
            {
                if (1 == fileStartType)
                {
                    if (null != base)
                        base = base.substring(6);

                    fileStartType = 1;
                }
                else if (2 == fileStartType)
                {
                    if (null != base)
                        base = base.substring(5);

                    fileStartType = 2;
                }

                File f = new File(urlString);

                if (!f.isAbsolute() && (null != base))
                {

                    // String dir = f.isDirectory() ? f.getAbsolutePath() : f.getParent();
                    // System.out.println("prebuiltUrlString (1): "+base);
                    StringTokenizer tokenizer = new StringTokenizer(base,
                                                    "\\/");
                    String fixedBase = null;

                    while (tokenizer.hasMoreTokens())
                    {
                        String token = tokenizer.nextToken();

                        if (null == fixedBase)
                        {

                            // Thanks to Rick Maddy for the bug fix for UNIX here.
                            if (base.charAt(0) == '\\'
                                    || base.charAt(0) == '/')
                            {
                                fixedBase = File.separator + token;
                            }
                            else
                            {
                                fixedBase = token;
                            }
                        }
                        else
                        {
                            fixedBase += File.separator + token;
                        }
                    }

                    // System.out.println("rebuiltUrlString (1): "+fixedBase);
                    f = new File(fixedBase);

                    String dir = f.isDirectory()
                                 ? f.getAbsolutePath() : f.getParent();

                    // System.out.println("dir: "+dir);
                    // System.out.println("urlString: "+urlString);
                    // f = new File(dir, urlString);
                    // System.out.println("f (1): "+f.toString());
                    // urlString = f.getAbsolutePath();
                    f = new File(urlString);

                    boolean isAbsolute = f.isAbsolute()
                                         || (urlString.charAt(0) == '\\')
                                         || (urlString.charAt(0) == '/');

                    if (!isAbsolute)
                    {

                        // Getting more and more ugly...
                        if (dir.charAt(dir.length() - 1)
                                != File.separator.charAt(0)
                                && urlString.charAt(0)
                                   != File.separator.charAt(0))
                        {
                            urlString = dir + File.separator + urlString;
                        }
                        else
                        {
                            urlString = dir + urlString;
                        }

                        // System.out.println("prebuiltUrlString (2): "+urlString);
                        tokenizer = new StringTokenizer(urlString, "\\/");

                        String rebuiltUrlString = null;

                        while (tokenizer.hasMoreTokens())
                        {
                            String token = tokenizer.nextToken();

                            if (null == rebuiltUrlString)
                            {

                                // Thanks to Rick Maddy for the bug fix for UNIX here.
                                if (urlString.charAt(0) == '\\'
                                        || urlString.charAt(0) == '/')
                                {
                                    rebuiltUrlString = File.separator + token;
                                }
                                else
                                {
                                    rebuiltUrlString = token;
                                }
                            }
                            else
                            {
                                rebuiltUrlString += File.separator + token;
                            }
                        }

                        // System.out.println("rebuiltUrlString (2): "+rebuiltUrlString);
                        if (null != rebuiltUrlString)
                            urlString = rebuiltUrlString;
                    }

                    // System.out.println("fileStartType: "+fileStartType);
                    if (1 == fileStartType)
                    {
                        if (urlString.charAt(0) == '/')
                        {
                            urlString = "file://" + urlString;
                        }
                        else
                        {
                            urlString = "file:/" + urlString;
                        }
                    }
                    else if (2 == fileStartType)
                    {
                        urlString = "file:" + urlString;
                    }

                    try
                    {

                        // System.out.println("Final before try: "+urlString);
                        url = new URL(urlString);
                    }
                    catch (MalformedURLException e)
                    {

                        // System.out.println("Error trying to make URL from "+urlString);
                    }
                }
            }

            if (null == url)
            {

                // The sun java VM doesn't do this correctly, but I'll 
                // try it here as a second-to-last resort.
                if ((null != origBase) && (origBase.length() > 0))
                {
                    try
                    {
                        URL baseURL = new URL(origBase);

                        // System.out.println("Trying to make URL from "+origBase+" and "+origURLString);
                        url = new URL(baseURL, origURLString);

                        // System.out.println("Success! New URL is: "+url.toString());
                    }
                    catch (MalformedURLException e)
                    {

                        // System.out.println("Error trying to make URL from "+origBase+" and "+origURLString);
                    }
                }

                if (null == url)
                {
                    try
                    {
                        String lastPart;

                        if (null != origBase)
                        {
                            File baseFile = new File(origBase);

                            if (baseFile.isDirectory())
                            {
                                lastPart =
                                    new File(baseFile,
                                             urlString).getAbsolutePath();
                            }
                            else
                            {
                                String parentDir = baseFile.getParent();

                                lastPart =
                                    new File(parentDir,
                                             urlString).getAbsolutePath();
                            }
                        }
                        else
                        {
                            lastPart = new File(urlString).getAbsolutePath();
                        }

                        // Hack
                        // if((lastPart.charAt(0) == '/') && (lastPart.charAt(2) == ':'))
                        //   lastPart = lastPart.substring(1, lastPart.length() - 1);
                        String fullpath;

                        if (lastPart.charAt(0) == '\\'
                                || lastPart.charAt(0) == '/')
                        {
                            fullpath = "file://" + lastPart;
                        }
                        else
                        {
                            fullpath = "file:" + lastPart;
                        }

                        url = new URL(fullpath);
                    }
                    catch (MalformedURLException e2)
                    {
                        throw new SAXException("Cannot create url for: "
                                               + urlString, e2);

                        //XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_CREATE_URL, new Object[]{urlString}),e2); //"Cannot create url for: " + urlString, e2 );
                    }
                }
            }
        }
        catch (SecurityException se)
        {
            try
            {
                url = new URL("http://xml.apache.org/xslt/"
                              + java.lang.Math.random());  // dummy
            }
            catch (MalformedURLException e2)
            {

                // I give up
            }
        }

        // System.out.println("url: "+url.toString());
        return url;
    }
}  // end of class TestThreads

/**
 * Worker class to run a processor on a separate thread.
 * <p>Currently, no automated validation is done, however most
 * output files and all error logs are saved to disk allowing for
 * later manual verification.</p>
 */
class TestThreadsRunner implements Runnable
{

    /** NEEDSDOC Field xslStylesheet          */
    Templates xslStylesheet;

    /** NEEDSDOC Field numProcesses          */
    int numProcesses;

    /** NEEDSDOC Field runnerID          */
    String runnerID;

    /** NEEDSDOC Field xmlName          */
    String xmlName;

    /** NEEDSDOC Field xslName          */
    String xslName;

    /** NEEDSDOC Field outName          */
    String outName;

    /** NEEDSDOC Field paramName          */
    String paramName;

    /** NEEDSDOC Field paramVal          */
    String paramVal;

    /** NEEDSDOC Field liaison          */
    String liaison;

    /** NEEDSDOC Field polite          */
    boolean polite = false;  // if we should yield each loop

    /** NEEDSDOC Field recreate          */
    boolean recreate = false;  // if we should re-create a new processor each time

    /** NEEDSDOC Field validate          */
    boolean validate = false;  // if we should attempt to validate output files (FUTUREWORK)

    /** NEEDSDOC Field reportMem          */
    boolean reportMem = false;  // if we should report memory usage periodically

    /** NEEDSDOC Field setParam          */
    boolean setParam = false;  // if we should set our parameter or not

    /**
     * Constructor TestThreadsRunner
     *
     *
     * NEEDSDOC @param params
     * NEEDSDOC @param xslStylesheet
     * NEEDSDOC @param numProcesses
     */
    TestThreadsRunner(String[] params, Templates xslStylesheet,
                      int numProcesses)
    {

        this.xslStylesheet = xslStylesheet;
        this.numProcesses = numProcesses;
        this.runnerID = params[TestThreads.ID];
        this.xmlName = params[TestThreads.XMLNAME];
        this.xslName = params[TestThreads.XSLNAME];
        this.outName = params[TestThreads.OUTNAME];
        this.paramName = params[TestThreads.PARAMNAME];
        this.paramVal = params[TestThreads.PARAMVAL];

        if (params[TestThreads.OPTIONS].indexOf("polite") > 0)
            polite = true;

        if (params[TestThreads.OPTIONS].indexOf("recreate") > 0)
            recreate = true;

        if (params[TestThreads.OPTIONS].indexOf("validate") > 0)
            validate = true;

        if (params[TestThreads.OPTIONS].indexOf("memory") > 0)
            reportMem = true;

        if (params[TestThreads.OPTIONS].indexOf("param") > 0)
            setParam = true;

        if (params[TestThreads.LIAISON] != null)  // TRAX unused
            liaison = params[TestThreads.LIAISON];
    }

    /**
     * Bottleneck output; both to System.out and to our private errWriter.  
     *
     * NEEDSDOC @param s
     */
    protected void println(String s)
    {

        System.out.println(s);

        if (errWriter != null)
            errWriter.println(s);
    }

    /**
     * Bottleneck output; both to System.out and to our private errWriter.  
     *
     * NEEDSDOC @param s
     */
    protected void print(String s)
    {

        System.out.print(s);

        if (errWriter != null)
            errWriter.print(s);
    }

    /** NEEDSDOC Field errWriter          */
    PrintWriter errWriter = null;

    /**
     * NEEDSDOC Method createErrWriter 
     *
     */
    protected void createErrWriter()
    {

        try
        {
            errWriter = new PrintWriter(new FileWriter(outName + ".log"),
                                        true);
        }
        catch (Exception e)
        {
            System.err.println("<message desc=\"" + runnerID + ":threw: "
                               + e.toString() + "\"/>");
        }
    }

    /** Main entrypoint; loop and perform lots of processes. */
    public void run()
    {

        int i = 0;  // loop counter; used for error reporting

        createErrWriter();
        println("<?xml version=\"1.0\"?>");
        println("<testrunner desc=\"" + runnerID + ":started\" fileName=\""
                + xslName + "\">");

        Processor p = null;

        try
        {

            // Each runner creates it's own processor for use and it's own error log
            p = Processor.newInstance("xslt");

            // Munge the input filenames to be URLs
            xmlName = TestThreads.getURLFromString(xmlName,
                                                   null).toExternalForm();
            xslName = TestThreads.getURLFromString(xslName,
                                                   null).toExternalForm();

            println("<arbitrary desc=\"" + runnerID + ":processing\">");
        }
        catch (Throwable ex)
        {  // If we got here, just log it and bail, no sense continuing
            println("<throwable desc=\"" + ex.toString() + "\"><![CDATA[");
            ex.printStackTrace(errWriter);
            println("\n</throwable>");
            println("<message desc=\"" + runnerID + ":complete-ERROR:after:"
                    + i + "\"/>");
            println("</testrunner>");

            if (errWriter != null)
                errWriter.close();

            return;
        }

        try
        {

            // Loop away...
            for (i = 0; i < numProcesses; i++)
            {

                // Run a process using the pre-compiled stylesheet we were construced with
                {
                    Transformer transformer1 = xslStylesheet.newTransformer();
                    FileOutputStream resultStream1 =
                        new FileOutputStream(outName + ".out");
                    Result result1 = new Result(resultStream1);

                    if (setParam)
                        transformer1.setParameter(paramName, null, paramVal);

                    print(".");  // Note presence of this in logs shows which process threw an exception
                    transformer1.transform(new InputSource(xmlName), result1);
                    resultStream1.close();

                    // Temporary vars go out of scope for cleanup here
                }

                // Now process something with a newly-processed stylesheet
                {
                    Templates templates2 =
                        p.process(new InputSource(xslName));
                    Transformer transformer2 = templates2.newTransformer();
                    FileOutputStream resultStream2 =
                        new FileOutputStream(outName + "_.out");
                    Result result2 = new Result(resultStream2);

                    if (setParam)
                        transformer2.setParameter(paramName, null, paramVal);

                    print("*");  // Note presence of this in logs shows which process threw an exception
                    transformer2.transform(new InputSource(xmlName), result2);
                    resultStream2.close();
                }

                // if asked, report memory statistics
                if (reportMem)
                {
                    Runtime r = Runtime.getRuntime();

                    r.gc();

                    long freeMemory = r.freeMemory();
                    long totalMemory = r.totalMemory();

                    println("<statistic desc=\"" + runnerID
                            + ":memory:longval-free:doubleval-total\">");
                    println("<longval>" + freeMemory + "</longval>");
                    println("<doubleval>" + totalMemory + "</doubleval>");
                    println("</statistic>");
                }

                // if we're polite, let others play for a bit
                if (polite)
                    java.lang.Thread.yield();
            }

            // IF we get here, we worked without exceptions (presumably successfully)
            println("</arbitrary>");
            println("<message desc=\"" + runnerID + ":complete-OK:after:"
                    + numProcesses + "\"/>");
        }

        // Separate messages for each kind of exception
        catch (TransformException te)
        {
            println("\n<transformexception desc=\"" + te.toString() + "\">");
            logStackTrace(te, errWriter);
            logContainedException(te, errWriter);
            println("</transformexception>");
            println("</arbitrary>");
            println("<message desc=\"" + runnerID + ":complete-ERROR:after:"
                    + i + "\"/>");
        }
        catch (SAXException se)
        {
            println("\n<saxexception desc=\"" + se.toString() + "\">");
            logStackTrace(se, errWriter);
            logContainedException(se, errWriter);
            println("</saxexception>");
            println("</arbitrary>");
            println("<message desc=\"" + runnerID + ":complete-ERROR:after:"
                    + i + "\"/>");
        }
        catch (Throwable ex)
        {
            logThrowable(ex, errWriter);
            println("</arbitrary>");
            println("<message desc=\"" + runnerID + ":complete-ERROR:after:"
                    + i + "\"/>");
        }
        finally
        {

            // Cleanup our references, etc.
            println("</testrunner>");

            if (errWriter != null)
                errWriter.close();

            runnerID = null;
            xmlName = null;
            xslName = null;
            xslStylesheet = null;
            outName = null;
        }
    }  // end of run()...

    /**
     * NEEDSDOC Method logContainedException 
     *
     *
     * NEEDSDOC @param parent
     * NEEDSDOC @param p
     */
    private void logContainedException(SAXException parent, PrintWriter p)
    {

        Exception containedException = parent.getException();

        if (null != containedException)
        {
            println("<containedexception desc=\""
                    + containedException.toString() + "\">");
            logStackTrace(containedException, p);
            println("</containedexception>");
        }
    }

    /**
     * NEEDSDOC Method logThrowable 
     *
     *
     * NEEDSDOC @param t
     * NEEDSDOC @param p
     */
    private void logThrowable(Throwable t, PrintWriter p)
    {

        println("\n<throwable desc=\"" + t.toString() + "\">");
        logStackTrace(t, p);
        println("</throwable>");
    }

    /**
     * NEEDSDOC Method logStackTrace 
     *
     *
     * NEEDSDOC @param t
     * NEEDSDOC @param p
     */
    private void logStackTrace(Throwable t, PrintWriter p)
    {

        // Should check if (errWriter == null)
        println("<stacktrace><![CDATA[");
        t.printStackTrace(p);

        // Could also echo to stdout, but not really worth it
        println("]]></stacktrace>");
    }
}  // end of class TestThreadsRunner...

// END OF FILE
