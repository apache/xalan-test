/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights 
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
 * TestMultiTypeThreads.java
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

// Needed DOM classes
import org.w3c.dom.Node;
import org.w3c.dom.Document;

// For optional URI/URLs instead of string filenames
import java.net.URL;
import java.net.MalformedURLException;

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;    // We assume Features.STREAM for some tests
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

// HACK: Use Xalan's Serializers - should be changed
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;

//-------------------------------------------------------------------------

/**
 * Testing multiple simultaneous processors on different threads 
 * using different processing methods with TRAX.
 * <p>No validation of output files is currently done!  You must manually
 * inspect any logfiles.  Most options can be passed in with a Properties file.</p>
 * <p>Note: Most automated tests extend XSLProcessorTestBase, and 
 * are named *Test.java.  Since we are semi-manual, we're 
 * named Test*.java instead.</p>
 * We assume Features.STREAM.
 * @author shane_curcuru@lotus.com
 */
public class TestMultiTypeThreads
{

    /**
     * Convenience method to print out usage information.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public static String usage()
    {

        return ("Usage: TestMultiTypeThreads [-load] file.properties :\n"
                + "    where the properties file can set:,\n"
                + "    inputDir=e:\\builds\\xsl-test\n"
                + "    outputDir=e:\\builds\\xsl-test\\results\n"
                + "    logFile=e:\\builds\\xsl-test\\results\\TestMultiTypeThreads.xml\n"
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

    /**
     * Number of sets of worker threads to create and loops per runner.
     * <p>'numRunners=xx', default is 10; 'numRunnerCalls=xx', default is 50.</p>
     */
    protected int numRunnerCalls = 50;

    /**
     * Root input filenames that certain runners should use, in the inputDir.
     * <p>'setOneFile=File'; 'setTwoFile=File'; 'setThreeFile=File'
     * in .prop file to set; default is TestMultiTypeThreads1, TestMultiTypeThreads2, TestMultiTypeThreads3.</p>
     * <p>Files are found in 'inputDir=c:\bar\baz' from .prop file.</p>
     */
    protected String inputDir = null;

    /** NEEDSDOC Field setOneFilenameRoot          */
    protected String setOneFilenameRoot = "TestMultiTypeThreads1";

    /** NEEDSDOC Field setTwoFilenameRoot          */
    protected String setTwoFilenameRoot = "TestMultiTypeThreads2";

    /** NEEDSDOC Field setThreeFilenameRoot          */
    protected String setThreeFilenameRoot = "TestMultiTypeThreads3";

    /**
     * All output logs and files get put in the outputDir.
     */
    protected String outputDir = null;

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

    /** RunnerID offset in ctor's array initializer.   */
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

    /** TRANSFORM_TYPE defines which 'type' - dom, sax, streams, etc.  */
    public static final int TRANSFORM_TYPE = 8;

    /** NEEDSDOC Field FUTUREUSE          */
    public static final int FUTUREUSE = 9;

    /**
     * Name of main file's output logging; each runner also has separate output.
     */
    protected String logFileName = "TestMultiTypeThreads.xml";

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
        println("<resultsfile logFile=\"" + logFileName + "\">");
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
            String setOneURL = filenameToURI(inputDir + setOneFilenameRoot + ".xsl");
            String setTwoURL = filenameToURI(inputDir + setTwoFilenameRoot + ".xsl");
            String setThreeURL = filenameToURI(inputDir + setThreeFilenameRoot + ".xsl");

            TransformerFactory factory = TransformerFactory.newInstance();

            // Note: for now, just use StreamSources to build all stylesheets
            errStr = "Processing stylesheet1 threw: ";
            stylesheet1 =
                factory.newTemplates(new StreamSource(setOneURL));
            errStr = "Processing stylesheet2 threw: ";
            stylesheet2 =
                factory.newTemplates(new StreamSource(setTwoURL));
            errStr = "Processing stylesheet3 threw: ";
            stylesheet3 =
                factory.newTemplates(new StreamSource(setThreeURL));
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
                TMTThreadsRunner r1, r2, r3;
                Thread t1, t2, t3;
                String transformType = StreamSource.FEATURE;

