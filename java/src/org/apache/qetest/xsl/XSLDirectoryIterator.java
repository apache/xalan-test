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
 * XSLDirectoryIterator.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.*;
import org.apache.qetest.xslwrapper.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URL;  // For optional URI/URLs instead of string filenames

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

// Try to catch this specific exception from the ProcessorWrapper, if it is thrown
import org.xml.sax.SAXException;


//-------------------------------------------------------------------------

/**
 * Sample Xalan test driver base that iterates over directories
 * and xml/xsl file pairs.
 * <p>This class provides generic support for iterating over directories full
 * of xsl/xml file pairs, and sending them through the processor.  Subclasses
 * may override processSingleFile to call different variations of .process(...).</p>
 * <p>Alternately, users may specify -fileList file.lst that contains listings
 * of specific files to run (instead of iterating over the directories by following
 * the appropriate Conformance rules).</p>
 * <ul>initializes all members from XSLProcessorTestBase, plus
 * <li>fileList</li>
 * </ul>
 * @todo improve file discovery - file execution paradigm
 * @author Shane Curcuru
 * @version $Id$
 */
public class XSLDirectoryIterator extends XSLProcessorTestBase
{

    /**
     * Convenience method to print out usage information.
     * @author Shane Curcuru
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String usage()
    {

        return ("Common options supported by XSLDirectoryIterator:\n"
                + "    -" + OPT_FILELIST
                + "  <name of listfile of tests to run>\n" + super.usage());
    }

    //-----------------------------------------------------
    //-------- Constants for common input params --------
    //-----------------------------------------------------

    /**
     * Parameter: Run a specific list of files, instead of iterating in dir.
     * <p>Default: null, do normal ConformanceDir iteration.</p>
     */
    public static final String OPT_FILELIST = "fileList";

    /** NEEDSDOC Field fileList          */
    protected String fileList = "name_of_file.txt";

    /**
     * Token in xsl files signifying that an exception is expected.
     * </p>&lt;!-- ExpectedException: 'put the .toString() representation of the exception here' --&gt;</p>
     */
    public static final String EXPECTED_EXCEPTION = "ExpectedException:";

    /**
     * Returned from processSingleFile; signifies process completed without exceptions.
     * <p>No other validation is performed (i.e. user must validate expected
     * outputs themselves after this call returns).</p>
     */
    public static final int PROCESS_OK = 1;

    /**
     * Returned from processSingleFile; signifies process threw an EXPECTED_EXCEPTION.
     * <p>No other validation is performed (i.e. user must validate expected
     * outputs, if any, themselves after this call returns).</p>
     */
    public static final int GOT_EXPECTED_EXCEPTION = 2;

    /**
     * Returned from processSingleFile; signifies process threw a non-EXPECTED_EXCEPTION.
     * <p>No other validation is performed (i.e. user must validate expected
     * outputs, if any, themselves after this call returns, as well as ensuring
     * that the test is still valid to continue).</p>
     */
    public static final int UNEXPECTED_EXCEPTION = 3;

    /** Convenience constant: .xml extension for input data file. */
    protected static final String xmlExtension = ".xml";

    /** Convenience constant: .xsl extension for stylesheet file. */
    protected static final String xslExtension = ".xsl";

    /** Convenience constant: .out extension for output result file. */
    protected static final String outExtension = ".out";

    /** Convenience constant: .log extension for logs and reporting files. */
    protected static final String diagExtension = ".log";

    /** Cheap-o way to set an indent on the processor. */
    protected static final int NO_INDENT = -1;  // an illegal value specifying 'default indent'

    /** NEEDSDOC Field indentLevel          */
    protected int indentLevel = NO_INDENT;

    /** Cheap-o diagnotic name manager. */
    protected OutputNameManager diagNameMgr = null;

    //-----------------------------------------------------
    //-------- Worker methods to iterate over directories --------
    //-----------------------------------------------------

    /**
     * Accesor for a processor wrapper.
     * <ul>Wrapper must support the following operations:
     * <li>Object createNewProcessor (String liaisonClassName) throws Exception</li>
     * <li>long processToFile (String xmlSource, String xslStylesheet, String resultTree) throws Exception</li>
     * <li>void reset()</li>
     * <li>void setDiagnosticsOutput(java.io.PrintWriter pw)</li>
     * </ul>
     * <p>This allows tests to be agnostic to different flavors of processors.</p>
     * @todo Update to new calls everywhere!
     */
    protected org.apache.qetest.xslwrapper.ProcessorWrapper processorW;

    /** Cheap-o benchmarking: total time spent in all process calls. */
    long overallTime = 0;

    /** Cheap-o benchmarking: total number of files processed. */
    long overallFilesProcessed = 0;

    /** Cheap-o benchmarking: total time spent in all process calls per directory. */
    long dirTime = 0;

    /** Cheap-o benchmarking: total number of files processed per directory. */
    long dirFilesProcessed = 0;

    // We also use XLTest's fields: reporter, flavor, liaison, diagnostics
    // These would have to be parameterized to turn this class into an interface

    /**
     * Subclassed callback to provide info about the processor you're testing.
     * <p>Override to return a string describing the specific version/flavor/etc.
     * of the processor you're testing.  Provides a default implementation that
     * can be used if you wish.  Called from logTestProps
     * (which is called from processTestDir).</p>
     * @return 'ProcessorVersion;' + getDescription()
     */
    public String getDescription()
    {

        if (processorW != null)
        {

            // Auto-create a ProcessorWrapper as needed
            createNewProcessor();
        }

        return "ProcessorVersion;" + processorW.getDescription();
    }

    /**
     * Log out any version or system info.
     * <p>Logs System.getProperties(), the testProps block, plus the provided string
     * indicating the version info from the processor.  Called from processTestDir.</p>
     */
    public void logTestProps()
    {

        // Log out system/build info - use crit msg so it's always reported
        reporter.logCriticalMsg(getDescription());
        reporter.logHashtable(reporter.CRITICALMSG, System.getProperties(),
                              "System.getProperties");
        reporter.logHashtable(reporter.CRITICALMSG, testProps, "testProps");
    }

