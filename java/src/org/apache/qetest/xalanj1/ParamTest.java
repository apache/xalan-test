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

import org.apache.xalan.xpath.XObject;
import org.apache.xalan.xpath.XNodeSet;
import org.apache.xalan.xpath.XBoolean;
import org.apache.xalan.xpath.XNumber;
import org.apache.xalan.xpath.XNull;
import org.apache.xalan.xpath.XString;
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
        numTestCases = 4;  // REPLACE_num
        testName = "ParamTest";
        testComment = "Testing Xalan-J 1.x setStylesheetParam with XSL files";
    }

    /**
     * Initialize this test - Update with your tests's data.  
     *
     * @param p Properties to initialize from (unused)
     * @return false if we should abort the test; true otherwise
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
        return true;
    }

    /**
     * Testing setStylesheetParam.  
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {

        reporter.testCaseInit("Testing setStylesheetParam");
        try
        {
            if ((liaison == null) || ("".equals(liaison)))
            {
                processor = XSLTProcessorFactory.getProcessor();
            }
            else
            {
                processor = XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
            }
        }
        catch (Exception e)
        {
            reporter.checkFail("Could not create processor, threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Could not create processor");
            return true;
        }

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
                              + "(0) Stylesheet with default param value");

            // Also verify that $t1 tests are correct
            checkFileContains(
                outNames.currentName(),
                "<outt>true,false,false,false,notset</outt>",
                outNames.currentName()
                + "(1) ... also with default param value in select expr");


            String paramTests[][] = 
            {
                // { paramName to test,
                //   paramValue to test
                //   expected output string,
                //   description of the test
                // }
                { 
                    "t1", 
                    "'a'",
                    "<outt>false,false,true,false,a</outt>",
                    "(10)Select expr of a 'param' string"
                },
                // SPR SCUU4T5Q3W in above data: 
                //  Xalan-J 1.x produces: ...false,a</outt>
                //  Xalan-J 2.x compat.jar produces: ...false,'a'</outt>
                {
                    "t1", 
                    "'1'",
                    "<outt>false,false,false,true,1</outt>",
                    "(11)Select expr of a param number"
                },
                { 
                    "t1", 
                    "''",
                    "<outt>false,true,false,false,</outt>",
                    "(12)Select expr of a param 'blank' string"
                },
                { 
                    "p1", 
                    "'foo'",
                    "foo,foo;",
                    "(13)Stylesheet with literal param value"
                },
                { 
                    "p1", 
                    "'bar'",
                    "bar,bar;",
                    "(14)Stylesheet with replaced/another literal param value"
                },
                { 
                    "p2", 
                    "'&lt;item&gt;bar&lt;/item&gt;'",
                    "&amp;lt;item&amp;gt;bar&amp;lt;/item&amp;gt;,&amp;lt;item&amp;gt;bar&amp;lt;/item&amp;gt;;",
                    "(15)Stylesheet with param value with nodes"
                },
                { 
                    "p3", 
                    "'foo3'",
                    "GHI,<B>GHI</B>;",
                    "(16)Stylesheet with literal param value in a template, is not passed"
                },
                { 
                    "s1", 
                    "'foos'",
                    "foos,foos;",
                    "(17)Stylesheet with literal param select"
                },
                { 
                    "s1", 
                    "'bars'",
                    "bars,bars;",
                    "(18)Stylesheet with replaced/another literal param select"
                },
                { 
                    "s2", 
                    "'&lt;item/&gt;'",
                    "&amp;lt;item/&amp;gt;,&amp;lt;item/&amp;gt;;",
                    "(19)Stylesheet with nodes(?) param select"
                },
                { 
                    "s3", 
                    "'foos3'",
                    "s3val,s3val;",
                    "(20)Stylesheet with literal param select in a template, is not passed"
                },
                { 
                    "t1", 
                    "",
                    "<outt>false,true,false,false,</outt>",
                    "(21)Select expr of a param blank string"
                },
                { 
                    "t1", 
                    "a",
                    "<outt>false,false,true,false,a</outt>",
                    "(22)Select expr of a param string"
                }
            }; // end of paramTests array

            // Just loop through test elements and try each one
            for (int i = 0; i < paramTests.length; i++)
            {
                // Note processor, inputs are just re-used
                // This method calls check() for us
                testSetParam(paramTests[i][0], paramTests[i][1],
                             processor, new XSLTInputSource(xmlFilename), new XSLTInputSource(xslFilename), 
                             paramTests[i][2], paramTests[i][3]);
                // Try it again, on a completely independent 
                //  processor and sources each time
                testSetParam(paramTests[i][0], paramTests[i][1],
                             new XSLTInputSource(xmlFilename), new XSLTInputSource(xslFilename), 
                             paramTests[i][2], paramTests[i][3]);
            }
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
     * Testing setStylesheetParam with XObjects.  
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {

        reporter.testCaseInit("Testing setStylesheetParam with XObjects");
        try
        {
            if ((liaison == null) || ("".equals(liaison)))
            {
                processor = XSLTProcessorFactory.getProcessor();
            }
            else
            {
                processor = XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
            }
        }
        catch (Exception e)
        {
            reporter.checkFail("Could not create processor, threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Could not create processor");
            return true;
        }

        try
        {
            XSLTInputSource xmlSource = new XSLTInputSource(xmlFilename);
            XSLTInputSource xslStylesheet = new XSLTInputSource(xslFilename);

            // Create some XObjects to use
            XBoolean myBoolean = null;
            XNull myNull = null;
            XNumber myNumber = null;
            XObject myObject = null;
            XString myString = null;
            myBoolean = processor.createXBoolean(true);
            myNull = processor.createXNull();
            myNumber = processor.createXNumber(1);
            myObject = processor.createXObject("a");
            myString = processor.createXString("a");
            
            // Test setting the value and checking it in a select expr
/************************************************ COMPILE PROBLEM
// Compile problem: this block gives a number of compile errors 
//  when run against Xalan-J 2.x compat.jar:
//  Incompatible type for method. Can't convert org.apache.xalan.xpath.XNull to java.lang.String.
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
************************************************ COMPILE PROBLEM */
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
     * Testing multiple setStylesheetParam calls.  
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase3()
    {
        reporter.testCaseInit("Testing multiple setStylesheetParam calls-A");
        reporter.logInfoMsg("This testCase3/testCase4 is basically repeated to check how Xalan-J 2.x's backwards compatibility layer works");
        try
        {
            if ((liaison == null) || ("".equals(liaison)))
            {
                processor = XSLTProcessorFactory.getProcessor();
            }
            else
            {
                processor = XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
            }
        }
        catch (Exception e)
        {
            reporter.checkFail("Could not create processor, threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Could not create processor");
            return true;
        }

        try
        {
            reporter.logInfoMsg("Using XSLTInputSources " + xslFilename + ", " + xmlFilename);
                
            // Set one parameter and process the file
            processor.setStylesheetParam("t1", "'a'");
            processor.process(new XSLTInputSource(xmlFilename), new XSLTInputSource(xslFilename), new XSLTResultTarget(outNames.nextName()));
            processor.reset();

            // Verify our param was set,...
            checkFileContains(outNames.currentName(), 
                              "<outt>false,false,true,false,a</outt>",
                              outNames.currentName() + " Stylesheet one param set(1)");

            // ... and that the other one wasn't
            checkFileContains(outNames.currentName(),
                              "<outs>s1val,s1val; s2val,s2val; s3val,s3val; </outs>",
                              outNames.currentName() + " Stylesheet one param set(2)");

            // Set another parameter and process the file
            // SPR SCUU4T5QEC 1.x and 2.x compat.jar behave differently:
            //  1.x reset() appears to reset params
            //  2.x compat.jar reset() does not appear to reset params
            reporter.logTraceMsg("about to setStylesheetParam(s1, 'foos')");
            processor.setStylesheetParam("s1", "'foos'");
            reporter.logTraceMsg("about to process(...)");
            processor.process(new XSLTInputSource(xmlFilename), new XSLTInputSource(xslFilename), new XSLTResultTarget(outNames.nextName()));
            processor.reset();

            // Verify our previous param is still set...
            // NOTE: double-check how parameters get reset in 1.x 
            //  API, and in compatibility layer
            checkFileContains(outNames.currentName(), 
                              "<outt>false,false,true,false,a</outt>",
                              outNames.currentName() + " Stylesheet second param set(1)");

            // ... and that the new one also now is
            checkFileContains(outNames.currentName(),
                              "foos,foos;",
                              outNames.currentName() + " Stylesheet second param set(2)");
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
     * Testing multiple setStylesheetParam calls-B.  
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase4()
    {
        reporter.testCaseInit("Testing multiple setStylesheetParam calls-B");
        reporter.logInfoMsg("This testCase3/testCase4 is basically repeated to check how Xalan-J 2.x's backwards compatibility layer works");
        try
        {
            if ((liaison == null) || ("".equals(liaison)))
            {
                processor = XSLTProcessorFactory.getProcessor();
            }
            else
            {
                processor = XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
            }
        }
        catch (Exception e)
        {
            reporter.checkFail("Could not create processor, threw: " + e.toString());
            reporter.logThrowable(Logger.ERRORMSG, e, "Could not create processor");
            return true;
        }

        try
        {
            reporter.logInfoMsg("Using XSLTInputSources " + xslFilename + ", " + xmlFilename);
                
            // Set BOTH parameters at once and process the file
            reporter.logTraceMsg("about to setStylesheetParam(t1, 'a')");
            processor.setStylesheetParam("t1", "'a'");
            reporter.logTraceMsg("about to setStylesheetParam(s1, 'foos')");
            processor.setStylesheetParam("s1", "'foos'");
            reporter.logTraceMsg("about to process with both params");
            processor.process(new XSLTInputSource(xmlFilename), new XSLTInputSource(xslFilename), new XSLTResultTarget(outNames.nextName()));
            processor.reset();

            // Verify our param was set,...
            checkFileContains(outNames.currentName(), 
                              "<outt>false,false,true,false,a</outt>",
                              outNames.currentName() + " Stylesheet both param set(1)");

            // ... and that the other one also is
            checkFileContains(outNames.currentName(),
                              "foos,foos;",
                              outNames.currentName() + " Stylesheet both param set(1a)");

            // Set another parameter and process the file
            // SPR SCUU4T5QEC 1.x and 2.x compat.jar behave differently:
            //  1.x reset() appears to reset params
            //  2.x compat.jar reset() does not appear to reset params
            reporter.logTraceMsg("about to process again after reset(...)");
            processor.process(new XSLTInputSource(xmlFilename), new XSLTInputSource(xslFilename), new XSLTResultTarget(outNames.nextName()));
            processor.reset();

            // Verify our previous param is still set...
            // NOTE: double-check how parameters get reset in 1.x 
            //  API, and in compatibility layer
            checkFileContains(outNames.currentName(), 
                              "<outt>false,false,true,false,a</outt>",
                              outNames.currentName() + " Stylesheet after reset param set(1b)");

            // ... and that the new one also now is
            checkFileContains(outNames.currentName(),
                              "foos,foos;",
                              outNames.currentName() + " Stylesheet after reset param set(1c)");
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
     * Test setting a single string-type parameter.  
     * <P>Calls setStylesheetParam(); process(); then result().
     * calls checkFileContains() to validate and output results.</P>
     *
     * @param paramName simple name of parameter
     * @param paramVal String value of parameter
     * @param processor object to use 
     * @param xmlSource object to use in transform
     * @param xslStylesheet object to use in transform
     * @param checkString to look for in output file (logged)
     * @param comment to log with check() call
     * @return true if pass, false otherwise
     */
    protected boolean testSetParam(String paramName, String paramVal,
                                   XSLTProcessor processor, 
                                   XSLTInputSource xmlSource, 
                                   XSLTInputSource xslStylesheet, 
                                   String checkString, String comment)
    {
        try
        {
            reporter.logTraceMsg("setStylesheetParam(" + paramName + ", " + paramVal +")");
            processor.setStylesheetParam(paramName, paramVal);
            reporter.logTraceMsg("process(" + xmlSource.getSystemId() + ", " + xslStylesheet.getSystemId() +", ...)");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
        }
        catch (Throwable t)
        {
            reporter.logThrowable(Logger.ERRORMSG, t, "testSetParam unexpectedly threw");
        }
        finally
        {
            try
            {
                processor.reset();
            }
            catch (Throwable t2)
            {
                reporter.checkErr("Resetting processor threw:" + t2.toString());
                reporter.logThrowable(Logger.ERRORMSG, t2, "Resetting processor  threw");
            }
        }
        return checkFileContains(outNames.currentName(), checkString,
                                 comment + " into: " + outNames.currentName());
    }

    /**
     * Test setting a single string-type parameter.  
     * <P>Calls setStylesheetParam(); process(); then result().
     * calls checkFileContains() to validate and output results.</P>
     *
     * @param paramName simple name of parameter
     * @param paramVal String value of parameter
     * @param xmlSource object to use in transform
     * @param xslStylesheet object to use in transform
     * @param checkString to look for in output file (logged)
     * @param comment to log with check() call
     * @return true if pass, false otherwise
     */
    protected boolean testSetParam(String paramName, String paramVal,
                                   XSLTInputSource xmlSource, 
                                   XSLTInputSource xslStylesheet, 
                                   String checkString, String comment)
    {
        try
        {
            if ((liaison == null) || ("".equals(liaison)))
            {
                processor = XSLTProcessorFactory.getProcessor();
            }
            else
            {
                processor = XSLTProcessorFactory.getProcessorUsingLiaisonName(liaison);
            }

            reporter.logTraceMsg("setStylesheetParam(" + paramName + ", " + paramVal +")");
            processor.setStylesheetParam(paramName, paramVal);
            reporter.logTraceMsg("process(" + xmlSource.getSystemId() + ", " + xslStylesheet.getSystemId() +", ...)");
            processor.process(xmlSource, xslStylesheet,
                              new XSLTResultTarget(outNames.nextName()));
        }
        catch (Throwable t)
        {
            reporter.logThrowable(Logger.ERRORMSG, t, "testSetParam unexpectedly threw");
        }
        finally
        {
            processor.reset();
        }
        return checkFileContains(outNames.currentName(), checkString,
                                 comment + " into: " + outNames.currentName());
    }

    /**
     * Checks and reports if a file contains a certain string (within one line).
     * <P>We should really validate the entire output file, but this will do for now.</P>
     *
     * @param fName name of file to check
     * @param checkStr String to look for in the file
     * @param comment to log with the check() call
     * @return true if pass, false otherwise
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
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by ParamTest:\n"
                + "(Note: assumes inputDir=tests\\api)\n" + super.usage());
    }

    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {

        ParamTest app = new ParamTest();

        app.doMain(args);
    }
}  // end of class ParamTest

