/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
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
 * SystemIdImpInclTest.java
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Test behavior of imports/includes with various setSystemId sources.  
 * <b>Note:</b> This test is directory-dependent, so if there are 
 * any fails, check the code to see what the test file is expecting 
 * the path/directory/etc. to be.
 *
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class SystemIdImpInclTest extends XSLProcessorTestBase
{

    /**
     * Provides nextName(), currentName() functionality for tests 
     * that may produce any number of output files.
     */
    protected OutputNameManager outNames;

    /** 
     * Name of a valid, known-good xsl/xml file pair we can use.
     */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();

    /** 
     * Just basename of a valid, known-good file, both .xsl/.xml .
     */
    protected String knownGoodBaseName = null;

    /** Gold filename for level0, i.e. one directory above the testfile.  */
    protected String goldFileLevel0 = "SystemIdImpInclLevel0.out";

    /** Gold filename for level1, i.e. the directory of the testfile.  */
    protected String goldFileLevel1 = "SystemIdImpInclLevel1.out";

    /** Gold filename for level2, i.e. a directory below the testfile.  */
    protected String goldFileLevel2 = "SystemIdImpInclLevel2.out";

    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String TRAX_SUBDIR = "trax";

    /** Convenience variable for user.dir - cached during test.  */
    protected String savedUserDir = null;

    /** Just initialize test name, comment, numTestCases. */
    public SystemIdImpInclTest()
    {
        numTestCases = 2;  // REPLACE_num
        testName = "SystemIdImpInclTest";
        testComment = "Test behavior of imports/includes with various setSystemId sources";
    }


    /**
     * Initialize this test - Set names of xml/xsl test files,
     * cache user.dir property.  
     *
     * @param p Properties to initialize from (unused)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
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

        // Just bare pathnames, not URI's
        knownGoodBaseName = "SystemIdImpIncl";
        testFileInfo.inputName = testBasePath + knownGoodBaseName + ".xsl";
        testFileInfo.xmlName = testBasePath + knownGoodBaseName + ".xml";
        testFileInfo.goldName = goldBasePath + knownGoodBaseName + ".out";
        goldFileLevel0 = goldBasePath + goldFileLevel0; // just prepend path
        goldFileLevel1 = goldBasePath + goldFileLevel1; // just prepend path
        goldFileLevel2 = goldBasePath + goldFileLevel2; // just prepend path

        // Cache user.dir property
        savedUserDir = System.getProperty("user.dir");
        reporter.logHashtable(Logger.STATUSMSG, System.getProperties(), "System.getProperties()");
        reporter.logHashtable(Logger.STATUSMSG, testProps, "testProps");

        return true;
    }


    /**
     * Cleanup this test - uncache user.dir property.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileClose(Properties p)
    {
        // Uncache user.dir property
        System.getProperties().put("user.dir", savedUserDir);
        return true;
    }


    /**
     * Simple StreamSources with different setSystemIds.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Simple StreamSources with different setSystemIds");

        TransformerFactory factory = null;
        try
        {
            factory = TransformerFactory.newInstance();
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem creating factory; can't continue testcase");
            reporter.logThrowable(reporter.ERRORMSG, t,
                                  "Problem creating factory; can't continue testcase");
            return true;
        }
        try
        {
            // Verify we can do basic transforms with readers/streams,
            //  with the 'normal' systemId
            reporter.logInfoMsg("StreamSource.setSystemId(level1)");
            InputStream xslStream1 = new FileInputStream(testFileInfo.inputName);
            Source xslSource1 = new StreamSource(xslStream1);
            xslSource1.setSystemId(filenameToURL(testFileInfo.inputName));
            
            InputStream xmlStream1 = new FileInputStream(testFileInfo.xmlName);
            Source xmlSource1 = new StreamSource(xmlStream1);
            xmlSource1.setSystemId(filenameToURL(testFileInfo.xmlName));

            reporter.logTraceMsg("Create FileOutputStream to " + outNames.nextName());
            FileOutputStream fos1 = new FileOutputStream(outNames.currentName());
            Result result1 = new StreamResult(fos1);

            Templates templates1 = factory.newTemplates(xslSource1);
            Transformer transformer1 = templates1.newTransformer();
            reporter.logInfoMsg("About to transform, systemId(level1)");
            transformer1.transform(xmlSource1, result1);
            int result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(goldFileLevel1), 
                              "transform after setSystemId(level1) into " + outNames.currentName());
            if (result == reporter.FAIL_RESULT)
                reporter.logInfoMsg("transform after setSystemId(level1)... failure reason:" + fileChecker.getExtendedInfo());
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with setSystemId(level1)");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with after setSystemId(level1)");
        }

        try
        {
            // Verify we can do basic transforms with readers/streams,
            //  systemId set up one level
            reporter.logInfoMsg("StreamSource.setSystemId(level0)");
            InputStream xslStream1 = new FileInputStream(testFileInfo.inputName);
            Source xslSource1 = new StreamSource(xslStream1);
            xslSource1.setSystemId(filenameToURL(inputDir + File.separator + knownGoodBaseName + ".xsl"));
            
            InputStream xmlStream1 = new FileInputStream(testFileInfo.xmlName);
            Source xmlSource1 = new StreamSource(xmlStream1);
            xmlSource1.setSystemId(filenameToURL(inputDir + File.separator + knownGoodBaseName + ".xml"));

            reporter.logTraceMsg("Create FileOutputStream to " + outNames.nextName());
            FileOutputStream fos1 = new FileOutputStream(outNames.currentName());
            Result result1 = new StreamResult(fos1);

            Templates templates1 = factory.newTemplates(xslSource1);
            Transformer transformer1 = templates1.newTransformer();
            reporter.logInfoMsg("About to transform, systemId(level0)");
            transformer1.transform(xmlSource1, result1);
            int result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(goldFileLevel0), 
                              "transform after setSystemId(level0) into " + outNames.currentName());
            if (result == reporter.FAIL_RESULT)
                reporter.logInfoMsg("transform after setSystemId(level0)... failure reason:" + fileChecker.getExtendedInfo());
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with setSystemId(level0)");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with setSystemId(level0)");
        }

        try
        {
            // Verify we can do basic transforms with readers/streams,
            //  with the systemId down one level
            reporter.logInfoMsg("StreamSource.setSystemId(level2)");
            InputStream xslStream1 = new FileInputStream(testFileInfo.inputName);
            Source xslSource1 = new StreamSource(xslStream1);
            xslSource1.setSystemId(filenameToURL(inputDir + "/trax/systemid/" + knownGoodBaseName + ".xsl"));
            
            InputStream xmlStream1 = new FileInputStream(testFileInfo.xmlName);
            Source xmlSource1 = new StreamSource(xmlStream1);
            xmlSource1.setSystemId(filenameToURL(inputDir + "/trax/systemid/" + knownGoodBaseName + ".xml"));

            reporter.logTraceMsg("Create FileOutputStream to " + outNames.nextName());
            FileOutputStream fos1 = new FileOutputStream(outNames.currentName());
            Result result1 = new StreamResult(fos1);

            Templates templates1 = factory.newTemplates(xslSource1);
            Transformer transformer1 = templates1.newTransformer();
            reporter.logInfoMsg("About to transform, systemId(level2)");
            transformer1.transform(xmlSource1, result1);
            int result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(goldFileLevel2), 
                              "transform after setSystemId(level2) into " + outNames.currentName());
            if (result == reporter.FAIL_RESULT)
                reporter.logInfoMsg("transform after setSystemId(level2)... failure reason:" + fileChecker.getExtendedInfo());
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with setSystemId(level2)");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with setSystemId(level2)");
        }

        try
        {
            // Verify we can do basic transforms with readers/streams,
            //  with the systemId down one level
            reporter.logInfoMsg("StreamSource.setSystemId(xslonly level2)");
            InputStream xslStream1 = new FileInputStream(testFileInfo.inputName);
            Source xslSource1 = new StreamSource(xslStream1);
            xslSource1.setSystemId(filenameToURL(inputDir + "/trax/systemid/" + knownGoodBaseName + ".xsl"));
            
            InputStream xmlStream1 = new FileInputStream(testFileInfo.xmlName);
            Source xmlSource1 = new StreamSource(xmlStream1);
            // Explicitly don't set the xmlId - shouldn't be needed

            reporter.logTraceMsg("Create FileOutputStream to " + outNames.nextName());
            FileOutputStream fos1 = new FileOutputStream(outNames.currentName());
            Result result1 = new StreamResult(fos1);

            Templates templates1 = factory.newTemplates(xslSource1);
            Transformer transformer1 = templates1.newTransformer();
            reporter.logInfoMsg("About to transform, systemId(xslonly level2)");
            transformer1.transform(xmlSource1, result1);
            int result = fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(goldFileLevel2), 
                              "transform after setSystemId(xslonly level2) into " + outNames.currentName());
            if (result == reporter.FAIL_RESULT)
                reporter.logInfoMsg("transform after setSystemId(xslonly level2)... failure reason:" + fileChecker.getExtendedInfo());
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with setSystemId(xslonly level2)");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with setSystemId(xslonly level2)");
        }
        reporter.testCaseClose();
        return true;
    }

    /**
     * Test setting various forms of systemIds to see what happens.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Test setting various forms of systemId to see what happens");

        // This will have imports/includes on various levels, and then 
        //  set the systemId of a Source to hopefully pull in the 
        //  different imports/includes
        reporter.checkPass("//@todo implement this testcase");

        reporter.testCaseClose();
        return true;
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by SystemIdImpInclTest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + "(Note: test is directory-dependent!)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        SystemIdImpInclTest app = new SystemIdImpInclTest();
        app.doMain(args);
    }
}