    /**
     * Iterate through our inputDir, going into subdirs and processing xml/xsl file pairs therein.
     * <p>.</p>
     * <p><B>NOTE:</B> It is the responsibility of the subclassed test to call
     * createNewProcessor (or it's equivalent) before calling processTestDir!</p>
     */
    public void processTestDir()  // TODO change to processTests()
    {

        // NOTE: This method simply directly uses our/our parents instance variables:
        //      inputDir, reporter, category, outDir, goldDir, noErrTest, 
        //      plus each of the benchmarking variables: 
        //      overallTime, overallFilesProcessed, dirTime, dirFilesProcessed 
        // report out version info, properties, etc.
        logTestProps();

        // Ensure the inputDir is there - we must have a valid location for input files
        File testDirectory = new File(inputDir);

        if (!testDirectory.exists())
        {
            if (!lookForTestDir())  // attempt self-discovery of dirs
            {

                // Note: This might want to be inside of a testCase
                reporter.checkErr("inputDir(" + testDirectory.getPath()
                                  + ") does not exist, aborting!");

                return;
            }
        }

        // We process tests in one of two different ways:
        //  - from a user specified -fileList
        //  - by iterating over all subdirs that follow the Conformance rules
        String fileListFileName = testProps.getProperty(OPT_FILELIST);

        if (fileListFileName != null)
        {

            // We're going to grab the list of files the user specified
            //  This should be a vector of XSLTestfileInfo objects, each one a test to run
            // Currently, the readFileListFile method forces all files to be 
            //  specified absolutely by the time it's done
            reporter.logWarningMsg(
                "-fileList property implies we ignore category and excludes!");

            Vector list = readFileListFile(fileListFileName);

            reporter.testCaseInit("Conformance Test of: fileList "
                                  + fileListFileName);
            processFileList(list);
            reporter.testCaseClose();
        }

        // TODO make the dir iteration below follow the same discovery - execution
        //  model as the fileList case above - requires re-writing the rest of this 
        //  method as well as processSingleDir
        else
        {

            // We're going to scan the test directory for subdirectories to iterate over
            // Then for each subdir, we'll scan for files according to the 
            reporter.logInfoMsg("inputDir(" + testDirectory.getPath()
                                + ") looking for valid subdirs...");

            FilenameFilter dirFilter;

            if ((category != null) && (category != ""))
            {

                // Filter on the directories we were asked for
                // semi HACK - should implement a full list of categories, not just one
                dirFilter = new ConformanceDirRules(category);
            }
            else
            {  // Just use default filtering (skip dirs beginning with [x|X])
                dirFilter = new ConformanceDirRules();
            }

            String dirNames[] = testDirectory.list(dirFilter);

            if ((null == dirNames) || (dirNames.length <= 0))
            {

                // TODO should this be wrapped in a testCase?
                reporter.checkFail("inputDir(" + testDirectory.getPath()
                                   + ") no valid subdirs found!");

                return;
            }

            int nDirs = dirNames.length;

            // For every subdirectory, check if we should run tests in it
            for (int i = 0; i < nDirs; i++)
            {
                File subTestDir = new File(testDirectory, dirNames[i]);

                // Convert all dir references to absolute ones, to get around 
                //  potential problems with relative paths and test harnesses
                //  that change the current directory
                // TODO: Check if we should use getCanonicalPath instead!
                subTestDir = new File(subTestDir.getAbsolutePath());

                if (null == subTestDir)
                {
                    reporter.logWarningMsg("Could not find subdir: "
                                           + testDirectory.getPath()
                                           + File.pathSeparator
                                           + dirNames[i]);

                    continue;
                }

                File subOutDir = new File(outputDir, dirNames[i]);

                subOutDir = new File(subOutDir.getAbsolutePath());

                File subGoldDir = new File(goldDir, dirNames[i]);

                subGoldDir = new File(subGoldDir.getAbsolutePath());

                // Run our positive case tests
                reporter.testCaseInit("Conformance Test of: " + dirNames[i]);
                processSingleDir(subTestDir, subOutDir, subGoldDir, null);  // using default file filter

                if (dirFilesProcessed > 0)
                {

                    // Only log these stats if perfLogging is set to true
                    String tmp =
                        testProps.getProperty(Reporter.OPT_PERFLOGGING);

                    if ((tmp != null) && tmp.equalsIgnoreCase("true"))
                    {
                        reporter.logStatistic(
                            reporter.STATUSMSG,
                            (dirTime / dirFilesProcessed), 0,
                            "Dir average time to process files in this dir");
                        reporter.logStatistic(
                            reporter.STATUSMSG,
                            (overallTime / overallFilesProcessed), 0,
                            "Running average time of dirs so far");
                    }
                }

                reporter.testCaseClose();

                if (!noErrTest)  // Note the double negative
                {

                    // Run our negative, or 'err' case tests within this subdir
                    final String ERRDIRNAME = "err";
                    FilenameFilter errDirFilter =
                        new ConformanceDirRules(ERRDIRNAME);
                    String errDirNames[] = subTestDir.list(errDirFilter);

                    if ((null != errDirNames) && (errDirNames.length > 0))
                    {

                        // Hopefully, there's only the one 'err' directory!
                        for (int k = 0; k < errDirNames.length; k++)
                        {
                            File errTestDir = new File(subTestDir,
                                                       errDirNames[k]);

                            if (null == errTestDir)
                            {
                                reporter.logWarningMsg(
                                    "Could not find errDir: "
                                    + subTestDir.getPath()
                                    + File.pathSeparator + errDirNames[i]);

                                continue;
                            }

                            // Optimization: if there are no files in the errDir, don't bother going there
                            FilenameFilter errFileFilter =
                                new ConformanceErrFileRules(dirNames[i]);
                            String anyErrNames[] =
                                errTestDir.list(errFileFilter);

                            if ((anyErrNames != null)
                                    && (anyErrNames.length > 0))
                            {
                                File errOutDir = new File(subOutDir,
                                                          errDirNames[k]);
                                File errGoldDir = new File(subGoldDir,
                                                           errDirNames[k]);

                                // Use special filter to look for parentName+"err"
                                reporter.testCaseInit(
                                    "Conformance Test of errDir: "
                                    + errDirNames[k]);
                                processSingleDir(errTestDir, errOutDir,
                                                 errGoldDir, errFileFilter);

                                if (dirFilesProcessed > 0)
                                {
                                    reporter.logStatistic(
                                        reporter.STATUSMSG,
                                        (dirTime / dirFilesProcessed), 0,
                                        "Dir average time to process files in this dir");
                                    reporter.logStatistic(
                                        reporter.STATUSMSG,
                                        (overallTime / overallFilesProcessed),
                                        0, "Running average time of dirs so far");
                                }

                                reporter.testCaseClose();
                            }  // of if ((anyErrNames != null).. Optimization
                            else
                            {
                                reporter.logStatusMsg(
                                    "inputDir(" + errTestDir.getPath()
                                    + ") no 'err' files found");
                            }
                        }  // of for (int k = 0...
                    }  // of if (null != errDirNames)
                    else
                    {
                        reporter.logInfoMsg("inputDir("
                                            + testDirectory.getPath()
                                            + ") no 'err' subdirs found");
                    }  // of if...else (null != errDirNames)
                }  // of if (runErrTests)
            }  // of for (int i = 0... 'For every subdirectory'

            // Done with all dirs, report last statistic
            if (overallFilesProcessed > 0)
            {

                // Only log these stats if perfLogging is set to true
                String tmp = testProps.getProperty(Reporter.OPT_PERFLOGGING);

                if ((tmp != null) && tmp.equalsIgnoreCase("true"))
                {
                    reporter.logStatistic(
                        reporter.STATUSMSG,
                        (overallTime / overallFilesProcessed), 0,
                        "Overall average process time");
                }
            }
        }  // of if (fileListFileName != null)
    }