                // Alternate sets of runners use alternate transform types
                if ((i % 3) == 2)
                {
                    transformType = DOMSource.FEATURE;
                }
                else if ((i % 3) == 1)
                {
                    transformType = StreamSource.FEATURE;
                }
                else
                {
                    transformType = SAXSource.FEATURE;
                }
                // First set of runners reports on memory usage periodically
                rValues[ID] = "one-" + i;
                rValues[XMLNAME] = filenameToURI(inputDir + setOneFilenameRoot + ".xml");
                rValues[XSLNAME] = filenameToURI(inputDir + setOneFilenameRoot + ".xsl");
                rValues[OUTNAME] = outputDir + setOneFilenameRoot + "r" + i;
                rValues[PARAMNAME] = paramName;
                rValues[PARAMVAL] = paramVal;
                rValues[OPTIONS] = "memory;param";
                rValues[TRANSFORM_TYPE] = transformType;
                errStr = "Creating runnerone-" + i + " threw: ";
                r1 = new TMTThreadsRunner(rValues, stylesheet1,
                                           numRunnerCalls);
                t1 = new Thread(r1);

                t1.start();

                // Second set of runners is polite; uses optional liaison
                rValues[ID] = "two-" + i;
                rValues[XMLNAME] = filenameToURI(inputDir + setTwoFilenameRoot + ".xml");
                rValues[XSLNAME] = filenameToURI(inputDir + setTwoFilenameRoot + ".xsl");
                rValues[OUTNAME] = outputDir + setTwoFilenameRoot + "r" + i;
                rValues[PARAMNAME] = paramName;
                rValues[PARAMVAL] = paramVal;
                rValues[OPTIONS] = "polite;param";
                rValues[TRANSFORM_TYPE] = transformType;

                if ((liaison != null) &&!(liaison.equals("")))
                    rValues[LIAISON] = liaison;

                errStr = "Creating runnertwo-" + i + " threw: ";
                r2 = new TMTThreadsRunner(rValues, stylesheet2,
                                           numRunnerCalls);
                t2 = new Thread(r2);

                t2.start();

                rValues[LIAISON] = null;

                // Third set of runners will recreate it's processor each time
                // and report memory usage; but not set the param
                // Note: this causes lots of calls to System.gc
                rValues[ID] = "thr-" + i;
                rValues[XMLNAME] = filenameToURI(inputDir + setThreeFilenameRoot + ".xml");
                rValues[XSLNAME] = filenameToURI(inputDir + setThreeFilenameRoot + ".xsl");
                rValues[OUTNAME] = outputDir + setThreeFilenameRoot + "r" + i;
                rValues[PARAMNAME] = paramName;
                rValues[PARAMVAL] = paramVal;
                rValues[OPTIONS] = "recreate;memory";
                rValues[TRANSFORM_TYPE] = transformType;
                errStr = "Creating runnerthree-" + i + " threw: ";
                r3 = new TMTThreadsRunner(rValues, stylesheet3,
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
        println("<message desc=\"TestMultiTypeThreads main thread now complete\"/>");
        println("</resultsfile>");

        if (pWriter != null)
            pWriter.flush();
    }

