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
 * XSLProcessorTestBase.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

//------------------------------------------------------------------------

/**
 * Base class for all Xalan tests.
 * <p>XSLProcessorTestBase defines a number of common fields
 * that most tests will use in addition to FileBasedTest.  It
 * also defaults to using an XMLFileLogger as well as a
 * ConsoleLogger, if none have been specified.</p>
 * <ul>initializes all members from FileBasedTest, plus
 * <li>category</li>
 * <li>excludes</li>
 * <li>embedded - special case: tests with no .xsl file (embedded stylesheets)</li>
 * <li>liaison</li>
 * <li>flavor</li>
 * <li>diagnostics</li>
 * <li>noReuse</li>
 * <li>precompile</li>
 * <li>runErr</li>
 * </ul>
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class XSLProcessorTestBase extends FileBasedTest
{

    /**
     * Convenience method to print out usage information.
     * @author Shane Curcuru
     *
     * @return String describing our usage; includes most of the 
     * flags we support and our parent class' usage
     */
    public String usage()
    {

        return ("Common options supported by XSLProcessorTestBase:\n"
                + "    -" + OPT_CATEGORY
                + "  <name of single subdir within testDir to run>\n"
                + "    -" + OPT_EXCLUDES
                + "  <list;of;specific file.xsl tests to skip>\n" 
                + "    -" + OPT_EMBEDDED
                + "  <list;of;specific file.xml embedded tests to run>\n" 
                + "    -" + OPT_LIAISON 
                + "   <liaisonClassName>\n" 
                + "    -" + OPT_FLAVOR
                + "    <xalan|lotusxsl|xt|etc... - which kind of Processor to test>\n"
                + "    -" + OPT_DIAGNOSTICS
                + "  <root filename for diagnostics output>\n" 
                + "    -" + OPT_NOREUSE
                + "   (will force recreate processor each time)\n" 
                + "    -" + OPT_PRECOMPILE
                + " (will use precompiled stylesheet, if applicable)\n"
                + "    -" + OPT_NOERRTEST
                + "  (will skip running 'err' tests, if applicable)\n"
                + super.usage());
    }

    //-----------------------------------------------------
    //-------- Constants for common input params --------
    //-----------------------------------------------------

    /**
     * Parameter: Only run a single subcategory of the tests.
     * <p>Default: blank, runs all tests - supply the directory name
     * of a subcategory to run just that set.</p>
     */
    public static final String OPT_CATEGORY = "category";

    /** Parameter: Only run a single subcategory of the tests.  */
    protected String category = null;

    /**
     * Parameter: Reuse/reset processor between cases or create a new one each time?
     * <p>Default: false - create one processor and call reset between tests.</p>
     * <p>Should be deprecated, I think. -sc 21-Mar-01</p>
     */
    // TODO: Move to directoryiterator
    public static final String OPT_NOREUSE = "noReuse";

    /** Parameter: Reuse/reset processor between cases or create a new one each time?  */
    protected boolean noReuse = true;

    /**
     * Parameter: What parser liaison or option to use?
     * <p>Default: null, Whichever default your processor uses.</p>
     */

    // TODO: Move to directoryiterator
    public static final String OPT_LIAISON = "liaison";

    /** Parameter: What parser liaison or option to use?.   */
    protected String liaison = null;

    /**
     * Parameter: What flavor of ProcessorWrapper to use: trax|xalan1|other?
     * <p>Default: trax.</p>
     */
    public static final String OPT_FLAVOR = "flavor";

    /** Parameter: What flavor of ProcessorWrapper to use: trax|xalan1|other?  */
    protected String flavor = "trax";

    /**
     * Parameter: Name of output file for diagnostics/error logs?
     * <p>Default: null, do not use one</p>
     * <p>Should be deprecated, I think. -sc 21-Mar-01</p>
     */

    // TODO: Move to directoryiterator
    public static final String OPT_DIAGNOSTICS = "diagnostics";

    /** Parameter: Name of output file for diagnostics/error logs?  */
    protected String diagnostics = null;

    /**
     * Parameter: Should we try to use a precompiled stylesheet?
     * <p>Default: false, no (not applicable in all cases).</p>
     */
    public static final String OPT_PRECOMPILE = "precompile";

    /** Parameter: Should we try to use a precompiled stylesheet?  */
    protected boolean precompile = false;

    /**
     * Parameter: Should we run any 'err' subdir tests or not?
     * <p>Default: false (i.e. <b>do</B> run error tests by default).</p>
     * <p>Should be deprecated, I think: don't try to run both 
     * positive and negative tests together, do them separately.
     * -sc 21-Mar-01</p>
     */
    public static final String OPT_NOERRTEST = "noErrTest";

    /** Parameter: Should we run any 'err' subdir tests or not?  */
    protected boolean noErrTest = false;

    /**
     * Parameter: force use of URI's or leave filenames alone?
     * <p>Default: true, use URI's</p>
     * @todo update, this is clumsy
     */
    public static final String OPT_USEURI = "useURI";

    /** Parameter: force use of URI's or leave filenames alone?  */
    protected boolean useURI = true;

    /**
     * Parameter: Which CheckService should we use for XML output Files?
     * <p>Default: org.apache.qetest.XHTFileCheckService.</p>
     */
    public static final String OPT_FILECHECKER = "fileChecker";

    /** Parameter: Which CheckService should we use for XML output Files?  */
    protected String fileCheckerName =
        "org.apache.qetest.xsl.XHTFileCheckService";

    /** FileChecker instance for use by subclasses; created in preTestFileInit()  */
    protected CheckService fileChecker = null;

    /**
     * Parameter: Should we exclude any specific test files?
     * <p>Default: null (no excludes; otherwise specify 
     * semicolon delimited list of bare filenames something like 
     * 'axes01.xsl;bool99.xsl').</p>
     */
    public static final String OPT_EXCLUDES = "excludes";

    /** Parameter: Should we exclude any specific test files?  */
    protected String excludes = null;

    /**
     * Parameter: Are there any embedded stylesheets in XML files?
     * <p>Default: null (no embedded tests; otherwise specify 
     * semicolon delimited list of bare filenames something like 
     * 'axes02.xml;bool98.xml').</p>
     */
    public static final String OPT_EMBEDDED = "embedded";

    /** Parameter: Are there any embedded stylesheets in XML files?  */
    protected String embedded = null;

    /**
     * Default constructor - initialize testName, Comment.
     */
    public XSLProcessorTestBase()
    {

        // Only set them if they're not set
        if (testName == null)
            testName = "XSLProcessorTestBase.defaultName";

        if (testComment == null)
            testComment = "XSLProcessorTestBase.defaultComment";
    }

    //-----------------------------------------------------
    //-------- Implement Test/TestImpl methods --------
    //----------------------------------------------------

    /**
     * Initialize this test - called once before running testcases.
     * <p>We auto-create a reporter and some loggers: if logFile is 
     * set, we add an XMLFileLogger, and we usually add a 
     * ConsoleLogger.  We also create a fileChecker for later use.</p>
     * @author Shane_Curcuru@lotus.com
     *
     * @param p unused
     *
     * @return true always
     */
    public boolean preTestFileInit(Properties p)
    {

        // Ensure we have an XMLFileLogger if we have a logName
        String logF = testProps.getProperty(Logger.OPT_LOGFILE);

        if ((logF != null) && (!logF.equals("")))
        {

            // We should ensure there's an XMLFileReporter
            String r = testProps.getProperty(Reporter.OPT_LOGGERS);

            if (r == null)
            {
                testProps.put(Reporter.OPT_LOGGERS,
                              "org.apache.qetest.XMLFileLogger");
            }
            else if (r.indexOf("XMLFileLogger") <= 0)
            {
                testProps.put(Reporter.OPT_LOGGERS,
                              r + Reporter.LOGGER_SEPARATOR
                              + "org.apache.qetest.XMLFileLogger");
            }
        }

        // Ensure we have a ConsoleLogger unless asked not to
        // @todo improve and document this feature
        String noDefault = testProps.getProperty("noDefaultReporter");

        if (noDefault == null)
        {

            // We should ensure there's an XMLFileReporter
            String r = testProps.getProperty(Reporter.OPT_LOGGERS);

            if (r == null)
            {
                testProps.put(Reporter.OPT_LOGGERS,
                              "org.apache.qetest.ConsoleLogger");
            }
            else if (r.indexOf("ConsoleLogger") <= 0)
            {
                testProps.put(Reporter.OPT_LOGGERS,
                              r + Reporter.LOGGER_SEPARATOR
                              + "org.apache.qetest.ConsoleLogger");
            }
        }

        // Pass our properties block directly to the reporter
        //  so it can use the same values in initialization
        // A Reporter will auto-initialize from the values
        //  in the properties block
        setReporter(new Reporter(testProps));
        reporter.addDefaultLogger();  // add default logger if needed
        reporter.testFileInit(testName, testComment);

        // Create a file-based CheckService for later use
        if (fileChecker == null)
        {
            try
            {
                String[] testPackages = 
                {
                    "org.apache.qetest.xsl",
                    "org.apache.qetest.trax",
                    "org.apache.qetest.xalanj2",
                    "org.apache.qetest.xalanj1",
                    "org.apache.qetest"
                };
                // Use utility method to attempt to guess classnames 
                //  that are not fully specified, if needed
                Class fClass = QetestUtils.testClassForName(fileCheckerName, 
                                                            testPackages, 
                                                            fileCheckerName);

                fileChecker = (CheckService) fClass.newInstance();

                reporter.logTraceMsg("Using file-based CheckService: "
                                     + fileChecker);
            }
            catch (Exception e)
            {
                reporter.logErrorMsg("Failed to create an instance of "
                                       + fileCheckerName + ": "
                                       + e.toString());

                // This could cause NullPointerExceptions for those 
                //  tests that rely on our fileChecker...
            }
        }

        return true;
    }

    /**
     * Override mostly blank routine to dump environment info.
     * <p>Log out information about our environment in a structured 
     * way: mainly by calling logTestProps() here.</p>
     * 
     * @param p unused
     *
     * @return true always
     */
    public boolean postTestFileInit(Properties p)
    {
        logTestProps();
        return true;
    }

    /**
     * Log out any version or system info.
     * <p>Logs System.getProperties(), and our the testProps block.</p>
     */
    public void logTestProps()
    {
        reporter.logHashtable(reporter.CRITICALMSG, System.getProperties(),
                              "System.getProperties");
        reporter.logHashtable(reporter.CRITICALMSG, testProps, "testProps");
    }

    /**
     * Mark the test complete - called once after running testcases.
     * <p>Currently logs a summary of our test status and then tells 
     * our reporter to log the testFileClose. This will calculate 
     * final results, and complete logging for any structured 
     * output logs (like XML files).</p>
     *
     * @param p Unused; passed through to super
     *
     * @return true if OK, false otherwise
     */
    protected boolean postTestFileClose(Properties p)
    {
        // Log out a special summary status, with marker file
        reporter.writeResultsStatus(true);

        // Ask our superclass to handle this as well
        return super.postTestFileClose(p);
    }

    // use other implementations from FileBasedTest
    //-----------------------------------------------------
    //-------- Initialize our common input params --------
    //-----------------------------------------------------

    /**
     * Set our instance variables from a Properties file.
     * Simply clones the Properties block into our testProps;
     * and then explicitly sets known instance variables.
     *
     * //@todo improve error checking, if needed
     * @param props Properties block to initialize our internal 
     * state from: mainly our common parameters/options 
     * @return status - true if OK, false if error.
     */
    public boolean initializeFromProperties(Properties props)
    {
        // Get any superclass' default processing
        boolean b = super.initializeFromProperties(props);

        // Copy over all properties into our local block
        //  this is a little unusual, but it does allow users 
        //  to set any new sort of properties via the properties 
        //  file, and we'll pick it up - that way this class doesn't
        //  have to get updated when we have new properties
        for (Enumeration enum = props.propertyNames();
                enum.hasMoreElements(); /* no increment portion */ )
        {
            Object key = enum.nextElement();

            testProps.put(key, props.get(key));
        }

        // Now set any convenience instance variables based on 
        //  values set from the properties block
        category = props.getProperty(OPT_CATEGORY, category);
        liaison = props.getProperty(OPT_LIAISON, liaison);
        flavor = props.getProperty(OPT_FLAVOR, flavor);
        fileCheckerName = props.getProperty(OPT_FILECHECKER, fileCheckerName);
        excludes = props.getProperty(OPT_EXCLUDES, excludes);
        embedded = props.getProperty(OPT_EMBEDDED, embedded);
        diagnostics = props.getProperty(OPT_DIAGNOSTICS, diagnostics);

        String prec = props.getProperty(OPT_PRECOMPILE);
        if ((prec != null) && prec.equalsIgnoreCase("true"))
        {
            precompile = true;
            // Default the value in properties block too
            testProps.put(OPT_PRECOMPILE, "true");
        }

        String noet = props.getProperty(OPT_NOERRTEST);

        if ((noet != null) && noet.equalsIgnoreCase("true"))
        {
            noErrTest = true;
            // Default the value in properties block too
            testProps.put(OPT_NOERRTEST, "true");
        }

        String uURI = props.getProperty(OPT_USEURI);

        if ((uURI != null) && uURI.equalsIgnoreCase("false"))
        {
            useURI = false;
            // Default the value in properties block too
            testProps.put(OPT_USEURI, "false");
        }

        return b;
    }

    /**
     * Sets the provided fields with data from an array, presumably
     * from the command line.
     * <p>Overridden from FileBasedTest, but calls super.() to
     * get default properties.</p>
     * @author Shane Curcuru
     *
     * NEEDSDOC @param args
     * @return status - true if OK, false if error.
     */
    public boolean initializeFromArray(String[] args)
    {
        // Read in command line args and setup internal variables
        String optPrefix = "-";
        int nArgs = args.length;

        if (nArgs == 0)
        {
            System.out.println("ERROR: you must supply required arguments!");

            return false;
        }

        // Our parent class already read in the properties file
        //  override values from properties file
        for (int k = 0; k < nArgs; k++)
        {
            if (args[k].equalsIgnoreCase(optPrefix + OPT_LOAD))
            {
                if (++k >= nArgs)
                {
                    System.out.println(
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
                    initializeFromProperties(p);
                }
                catch (Exception e)
                {
                    System.out.println(
                        "ERROR: loading properties file failed: " + load);
                    e.printStackTrace();

                    return false;
                }

                break;
            }
        }

        // Now read in the rest of the command line
        // @todo cleanup loop to be more table-driven
        for (int i = 0; i < nArgs; i++)
        {
            if (args[i].equalsIgnoreCase(optPrefix + OPT_CATEGORY))
            {
                if (++i >= nArgs)
                {
                    System.out.println("ERROR: must supply arg for: "
                                       + optPrefix + OPT_CATEGORY);

                    return false;
                }

                category = args[i];

                testProps.put(OPT_CATEGORY, category);

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_LIAISON))
            {
                if (++i >= nArgs)
                {
                    System.out.println("ERROR: must supply arg for: "
                                       + optPrefix + OPT_LIAISON);

                    return false;
                }

                liaison = args[i];

                testProps.put(OPT_LIAISON, liaison);

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_FLAVOR))
            {
                if (++i >= nArgs)
                {
                    System.out.println("ERROR: must supply arg for: "
                                       + optPrefix + OPT_FLAVOR);

                    return false;
                }

                flavor = args[i];

                testProps.put(OPT_FLAVOR, flavor);

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_DIAGNOSTICS))
            {
                if (++i >= nArgs)
                {
                    System.out.println("ERROR: must supply arg for: "
                                       + optPrefix + OPT_DIAGNOSTICS);

                    return false;
                }

                diagnostics = args[i];

                testProps.put(OPT_DIAGNOSTICS, diagnostics);

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_FILECHECKER))
            {
                if (++i >= nArgs)
                {
                    System.out.println("ERROR: must supply arg for: "
                                       + optPrefix + OPT_FILECHECKER);

                    return false;
                }

                fileCheckerName = args[i];

                testProps.put(OPT_FILECHECKER, fileCheckerName);

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_EXCLUDES))
            {
                if (++i >= nArgs)
                {
                    System.out.println("ERROR: must supply arg for: "
                                       + optPrefix + OPT_EXCLUDES);

                    return false;
                }

                excludes = args[i];

                testProps.put(OPT_EXCLUDES, excludes);

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_EMBEDDED))
            {
                if (++i >= nArgs)
                {
                    System.out.println("ERROR: must supply arg for: "
                                       + optPrefix + OPT_EMBEDDED);

                    return false;
                }

                embedded = args[i];

                testProps.put(OPT_EMBEDDED, embedded);

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_NOREUSE))
            {
                noReuse = false;

                testProps.put(OPT_NOREUSE, "false");

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_PRECOMPILE))
            {
                precompile = true;

                testProps.put(OPT_PRECOMPILE, "true");

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_NOERRTEST))
            {
                noErrTest = true;

                testProps.put(OPT_NOERRTEST, "true");

                continue;
            }

            if (args[i].equalsIgnoreCase(optPrefix + OPT_USEURI))
            {
                useURI = false;  // Toggle from default of true; ugly but I'm in a hurry

                testProps.put(OPT_USEURI, "false");

                continue;
            }
        }

        // Be sure to ask our superclass to read it's options as well!
        return super.initializeFromArray(args, true);
    }

    //-----------------------------------------------------
    //-------- Other useful and utility methods --------
    //-----------------------------------------------------
    /**
     * Main worker method to run test from the command line.
     * Test subclasses generally need not override.
     * <p>This is primarily provided to make subclasses implementations
     * of the main method as simple as possible: in general, they
     * should simply do:
     * <code>
     *   public static void main (String[] args)
     *   {
     *       TestSubClass app = new TestSubClass();
     *       app.doMain(args);
     *   }
     * </code>
     * @author Shane Curcuru
     *
     * NEEDSDOC @param args
     */
    public void doMain(String[] args)
    {
        // Initialize any instance variables from the command line 
        //  OR specified properties block
        if (!initializeFromArray(args))
        {
            System.err.println("ERROR in usage:");
            System.err.println(usage());

            // Don't use System.exit, since that will blow away any containing harnesses
            return;
        }

        // Also pass along the command line, in case someone has 
        //  specific code that's counting on this
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < args.length; i++)
        {
            buf.append(args[i]);
            buf.append(' ');
        }
        testProps.put(MAIN_CMDLINE, buf.toString());

        // Actually go and execute the test
        runTest(testProps);
    }

    /**
     * Main method to run test from the command line.
     * @author Shane Curcuru
     * <p>Test subclasses <b>must</b> override, obviously.
     * Only provided here for debugging.</p>
     *
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        XSLProcessorTestBase app = new XSLProcessorTestBase();
        app.doMain(args);
    }
}  // end of class XSLProcessorTestBase

