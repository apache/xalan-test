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
 * BugzillaTestletDriver.java
 *
 */
package org.apache.qetest.xsl;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xslwrapper.TransformWrapper;
import org.apache.qetest.xslwrapper.TransformWrapperFactory;

// java classes
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

//-------------------------------------------------------------------------

/**
 * Test driver for Bugzilla tests with .java/.xsl files..
 * 
 * This driver does not iterate over a directory tree; only 
 * over a single directory.  It supports either 'classic' tests
 * with matching .xsl/.xml/.out files like the conformance test, 
 * or tests that also include a .java file that is the specific 
 * testlet to execute for that test.
 *
 *
 *
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class BugzillaTestletDriver extends StylesheetTestletDriver
{

    /** Convenience constant: .java extension for Java Testlet source.  */
    public static final String JAVA_EXTENSION = ".java";

    /** Convenience constant: Property key for java filenames.  */
    public static final String JAVA_SOURCE_NAME = "java.source.name";

    /** Convenience constant: Default .xml file to use.  */
    public static final String DEFAULT_XML_FILE = "identity.xml";

    /** 
     * Default FilenameFilter FQCN for files - overridden.  
     * By default, use a custom FilenameFilter that picks up 
     * both .java and .xsl files, with slightly different 
     * naming conventions than normal.
     */
    protected String defaultFileFilter = "org.apache.qetest.xsl.BugzillaFileRules";


    /** Just initialize test name, comment; numTestCases is not used. */
    public BugzillaTestletDriver()
    {
        testName = "BugzillaTestletDriver";
        testComment = "Test driver for Bugzilla tests with .java/.xsl files.";
    }


    /**
     * Special: test all Bugzilla* files in just the bugzilla directory.
     * This does not iterate down directories.
     * This is a specific test driver for testlets that may have 
     * matching foo*.java and foo*.xml/xsl/out
     * Parameters: none, uses our internal members inputDir, 
     * outputDir, testlet, etc.
     */
    public void processInputDir()
    {
        // Ensure the inputDir is there - we must have a valid location for input files
        File testDirectory = new File(inputDir);

        if (!testDirectory.exists())
        {
            // Try a default inputDir
            String oldInputDir = inputDir; // cache for potential error message
            testDirectory = new File((inputDir = getDefaultInputDir()));
            if (!testDirectory.exists())
            {
                // No inputDir, can't do any tests!
                // @todo check if this is the best way to express this
                reporter.checkErr("inputDir(" + oldInputDir
                                  + ", or " + inputDir + ") does not exist, aborting!");
                return;
            }
        }

        // Validate that each of the specified dirs exists
        // Returns directory references like so:
        //  testDirectory = 0, outDirectory = 1, goldDirectory = 2
        File[] dirs = validateDirs(new File[] { testDirectory }, 
                                   new File[] { new File(outputDir), new File(goldDir) });

        if (null == dirs)  // this should never happen...
        {
            // No inputDir, can't do any tests!
            // @todo check if this is the best way to express this
            reporter.checkErr("inputDir(" + dirs[0] + ") does not exist, aborting!");
            return;
        }

        // Call worker method to process the individual directory
        //  and get a list of .java or .xsl files to test
        Vector files = getFilesFromDir(dirs[0], getFileFilter(), embedded);

        // 'Transform' the list of individual test files into a 
        //  list of Datalets with all fields filled in
        //@todo should getFilesFromDir and buildDatalets be combined?
        Vector datalets = buildDatalets(files, dirs[0], dirs[1], dirs[2]);

        if ((null == datalets) || (0 == datalets.size()))
        {
            // No tests, log error and return
            //  other directories to test
            reporter.checkErr("inputDir(" + dirs[0] + ") did not contain any tests, aborting!");
            return;
        }

        // Now process the list of files found in this dir
        processFileList(datalets, "Bugzilla tests of: " + dirs[0]);
    }


    /**
     * Run a list of bugzilla-specific tests.
     * Bugzilla tests may either be encoded as a .java file that 
     * defines a Testlet, or as a normal .xsl/.xml file pair that 
     * should simply be transformed simply, by a StylesheetTestlet.
     *
     * @param vector of Datalet objects to pass in
     * @param desc String to use as testCase description
     */
    public void processFileList(Vector datalets, String desc)
    {
        // Validate arguments
        if ((null == datalets) || (0 == datalets.size()))
        {
            // Bad arguments, report it as an error
            // Note: normally, this should never happen, since 
            //  this class normally validates these arguments 
            //  before calling us
            reporter.checkErr("Testlet or datalets are null/blank, nothing to test!");
            return;
        }

        // Now just go through the list and process each set
        int numDatalets = datalets.size();
        reporter.logInfoMsg("processFileList() with " + numDatalets
                            + " potential Bugzillas");
        // Iterate over every datalet and test it
        for (int ctr = 0; ctr < numDatalets; ctr++)
        {
            try
            {
                // Depending on the Datalet class, run a different algorithim
                Datalet d = (Datalet)datalets.elementAt(ctr);
                if (d instanceof TraxDatalet)
                {
                    // Assume we the datalet holds the name of a 
                    //  .java file that's a testlet, and just 
                    //  execute that itself
                    // Note: Since they're packageless and have 
                    //  hardcoded paths to the current dir, must 
                    //  change user.dir each time in worker method
                    Testlet t = getTestlet((TraxDatalet)d);
                    // Each Bugzilla is it's own testcase
                    reporter.testCaseInit(t.getDescription());
                    executeTestletInDir(t, d, inputDir);
                }
                else if (d instanceof StylesheetDatalet)
                {
                    // Create plain Testlet to execute a test with this 
                    //  next datalet - the Testlet will log all info 
                    //  about the test, including calling check*()
                    // Each Bugzilla is it's own testcase
                    reporter.testCaseInit(d.getDescription());
                    getTestlet().execute(d);
                }
                else
                {
                    reporter.checkErr("Unknown Datalet type: " + d);                
                }
            } 
            catch (Throwable t)
            {
                // Log any exceptions as fails and keep going
                //@todo improve the below to output more useful info
                reporter.checkFail("Datalet num " + ctr + " threw: " + t.toString());
                reporter.logThrowable(Logger.ERRORMSG, t, "Datalet threw");
            }
            reporter.testCaseClose();
        }  // of while...
    }
    
    
    /**
     * Transform a vector of individual test names into a Vector 
     * of filled-in datalets to be tested - Bugzilla-specific.  
     *
     * This does special processing since we may either have .java 
     * files that should be compiled, or we may have plain .xsl/.xml 
     * file pairs that we should simpy execute through a default 
     * StylesheetTestlet as-is.
     * This basically just calculates local path\filenames across 
     * the three presumably-parallel directory trees of testLocation 
     * (inputDir), outLocation (outputDir) and goldLocation 
     * (forced to be same as inputDir).  It then stuffs each of 
     * these values plus some generic info like our testProps 
     * into each datalet it creates.
     * 
     * @param files Vector of local path\filenames to be tested
     * @param testLocation File denoting directory where all 
     * .xml/.xsl tests are found
     * @param outLocation File denoting directory where all 
     * output files should be put
     * @param goldLocation File denoting directory where all 
     * gold files are found - IGNORED; forces testLocation instead
     * @return Vector of StylesheetDatalets that are fully filled in,
     * i.e. outputName, goldName, etc are filled in respectively 
     * to inputName
     */
    public Vector buildDatalets(Vector files, File testLocation, 
                                File outLocation, File goldLocation)
    {
        // Validate arguments
        if ((null == files) || (files.size() < 1))
        {
            // Bad arguments, report it as an error
            // Note: normally, this should never happen, since 
            //  this class normally validates these arguments 
            //  before calling us
            reporter.logWarningMsg("buildDatalets null or empty file vector");
            return null;
        }
        Vector v = new Vector(files.size());
        int xslCtr = 0;
        int javaCtr = 0;

        // For every file in the vector, construct the matching 
        //  out, gold, and xml/xsl files; plus see if we have 
        //  a .java file as well
        for (Enumeration enum = files.elements();
                enum.hasMoreElements(); /* no increment portion */ )
        {
            String file = null;
            try
            {
                file = (String)enum.nextElement();
            }
            catch (ClassCastException cce)
            {
                // Just skip this entry
                reporter.logWarningMsg("Bad file element found, skipping: " + cce.toString());
                continue;
            }

            Datalet d = null;
            // If it's a .java file: just set java.source.name/java.class.name
            if (file.endsWith(JAVA_EXTENSION))
            {
                // Use TraxDatalets if we have .java
                d = new TraxDatalet();
                ((TraxDatalet)d).options = new Properties(testProps);
                ((TraxDatalet)d).options.put("java.source.dir", testLocation);
                ((TraxDatalet)d).options.put(JAVA_SOURCE_NAME, file);
                ((TraxDatalet)d).options.put("fileCheckerImpl", fileChecker);
                // That's it - when we execute tests later on, if 
                //  there's a JAVA_SOURCE_NAME we simply use that to 
                //  find the testlet to execute
                javaCtr++;
            }
            // If it's a .xsl file, just set the filenames as usual
            else if (file.endsWith(XSL_EXTENSION))
            {
                // Use plain StylesheetDatalets if we just have .xsl
                d = new StylesheetDatalet();
                ((StylesheetDatalet)d).inputName = testLocation.getPath() + File.separator + file;

                String fileNameRoot = file.substring(0, file.indexOf(XSL_EXTENSION));
                // Check for existence of xml - if not there, then set to some default
                //@todo this would be a perfect use of TraxDatalet.setXMLString()
                String xmlFileName = testLocation.getPath() + File.separator + fileNameRoot + XML_EXTENSION;
                if ((new File(xmlFileName)).exists())
                {
                    ((StylesheetDatalet)d).xmlName = xmlFileName;
                }
                else
                {
                    ((StylesheetDatalet)d).xmlName = testLocation.getPath() + File.separator + DEFAULT_XML_FILE;
                }
                ((StylesheetDatalet)d).outputName = outLocation.getPath() + File.separator + fileNameRoot + OUT_EXTENSION;
                ((StylesheetDatalet)d).goldName = testLocation.getPath() + File.separator + fileNameRoot + OUT_EXTENSION;
                ((StylesheetDatalet)d).flavor = flavor;
                ((StylesheetDatalet)d).options = new Properties(testProps);
                ((StylesheetDatalet)d).options.put("fileCheckerImpl", fileChecker);
                // These tests will be run by a plain StylesheetTestlet
                xslCtr++;
            }
            else
            {
                // Hmmm - I'm not sure what we should do here
                reporter.logWarningMsg("Unexpected test file found, skipping: " + file);
                continue;
            }
            d.setDescription(file);
            v.addElement(d);
        }
        reporter.logTraceMsg("Bugzilla buildDatalets with " + javaCtr 
                + " .java Testlets, and " + xslCtr + " .xsl files to test");
        return v;
    }


    /**
     * Execute a Testlet with a specific user.dir.
     * Bugzilla testlets hardcode their input file names, assuming 
     * they're in the current directory.  But this automation is 
     * frequently run in another directory, and uses the inputDir 
     * setting to point where the files are.  Hence this worker 
     * method to change user.dir, execute the Testlet, and then 
     * switch back.
     * Note: will not work in Applet context, obviously.
     *
     * @param t Testlet to execute
     * @param dir to change user.dir to first
     * @throws propagates any non-user.dir exceptions
     */
    public void executeTestletInDir(Testlet t, Datalet d, String dir)
        throws Exception
    {
        final String USER_DIR = "user.dir";
        try
        {
            String cacheDir = System.getProperty(USER_DIR);
            Properties p = System.getProperties();
            // This should, I hope, properly get the correct path 
            //  for what the inputDir would be, whether it's a 
            //  relative or absolute path from where we are now
            File f = new File(inputDir);
            try
            {
                // Note the canonical form seems to be the most reliable for our purpose
                p.put(USER_DIR, f.getCanonicalPath());
            } 
            catch (IOException ioe)
            {
                p.put(USER_DIR, f.getAbsolutePath());
            }
            System.setProperties(p);

            // Now just execute the Testlet from here
            t.execute(d);

            // Replace the user.dir to be polite!
            p.put(USER_DIR, cacheDir);
            System.setProperties(p);
        } 
        catch (SecurityException se)
        {
            reporter.logThrowable(Logger.ERRORMSG, se, "executeTestletInDir threw");
            reporter.checkErr("executeTestletInDir threw :" + se 
                    + " cannot execute Testlet in correct dir " + dir);
        }
    }


    /**
     * Convenience method to get a Bugzilla Testlet to use.  
     * Take the TraxDatalet given and find the java classname 
     * from it.  Then just load an instance of that Testlet class.
     * 
     * @return Testlet for use in this test; null if error
     */
    public Testlet getTestlet(TraxDatalet d)
    {
        try
        {
            // Calculate the java classname
            String testletSourceName = (String)d.options.get(JAVA_SOURCE_NAME);
            // Potential problem: what if the SourceName doesn't have .java at end?
            String testletClassName = testletSourceName.substring(0, testletSourceName.indexOf(JAVA_EXTENSION));
            //@todo should we attempt to compile to a .class file 
            //  if we can't find the class here?  This adds a bunch 
            //  of complexity here; so I'm thinking it's better to 
            //  simply require the user to 'build all' first
            Class testletClazz = Class.forName(testletClassName);
            // Create it and set our reporter into it
            Testlet t = (Testlet)testletClazz.newInstance();
            t.setLogger((Logger)reporter);
            return (Testlet)t;
        }
        catch (Exception e)
        {
            // Ooops, none found, log an error
            reporter.logThrowable(Logger.ERRORMSG, e, "getTestlet(d) threw");
            reporter.checkErr("getTestlet(d) threw: " + e.toString());
            return null;
        }
    }


    /**
     * Convenience method to get a default filter for files.  
     * Returns special file filter for our use.
     * 
     * @return FilenameFilter using BugzillaFileRules(excludes).
     */
    public FilenameFilter getFileFilter()
    {
        // Find a Testlet class to use
        Class clazz = QetestUtils.testClassForName("org.apache.qetest.xsl.BugzillaFileRules", 
                                                   testPackages,
                                                   defaultFileFilter);
        try
        {
            // Create it, optionally with a category
            if ((null != excludes) && (excludes.length() > 1))  // Arbitrary check for non-null, non-blank string
            {
                Class[] parameterTypes = { java.lang.String.class };
                Constructor ctor = clazz.getConstructor(parameterTypes);

                Object[] ctorArgs = { excludes };
                return (FilenameFilter) ctor.newInstance(ctorArgs);
            }
            else
            {
                return (FilenameFilter)clazz.newInstance();
            }
        }
        catch (Exception e)
        {
            // Ooops, none found!
            return null;
        }
    }


    /**
     * Convenience method to get a default inputDir when none or
     * a bad one was given.  
     * @return String pathname of default inputDir "tests\bugzilla".
     */
    public String getDefaultInputDir()
    {
        return "tests" + File.separator + "bugzilla";
    }


    /**
     * Convenience method to print out usage information - update if needed.  
      * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by BugzillaTestletDriver:\n"
                + "    (Note: assumes inputDir=test/tests/bugzilla)"
                + "    (Note: we do *not* support -embedded)"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        BugzillaTestletDriver app = new BugzillaTestletDriver();
        app.doMain(args);
    }
}