    /**
     * Run one directory full of xsl/xml file pairs through the processor; virtually stateless.
     * <p>This assumes the roots of each of test and out already exist.
     * This then takes an existing test subdir, creates corresponding out and gold
     * subdirs, and then processes all xsl/xml file pairs using processSingleFile().</p>
     * <p>Default processing is provided for Conformance-style tests.</p>
     * <p>Each subdirectory is treated like a test case.</p>
     * <p>This method does not access the processorWrapper (or it's processor)
     * in any way - thus is suitable for use in subclasses with overriden
     * createNewProcessor() methods.</p>
     * @param testDirectory specific dir with xml/xsl file pairs to iterate through
     * @param outDirectory dir to put any output files in
     * @param goldDirectory dir to look for expected gold files in
     * @param filter filter for filenames to look for (null=default, name starts-with parent)
     */
    public void processSingleDir(File testDirectory, File outDirectory,
                                 File goldDirectory, FilenameFilter filter)
    {

        // Reset performance stuff first, so we don't double-report if we fail to run any tests here
        dirTime = 0;
        dirFilesProcessed = 0;

        File[] requiredDirs = { testDirectory, outDirectory };
        File[] otherDirs = { goldDirectory };

        // testDirectory = 0, outDirectory = 1, goldDirectory = 2
        File[] dirs = validateDirs(requiredDirs, otherDirs);

        if (dirs == null)  // also ensures that dirs[0] is non-null
        {
            reporter.checkFail(
                "testDir or outDir do not exist, aborting test!");

            return;
        }

        // Process any XML-only tests (where there is no XSL stylesheet file)
        if ((embedded != null) && (embedded.length() > 1))  // Arbitrary check for non-null, non-blank string
        {
            // HACK: this structure is cheap - this should either be 
            //  integrated into the fileList stuff, or should be read 
            //  from each .xsl file as part of it's comments (even 
            //  if the .xsl file shouldn't be used in the test
            // @todo optimize to only call when dir name is somehow 
            //  contained in embedded member var
            processEmbeddedFiles(dirs[0], dirs[1], dirs[2]);
        }
        // Set a default filter (<name>*.xsl where <name>=<dir>) only if not already set
        if (filter == null)
        {
            if ((excludes != null) && (excludes.length() > 1))  // Arbitrary check for non-null, non-blank string
            {
                filter = new ConformanceFileRules(excludes);  // NOTE: changes caller's copy as well

                reporter.logTraceMsg(
                    "Using default ConformanceFileRules with excludes: "
                    + excludes);
            }
            else
            {
                filter = new ConformanceFileRules();  // NOTE: changes caller's copy as well

                reporter.logTraceMsg("Using default ConformanceFileRules");
            }
        }
        else
        {  // NEEDSWORK: should only print this if it's also not already a ConformanceFileRules
            reporter.logInfoMsg("Using custom fileFilter: "
                                + filter.toString());
        }

        // Use the fileFilter to get only the names we want to test
        String xslnames[] = dirs[0].list(filter);

        if (null != xslnames)
        {
            int numXSLFiles = xslnames.length;

            reporter.logInfoMsg("processOneDir(" + dirs[0].getPath()
                                + ") with " + numXSLFiles
                                + " potential .xsl files to test");

            // Iterate over every xsl file found and check if we should test it
            for (int k = 0; k < numXSLFiles; k++)
            {
                String xslName = xslnames[k];

                // Find the root of the filename, less extension, and create .xml and .out versions of the name
                // OPTIMIZATION? ConformanceDirRules probably ensure that we have a good name, so we may not need this.
                int dotIndex = xslName.indexOf('.');

                if (dotIndex > 0)
                {

                    // Construct the various inputs to the LotusXSL processor, based on .xsl filename
                    String rootFileName = xslName.substring(0, dotIndex);

                    // Switch to CanonicalPaths which seem to be safer SC 28-Jun-00
                    String absXSLName = null;

                    try
                    {
                        absXSLName = dirs[0].getCanonicalPath()
                                     + File.separatorChar + xslName;
                    }
                    catch (IOException ioe1)
                    {
                        absXSLName = dirs[0].getAbsolutePath()
                                     + File.separatorChar + xslName;
                    }

                    String absXMLName = null;

                    try
                    {
                        absXMLName = dirs[0].getCanonicalPath()
                                     + File.separatorChar + rootFileName
                                     + xmlExtension;
                    }
                    catch (IOException ioe2)
                    {
                        absXMLName = dirs[0].getAbsolutePath()
                                     + File.separatorChar + rootFileName
                                     + xmlExtension;
                    }

                    if (dirs[1] == null)
                    {

                        // Provide some sort of default TODO is this the best cross-platform default?
                        dirs[1] = new File("\badOutDir");

                        reporter.logErrorMsg(
                            "outDir(" + outDirectory
                            + ") is bad - resetting to \badOutDir");
                    }

                    String absOutName = null;

                    try
                    {
                        absOutName = dirs[1].getCanonicalPath()
                                     + File.separatorChar + rootFileName
                                     + outExtension;
                    }
                    catch (IOException ioe3)
                    {
                        absOutName = dirs[1].getAbsolutePath()
                                     + File.separatorChar + rootFileName
                                     + outExtension;
                    }

                    if (dirs[2] == null)
                    {

                        // Provide some sort of default
                        dirs[2] = new File("\badGoldDir");

                        reporter.logErrorMsg(
                            "goldDir(" + goldDirectory
                            + ") is bad - resetting to \badGoldDir");
                    }

                    String absGoldName = null;

                    try
                    {
                        absGoldName = dirs[2].getCanonicalPath()
                                      + File.separatorChar + rootFileName
                                      + outExtension;
                    }
                    catch (IOException ioe3)
                    {
                        absGoldName = dirs[2].getAbsolutePath()
                                      + File.separatorChar + rootFileName
                                      + outExtension;
                    }

                    // Sanity check - see if files exist (not strictly needed?)
                    File xslF = new File(absXSLName);
                    File xmlF = new File(absXMLName);

                    if (xslF.exists() && xmlF.exists())
                    {
                        reporter.logTraceMsg("About to test:"
                                             + xmlF.getName() + " with:"
                                             + xslF.getName() + " into:"
                                             + absOutName);
                    }
                    else
                    {
                        reporter.logWarningMsg("Files may not exist:"
                                               + absXMLName + " or:"
                                               + absXSLName);
                    }

                    // Actually run the xsl/xml file pair through the processor
                    // Note subclasses may override this to do different kinds of processing
                    // FUTUREWORK: put this switch in a method, so subclasses can ovverride
                    //       to also do different kinds of validation!
                    switch (processSingleFile(absXMLName, absXSLName,
                                              absOutName))
                    {
                    case PROCESS_OK :
                        if (!reporter.check(fileChecker, 
                                            new File(absOutName), 
                                            new File(absGoldName), 
                                            xslF.getName() + " output comparison",
                                            xslF.getName()))
                        {
                            // If we're using an appropriate fileChecker, 
                            //  get extra info on a fail, as well as file references to the test file

                            // Report extra info about why it failed
                            String tmp = fileChecker.getExtendedInfo();
                            if (tmp != null)
                                reporter.logArbitrary(reporter.INFOMSG, tmp);
                            else
                                reporter.logTraceMsg("getFileChecker().getExtendedInfo() not available");

                            // Log a custom element with all the file refs in it
                            // Closely related to viewResults.xsl select='fileref"
                            Hashtable attrs = new Hashtable();
                            attrs.put("idref", xslF.getName());
                            attrs.put("inputName", absXSLName);
                            attrs.put("xmlName", absXMLName);
                            attrs.put("outputName", absOutName);
                            attrs.put("goldName", absGoldName);
                            reporter.logElement(Logger.STATUSMSG, "fileref", attrs, "File references for Conformance Test");
                        }
                        break;

                    case GOT_EXPECTED_EXCEPTION :
                        reporter.checkPass(xslF.getName()
                                           + " got expected exception", xslF.getName());
                        break;

                    case UNEXPECTED_EXCEPTION :
                        reporter.checkFail(xslF.getName()
                                           + " got unexpected exception", xslF.getName());
                        Hashtable attrs = new Hashtable();
                        attrs.put("idref", xslF.getName());
                        attrs.put("inputName", absXSLName);
                        attrs.put("xmlName", absXMLName);
                        attrs.put("outputName", absOutName);
                        attrs.put("goldName", absGoldName);
                        reporter.logElement(Logger.STATUSMSG, "fileref", attrs, "File references for Conformance Test");
                        break;

                    default:
                        // This should never happen
                        reporter.logErrorMsg("Unexpected return value from processEmbeddedFile!");
                    }
                }  // of if (dotIndex > 0)
                else
                {
                    reporter.logWarningMsg("Problem with xsl filename: "
                                           + xslName);
                }
            }  // of for (int k = 0...
        }  // of if (null != xslnames)
        else
        {
            reporter.logWarningMsg("Could not find any xsl files in: "
                                   + dirs[0].getPath());
        }

        // update overall statistics
        overallTime += dirTime;
        overallFilesProcessed += dirFilesProcessed;
    }

