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
 * FileBasedTest.java
 *
 */
package org.apache.qetest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

//-------------------------------------------------------------------------

/**
 * Base class for file-based tests.
 * Many tests will need to operate on files external to a product
 * under test.  This class provides useful, generic functionality
 * in these cases.
 * <p>FileBasedTest defines a number of common fields that many
 * tests that operate on data files may use.</p>
 * <ul>These are each pre-initialized for you from the command line or property file.
 * <li>inputDir (string representing dir where input files come from)</li>
 * <li>outputDir (string representing dir where output, working, temp files go)</li>
 * <li>goldDir  (string representing dir where known good reference files are)</li>
 * <li>debug (generic boolean flag for debugging)</li>
 * <li>loggers (FQCN;of;Loggers to add to our Reporter)</li>
 * <li>loggingLevel (passed to Reporters)</li>
 * <li>logFile (string filename for any file-based Reporter)</li>
 * </ul>
 * @author Shane_Curcuru@lotus.com
 * @version 3.0
 */
public class FileBasedTest extends TestImpl
{

    /**
     * Convenience method to print out usage information.
     * @author Shane Curcuru
     * <p>Should be overridden by subclasses, although they are free
     * to call super.usage() to get the common options string.</p>
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String usage()
    {

        return ("Common options supported by FileBasedTest:\n" + "    -"
                + OPT_LOAD
                + " <loadPropFile>  (read in a .properties file,\n"
                + "                           that can set any/all of the other opts)\n"
                + "    -" + OPT_INPUTDIR + "     <path to input files>\n"
                + "    -" + OPT_OUTPUTDIR
                + "    <path to output area - where all output is sent>\n"
                + "    -" + OPT_GOLDDIR
                + "      <path to gold reference output>\n" + "    -"
                + Logger.OPT_LOGFILE
                + "      <resultsFileName> (sends test results to file)\n"
                + "    -" + Reporter.OPT_LOGGERS
                + "      <FQCN;of;Loggers to use >\n" + "    -"
                + Reporter.OPT_LOGGINGLEVEL + " <int level>\n" + "    -"
                + Reporter.OPT_DEBUG
                + "        (prints extra debugging info)\n");
    }

    //-----------------------------------------------------
    //-------- Constants for common input params --------
    //-----------------------------------------------------

    /**
     * Parameter: Load properties file for options
     * <p>Will load named file as a Properties block, setting any
     * applicable options. Command line takes precedence.
     * Format: <code>-load FileName.prop</code></p>
     */
    public static final String OPT_LOAD = "load";

    /** NEEDSDOC Field load          */
    protected String load = null;

    /**
     * Parameter: Where are test input files?
     * <p>Default: .\inputs.
     * Format: <code>-inputDir path\to\dir</code></p>
     */
    public static final String OPT_INPUTDIR = "inputDir";

    /** NEEDSDOC Field inputDir          */
    protected String inputDir = "." + File.separator + "inputs";

    /**
     * Parameter: Where should we place output files (or temp files, etc.)?
     * <p>Default: .\outputs.
     * Format: <code>-outputDir path\to\dir</code></p>
     */
    public static final String OPT_OUTPUTDIR = "outputDir";

    /** NEEDSDOC Field outputDir          */
    protected String outputDir = "." + File.separator + "outputs";

    /**
     * Parameter: Where should get "gold" pre-validated XML files?
     * <p>Default: .\golds.
     * Format: <code>-goldDir path\to\dir</code></p>
     */
    public static final String OPT_GOLDDIR = "goldDir";

    /** NEEDSDOC Field goldDir          */
    protected String goldDir = "." + File.separator + "golds";

    /**
     * Parameter: if Reporters should log performance data, true/false.
     */
    protected boolean perfLogging = false;

    /**
     * Parameter: general purpose debugging flag.
     */
    protected boolean debug = false;

    //-----------------------------------------------------
    //-------- Class members and accessors --------
    //-----------------------------------------------------

    /**
     * Total Number of test case methods defined in this test.
     * <p>Tests must either set this variable or override runTestCases().</p>
     * <p>Unless you override runTestCases(), test cases must be named like so:.</p>
     * <p>Tests must either set this variable or override runTestCases().</p>
     * <p>&nbsp;&nbsp;testCase<I>N</I>, where <I>N</I> is a consecutively
     * numbered whole integer (1, 2, 3,....</p>
     * @see #runTestCases
     */
    public int numTestCases = 0;

    /**
     * Generic Properties block for storing initialization info.
     * All startup options get stored in here for later use, both by
     * the test itself and by any Reporters we use.
     */
    protected Properties testProps = new Properties();

