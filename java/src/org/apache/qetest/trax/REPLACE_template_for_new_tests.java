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
 * REPLACE_template_for_new_tests.java
 *
 */
package org.apache.qetest.trax;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

// Needed SAX, DOM, JAXP classes

// java classes
import java.io.File;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * REPLACE_overall_test_comment.
 * INSTRUCTIONS:
 * - save as YourNewFilename,java
 * - search-and-replace 'REPLACE_template_for_new_tests' with 'YourNewFilename'
 * - search-and-replace all 'REPLACE_*' strings as appropriate
 * - remove this block of comments and replace with your javadoc
 * - implement sequentially numbered testCase1, testCase2, ... for 
 *   each API or group or related API's
 * - update the line: numTestCases = 2;  // REPLACE_num
 * - execute 'build package.trax', 'traxapitest REPLACE_template_for_new_tests'
 * - a bunch of convenience variables/initializers are included, 
 *   use or delete as is useful
 * @author REPLACE_authorname
 * @version $Id$
 */
public class REPLACE_template_for_new_tests extends FileBasedTest
{

    /**
     * Provides nextName(), currentName() functionality for tests 
     * that may produce any number of output files.
     */
    protected OutputNameManager outNames;

    /** 
     * Information about an xsl/xml file pair for transforming.  
     * Public members include inputName (for xsl); xmlName; goldName; etc.
     */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();

    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String TRAX_SUBDIR = "trax";


    /** Just initialize test name, comment, numTestCases. */
    public REPLACE_template_for_new_tests()
    {
        numTestCases = 1;  // REPLACE_num
        testName = "REPLACE_template_for_new_tests";
        testComment = "REPLACE_overall_test_comment";
    }


    /**
     * Initialize this test - Set names of xml/xsl test files,
     * REPLACE_other_test_file_init.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        // NOTE: 'reporter' variable is already initialized at this point

        // Used for all tests; just dump files in trax subdir
        File outSubDir = new File(outputDir + File.separator + TRAX_SUBDIR);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + TRAX_SUBDIR
                                         + File.separator + testName, ".out");

        String testBasePath = inputDir 
                              + File.separator 
                              + TRAX_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator 
                              + TRAX_SUBDIR
                              + File.separator;

        testFileInfo.inputName = testBasePath + "REPLACE_xslxml_filename.xsl";
        testFileInfo.xmlName = testBasePath + "REPLACE_xslxml_filename.xml";
        testFileInfo.goldName = goldBasePath + "REPLACE_xslxml_filename.out";

        return true;
    }


    /**
     * Cleanup this test - REPLACE_other_test_file_cleanup.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileClose(Properties p)
    {
        // Often will be a no-op
        return true;
    }


    /**
     * REPLACE_brief_description_testCase1.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("REPLACE_brief_description_testCase1");

        // REPLACE_init_data_for_this_independent_test_case
        
        // REPLACE_call_trax_apis_here_to_use_product
        reporter.logInfoMsg("Use reporter.log*Msg() to report information about what the test is doing, as needed");
        // REPLACE_call_trax_apis_here_to_verify_test_point
        reporter.check(true, true, "use reporter.check(actual, gold, description) to report test point results");
        reporter.logTraceMsg("Multiple test points per test case are fine; results are summed automatically");

        // REPLACE_cleanup_data_for_this_independent_test_case

        reporter.testCaseClose();
        return true;
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by REPLACE_template_for_new_tests:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + "REPLACE_any_new_test_arguments\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {

        REPLACE_template_for_new_tests app = new REPLACE_template_for_new_tests();

        app.doMain(args);
    }
}