    /**
     * Run only the special embedded files here.
     * HACK: this should be integrated with the fileList 
     * processing somehow.
     * @todo documentation
     */
    public void processEmbeddedFiles(File testDirectory, File outDirectory, File goldDirectory)
    {
        // See if we might have any files to process (optimization)
        if ((embedded == null) || (embedded.length() < 1))
        {
            reporter.logErrorMsg("processEmbeddedFiles() with no embedded files, returning!");
            return;
        }
        
        // Don't continue if there are no files who's names contain our dir (optimization)
        if (embedded.indexOf(testDirectory.getName()) < 0)
        {
            reporter.logInfoMsg("processEmbeddedFiles() in other directory, returning");
            return;
        }
        // OK, presumably we have an embedded file in the current dir, 
        //  go process just that .xml file
        StringTokenizer st = new StringTokenizer(embedded, ";");    // @todo resource ;
        while (st.hasMoreTokens())
        {
            String xmlFilename = st.nextToken();
            // Check if it's in our dir
            if (!(xmlFilename.startsWith(testDirectory.getName())))
            {
                reporter.logTraceMsg("embedded item " + xmlFilename + " not in dir " + testDirectory.getName());
                continue;
            }
            // Presumably the file is in our directory, so test it
            reporter.logTraceMsg("Now testing embedded item " + xmlFilename);

            // Construct XML filename, output filename, gold filename
            int dotIndex = xmlFilename.indexOf('.');
            if (dotIndex > 0)
            {
                // Construct the various inputs to the processor, based on embedded filename
                String rootFileName = xmlFilename.substring(0, dotIndex);
                // No need for stylesheet name!
                String absXMLName = null;
                try
                {
                    absXMLName = testDirectory.getCanonicalPath()
                                 + File.separatorChar + rootFileName
                                 + xmlExtension;
                }
                catch (IOException ioe2)
                {
                    absXMLName = testDirectory.getAbsolutePath()
                                 + File.separatorChar + rootFileName
                                 + xmlExtension;
                }
                if (outDirectory == null)
                {

                    // Provide some sort of default TODO is this the best cross-platform default?
                    outDirectory = new File("\badOutDir");
                    reporter.logErrorMsg(
                        "outDir(" + outDirectory
                        + ") is bad - resetting to \badOutDir");
                }

                String absOutName = null;
                try
                {
                    absOutName = outDirectory.getCanonicalPath()
                                 + File.separatorChar + rootFileName
                                 + outExtension;
                }
                catch (IOException ioe3)
                {
                    absOutName = outDirectory.getAbsolutePath()
                                 + File.separatorChar + rootFileName
                                 + outExtension;
                }

                if (goldDirectory == null)
                {

                    // Provide some sort of default
                    goldDirectory = new File("\badGoldDir");
                    reporter.logErrorMsg(
                        "goldDir(" + goldDirectory
                        + ") is bad - resetting to \badGoldDir");
                }
                String absGoldName = null;
                try
                {
                    absGoldName = goldDirectory.getCanonicalPath()
                                  + File.separatorChar + rootFileName
                                  + outExtension;
                }
                catch (IOException ioe3)
                {
                    absGoldName = goldDirectory.getAbsolutePath()
                                  + File.separatorChar + rootFileName
                                  + outExtension;
                }

                // Sanity check - see if file exists
                File xmlF = new File(absXMLName);

                if (xmlF.exists())
                {
                    reporter.logTraceMsg("About to test embedded:"
                                         + xmlF.getName() + " into:"
                                         + absOutName);
                }
                else
                {
                    reporter.logWarningMsg("Files may not exist:"
                                           + absXMLName);
                }

                // Actually run the xml file with embedded stylesheet through the processor
                switch (processEmbeddedFile(absXMLName, absOutName))
                {
                case PROCESS_OK :
                    if (!reporter.check(fileChecker, 
                                        new File(absOutName), 
                                        new File(absGoldName), 
                                        xmlF.getName() + " output comparison",
                                        xmlF.getName()))
                    {
                        // If we're using an appropriate fileChecker, 
                        //  get extra info on a fail, as well as file references to the test file

                        // Report extra info about why it failed
                        String tmp = fileChecker.getExtendedInfo();
                        if (tmp != null)
                            reporter.logArbitrary(reporter.INFOMSG, tmp);
                        else
                            reporter.logTraceMsg("getFileChecker().getExtendedInfo() not available");

                        // Log a custom element with all the file refs in it
                        // Closely related to viewResults.xsl select='fileref"
                        Hashtable attrs = new Hashtable();
                        attrs.put("idref", xmlF.getName());
                        attrs.put("xmlName", absXMLName);
                        attrs.put("outputName", absOutName);
                        attrs.put("goldName", absGoldName);
                        reporter.logElement(Logger.STATUSMSG, "fileref", attrs, "File references for Conformance Test");
                    }

                    break;
                // Currently can't have EXPECTED_EXCEPTION from embedded files
                case UNEXPECTED_EXCEPTION :
                    reporter.checkFail(xmlF.getName()
                                       + " got unexpected exception", xmlF.getName());
                    Hashtable attrs = new Hashtable();
                    attrs.put("idref", xmlF.getName());
                    attrs.put("xmlName", absXMLName);
                    attrs.put("outputName", absOutName);
                    attrs.put("goldName", absGoldName);
                    reporter.logElement(Logger.STATUSMSG, "fileref", attrs, "File references for Conformance Test");
                    break;
                
                default:
                    // This should never happen
                    reporter.logErrorMsg("Unexpected return value from processEmbeddedFile!");
                }
            }  // of if (dotIndex > 0)
            else
            {
                reporter.logWarningMsg("Problem with embedded filename: "
                                       + xmlFilename);
            }

        } // of while
    }


