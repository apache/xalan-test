/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights 
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
 * SerializedStylesheetTest.java
 *
 */
package org.apache.qetest.xalanj2;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

// java classes
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Basic functional test of serialized Templates objects.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class SerializedStylesheetTest extends FileBasedTest
{

    /** Provides nextName(), currentName() functionality.  */
    protected OutputNameManager outNames;

    /** Simple identity.xml/xsl file pair.  */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();

    /** Complex minitest.xml/xsl file pair.  */
    protected XSLTestfileInfo minitestFileInfo = new XSLTestfileInfo();

    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String TRAX_SUBDIR = File.separator + "trax" + File.separator;

    /** Just initialize test name, comment, numTestCases. */
    public SerializedStylesheetTest()
    {
        numTestCases = 1;  // REPLACE_num
        testName = "SerializedStylesheetTest";
        testComment = "Basic functional test of serialized Templates objects";
    }


    /**
     * Initialize this test - Set names of xml/xsl test files.
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        // Used for all tests; just dump files in trax subdir
        File outSubDir = new File(outputDir + TRAX_SUBDIR);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + TRAX_SUBDIR
                                         + testName, ".out");

        testFileInfo.inputName = QetestUtils.filenameToURL(inputDir 
                              + TRAX_SUBDIR + "identity.xsl");
        testFileInfo.xmlName = QetestUtils.filenameToURL(inputDir
                              + TRAX_SUBDIR + "identity.xml");
        testFileInfo.goldName = goldDir + TRAX_SUBDIR + "identity.out";

        minitestFileInfo.inputName = QetestUtils.filenameToURL(inputDir 
                              + File.separator + "Minitest.xsl");
        minitestFileInfo.xmlName = QetestUtils.filenameToURL(inputDir
                              + File.separator + "Minitest.xml");
        minitestFileInfo.goldName = goldDir + File.separator + "Minitest-xalanj2.out";

        return true;
    }


    /**
     * Basic functional test of serialized Templates objects.
     * Reproduce Bugzilla 2005 by rob.stanley@geac.com
     * http://nagoya.apache.org/bugzilla/show_bug.cgi?id=2005
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Basic functional test of serialized Templates objects");

        try
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            // Create templates from normal stylesheet
            Templates origTemplates = factory.newTemplates(new StreamSource(testFileInfo.inputName));

            // Serialize the Templates to disk
            reporter.logInfoMsg("About to serialize " + testFileInfo.inputName + " templates to: " + outNames.nextName());
            FileOutputStream ostream = new FileOutputStream(outNames.currentName());
            ObjectOutputStream oos = new java.io.ObjectOutputStream(ostream);
            oos.writeObject(origTemplates);
            oos.flush();
            ostream.close();            
            
            // Read the Templates back in
            reporter.logInfoMsg("About to read templates back");
            FileInputStream istream = new FileInputStream(outNames.currentName());
            ObjectInputStream ois = new ObjectInputStream(istream);
            Templates templates = (Templates)ois.readObject();
            istream.close();
 
            // Use the Templates in a transform
            reporter.logInfoMsg("About to call newTransformer");
            Transformer transformer = templates.newTransformer();
            reporter.logInfoMsg("About to transform(xmlDoc, StreamResult(" + outNames.nextName() + "))");
            transformer.transform(new StreamSource(testFileInfo.xmlName), new StreamResult(outNames.currentName()));
            fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(testFileInfo.goldName), 
                              "Using serialized Templates, transform into " + outNames.currentName());
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with serialized Template");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with serialized Template");
        }

        try
        {
            reporter.logTraceMsg("Try again, using the Minitest (with imports/includes/etc.)");
            TransformerFactory factory = TransformerFactory.newInstance();
            // Create templates from normal stylesheet
            Templates origTemplates = factory.newTemplates(new StreamSource(minitestFileInfo.inputName));

            // Serialize the Templates to disk
            reporter.logInfoMsg("About to serialize " + minitestFileInfo.inputName + " templates to: " + outNames.nextName());
            FileOutputStream ostream = new FileOutputStream(outNames.currentName());
            ObjectOutputStream oos = new java.io.ObjectOutputStream(ostream);
            oos.writeObject(origTemplates);
            oos.flush();
            ostream.close();            
            
            // Read the Templates back in
            reporter.logInfoMsg("About to read templates back");
            FileInputStream istream = new FileInputStream(outNames.currentName());
            ObjectInputStream ois = new ObjectInputStream(istream);
            Templates templates = (Templates)ois.readObject();
            istream.close();
 
            // Use the Templates in a transform
            reporter.logInfoMsg("About to call newTransformer");
            Transformer transformer = templates.newTransformer();
            reporter.logInfoMsg("About to transform(xmlDoc, StreamResult(" + outNames.nextName() + "))");
            transformer.transform(new StreamSource(minitestFileInfo.xmlName), new StreamResult(outNames.currentName()));
            if (Logger.PASS_RESULT != 
                fileChecker.check(reporter, 
                        new File(outNames.currentName()), 
                        new File(minitestFileInfo.goldName), 
                        "Using serialized Templates, transform into " + outNames.currentName())
               )
                reporter.logStatusMsg("Using serialized Templates: failure reason:" + fileChecker.getExtendedInfo());
        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with serialized Minitest Template");
            reporter.logThrowable(reporter.ERRORMSG, t, "Problem with serialized Minitest Template");
        }
        reporter.testCaseClose();
        return true;
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by SerializedStylesheetTest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        SerializedStylesheetTest app = new SerializedStylesheetTest();
        app.doMain(args);
    }
}