    /**
     * Read in properties file and set instance variables.  
     *
     * @param fName name of .properties file to read
     * @return false if error occoured
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
            outputDir = p.getProperty("outputDir", outputDir);

            // Validate the outputDir and use it to reset the logFileName
            File oDir = new File(outputDir);

            if (!oDir.exists())
            {
                if (!oDir.mkdirs())
                {

                    // Error, we can't create the outputDir, default to current dir
                    println("<message desc=\"outputDir(" + outputDir
                            + ") does not exist, defaulting to .\"/>");

                    outputDir = ".";
                }
            }

            // Verify inputDir as well
            inputDir = p.getProperty("inputDir", inputDir);

            File tDir = new File(inputDir);

            if (!tDir.exists())
            {
                if (!tDir.mkdirs())
                {

                    // Error, we can't create the inputDir, abort
                    println("<message desc=\"inputDir(" + inputDir
                            + ") does not exist, terminating test\"/>");

                    return false;
                }
            }

            // Add on separators
            inputDir += File.separator;
            outputDir += File.separator;

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

        if (args.length < 1)
        {
            System.err.println("ERROR! Must have at least one argument\n" + usage());

            return;  // Don't System.exit, it's not polite
        }

        TestMultiTypeThreads app = new TestMultiTypeThreads();
        // semi-HACK: accept and ignore -load as first arg only
        String propFileName = null;
        if ("-load".equalsIgnoreCase(args[0]))
        {
            propFileName = args[1];
        }
        else
        {
            propFileName = args[0];
        }
        if (!app.initPropFile(propFileName))  // Side effect: creates pWriter for logging
        {
            System.err.println("ERROR! Could not read properties file: "
                               + propFileName);

            return;
        }

        app.runTest();
    }

    /**
     * Worker method to translate String to URI.  
     * Note: Xerces and Crimson appear to handle some URI references 
     * differently - this method needs further work once we figure out 
     * exactly what kind of format each parser wants (esp. considering 
     * relative vs. absolute references).
     * @param String path\filename of test file
     * @return URL to pass to SystemId
     */
    public static String filenameToURI(String filename)
    {
        File f = new File(filename);
        String tmp = f.getAbsolutePath();
            if (File.separatorChar == '\\') {
                tmp = tmp.replace('\\', '/');
            }
        return "file:///" + tmp;
    }
}  // end of class TestMultiTypeThreads

/**
 * Worker class to run a processor on a separate thread.
 * <p>Currently, no automated validation is done, however most
 * output files and all error logs are saved to disk allowing for
 * later manual verification.</p>
 */
