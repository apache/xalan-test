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
 * OutputSettingsTest.java
 *
 */
package org.apache.qetest.xalanj2;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

// Needed SAX, DOM, JAXP classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Node;
import org.w3c.dom.Document;

// java classes
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Verify xalan:-specific output properties.
 * This test is similar to trax.OutputPropertiesTest but tests 
 * some Xalan-J 2.2.x+ specific features for the xalan: namespace, 
 * like: indent-amount, content-handler, entities,
 * use-url-escaping, and omit-meta-tag.
 * 
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class OutputSettingsTest extends FileBasedTest
{

    /** Used for generating names of actual output files.   */
    protected OutputNameManager outNames;

    /** Default OutputSettingsTest.xml/xsl file pair.   */
    protected XSLTestfileInfo xmlFileInfo = new XSLTestfileInfo();

    /** OutputEntities.xml/xsl/ent file pair.   */
    protected XSLTestfileInfo entFileInfo = new XSLTestfileInfo();

    /** Just initialize test name, comment, numTestCases. */
    public OutputSettingsTest()
    {
        numTestCases = 2;  // REPLACE_num
        testName = "OutputSettingsTest";
        testComment = "Verify xalan:-specific output properties";
    }


    /**
     * Initialize this test - Set names of xml/xsl test files, etc.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        // Used for all tests; just dump files in trax subdir
        File outSubDir = new File(outputDir + File.separator + "xalanj2");
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Possible problem creating output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + "xalanj2"
                                         + File.separator + testName, ".out");

        String testBasePath = inputDir 
                              + File.separator 
                              + "xalanj2"
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator 
                              + "xalanj2"
                              + File.separator;

        xmlFileInfo.inputName = testBasePath + "OutputSettingsXML.xsl";
        xmlFileInfo.xmlName = testBasePath + "OutputSettingsXML.xml";
        // Only root of the output gold name
        xmlFileInfo.goldName = goldBasePath + "OutputSettingsXML";

        // xsl file references OutputEntities.ent
        entFileInfo.inputName = testBasePath + "OutputEntities.xsl";
        entFileInfo.xmlName = testBasePath + "identity.xml";
        entFileInfo.goldName = goldBasePath + "OutputEntities.out";
        return true;
    }


    /**
     * Verify xalan:entities output property.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Verify xalan:entities output property");
        TransformerFactory factory = null;
        Templates templates = null;

        try
        {
            // Process stylesheet with replaced entities
            factory = TransformerFactory.newInstance();
            reporter.logInfoMsg("entFileInfo newTemplates(" + QetestUtils.filenameToURL(entFileInfo.inputName) + ")");
            templates = factory.newTemplates(new StreamSource(QetestUtils.filenameToURL(entFileInfo.inputName)));
            reporter.logHashtable(Logger.STATUSMSG, 
                                  templates.getOutputProperties(), "entFileInfo templates output properties");

            // Process the file once with default properties
            Transformer transformer = templates.newTransformer();
            Result result = new StreamResult(outNames.nextName());
            reporter.logInfoMsg("(1)replaced entities transform(" + QetestUtils.filenameToURL(entFileInfo.xmlName) 
                                + ", " + outNames.currentName() + ")");
            transformer.transform(new StreamSource(QetestUtils.filenameToURL(entFileInfo.xmlName)), result);
            // Validate the default transform to base gold file
            fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(entFileInfo.goldName), 
                              "(1)replaced entities transform into: " + outNames.currentName()
                              + " gold: " + entFileInfo.goldName);

        }
        catch (Throwable t)
        {
            reporter.checkErr("(1)replaced entities threw:" + t.toString());
            reporter.logThrowable(reporter.ERRORMSG, t, "(1)replaced entities threw ");
            return true;
        }
        reporter.testCaseClose();
        return true;
    }

    /**
     * Verify xalan:indent-amount output property.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Verify xalan:indent-amount output property");
        TransformerFactory factory = null;
        Templates templates = null;
        try
        {
            // Process simple XML output stylesheet
            factory = TransformerFactory.newInstance();
            reporter.logInfoMsg("xmlFileInfo newTemplates(" + QetestUtils.filenameToURL(xmlFileInfo.inputName) + ")");
            templates = factory.newTemplates(new StreamSource(QetestUtils.filenameToURL(xmlFileInfo.inputName)));
            reporter.logHashtable(Logger.STATUSMSG, 
                                  templates.getOutputProperties(), "xmlFileInfo templates output properties");

            // Process the file once with default properties
            Transformer transformer = templates.newTransformer();
            Result result = new StreamResult(outNames.nextName());
            reporter.logInfoMsg("(2)xml transform(" + QetestUtils.filenameToURL(xmlFileInfo.xmlName) 
                                + ", " + outNames.currentName() + ")");
            transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlFileInfo.xmlName)), result);
            // Validate the default transform to base gold file
            fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(xmlFileInfo.goldName + ".out"), 
                              "(2)xml transform into: " + outNames.currentName()
                              + " gold: " + xmlFileInfo.goldName + ".out");

            // Set Xalan-specific output property 
            reporter.logInfoMsg("setOutputProperty({http://xml.apache.org/xslt}indent-amount, 2)");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            result = new StreamResult(outNames.nextName());
            reporter.logInfoMsg("(2)xml-2 transform(" + QetestUtils.filenameToURL(xmlFileInfo.xmlName) 
                                + ", " + outNames.currentName() + ")");
            transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlFileInfo.xmlName)), result);
            // Validate the default transform to base gold file
            fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(xmlFileInfo.goldName + "-2.out"), 
                              "(2)xml-2 transform into: " + outNames.currentName()
                              + " gold: " + xmlFileInfo.goldName + "-2.out");
            reporter.logHashtable(Logger.STATUSMSG, 
                                  transformer.getOutputProperties(), "xml-2 transformer output properties");

            // Set Xalan-specific output property 
            reporter.logInfoMsg("setOutputProperty({http://xml.apache.org/xslt}indent-amount, 12)");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "12");

            result = new StreamResult(outNames.nextName());
            reporter.logInfoMsg("(2)xml-12 transform(" + QetestUtils.filenameToURL(xmlFileInfo.xmlName) 
                                + ", " + outNames.currentName() + ")");
            transformer.transform(new StreamSource(QetestUtils.filenameToURL(xmlFileInfo.xmlName)), result);
            // Validate the default transform to base gold file
            fileChecker.check(reporter, 
                              new File(outNames.currentName()), 
                              new File(xmlFileInfo.goldName + "-12.out"), 
                              "(2)xml-12 transform into: " + outNames.currentName()
                              + " gold: " + xmlFileInfo.goldName + "-12.out");
            reporter.logHashtable(Logger.STATUSMSG, 
                                  transformer.getOutputProperties(), "xml-12 transformer output properties");
        }
        catch (Throwable t)
        {
            reporter.checkErr("(2)xml stylesheet threw:" + t.toString());
            reporter.logThrowable(reporter.ERRORMSG, t, "(2)xml stylesheet threw ");
            return true;
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
        return ("Common [optional] options supported by OutputSettingsTest:\n"
                + "(Note: assumes inputDir=tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }

    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        OutputSettingsTest app = new OutputSettingsTest();
        app.doMain(args);
    }
}