    /**
     * Accessor method for our Properties block, for use by harnesses.
     *
     * NEEDSDOC @param p
     */
    public void setProperties(Properties p)
    {

        // Don't allow setting to null!
        if (p != null)
        {
            testProps = (Properties) p.clone();
        }
    }

    /**
     * Accessor method for our Properties block, for use by harnesses.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public Properties getProperties()
    {
        return testProps;
    }

    /**
     * Default constructor - initialize testName, Comment.
     */
    public FileBasedTest()
    {

        // Only set them if they're not set
        if (testName == null)
            testName = "FileBasedTest.defaultName";

        if (testComment == null)
            testComment = "FileBasedTest.defaultComment";
    }

    //-----------------------------------------------------
    //-------- Implement Test/TestImpl methods --------
    //-----------------------------------------------------

    /**
     * Initialize this test - called once before running testcases.
     * <p>Use the loggers field to create some loggers in a Reporter.</p>
     * @author Shane_Curcuru@lotus.com
     * @see TestImpl#testFileInit(java.util.Properties)
     *
     * NEEDSDOC @param p
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean preTestFileInit(Properties p)
    {

        // Pass our properties block directly to the reporter
        //  so it can use the same values in initialization
        // A Reporter will auto-initialize from the values
        //  in the properties block
        setReporter(new Reporter(p));
        reporter.addDefaultLogger();  // add default logger if needed
        reporter.testFileInit(testName, testComment);

        return true;
    }

    /**
     * Initialize this test - called once before running testcases.
     * <p>Subclasses <b>must</b> override this to do whatever specific
     * processing they need to initialize their product under test.</p>
     * <p>If for any reason the test should not continue, it <b>must</b>
     * return false from this method.</p>
     * @author Shane_Curcuru@lotus.com
     * @see TestImpl#testFileInit(java.util.Properties)
     *
     * NEEDSDOC @param p
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean doTestFileInit(Properties p)
    {

        // @todo implement in your subclass
        reporter.logTraceMsg(
            "FileBasedTest.doTestFileInit() default implementation - please override");

        return true;
    }

    // Use default implementation of postTestFileInit()

    /**
     * Run all of our testcases.
     * <p>use nifty FileBasedTestReporter.executeTests().  May be overridden
     * by subclasses to do their own processing.  If you do not override,
     * you must set numTestCases properly!</p>
     * @author Shane Curcuru
     *
     * NEEDSDOC @param p
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean runTestCases(Properties p)
    {

        // Properties may be currently unused
        reporter.executeTests(this, numTestCases, p);

        return true;
    }

    /**
     * Cleanup this test - called once after running testcases.
     * @author Shane Curcuru
     * <p>Tests should override if they need to do any cleanup.</p>
     *
     * NEEDSDOC @param p
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean doTestFileClose(Properties p)
    {

        reporter.logTraceMsg(
            "FileBasedTest.doTestFileClose() default implementation - please override");

        return true;
    }

    // Use default implementations of pre/postTestFileClose()
    //-----------------------------------------------------
    //-------- Initialize our common input params --------
    //-----------------------------------------------------

    /**
     * Set our instance variables from a Properties file.
     * <p>Must <b>not</b> use reporter.</p>
     * @author Shane Curcuru
     * @param Properties block to set name=value pairs from
     *
     * NEEDSDOC @param props
     * @return status - true if OK, false if error.
     * @todo improve error checking, if needed
     */
    public boolean initializeFromProperties(Properties props)
    {

        debugPrintln("FileBasedTest.initializeFromProperties(" + props + ")");

        // Parse out any values that match our internal convenience variables
        // default all values to our current values
        // String values are simply getProperty()'d
        inputDir = props.getProperty(OPT_INPUTDIR, inputDir);

        if (inputDir != null)
            testProps.put(OPT_INPUTDIR, inputDir);

        outputDir = props.getProperty(OPT_OUTPUTDIR, outputDir);

        if (outputDir != null)
            testProps.put(OPT_OUTPUTDIR, outputDir);

        goldDir = props.getProperty(OPT_GOLDDIR, goldDir);

        if (goldDir != null)
            testProps.put(OPT_GOLDDIR, goldDir);

        // Use a temp string for those properties we only set 
        //  in our testProps, but don't bother to save ourselves
        String temp = null;

        temp = props.getProperty(Reporter.OPT_LOGGERS);

        if (temp != null)
            testProps.put(Reporter.OPT_LOGGERS, temp);

        temp = props.getProperty(Logger.OPT_LOGFILE);

        if (temp != null)
            testProps.put(Logger.OPT_LOGFILE, temp);

        // boolean values just check for the non-default value
        String dbg = props.getProperty(Reporter.OPT_DEBUG);

        if ((dbg != null) && dbg.equalsIgnoreCase("true"))
        {
            debug = true;

            testProps.put(Reporter.OPT_DEBUG, "true");
        }

        String pLog = props.getProperty(Reporter.OPT_PERFLOGGING);

        if ((pLog != null) && pLog.equalsIgnoreCase("true"))
        {
            perfLogging = true;

            testProps.put(Reporter.OPT_PERFLOGGING, "true");
        }

        temp = props.getProperty(Reporter.OPT_LOGGINGLEVEL);

        if (temp != null)
            testProps.put(Reporter.OPT_LOGGINGLEVEL, temp);

        return true;
    }