    /**
     * Run a list of specific files through the processor; virtually stateless.
     * <p>The file names are assumed to be fully specified, and we assume
     * the corresponding directories exist.</p>
     * <p>This method does not access the processorWrapper (or it's processor)
     * in any way - thus is suitable for use in subclasses with overriden
     * createNewProcessor() methods.</p>
     * @param testDirectory specific dir with xml/xsl file pairs to iterate through
     * @param outDirectory dir to put any output files in
     * @param goldDirectory dir to look for expected gold files in
     * @param vector of XSLTestfileInfo objects of (xslFile, xmlFile, outFile, goldFile, options)
     *
     * NEEDSDOC @param fileList
     */
    public void processFileList(Vector fileList)
    {

        // Reset performance stuff first, so we don't double-report if we fail to run any tests here
        dirTime = 0;
        dirFilesProcessed = 0;

        // String xslnames[] = dirs[0].list(filter);
        if (null == fileList)
        {

            // No files, no testing - report an error and return
            reporter.logErrorMsg("Blank fileList - nothing to test!");

            return;
        }

        // Now just go through the list and process each set
        int numXSLFiles = fileList.size();

        reporter.logInfoMsg("processFileList() with " + numXSLFiles
                            + " potential .xsl files to test");

        // Iterate over every xsl file found and check if we should test it
        for (int ctr = 0; ctr < numXSLFiles; ctr++)
        {

            // If the object is not an XSLTestfileInfo, assume it's a string to create one with
            XSLTestfileInfo fileSet = null;

            try
            {
                fileSet = (XSLTestfileInfo) fileList.elementAt(ctr);
            }
            catch (ClassCastException cce)
            {
                fileSet =
                    new XSLTestfileInfo((String) fileList.elementAt(ctr), "");

                // If this line fails, too bad!
            }

            // Sanity check - see if files exist (not strictly needed?)
            File xslF = new File(fileSet.inputName);
            File xmlF = new File(fileSet.xmlName);

            if (xslF.exists() && xmlF.exists())
            {
                reporter.logTraceMsg("About to test:" + fileSet.xmlName
                                     + " with:" + fileSet.inputName
                                     + " into:" + fileSet.outputName);
            }
            else
            {
                reporter.logWarningMsg("Files may not exist:"
                                       + fileSet.xmlName + " or:"
                                       + fileSet.inputName);
            }

            // Actually run the xsl/xml file pair through the processor
            // Note subclasses may override this to do different kinds of processing
            // FUTUREWORK: put this switch in a method, so subclasses can ovverride
            //       to also do different kinds of validation!
            switch (processSingleFile(fileSet.xmlName, fileSet.inputName,
                                      fileSet.outputName))
            {
            case PROCESS_OK :
                if (!reporter.check(fileChecker, 
                                    new File(fileSet.outputName), 
                                    new File(fileSet.goldName), 
                                    xslF.getName() + " output comparison",
                                    xslF.getName()))
                {
                    // If we're using an appropriate fileChecker, 
                    //  get extra info on a fail, as well as file references to the test file

                    // Report extra info about why it failed
                    String tmp = fileChecker.getExtendedInfo();
                    if (tmp != null)
                        reporter.logArbitrary(reporter.INFOMSG, tmp);
                    else
                        reporter.logTraceMsg("getFileChecker().getExtendedInfo() not available");

                    // Log a custom element with all the file refs in it
                    // Closely related to viewResults.xsl select='fileref"
                    Hashtable attrs = new Hashtable();
                    attrs.put("idref", xslF.getName());
                    attrs.put("inputName", fileSet.inputName);
                    attrs.put("xmlName", fileSet.xmlName);
                    attrs.put("outputName", fileSet.outputName);
                    attrs.put("goldName", fileSet.goldName);
                    reporter.logElement(Logger.STATUSMSG, "fileref", attrs, "File references for Conformance Test");
                }
                break;

            case GOT_EXPECTED_EXCEPTION :
                reporter.checkPass(xslF.getName()
                                   + " got expected exception", xslF.getName());
                break;

            case UNEXPECTED_EXCEPTION :
                reporter.checkFail(xslF.getName()
                                   + " got unexpected exception", xslF.getName());
                // Log a custom element with all the file refs in it
                // Closely related to viewResults.xsl select='fileref"
                Hashtable attrs = new Hashtable();
                attrs.put("idref", xslF.getName());
                attrs.put("inputName", fileSet.inputName);
                attrs.put("xmlName", fileSet.xmlName);
                attrs.put("outputName", fileSet.outputName);
                attrs.put("goldName", fileSet.goldName);
                reporter.logElement(Logger.STATUSMSG, "fileref", attrs, "File references for Conformance Test");
                break;

            default:
                // This should never happen
                reporter.logErrorMsg("Unexpected return value from processSingleFile!");
            }
        }  // of while...

        // update overall statistics
        overallTime += dirTime;
        overallFilesProcessed += dirFilesProcessed;
    }

