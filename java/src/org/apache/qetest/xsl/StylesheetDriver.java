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

package org.apache.qetest.xsl;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

import org.apache.qetest.Logger;
import org.apache.qetest.xslwrapper.TransformWrapperFactory;

/**
 * Test driver for running a single XSLT test.
 * 
 * This is a simplification of the generic StylesheetTestletDriver 
 * that simply executes a single stylesheet test using minimal 
 * parameters but still supporting all the normal testing options.
 *
 * @author shane_curcuru@us.ibm.com
 * @version $Id$
 */
public class StylesheetDriver extends StylesheetTestletDriver
{
    /** Just initialize test name, comment; numTestCases is not used. */
    public StylesheetDriver()
    {
        testName = "StylesheetDriver";
        testComment = "Test driver for single XSLT stylesheets";
    }


    /**
     * Override default processing to simply create a fileList 
     * based on the single input file desired.
     *
     * @param p Properties block of options to use - unused
     * @return true if OK, false if we should abort
     */
    public boolean runTestCases(Properties p)
    {
        // First log out any other runtime information, like the 
        //  actual flavor of ProcessorWrapper, etc.
        try
        {
            // Just grab all the info from the TransformWrapper...
            Properties runtimeProps = TransformWrapperFactory.newWrapper(flavor).getProcessorInfo();
            // ... and add a few extra things ourselves
            runtimeProps.put("actual.testlet", getTestlet());
            runtimeProps.put("actual.dirFilter", getDirFilter());
            runtimeProps.put("actual.fileFilter", getFileFilter());
            reporter.logHashtable(Logger.CRITICALMSG, runtimeProps, 
                                  "actual.runtime information");
        }
        catch (Exception e)
        {
            reporter.logThrowable(Logger.WARNINGMSG, e, "Logging actual.runtime threw");
        }

        // Get the baseName of the test: axes01, boolean34, etc.
        String testBaseName = testProps.getProperty("test", "processorinfo01"); // HACK some default test

        // Create the datalet to run for this test
        Vector datalets = buildDatalet(testBaseName);

        // Actually process the specified file(s) in a testCase as normal
        processFileList(datalets, "test1:" + testBaseName);
        return true;
    }


    /**
     * Create datalet(s) from a single user-supplied test name 
     * in the format of 'axes44' or 'string132'.
     *
     * @param baseName of just one stylesheet to test
     * @return Vector of local path\filenames of tests to run;
     * the tests themselves will exist; null if error
     */
    public Vector buildDatalet(String baseName)
    {
        // Calculate directory name; pattern assumed to be 
        //  [chars]N... where N... is numeric
        String subdirName = baseName.substring(0, (baseName.length() - 2)); // HACK must actually calculate name, not assume 2 digits
        reporter.logTraceMsg("buildDatalet:" + baseName + " baseName " + subdirName);

        File subTestDir = new File(new File(inputDir), subdirName);
        File subOutDir = new File(outputDir, subdirName);
        File subGoldDir = new File(goldDir, subdirName);
        // Validate that each of the specified dirs exists
        // Returns directory references like so:
        //  testDirectory = 0, outDirectory = 1, goldDirectory = 2
        File[] dirs = validateDirs(new File[] { subTestDir }, 
                                   new File[] { subOutDir, subGoldDir });

        String testFilename = baseName + XSL_EXTENSION; // HACK should allow for xml embedded as well
        File testFile = new File(subTestDir.getPath() + File.separator + testFilename);
        if (!testFile.exists())
        {
            reporter.checkErr("test1: file does not exist: " + testFilename);
            return null;
        }
        // Go and construct the datalet with all info
        Vector v = new Vector(1);
        // Check if it's a normal .xsl file, or a .xml file
        //  (we assume .xml files are embedded tests!)
        StylesheetDatalet d = new StylesheetDatalet();
        if (testFilename.endsWith(XML_EXTENSION))
        {
            d.xmlName = subTestDir.getPath() + File.separator + testFilename;

            String fileNameRoot = testFilename.substring(0, testFilename.indexOf(XML_EXTENSION));
            d.inputName = null;
            d.outputName = subOutDir.getPath() + File.separator + fileNameRoot + OUT_EXTENSION;
            d.goldName = subGoldDir.getPath() + File.separator + fileNameRoot + OUT_EXTENSION;
        }
        else if (testFilename.endsWith(XSL_EXTENSION))
        {
            d.inputName = subTestDir.getPath() + File.separator + testFilename;

            String fileNameRoot = testFilename.substring(0, testFilename.indexOf(XSL_EXTENSION));
            d.xmlName = subTestDir.getPath() + File.separator + fileNameRoot + XML_EXTENSION;
            d.outputName = subOutDir.getPath() + File.separator + fileNameRoot + OUT_EXTENSION;
            d.goldName = subGoldDir.getPath() + File.separator + fileNameRoot + OUT_EXTENSION;
        }
        else
        {
            // Hmmm - I'm not sure what we should do here
            reporter.logWarningMsg("Unexpected test file found, skipping: " + testFilename);
        }
        d.setDescription(testFilename);
        d.flavor = flavor;
        // Also copy over our own testProps as it's 
        //  options: this allows for future expansion
        //  of values in the datalet
        d.options = new Properties(testProps);
        // Optimization: put in a copy of our fileChecker, so 
        //  that each testlet doesn't have to create it's own
        //  fileCheckers should not store state, so this 
        //  shouldn't affect the testing at all
        d.options.put("fileCheckerImpl", fileChecker);
        v.addElement(d);

        return v;
    }



    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by StylesheetDriver:\n"
                + "    -" + "test"
                + "  <baseName of tests to run - axes01 >\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        StylesheetDriver app = new StylesheetDriver();
        app.doMain(args);
    }
}
