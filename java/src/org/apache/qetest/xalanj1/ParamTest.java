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
 * ParamTest.java
 *
 */
package org.apache.qetest.xalanj1;

import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Properties;

// Specific imports for testing Xalan
import org.xml.sax.SAXException;

import org.apache.xalan.xslt.XSLTProcessor;
import org.apache.xalan.xslt.XSLTProcessorFactory;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTResultTarget;

//-------------------------------------------------------------------------

/**
 * Testing setStylesheetParam with XSL files.
 */
public class ParamTest extends XSLProcessorTestBase
{

    /** Our version of the processor. */
    protected org.apache.xalan.xslt.XSLTProcessor processor;

    /** NEEDSDOC Field xmlFilename          */
    protected String xmlFilename;

    /** NEEDSDOC Field xslFilename          */
    protected String xslFilename;

    /** NEEDSDOC Field outNames          */
    protected OutputNameManager outNames;

    /**
     * Default constructor - initialize testName, Comment.
     */
    public ParamTest()
    {

        numTestCases = 2;  // REPLACE_num
        testName = "ParamTest";
        testComment = "Testing Xalan-J 1.x setStylesheetParam with XSL files";
    }

    /**
     * Initialize this test - Update with your tests's data.  
     *
     * NEEDSDOC @param p
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean doTestFileInit(Properties p)
    {

        outNames = new OutputNameManager(outputDir + File.separator
                                         + testName, ".out");

        // inputDir should be parent of xapi directory
        xmlFilename = inputDir + File.separator + "xalanj1" + File.separator
                      + "ParamTest1.xml";
        xslFilename = inputDir + File.separator + "xalanj1" + File.separator
                      + "ParamTest1.xsl";

        try
        {
            if ((liaison == null) || ("".equals(liaison)))
            {
                processor = XSLTProcessorFactory.getProcessor();
            }
            else
            {
                processor = XSLTProcessorFactory.getProcessorUsingLiaisonName(
                    liaison);
            }
        }
        catch (Exception e)
        {
            reporter.checkFail("Could not create processor, threw: "
                               + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Could not create processor");
            setAbortTest(true);
            return false;
        }

        return true;
    }

    /**
     * Write some test cases!  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase1()
    {

        reporter.testCaseInit("Testing setStylesheetParam");

        try
        {
            reporter.logInfoMsg("Creating XSLTInputSources " + xslFilename + ", " + xmlFilename);
            org.apache.xalan.xslt.XSLTInputSource xmlSource =
                new XSLTInputSource(xmlFilename);
            org.apache.xalan.xslt.XSLTInputSource xslStylesheet =
                new XSLTInputSource(xslFilename);
                

            // Process the file as-is
            org.apache.xalan.xslt.XSLTResultTarget xmlOutput1 =
                new XSLTResultTarget(outNames.nextName());
            reporter.logInfoMsg("Created XSLTResultTarget " + outNames.currentName());

            processor.process(xmlSource, xslStylesheet, xmlOutput1);
            processor.reset();
            checkFileContains(outNames.currentName(), "ABC,<B>ABC</B>;",
                              outNames.currentName()
                              + ") Stylesheet with default param value");

            // Also verify that $t1 tests are correct
            checkFileContains(
                outNames.currentName(),
                "<outt>true,false,false,false,notset</outt>",
                outNames.currentName()
                + ") ... also with default param value in select expr");

            // Test setting the value and checking it in a select expr
            reporter.logTraceMsg("Setting t1 param and processing");            
            processor.setStylesheetParam("t1", "''");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(outNames.currentName(),
                              "<outt>false,true,false,false,</outt>",
                              outNames.currentName()
                              + ") Select expr of a param blank string");
            processor.setStylesheetParam("t1", "'a'");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(outNames.currentName(),
                              "<outt>false,false,true,false,a</outt>",
                              outNames.currentName()
                              + ") Select expr of a param string");
            processor.setStylesheetParam("t1", "'1'");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(outNames.currentName(),
                              "<outt>false,false,false,true,1</outt>",
                              outNames.currentName()
                              + ") Select expr of a param number");

            // Now re-set the value of the element-value params in the xsl file
            processor.setStylesheetParam("p1", "'foo'");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(outNames.currentName(), "foo,foo;",
                              outNames.currentName()
                              + ") Stylesheet with literal param value");

            // Test resetting the value of a parameter with the same processor instance
            processor.setStylesheetParam("p1", "'bar'");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(
                outNames.currentName(), "bar,bar;",
                outNames.currentName()
                + ") Stylesheet with replaced literal param value");

            // Test putting other nodes in the value
            processor.setStylesheetParam("p2",
                                         "'&lt;item&gt;bar&lt;/item&gt;'");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(
                outNames.currentName(),
                "&amp;lt;item&amp;gt;bar&amp;lt;/item&amp;gt;,&amp;lt;item&amp;gt;bar&amp;lt;/item&amp;gt;;",
                outNames.currentName()
                + ") Stylesheet with param value with nodes(?)");

            // Param within a template
            processor.setStylesheetParam("p3", "'foo3'");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(
                outNames.currentName(), "GHI,<B>GHI</B>;",
                outNames.currentName()
                + ") Stylesheet with literal param value in a template, is not passed");

            // Now test the value of the select-value params in the xsl file
            processor.setStylesheetParam("s1", "'foos'");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(outNames.currentName(), "foos,foos;",
                              outNames.currentName()
                              + ") Stylesheet with literal param select");
            processor.setStylesheetParam("s1", "'bars'");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(
                outNames.currentName(), "bars,bars;",
                outNames.currentName()
                + ") Stylesheet with replaced literal param select");
            processor.setStylesheetParam("s2", "'&lt;item/&gt;'");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(outNames.currentName(),
                              "&amp;lt;item/&amp;gt;,&amp;lt;item/&amp;gt;;",
                              outNames.currentName()
                              + ") Stylesheet with nodes(?) param select");
            processor.setStylesheetParam("s3", "'foos3'");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(
                outNames.currentName(), "s3val,s3val;",
                outNames.currentName()
                + ") Stylesheet with literal param select in a template, is not passed");
        }
        catch (Exception e)
        {
            reporter.logErrorMsg("Testcase threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Testcase threw");
        }

        reporter.testCaseClose();
        return true;
    }

    /**
     * Write some test cases!  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean testCase2()
    {

        reporter.testCaseInit("Testing setStylesheetParam with XObjects");

        try
        {

            // Note we may be implicitly using the same processor as before!
            org.apache.xalan.xslt.XSLTInputSource xmlSource =
                new XSLTInputSource(xmlFilename);
            org.apache.xalan.xslt.XSLTInputSource xslStylesheet =
                new XSLTInputSource(xslFilename);

            // Create some XObjects to use
            org.apache.xalan.xpath.XBoolean myBoolean = null;
            org.apache.xalan.xpath.XNull myNull = null;
            org.apache.xalan.xpath.XNumber myNumber = null;
            org.apache.xalan.xpath.XObject myObject = null;
            org.apache.xalan.xpath.XString myString = null;
            try
            {
                // Note explicit casts are necessary for when running 
                //  tests against Xalan 2.x's compatibility layer
                myBoolean = (org.apache.xalan.xpath.XBoolean)processor.createXBoolean(true);
                myNull = (org.apache.xalan.xpath.XNull)processor.createXNull();
                myNumber = (org.apache.xalan.xpath.XNumber)processor.createXNumber(1);
                myObject = (org.apache.xalan.xpath.XObject)processor.createXObject("a");
                myString = (org.apache.xalan.xpath.XString)processor.createXString("a");
            }
            catch (ClassCastException cce)
            {
                reporter.logThrowable(Logger.ERRORMSG, cce, "Creating XObjects* threw");
                //@todo Note rest of testcase is not valid
            }
            
            // Test setting the value and checking it in a select expr
            processor.setStylesheetParam("t1", myNull);
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(outNames.currentName(),
                              "<outt>false,false,false,false,</outt>",
                              outNames.currentName()
                              + ") Select expr with XNull");
            processor.setStylesheetParam("t1", myBoolean);
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(outNames.currentName(),
                              "<outt>true,false,true,true,true</outt>",
                              outNames.currentName()
                              + ") Select expr with XBoolean");
            processor.setStylesheetParam("t1", myString);  // XString = "a"
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(outNames.currentName(),
                              "<outt>false,false,true,false,a</outt>",
                              outNames.currentName()
                              + ") Select expr with XString");
            processor.setStylesheetParam("t1", myObject);  // XObject = (string)"a"
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(outNames.currentName(),
                              "<outt>false,false,true,false,a</outt>",
                              outNames.currentName()
                              + ") Select expr with xObject(string)");
            processor.setStylesheetParam("t1", myNumber);
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
            processor.reset();
            checkFileContains(outNames.currentName(),
                              "<outt>false,false,false,true,1</outt>",
                              outNames.currentName()
                              + ") Select expr with XNumber");
        }
        catch (Exception e)
        {
            reporter.checkErr("Testcase threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Testcase threw");
        }

        reporter.testCaseClose();

        return true;
    }

    /**
     * Checks and reports if a file contains a certain string (within one line).
     * <P>We should really validate the entire output file, but this will do for now.</P>
     * @todo update to use new CheckServices!
     *
     * NEEDSDOC @param fName
     * NEEDSDOC @param checkStr
     * NEEDSDOC @param comment
     *
     * NEEDSDOC ($objectName$) @return
     */
    protected boolean checkFileContains(String fName, String checkStr,
                                        String comment)
    {

        boolean passFail = false;
        File f = new File(fName);

        if (!f.exists())
        {
            reporter.checkFail("checkFileContains(" + fName
                               + ") does not exist: " + comment);

            return false;
        }

        try
        {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            for (;;)
            {
                String inbuf = br.readLine();

                if (inbuf == null)
                    break;

                if (inbuf.indexOf(checkStr) > 0)
                {
                    passFail = true;

                    reporter.logTraceMsg(
                        "checkFileContains passes with line: " + inbuf);

                    break;
                }
            }
        }
        catch (IOException ioe)
        {
            reporter.checkFail("checkFileContains(" + fName + ") threw: "
                               + ioe.toString() + " for: " + comment);

            return false;
        }

        reporter.check(passFail, true, comment);

        return passFail;
    }

    /**
     * Convenience method to print out usage information - update if needed.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String usage()
    {
        return ("Common [optional] options supported by ParamTest:\n"
                + "(Note: assumes inputDir=tests\\api)\n" + super.usage());
    }

    /**
     * Main method to run test from the command line - can be left alone.  
     *
     * NEEDSDOC @param args
     */
    public static void main(String[] args)
    {

        ParamTest app = new ParamTest();

        app.doMain(args);
    }
}  // end of class ParamTest