    /**
     * Sets the provided fields with data from an array, presumably
     * from the command line.
     * <p>May be overridden by subclasses, although you should probably
     * read the code to see what default options this handles. Must
     * not use reporter. Calls initializeFromProperties(). After that,
     * sets any internal variables that match items in the array like:
     * <code> -param1 value1 -paramNoValue -param2 value2 </code>
     * Any params that do not match internal variables are simply set
     * into our properties block for later use.  This allows subclasses
     * to simply get their initialization data from the testProps
     * without having to make code changes here.</p>
     * <p>Assumes all params begin with "-" dash, and that all values
     * do <b>not</b> start with a dash.</p>
     * @author Shane Curcuru
     * @param String[] array of arguments
     *
     * NEEDSDOC @param args
     * @param flag: are we being called from a subclass?
     * @return status - true if OK, false if error.
     */
    public boolean initializeFromArray(String[] args, boolean flag)
    {

        debugPrintln("FileBasedTest.initializeFromArray(" + args + ")");

        // Read in command line args and setup internal variables
        String optPrefix = "-";
        int nArgs = args.length;

        // We don't require any arguments: but subclasses might 
        //  want to require certain ones
        // Must read in properties file first, so cmdline can 
        //  override values from properties file
        boolean propsOK = true;

        // IF we are being called the first time on this 
        //  array of arguments, go ahead and process unknown ones
        //  otherwise, don't bother
        if (flag)
        {
            for (int k = 0; k < nArgs; k++)
            {
                if (args[k].equalsIgnoreCase(optPrefix + OPT_LOAD))
                {
                    if (++k >= nArgs)
                    {
                        System.err.println(
                            "ERROR: must supply properties filename for: "
                            + optPrefix + OPT_LOAD);

                        return false;
                    }

                    load = args[k];

                    try
                    {

                        // Load named file into our properties block
                        FileInputStream fIS = new FileInputStream(load);
                        Properties p = new Properties();

                        p.load(fIS);

                        propsOK &= initializeFromProperties(p);
                    }
                    catch (Exception e)
                    {
                        System.err.println(
                            "ERROR: loading properties file failed: " + load);
                        e.printStackTrace();

                        return false;
                    }

                    break;
                }
            }  // end of for(...)
        }  // end of if ((flag))

        // Now read in the rest of the command line
        // @todo cleanup loop to be more table-driven
        for (int i = 0; i < nArgs; i++)
        {

            // Set any String args and place them in testProps
            if (args[i].equalsIgnoreCase(optPrefix + OPT_INPUTDIR))
            {
                if (++i >= nArgs)
                {
                    System.err.println("ERROR: must supply arg for: "
                                       + optPrefix + OPT_INPUTDIR);

                    return false;
                }

                inputDir = args[i];

                testProps.put(OPT_INPUTDIR, inputDir);

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_OUTPUTDIR))
            {
                if (++i >= nArgs)
                {
                    System.err.println("ERROR: must supply arg for: "
                                       + optPrefix + OPT_OUTPUTDIR);

                    return false;
                }

                outputDir = args[i];

                testProps.put(OPT_OUTPUTDIR, outputDir);

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_GOLDDIR))
            {
                if (++i >= nArgs)
                {
                    System.err.println("ERROR: must supply arg for: "
                                       + optPrefix + OPT_GOLDDIR);

                    return false;
                }

                goldDir = args[i];

                testProps.put(OPT_GOLDDIR, goldDir);

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + Reporter.OPT_LOGGERS))
            {
                if (++i >= nArgs)
                {
                    System.err.println("ERROR: must supply arg for: "
                                       + optPrefix + Reporter.OPT_LOGGERS);

                    return false;
                }

                testProps.put(Reporter.OPT_LOGGERS, args[i]);

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + Logger.OPT_LOGFILE))
            {
                if (++i >= nArgs)
                {
                    System.err.println("ERROR: must supply arg for: "
                                       + optPrefix + Logger.OPT_LOGFILE);

                    return false;
                }

                testProps.put(Logger.OPT_LOGFILE, args[i]);

                continue;
            }

            // Boolean values are simple flags to switch from defaults only
            if (args[i].equalsIgnoreCase(optPrefix + Reporter.OPT_DEBUG))
            {
                debug = true;

                testProps.put(Reporter.OPT_DEBUG, "true");

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix
                                         + Reporter.OPT_PERFLOGGING))
            {
                testProps.put(Reporter.OPT_PERFLOGGING, "true");

                continue;
            }

            // Parse out the integer value
            //  This isn't strictly necessary since the catch-all 
            //  below should take care of it, but better safe than sorry
            if (args[i].equalsIgnoreCase(optPrefix
                                         + Reporter.OPT_LOGGINGLEVEL))
            {
                if (++i >= nArgs)
                {
                    System.err.println("ERROR: must supply arg for: "
                                       + optPrefix
                                       + Reporter.OPT_LOGGINGLEVEL);

                    return false;
                }

                try
                {
                    testProps.put(Reporter.OPT_LOGGINGLEVEL, args[i]);
                }
                catch (NumberFormatException numEx)
                { /* no-op */
                }

                continue;
            }

            // IF we are being called the first time on this 
            //  array of arguments, go ahead and process unknown ones
            //  otherwise, don't bother
            if (flag)
            {

                // Found an arg that we don't know how to process,
                //  so store it for any subclass' use as a catch-all
                // If it starts with - dash, and another non-dash arg follows,
                //  set as a name=value pair in the property block
                if ((args[i].startsWith(optPrefix)) && (i + 1 < nArgs)
                        && (!args[i + 1].startsWith(optPrefix)))
                {

                    // Scrub off the "-" prefix before setting the name
                    testProps.put(args[i].substring(1), args[i + 1]);

                    i++;  // Increment counter to skip next arg
                }

                // Otherwise, just set as name="" in the property block
                else
                {

                    // Scrub off the "-" prefix before setting the name
                    testProps.put(args[i].substring(1), "");
                }
            }
        }  // end of for() loop

        debugPrintln(
            "FileBasedTest.initializeFromArray(): testProps are now:");

        if (debug)
            testProps.list(System.err);

        // If we got here, we set the array params OK, so simply return 
        //  the value the initializeFromProperties method returned
        return propsOK;
    }

    //-----------------------------------------------------
    //-------- Other useful and utility methods --------
    //-----------------------------------------------------

    /**
     * Create a TestfileInfo object from our paths.
     * From a single base filename, fill in fully qualified paths for
     * the inputName, outputName, goldName from our inputDir,
     * outputDir, goldDir.
     * Note: uses semicolon ':' as separator!
     * @author Shane Curcuru
     * @param basename of file
     * @return TestfileInfo with *Name fields set
     */
    public TestfileInfo createTestfileInfo(String basename)
    {

        TestfileInfo t = new TestfileInfo();

        try
        {
            t.inputName = (new File(inputDir)).getCanonicalPath()
                          + File.separatorChar + basename;
        }
        catch (IOException ioe)
        {
            t.inputName = (new File(inputDir)).getAbsolutePath()
                          + File.separatorChar + basename;
        }

        try
        {
            t.outputName = (new File(outputDir)).getCanonicalPath()
                           + File.separatorChar + basename;
        }
        catch (IOException ioe)
        {
            t.outputName = (new File(outputDir)).getAbsolutePath()
                           + File.separatorChar + basename;
        }

        try
        {
            t.goldName = (new File(goldDir)).getCanonicalPath()
                         + File.separatorChar + basename;
        }
        catch (IOException ioe)
        {
            t.goldName = (new File(goldDir)).getAbsolutePath()
                         + File.separatorChar + basename;
        }

        return t;
    }

    /**
     * Debugging the Test infrastructure - dumps to System.err.  
     *
     * NEEDSDOC @param s
     */
    protected void debugPrintln(String s)
    {

        if (!debug)
            return;

        System.err.println(s);
    }

    /**
     * Main method to run test from the command line.
     * <p>Test subclasses <b>must</b> override, obviously.
     * Only provided here for debugging.</p>
     * @author Shane Curcuru
     *
     * NEEDSDOC @param args
     */
    public static void main(String[] args)
    {

        FileBasedTest app = new FileBasedTest();

        // Initialize any instance variables from the command line 
        //  OR specified properties block
        if (!app.initializeFromArray(args, true))
        {
            System.err.println("ERROR in usage:");
            System.err.println(app.usage());

            // Don't use System.exit, since that will blow away any containing harnesses
            return;
        }

        // Also pass along the command line, in case someone has 
        //  specific code that's counting on this
        app.testProps.put(MAIN_CMDLINE, args);
        app.runTest(app.testProps);
    }
}  // end of class FileBasedTest