    /**
     * Run one xsl/xml file pair through the processor.
     * <p>May be overriden by subclasses to do different kinds of processing -
     * mainly variations of the process(...) APIs.</p>
     * <p>XSLDirectoryIterator provides a default implementation that
     * is appropriate for use with Conformance tests.</p>
     * <p>processSingleFile should do all processing required to transform the XMLName,
     * via the XSLName, into an OutName file on disk.  It returns a status denoting
     * what happend during the processing.</p>
     * <p>Should increment dirTotalTime and dirFilesProcessed.</p>
     * <p>processSingleFile() and createNewProcessor() both rely on using the
     * same semantics to use the processor (or processorWrapper); thus if a
     * subclass overrides one, you should probably override the other.</p>
     * <p>Due to changes Jun-00 in Xerces 1.1.2; checks XLTest.useURI to see
     * if we can use unadulterated filenames or have to use URI's.</p>
     * @author Shane Curcuru
     * @todo update to use XSLTestfileInfo objects instead
     * @param XMLName path\filename of XML data file
     * @param XSLName path\filename of XSL Stylesheet file
     * @param OutName path\filename of desired output file
     * @return int status - pass, fail, expected exception or unexpected exception
     */
    public int processSingleFile(String XMLName, String XSLName,
                                 String OutName)
    {

        long fileTime = ProcessorWrapper.ERROR;

        try
        {

            // Reset the indent level each time, to ensure the process uses it (it may get reset() below)
            if (indentLevel > NO_INDENT)
            {
                reporter.logTraceMsg("processSingleFile() set indent "
                                     + indentLevel);
                processorW.setIndent(indentLevel);
            }

            // Force filerefs to be URI's if needed
            if (useURI)
            {

                // Use this static convenience method; returns a URL; convert to String via toExternalForm()
                XMLName = filenameToURL(XMLName);
                XSLName = filenameToURL(XSLName);

                // HACK (end)- replicate this code locally, since we may test Xalan2 which doesn't have this!
                // Note: Currently 28-Jun-00, the output of files is handled differently, so 
                //  we do NOT want to convert those.  Subject to change, however.
                reporter.logTraceMsg("processSingleFile() useURI: "
                                     + XSLName);
            }

            fileTime = processorW.processToFile(XMLName, XSLName, OutName);

            if (fileTime != ProcessorWrapper.ERROR)
            {
                dirTime += fileTime;

                dirFilesProcessed++;

                reporter.logTraceMsg("processSingleFile(" + XSLName
                                     + ") no exceptions; time " + fileTime);
            }
            else
            {

                // Do not increment performance counters if there's an error
                reporter.logWarningMsg("processSingleFile(" + XSLName
                                       + ") returned ERROR code!");
            }

            processorW.reset();
        }

        // Catch SAXExceptions and check if they're expected; restart to be safe
        catch (SAXException sax)
        {
            reporter.logStatusMsg("processSingleFile(" + XSLName
                                  + ") threw: " + sax.toString());

            int retVal = checkExpectedException(sax, XSLName, OutName);

            createNewProcessor();  // Should be configurable!

            return retVal;
        }

        // Catch general Exceptions, check if they're expected, and restart
        catch (Exception e)
        {
            reporter.logStatusMsg("processSingleFile(" + XSLName
                                  + ") threw: " + e.toString());

            int retVal = checkExpectedException(e, XSLName, OutName);

            createNewProcessor();

            return retVal;
        }

        // Catch any Throwable, check if they're expected, and restart
        catch (Throwable t)
        {
            reporter.logStatusMsg("processSingleFile(" + XSLName
                                  + ") threw: " + t.toString());

            int retVal = checkExpectedException(t, XSLName, OutName);

            createNewProcessor();

            return retVal;
        }

        return PROCESS_OK;
    }

    /**
     * Run an xml file with embedded stylesheet through the processor.
     * @param XMLName path\filename of XML data file with embedded xsl
     * @param OutName path\filename of desired output file
     * @return int status - pass, fail, or unexpected exception
     */
    public int processEmbeddedFile(String XMLName, String OutName)
    {
        long fileTime = ProcessorWrapper.ERROR;
        try
        {
            // Reset the indent level each time, to ensure the process uses it (it may get reset() below)
            if (indentLevel > NO_INDENT)
            {
                reporter.logTraceMsg("processEmbeddedFile() set indent "
                                     + indentLevel);
                processorW.setIndent(indentLevel);
            }

            // Force filerefs to be URI's if needed
            if (useURI)
            {
                // Use this static convenience method; returns a URL; convert to String via toExternalForm()
                XMLName = filenameToURL(XMLName);
            }
            fileTime = processorW.processEmbeddedToFile(XMLName, OutName);

            if (fileTime != ProcessorWrapper.ERROR)
            {
                dirTime += fileTime;
                dirFilesProcessed++;
                reporter.logTraceMsg("processEmbeddedFile(" + XMLName
                                     + ") no exceptions; time " + fileTime);
            }
            else
            {
                // Do not increment performance counters if there's an error
                reporter.logWarningMsg("processEmbeddedFile(" + XMLName
                                       + ") returned ERROR code!");
            }
            processorW.reset();
        }

        // Catch SAXExceptions and check if they're expected; restart to be safe
        catch (SAXException sax)
        {
            reporter.logStatusMsg("processEmbeddedFile(" + XMLName
                                  + ") threw: " + sax.toString());
            reporter.logThrowable(Logger.STATUSMSG, sax, 
                                  "processEmbeddedFile(" + XMLName + ") threw");
            createNewProcessor();  // Should be configurable!
            return UNEXPECTED_EXCEPTION;
        }

        // Catch general Exceptions, check if they're expected, and restart
        catch (Exception e)
        {
            reporter.logStatusMsg("processEmbeddedFile(" + XMLName
                                  + ") threw: " + e.toString());
            reporter.logThrowable(Logger.STATUSMSG, e, 
                                  "processEmbeddedFile(" + XMLName + ") threw");
            createNewProcessor();  // Should be configurable!
            return UNEXPECTED_EXCEPTION;
        }

        // Catch any Throwable, check if they're expected, and restart
        catch (Throwable t)
        {
            reporter.logStatusMsg("processEmbeddedFile(" + XMLName
                                  + ") threw: " + t.toString());
            reporter.logThrowable(Logger.STATUSMSG, t, 
                                  "processEmbeddedFile(" + XMLName + ") threw");
            createNewProcessor();  // Should be configurable!
            return UNEXPECTED_EXCEPTION;
        }

        return PROCESS_OK;
    }