class TMTThreadsRunner implements Runnable
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

    /** NEEDSDOC Field setParam          */
    String transformType = StreamSource.FEATURE;

    /**
     * Constructor TMTThreadsRunner
     *
     *
     * NEEDSDOC @param params
     * NEEDSDOC @param xslStylesheet
     * NEEDSDOC @param numProcesses
     */
    TMTThreadsRunner(String[] params, Templates xslStylesheet,
                      int numProcesses)
    {

        this.xslStylesheet = xslStylesheet;
        this.numProcesses = numProcesses;
        this.runnerID = params[TestMultiTypeThreads.ID];
        this.xmlName = params[TestMultiTypeThreads.XMLNAME]; // must already be legal URI
        this.xslName = params[TestMultiTypeThreads.XSLNAME]; // must already be legal URI
        this.outName = params[TestMultiTypeThreads.OUTNAME]; // must be local path/filename
        this.paramName = params[TestMultiTypeThreads.PARAMNAME];
        this.paramVal = params[TestMultiTypeThreads.PARAMVAL];

        if (params[TestMultiTypeThreads.OPTIONS].indexOf("polite") > 0)
            polite = true;

        if (params[TestMultiTypeThreads.OPTIONS].indexOf("recreate") > 0)
            recreate = true;

        if (params[TestMultiTypeThreads.OPTIONS].indexOf("validate") > 0)
            validate = true;

        // Optimization: only report memory if asked to and we're 
        //  in the first iteration of runners created
        if ((params[TestMultiTypeThreads.OPTIONS].indexOf("memory") > 0)
            && (this.runnerID.indexOf("0") >= 0))
            reportMem = true;

        if (params[TestMultiTypeThreads.OPTIONS].indexOf("param") > 0)
            setParam = true;

        if (params[TestMultiTypeThreads.LIAISON] != null)  // TRAX unused
            liaison = params[TestMultiTypeThreads.LIAISON];

        if (params[TestMultiTypeThreads.TRANSFORM_TYPE] != null)
            transformType = params[TestMultiTypeThreads.TRANSFORM_TYPE];
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

        TransformerFactory factory = null;

        try
        {

            // Each runner creates it's own processor for use and it's own error log
            factory = TransformerFactory.newInstance();

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
                    if (transformType == DOMSource.FEATURE)
                    {
                        doDOMTransform(transformer1, xmlName, outName + "d.out", "d");
                    }
                    else if (transformType == SAXSource.FEATURE)
                    {
                        doSAXTransform(xslName, xmlName, outName + "x.out", "x");
                    }
                    else if (transformType == StreamSource.FEATURE)
                    {
                        // Call String ctor so we don't have to setSystemId
                        Result result1 = new StreamResult(outName + "t.out");

                        if (setParam)
                            transformer1.setParameter(paramName, paramVal);

                        print("t");  // Note presence of this in logs shows which process threw an exception
                        transformer1.transform(new StreamSource(xmlName), result1);
                    }
                    else 
                    {
                        throw new RuntimeException("unsupported transformType: " + transformType);
                    }

                    // Temporary vars go out of scope for cleanup here
                }

                // Now process something with a newly-processed stylesheet
                {
                    Templates templates2 =
                        factory.newTemplates(new StreamSource(xslName));
                    Transformer transformer2 = templates2.newTransformer();
                    if (transformType == DOMSource.FEATURE)
                    {
                        doDOMTransform(transformer2, xmlName, outName + "_D.out", "D");
                    }
                    else if (transformType == SAXSource.FEATURE)
                    {
                        doSAXTransform(xslName, xmlName, outName + "_X.out", "X");
                    }
                    else // if (transformType == StreamSource.FEATURE)
                    {
                        Result result2 = new StreamResult(outName + "_T.out");

                        if (setParam)
                            transformer2.setParameter(paramName, paramVal);

                        print("T");  // Note presence of this in logs shows which process threw an exception
                        transformer2.transform(new StreamSource(xmlName), result2);
                    }
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
        catch (TransformerException te)
        {
            println("\n<TransformerException desc=\"" + te.toString() + "\">");
            logStackTrace(te, errWriter);
            logContainedException(te, errWriter);
            println("</TransformerException>");
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

    /** Worker method to do a specific type of transform  */
    private void doDOMTransform(Transformer t, String xmlName, String outName, String marker)
        throws Exception
    {
        if (setParam)
            t.setParameter(paramName, paramVal);

        // Parse in the xml data into a DOM
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
        Node xmlDoc = docBuilder.parse(new InputSource(xmlName));
        Source xmlSource = new DOMSource(xmlDoc);
        xmlSource.setSystemId(xmlName);

        // Prepare a result and transform it into a DOM
        org.w3c.dom.Document outNode = docBuilder.newDocument();
        print(marker);  // Note presence of this in logs shows which process threw an exception
        t.transform(xmlSource, new DOMResult(outNode));

        // Now serialize output to disk with identity transformer
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer serializer = factory.newTransformer();
        serializer.transform(new DOMSource(outNode), 
                             new StreamResult(new FileOutputStream(outName)));
        // do we need to FileOutputStream.close()?
    }

    /** Worker method to do a specific type of transform  */
    private void doSAXTransform(String xslName, String xmlName, String outName, String marker)
        throws Exception
    {
        TransformerFactory factory = TransformerFactory.newInstance();
        // should check for SAXResult.FEATURE first!
        SAXTransformerFactory sfactory = ((SAXTransformerFactory) factory);
        
        // Create an Document node as the root for the output.
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
        Document outNode = docBuilder.newDocument();
    
        // Create a ContentHandler that can liston to SAX events 
        // and transform the output to DOM nodes.
        TransformerHandler handler = sfactory.newTransformerHandler(new StreamSource(xslName));
        handler.setResult(new DOMResult(outNode));
    
        // Create a reader and set it's ContentHandler to be the 
        // transformer.
        SAXParserFactory spfactory = SAXParserFactory.newInstance();
        spfactory.setNamespaceAware(true);
        SAXParser jaxpParser = spfactory.newSAXParser();
        XMLReader reader = jaxpParser.getXMLReader();

        reader.setContentHandler(handler);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
    
        // Send the SAX events from the parser to the transformer,
        // and thus to the DOM tree.
        print(marker);  // Note presence of this in logs shows which process threw an exception
        handler.setSystemId(xmlName);
        reader.parse(xmlName);

        // Serialize the DOM tree out
            FileOutputStream fos = new FileOutputStream(outName);
        Transformer serializer = factory.newTransformer();
        //serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        //serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.transform(new DOMSource(outNode), new StreamResult(fos));
    }

    /**
     * NEEDSDOC Method logContainedException 
     *
     *
     * NEEDSDOC @param parent
     * NEEDSDOC @param p
     */
    private void logContainedException(TransformerException parent, PrintWriter p)
    {

        Throwable containedException = parent.getException();

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
}  // end of class TMTThreadsRunner...

// END OF FILE
