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
 * StylesheetTestletDriver.java
 *
 */
package org.apache.qetest.xsl;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xslwrapper.ProcessorWrapper;

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
 * Test driver for XSLT stylesheet Testlets.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class StylesheetTestletDriver extends XSLProcessorTestBase
{

    //-----------------------------------------------------
    //-------- Constants for common input params --------
    //-----------------------------------------------------

    /**
     * Parameter: Run a specific list of files, instead of 
     * iterating over directories.  
     * <p>Default: null, do normal iteration.</p>
     */
    public static final String OPT_FILELIST = "fileList";

    /** Name of fileList file to read in to get test definitions from.   */
    protected String fileList = null;

    /**
     * Parameter: FQCN or simple classname of Testlet to use.  
     * <p>User may pass in either a FQCN or just a base classname, 
     * and we will attempt to look it up in any of the most common 
     * Xalan-testing packages.  See QetestUtils.testClassForName().</p>
     * <p>Default: null, use StylesheetTestlet.</p>
     */
    public static final String OPT_TESTLET = "testlet";

    /** Classname of Testlet to use.   */
    protected String testlet = null;

    /**
     * Parameter: FQCN or simple classname of FilenameFilter for 
     * directories under testDir we will process.  
     * If fileList is not set, we simply go to our inputDir, and 
     * then use this filter to iterate through directories returned.
     * <p>Default: null, use ConformanceDirRules.</p>
     */
    public static final String OPT_DIRFILTER = "dirFilter";

    /** Classname of FilenameFilter to use for dirs.  */
    protected String dirFilter = null;

    /**
     * Parameter: FQCN or simple classname of FilenameFilter for 
     * files within subdirs we will process.  
     * If fileList is not set, we simply go through all directories 
     * specified by directoryFilter, and then use this filter to 
     * find all stylesheet test files in that directory to test.
     * Note that this does <b>not</b> handle embedded tests, where 
     * the XML document has an xml-stylesheet PI that defines the 
     * stylesheet to use to process it.
     * <p>Default: null, use ConformanceFileRules.</p>
     */
    public static final String OPT_FILEFILTER = "fileFilter";

    /** Classname of FilenameFilter to use for files.  */
    protected String fileFilter = null;


    /** Convenience constant: .xml extension for input data file.  */
    public static final String XML_EXTENSION = ".xml";

    /** Convenience constant: .xsl extension for stylesheet file.  */
    public static final String XSL_EXTENSION = ".xsl";

    /** Convenience constant: .out extension for output result file.  */
    public static final String OUT_EXTENSION = ".out";


    /** Just initialize test name, comment; numTestCases is not used. */
    public StylesheetTestletDriver()
    {
        testName = "StylesheetTestletDriver";
        testComment = "Test driver for XSLT stylesheet Testlets";
    }


    /**
     * Initialize this test - fill in parameters.
     * Simply fills in convenience variables from user parameters.
     *
     * @param p unused
     * @return true
     */
    public boolean doTestFileInit(Properties p)
    {
        // Copy any of our parameters from testProps to 
        //  our local convenience variables
        testlet = testProps.getProperty(OPT_TESTLET);
        dirFilter = testProps.getProperty(OPT_DIRFILTER);
        fileFilter = testProps.getProperty(OPT_FILEFILTER);
        fileList = testProps.getProperty(OPT_FILELIST);
        return true;
    }


    /**
     * Run through the directory given to us and run tests found
     * in subdirs; or run through our fileList.
     *
     * @param p Properties block of options to use - unused
     * @return true if OK, false if we should abort
     */
    public boolean runTestCases(Properties p)
    {
        // Grab the (potential) fileList from our properties
        //  block, in case it hasn't been read yet
        //@todo better specify how this is set!
        if (null == fileList)
        {
            fileList = testProps.getProperty(OPT_FILELIST);
        }

        // First log out any other runtime information, like the 
        //  actual flavor of ProcessorWrapper, etc.
        try
        {
            Hashtable runtimeProps = new Hashtable(4);
            runtimeProps.put("actual.ProcessorWrapper",
                             ProcessorWrapper.getWrapper(flavor).getDescription());
            runtimeProps.put("actual.testlet", getTestlet());
            runtimeProps.put("actual.dirFilter", getDirFilter());
            runtimeProps.put("actual.fileFilter", getFileFilter());
            reporter.logHashtable(Logger.CRITICALMSG, runtimeProps, 
                                  "actual.runtime information");
        }
        catch (Exception e)
        {
            reporter.logWarningMsg("Logging actual.runtime threw: " + e.toString());
            reporter.logThrowable(Logger.WARNINGMSG, e, "Logging actual.runtime threw");
        }

        // Now either run a list of specific tests the user specified, 
        //  or do the default of iterating over a set of directories
        if (null != fileList)
        {
            // Process the specific list of tests the user supplied
            String desc = "User-supplied fileList"; // provide default value
            Vector datalets = readFileList(fileList, desc);

            // Actually process the specified files in a testCase
            processFileList(datalets, desc);
        }
        else
        {
            // Do the default, which is to iterate over the inputDir
            // Note that this calls the testCaseInit/testCaseClose
            //  logging methods itself
            processInputDir();
        }
        return true;
    }


    /**
     * Do the default: test all stylesheets found in subdirs
     * of our inputDir, using FilenameFilters for dirs and files.
     * This only goes down one level in the tree, eg:
     * <ul>inputDir = tests/conf
     * <li>tests/conf - not tested</li>
     * <li>tests/conf/boolean - test all boolean*.xsl files</li>
     * <li>tests/conf/copy - test all copy*.xsl files</li>
     * <li>tests/conf/copy/foo - not tested</li>
     * <li>tests/conf/xmanual - not tested, since default 
     * ConformanceDirRules excludes dirs starting with 'x|X'</li>
     * <li>tests/whitespace - test all whitespace*.xsl files</li>
     * <li>etc.</li>
     * </ul>
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

        reporter.logInfoMsg("inputDir(" + testDirectory.getPath()
                            + ") looking for subdirs with: " + dirFilter);

        // Use our filter to get a list of directories to process
        String subdirs[] = testDirectory.list(getDirFilter());

        // Validate that we have some valid directories to process
        if ((null == subdirs) || (subdirs.length <= 0))
        {
            reporter.checkErr("inputDir(" + testDirectory.getPath()
                               + ") no valid subdirs found!");
            return;
        }

        int numSubdirs = subdirs.length;

        // For every subdirectory, check if we should run tests in it
        for (int i = 0; i < numSubdirs; i++)
        {
            File subTestDir = new File(testDirectory, subdirs[i]);

            if ((null == subTestDir) || (!subTestDir.exists()))
            {
                // Just log it and continue; presumably we'll find 
                //  other directories to test
                reporter.logWarningMsg("subTestDir(" + subTestDir.getPath() 
                                       + ") does not exist, skipping!");
                continue;
            }

            // Construct matching directories for outputs and golds
            File subOutDir = new File(outputDir, subdirs[i]);
            File subGoldDir = new File(goldDir, subdirs[i]);

            // Validate that each of the specified dirs exists
            // Returns directory references like so:
            //  testDirectory = 0, outDirectory = 1, goldDirectory = 2
            File[] dirs = validateDirs(new File[] { subTestDir }, 
                                       new File[] { subOutDir, subGoldDir });

            if (null == dirs)  // also ensures that dirs[0] is non-null
            {
                // Just log it and continue; presumably we'll find 
                //  other directories to test
                reporter.logWarningMsg("subTestDir(" + subTestDir.getPath() 
                                       + ") or associated dirs does not exist, skipping!");
                continue;
            }

            // Call worker method to process the individual directory
            //  and get a list of .xsl files to test
            Vector files = getFilesFromDir(subTestDir, getFileFilter(), embedded);

            // 'Transform' the list of individual test files into a 
            //  list of Datalets with all fields filled in
            Vector datalets = buildDatalets(files, subTestDir, subOutDir, subGoldDir);

            if ((null == datalets) || (0 == datalets.size()))
            {
                // Just log it and continue; presumably we'll find 
                //  other directories to test
                reporter.logWarningMsg("subTestDir(" + subTestDir.getPath() 
                                       + ") did not contain any tests, skipping!");
                continue;
            }

            processFileList(datalets, "Conformance test of: " + subdirs[i]);
        } // end of for...
    }


    /**
     * Run a list of stylesheet tests through a Testlet.
     * The file names are assumed to be fully specified, and we assume
     * the corresponding directories exist.
     * Each fileList is turned into a testcase.
     *
     * @param testlet StylesheetTestlet or subclass to use to test 
     * with the corresponding Datalets
     * @param vector of StylesheetDatalet objects to pass in
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

        // Put everything else into a testCase
        //  This is not necessary, but feels a lot nicer to 
        //  break up large test sets
        reporter.testCaseInit(desc);

        // Now just go through the list and process each set
        int numDatalets = datalets.size();
        reporter.logInfoMsg("processFileList() with " + numDatalets
                            + " potential tests");
        // Iterate over every datalet and test it
        for (int ctr = 0; ctr < numDatalets; ctr++)
        {
            try
            {
                // Create a Testlet to execute a test with this 
                //  next datalet - the Testlet will log all info 
                //  about the test, including calling check*()
                getTestlet().execute((Datalet)datalets.elementAt(ctr));
            } 
            catch (Throwable t)
            {
                // Log any exceptions as fails and keep going
                //@todo improve the below to output more useful info
                reporter.checkFail("Datalet num " + ctr + " threw: " + t.toString());
                reporter.logThrowable(Logger.ERRORMSG, t, "Datalet threw");
            }
        }  // of while...
        reporter.testCaseClose();
    }


    /**
     * Use the supplied filter on given directory to return a list 
     * of stylesheet tests to be run.
     * Uses the normal filter for variations of *.xsl files, and 
     * also constructs names for any -embedded tests found (which 
     * may be .xml with xml-stylesheet PI's, not just .xsl)
     *
     * @param dir directory to scan
     * @param filter to use on this directory; if null, uses default
     * @param embeddedFiles special list of embedded files to find
     * @return Vector of local path\filenames of tests to run;
     * the tests themselves will exist; null if error
     */
    public Vector getFilesFromDir(File dir, FilenameFilter filter, String embeddedFiles)
    {
        // Validate arguments
        if ((null == dir) || (!dir.exists()))
        {
            // Bad arguments, report it as an error
            // Note: normally, this should never happen, since 
            //  this class normally validates these arguments 
            //  before calling us
            reporter.logWarningMsg("getFilesFromDir(" + dir.toString() + ") dir null or does not exist");
            return null;
        }
        // Get the list of 'normal' test files
        String[] files = dir.list(filter);
        Vector v = new Vector(files.length);
        for (int i = 0; i < files.length; i++)
        {
            v.addElement(files[i]);
        }
        reporter.logTraceMsg("getFilesFromDir(" + dir.toString() + ") found " + v.size() + " xsl files to test");

        // Also get a list of any embedded test files here
        //  Optimization: only look for embedded files when likely to find them
        if ((null != embeddedFiles) && (embeddedFiles.indexOf(dir.getName()) > -1))
        {
            // OK, presumably we have an embedded file in the current dir, 
            //  add that name
            StringTokenizer st = new StringTokenizer(embeddedFiles, ";");//@todo resource ;
            while (st.hasMoreTokens())
            {
                String embeddedName = st.nextToken();
                // Check if it's in our dir...
                if (embeddedName.startsWith(dir.getName()))
                {
                    // ...and that it exists
                    if ((new File(dir.getPath() + File.separator + embeddedName)).exists())
                    {
                        v.addElement(embeddedName);
                    }
                    else
                    {
                        reporter.logWarningMsg("Requested embedded file " + dir.getPath() + File.separator + embeddedName
                                               + " does not exist, skipping");
                    }
                }
            }
        }
        reporter.logTraceMsg("getFilesFromDir(" + dir.toString() + ") found " + v.size() + " total files to test");
        return v;
    }


    /**
     * Transform a vector of individual test names into a Vector 
     * of filled-in datalets to be tested
     *
     * @param files Vector of local path\filenames to be tested
     * @param testLocation File denoting directory where all 
     * .xml/.xsl tests are found
     * @param outLocation File denoting directory where all 
     * output files should be put
     * @param goldLocation File denoting directory where all 
     * gold files are found
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

        // For every file in the vector, construct the matching 
        //  out, gold, and xml/xsl files
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
                //@todo log an error
                continue;
            }
            // Check if it's a normal .xsl file, or a .xml file
            //  (we assume .xml files are embedded tests!)
            StylesheetDatalet d = new StylesheetDatalet();
            if (file.endsWith(XML_EXTENSION))
            {
                d.xmlName = testLocation.getPath() + File.separator + file;

                String fileNameRoot = file.substring(0, file.indexOf(XML_EXTENSION));
                d.inputName = null;
                d.outputName = outLocation.getPath() + File.separator + fileNameRoot + OUT_EXTENSION;
                d.goldName = goldLocation.getPath() + File.separator + fileNameRoot + OUT_EXTENSION;
            }
            else if (file.endsWith(XSL_EXTENSION))
            {
                d.inputName = testLocation.getPath() + File.separator + file;

                String fileNameRoot = file.substring(0, file.indexOf(XSL_EXTENSION));
                d.xmlName = testLocation.getPath() + File.separator + fileNameRoot + XML_EXTENSION;
                d.outputName = outLocation.getPath() + File.separator + fileNameRoot + OUT_EXTENSION;
                d.goldName = goldLocation.getPath() + File.separator + fileNameRoot + OUT_EXTENSION;
            }
            else
            {
                // Hmmm - I'm not sure what we should do here
                reporter.logWarningMsg("Unexpected test file found, skipping: " + file);
            }
            d.setDescription(file);
            // Also copy over our own testProps as it's 
            //  options: this allows for future expansion
            //  of values in the datalet
            d.options = new Properties(testProps);
            v.addElement(d);
        }
        return v;
    }


    /**
     * Read in a file specifying a list of files to test.
     * <p>File format is pretty simple:</p>
     * <ul>
     * <li># first line of comments is copied into desc</li>
     * <li># beginning a line is a comment</li>
     * <li># rest of lines are whitespace delimited filenames and options</li>
     * <li>inputName xmlName outName goldName flavor options...</li>
     * <li><b>Note:</b> see {@link StylesheetDatalet} for
     * details on how the file lines are parsed!</li>
     * </ul>
     * <p>Most items are optional, but not having them may result 
     * in validation oddities</p>
     *
     * @param fileName String; name of the file
     * @param desc description; caller's copy changed
     * @return Vector of StylesheetDatalets, or null if error
     */
    public Vector readFileList(String fileName, String desc)
    {
        final String COMMENT_CHAR = "#";
        final String ABSOLUTE = "absolute";
        final String RELATIVE = "relative";

        // Verify the file is there
        File f = new File(fileName);
        if (!f.exists())
        {
            reporter.logErrorMsg("readFileList: " + fileName  + " does not exist!");
            return null;
        }

        Vector vec = new Vector();
        BufferedReader br = null;
        String line = null;
        try
        {
            br = new BufferedReader(new FileReader(f));
            line = br.readLine(); // read just first line
        }
        catch (IOException ioe)
        {
            reporter.logErrorMsg("readFileList: " + fileName + " threw: "
                                 + ioe.toString());
            return null;
        }

        // Verify the first line
        if (line == null)
        {
            reporter.logErrorMsg("readFileList: " + fileName
                                 + " appears to be blank!");
            return null;
        }

        // Check if the first line is a comment 
        if (line.startsWith(COMMENT_CHAR))
        {
            // Save it as the description
            desc = line;
        }

        // Load each line into a StylesheetDatalet
        for (;;)
        {
            // Skip any lines beginning with # comment char or that are blank
            if ((!line.startsWith(COMMENT_CHAR)) && (line.length() > 0))
            {
                // Create a Datalet and initialize with the line's contents
                StylesheetDatalet d = new StylesheetDatalet(line);

                //@todo Avoid spurious passes when output & gold not specified
                //  needs to detect when StylesheetDatalet doesn't 
                //  properly have outputName and goldName set

                // Add it to our vector
                vec.addElement(d);
            }

            // Read next line and loop
            try
            {
                line = br.readLine();
            }
            catch (IOException ioe2)
            {
                // Just force us out of the loop; if we've already 
                //  read part of the file, fine
                reporter.logWarningMsg("readFileList: " + fileName
                                       + " threw: " + ioe2.toString());
                break;
            }

            if (line == null)
                break;
        } // end of for (;;)

        if (vec.size() == 0)
        {
            reporter.logErrorMsg("readFileList: " + fileName
                                 + " did not have any non-comment lines!");
            return null;
        }
        return vec;
    }


    /**
     * Validate existence of or create various directories needed.
     * <p>If any optionalDir cannot be created, it's array entry 
     * will be null.</p>
     *
     * @param requiredDirs array of directories that must previously 
     * exist; if none do, will return null; if none passed, return null
     * @param optionalDirs array of optional directories; if they do 
     * not exist they'll be created
     * @return array of file objects, null if any error; all 
     * required dirs are first, in order; then all optionalDirs
     */
    public File[] validateDirs(File[] requiredDirs, File[] optionalDirs)
    {
        if ((null == requiredDirs) || (0 == requiredDirs.length))
        {
            return null;
        }
    
        File[] dirs = new File[(requiredDirs.length + optionalDirs.length)];
        int ctr = 0;

        try
        {
            // Validate requiredDirs exist first
            for (int ir = 0; ir < requiredDirs.length; ir++)
            {
                if (!requiredDirs[ir].exists())
                {
                    reporter.logErrorMsg("validateDirs("
                                         + requiredDirs[ir]
                                         + ") requiredDir did not exist!");
                    return null;
                }
                dirs[ctr] = requiredDirs[ir];
                ctr++;
            }

            // Create any optionalDirs needed
            for (int iopt = 0; iopt < optionalDirs.length; iopt++)
            {
                if (!optionalDirs[iopt].exists())
                {
                    if (!optionalDirs[iopt].mkdirs())
                    {
                        reporter.logWarningMsg("validateDirs("
                                               + optionalDirs[iopt]
                                               + ") optionalDir could not be created");
                        dirs[ctr] = null;
                    }
                    else
                    {
                        reporter.logTraceMsg("validateDirs("
                                             + optionalDirs[iopt]
                                             + ") optionalDir was created");
                        dirs[ctr] = optionalDirs[iopt];
                    }
                }
                else
                {  
                    // It does previously exist, so copy it over
                    dirs[ctr] = optionalDirs[iopt];
                }
                ctr++;
            }
        }
        catch (Exception e)
        {
            reporter.logThrowable(Logger.ERRORMSG, e, "validateDirs threw: " + e.toString());
            return null;
        }

        return dirs;
    }


    /**
     * List of common packages that Xalan testing classes are in.
     * Note that Xalan-J 2.x packages are listed before Xalan-J 1.x 
     * packages, and there is an inherent danger in the ordering 
     * when two classes have the same name.
     */
    protected String[] testPackages = 
    {
        "org.apache.qetest.xsl",
        "org.apache.qetest.trax",
        "org.apache.qetest.xalanj2",
        "org.apache.qetest.xalanj1",
        "org.apache.qetest"
    };

    /** Default FilenameFilter for directories.   */
    protected String defaultDirFilter = "org.apache.qetest.xsl.ConformanceDirRules";

    /** Default FilenameFilter for files.   */
    protected String defaultFileFilter = "org.apache.qetest.xsl.ConformanceFileRules";

    /** Default Testlet for executing stylesheet tests.   */
    protected String defaultTestlet = "org.apache.qetest.xsl.StylesheetTestlet";


    /**
     * Convenience method to get a Testlet to use.  
     * Attempts to return one as specified by our testlet parameter, 
     * otherwise returns a default StylesheetTestlet.
     * 
     * @return Testlet for use in this test; null if error
     */
    public Testlet getTestlet()
    {
        // Find a Testlet class to use
        Class clazz = QetestUtils.testClassForName(testlet, 
                                                   testPackages,
                                                   defaultTestlet);
        try
        {
            // Create it and set our reporter into it
            Testlet t = (Testlet)clazz.newInstance();
            t.setLogger((Logger)reporter);
            return (Testlet)t;
        }
        catch (Exception e)
        {
            // Ooops, none found! This should be very rare, since 
            //  we know the defaultTestlet should be found
            return null;
        }
    }


    /**
     * Convenience method to get a default filter for directories.  
     * Uses category member variable if set.
     * 
     * @return FilenameFilter using ConformanceDirRules(category).
     */
    public FilenameFilter getDirFilter()
    {
        // Find a Testlet class to use
        Class clazz = QetestUtils.testClassForName(dirFilter, 
                                                   testPackages,
                                                   defaultDirFilter);
        try
        {
            // Create it, optionally with a category
            if ((null != category) && (category.length() > 1))  // Arbitrary check for non-null, non-blank string
            {
                Class[] parameterTypes = { java.lang.String.class };
                Constructor ctor = clazz.getConstructor(parameterTypes);

                Object[] ctorArgs = { category };
                return (FilenameFilter)ctor.newInstance(ctorArgs);
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
     * Convenience method to get a default filter for files.  
     * Uses excludes member variable if set.
     * 
     * @return FilenameFilter using ConformanceFileRules(excludes).
     */
    public FilenameFilter getFileFilter()
    {
        // Find a Testlet class to use
        Class clazz = QetestUtils.testClassForName(fileFilter, 
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
     * @return String pathname of default inputDir "tests\conf".
     */
    public String getDefaultInputDir()
    {
        return "tests" + File.separator + "conf";
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by StylesheetTestletDriver:\n"
                + "    -" + OPT_FILELIST
                + "  <name of listfile of tests to run>\n"
                + "    -" + OPT_DIRFILTER
                + "  <classname of FilenameFilter for dirs>\n"
                + "    -" + OPT_FILEFILTER
                + "  <classname of FilenameFilter for files>\n"
                + "    -" + OPT_TESTLET
                + "  <classname of Testlet to execute tests with>\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        StylesheetTestletDriver app = new StylesheetTestletDriver();
        app.doMain(args);
    }
}