    /**
     * Worker method to validate expected exception text.
     * <p>Logs message and creates new processor if useDefaultProcessor;
     * also performs basic validation of .toString() of exception.</p>
     * <p>Does not store any state; suitable for use in interface.</p>
     * @author Shane Curcuru
     * @todo update to use XSLTestfileInfo objects instead
     * @param expected exception that was caught above
     * @param filename of XSL test file where 'ExpectedException:' may be
     * @param filename of output file , optional for future use
     *
     * NEEDSDOC @param ee
     * NEEDSDOC @param testName
     * NEEDSDOC @param outName
     * @return status, as one of PROCESS_OK, GOT_EXPECTED_EXCEPTION, UNEXPECTED_EXCEPTION
     */
    protected int checkExpectedException(Throwable ee, String testName,
                                         String outName)
    {

        // Is this exception the one we expected? Assume not
        boolean gotExpected = false;

        // Read in the testName file to see if it's expecting something        
        try
        {
            FileReader fr = new FileReader(testName);
            BufferedReader br = new BufferedReader(fr);

            // NEEDSWORK: should we optimize and only check the first xx num lines?
            for (;;)
            {
                String inbuf = br.readLine();

                if (inbuf == null)
                    break;  // end of file, break out and return default (false)

                int idx = inbuf.indexOf(EXPECTED_EXCEPTION);

                if (idx <= 0)
                    continue;  // not on this line, keep going

                // The expected exception.getMessage is the rest of the line, trimmed
                //   Note that there's actually a " -->" at the end of the line probably,
                //   but we'll ignore that by using .startsWith()
                String expExc =
                    inbuf.substring((idx + EXPECTED_EXCEPTION.length()),
                                    inbuf.length()).trim();

                if (expExc.startsWith(ee.toString()))
                {
                    gotExpected = true;  // equal, they pass

                    break;
                }
                else
                {

                    // Not equal, so keep looking for another flavor
                    continue;
                }
            }  // end for (;;)
        }
        catch (java.io.IOException ioe)
        {
            reporter.logWarningMsg("checkExpectedException() threw: "
                                   + ioe.toString());

            gotExpected = false;
        }

        return (gotExpected ? GOT_EXPECTED_EXCEPTION : UNEXPECTED_EXCEPTION);
    }

    /**
     * Ask the wrapper to construct a processor, with optional diagnostic logs.
     * <p>this worker method is called from within processSingleFile as well as
     * various places within subclassed tests.</p>
     * <p>processSingleFile() and createNewProcessor() both rely on using the
     * same semantics to use the processor (or processorWrapper); thus if a
     * subclass overrides one, you should probably override the other.</p>
     * @return status - OK if successfull, false if some error occoured
     */
    protected boolean createNewProcessor()
    {

        if ((flavor == null) || (flavor.equals("")))
        {
            return false;
        }

        if (processorW == null)
        {

            // Create the specific subclass for desired flavor
            processorW = ProcessorWrapper.getWrapper(flavor);

            if (processorW == null)
            {

                // Something must have gone wrong with the wrapper
                reporter.logErrorMsg(
                    "createNewProcessor() failed with flavor: " + flavor);
            }
        }

        try
        {
            Object temp = processorW.createNewProcessor(liaison);

            if (temp == null)
            {
                reporter.logErrorMsg(
                    "createNewProcessor() got null for processor");
            }
        }
        catch (Exception e)
        {
            reporter.logErrorMsg("createNewProcessor() threw: "
                                 + e.toString());
            reporter.logThrowable(reporter.CRITICALMSG, e,
                                  "createNewProcessor() threw: ");

            return false;
        }

        if (diagnostics != null)
        {
            if (diagNameMgr == null)
            {

                // Create a new name manager, hardcode the .log extension just because
                diagNameMgr = new OutputNameManager(diagnostics, ".log");
            }

            try
            {
                processorW.setDiagnosticsOutput(
                    new PrintWriter(new FileWriter(diagNameMgr.nextName())));
                reporter.logInfoMsg("createNewProcessor() new diagnostics = "
                                    + diagNameMgr.currentName());
            }
            catch (IOException ioe)
            {
                reporter.logWarningMsg(
                    "createNewProcessor() could not create new diagnostics = "
                    + diagNameMgr.currentName());

                return false;
            }
        }

        // Apply an optional indent if asked; this needs to be documented!
        // @todo this name conflicts with Logger.OPT_INDENT: we should 
        //       consider changing this one (which isn't a great property 
        //       anyway with new serializer models)
        String idt = testProps.getProperty("indent", null);

        // Only attempt to apply the indent if we think there's really a value there
        if ((idt != null) && (!idt.equals("")))
        {
            int temp = NO_INDENT;

            try
            {
                temp = Integer.parseInt(idt);
            }
            catch (NumberFormatException numEx)
            {
                temp = NO_INDENT;  // Make sure we don't try to use an illegal value

                reporter.logWarningMsg(
                    "createNewProcessor() failed to set indent " + idt);
            }

            if (temp > NO_INDENT)
            {
                indentLevel = temp;

                processorW.setIndent(indentLevel);
                reporter.logTraceMsg("createNewProcessor() set indent "
                                     + idt);
            }
            else
            {
                reporter.logTraceMsg(
                    "createNewProcessor() did not set indent " + idt);
            }
        }
        else
        {
            reporter.logTraceMsg(
                "createNewProcessor() skipping blank/null indent, now "
                + indentLevel);
        }

        return true;
    }

    /**
     * Read in a line-oriented file specifying a list of files to test.
     * <p>File format is pretty simple:</p>
     * <ul>
     * <li># beginning a line is a comment</li>
     * <li># if <b>first</b> line in file has 'relative' anywhere, paths are
     * relative to testDir, outDir, etc.</li>
     * <li># (default is assuming all paths are absolute)</li>
     * <li># rest of lines are space delimited filenames and options</li>
     * <li>xslName xmlName outName goldName options options options...</li>
     * <li><b>Note:</b> see {@link XSLTestfileInfo XSLTestfileInfo} for
     * details on how the file lines are parsed!</li>
     * </ul>
     * <p>Most items are optional, but not having them may result in validation oddities</p>
     * @param String; name of the file
     *
     * NEEDSDOC @param fileName
     * @return Vector of XSLTestfileInfo objects, null if any error
     */
    public Vector readFileListFile(String fileName)
    {

        final String COMMENT_CHAR = "#";
        final String ABSOLUTE = "absolute";
        final String RELATIVE = "relative";
        final String UNSPECIFIED_FILENAME = "UNSPECIFIED_FILENAME";

        // Verify the file is there and we can open it
        File f = new File(fileName);

        if (!f.exists())
        {
            reporter.logErrorMsg("FileListFile: " + fileName
                                 + " does not exist!");

            return null;
        }

        FileReader fr = null;
        BufferedReader br = null;
        Vector vec = new Vector();
        String line = null;

        try
        {
            br = new BufferedReader(new FileReader(f));
            line = br.readLine();
        }
        catch (IOException ioe)
        {
            reporter.logErrorMsg("FileListFile: " + fileName + " threw: "
                                 + ioe.toString());

            return null;
        }

        // Read in the file line by line
        if (line == null)
        {
            reporter.logErrorMsg("FileListFile: " + fileName
                                 + " appears to be blank!");

            return null;
        }

        // Check if the first line is a comment *and* has 'relative'
        //  Note default is relative paths to testDir/testDir/outDir/goldDir
        boolean isRelative = false;

        if ((line.startsWith(COMMENT_CHAR)) && (line.indexOf(RELATIVE) > 0))
        {
            isRelative = true;
        }

        // Load each line into an XSLTestfileInfo object
        for (;;)
        {

            // Skip any lines beginning with # comment char or that are blank
            if ((!line.startsWith(COMMENT_CHAR)) && (line.length() > 0))
            {

                // Create a testInfo object and initialize it with the line's contents
                XSLTestfileInfo testInfo = new XSLTestfileInfo(line,
                                               UNSPECIFIED_FILENAME);

                // Avoid spurious passes when output & gold both not specified
                if (testInfo.goldName.equals(UNSPECIFIED_FILENAME))
                {
                    testInfo.goldName += "-gold";
                }

                // Add it to our vector
                vec.addElement(testInfo);
            }

            // Read next line and loop
            try
            {
                line = br.readLine();
            }
            catch (IOException ioe2)
            {

                // Just force us out of the loop; if we've already read part of the file, fine
                reporter.logWarningMsg("FileListFile: " + fileName
                                       + " threw: " + ioe2.toString());

                break;
            }

            if (line == null)
                break;
        }

        if (vec.size() == 0)
        {
            reporter.logErrorMsg("FileListFile: " + fileName
                                 + " did not have any non-comment lines!");

            return null;
        }

        // Now, munge the paths if asked to
        if (isRelative)
        {
            XSLTestfileInfo basePaths = new XSLTestfileInfo();

            // xml,xsl are in testDir, out is in outDir, gold is in goldDir
            StringTokenizer st = new StringTokenizer(inputDir + "\t"
                                                     + inputDir + "\t"
                                                     + outputDir + "\t"
                                                     + goldDir, "\t");

            basePaths.load(st, "undefined");
            mungeListPaths(basePaths, vec);
        }

        if (debug)
        {

            // Cheap-o debugging for this stuff
            for (int ctr = 0; ctr < vec.size(); ctr++)
            {
                reporter.logTraceMsg(
                    "vec[" + ctr + "] = "
                    + ((XSLTestfileInfo) vec.elementAt(ctr)).dump());
            }
        }

        reporter.logTraceMsg("readFileListFile(" + fileName + ") isRelative="
                             + isRelative + ", size=" + vec.size());

        return vec;
    }

    /**
     * Munge relative paths to start from testDir,testDir,outDir,goldDir.
     * <p>Changes caller's copy of the vector.  options field is left untouched</p>
     * @param XSLTestfileInfo base directories for each member
     * @param Vector of XSLTestfileInfos to munge
     *
     * NEEDSDOC @param base
     * NEEDSDOC @param vec
     */
    public void mungeListPaths(XSLTestfileInfo base, Vector vec)
    {

        // TODO!!!
        reporter.logCriticalMsg(
            "mungeListPaths NOT IMPLEMENTED! Only absolute ones are done");
    }

    /**
     * Validate our test, output, and gold dirs; return test dir.
     * <p>If any optional dir cannot be created, it's array entry will be null.</p>
     * @author Shane Curcuru
     * @param array of directories that must previously exist
     * @param array of optional directories; if they do not exist they'll be created
     *
     * NEEDSDOC @param requiredDirs
     * NEEDSDOC @param optionalDirs
     * @return array of file objects, null if any error
     */
    public File[] validateDirs(File[] requiredDirs, File[] optionalDirs)
    {

        if ((requiredDirs == null) || (optionalDirs == null))
        {
            return null;
        }

        File[] dirs = new File[(requiredDirs.length + optionalDirs.length)];
        int ctr = 0;

        try
        {
            for (int ir = 0; ir < requiredDirs.length; ir++)
            {
                reporter.logTraceMsg("validateDirs(" + requiredDirs[ir]
                                     + ") loop");

                if (!requiredDirs[ir].exists())
                {
                    if (!requiredDirs[ir].mkdirs())
                    {
                        reporter.logErrorMsg("validateDirs("
                                             + requiredDirs[ir]
                                             + ") does not exist!");

                        return null;
                    }
                    else
                    {
                        reporter.logTraceMsg("validateDirs("
                                             + requiredDirs[ir]
                                             + ") was created");
                    }
                }

                dirs[ctr] = requiredDirs[ir];

                ctr++;
            }

            for (int iopt = 0; iopt < optionalDirs.length; iopt++)
            {
                reporter.logTraceMsg("validateDirs(" + optionalDirs[iopt]
                                     + ") loop");

                if (!optionalDirs[iopt].exists())
                {
                    if (!optionalDirs[iopt].mkdirs())
                    {
                        reporter.logWarningMsg("validateDirs("
                                               + optionalDirs[iopt]
                                               + ") could not be created");

                        dirs[ctr] = null;
                    }
                    else
                    {
                        reporter.logTraceMsg("validateDirs("
                                             + optionalDirs[iopt]
                                             + ") was created");

                        dirs[ctr] = optionalDirs[iopt];
                    }
                }
                else
                {  // It does previously exist, so copy it over
                    dirs[ctr] = optionalDirs[iopt];
                }

                ctr++;
            }
        }
        catch (Exception e)
        {
            reporter.logErrorMsg("validateDirs threw: " + e.toString());
            e.printStackTrace();  // NEEDSWORK

            return null;
        }

        return (dirs);
    }

    /**
     * Attempt to find a testDir if provided one doesn't exist.
     * Presumably if we have a known directory structure, we can
     * implement a simple search that will look for the
     * 'conformancetest' directory, whether it's below or above
     * us by one directory level.  For the terminally lazy who
     * don't bother specifying inputDir.
     * @return status - OK if we found it, false otherwise
     */
    public boolean lookForTestDir()
    {

        // Not implemented
        return false;
    }

}  // end of class XSLDirectoryIterator

