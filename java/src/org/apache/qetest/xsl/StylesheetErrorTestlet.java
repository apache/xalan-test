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
 * StylesheetErrorTestlet.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.CheckService;
import org.apache.qetest.Datalet;
import org.apache.qetest.Logger;
import org.apache.qetest.QetestUtils;
import org.apache.qetest.TestletImpl;
import org.apache.qetest.xslwrapper.TransformWrapper;
import org.apache.qetest.xslwrapper.TransformWrapperFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Testlet for testing of xsl stylesheets with expected errors.
 *
 * This class provides the testing algorithim used for verifying 
 * how a XSLT processor handles stylesheets with known expected 
 * errors conditions in them.  
 * The primary testing mode is attempting to build a stylesheet 
 * or use a stylesheet to process an XML document, where the 
 * stylesheet has a known error and should cause the processor 
 * to throw an exception.
 * This testlet then verifies the text of the exception against 
 * a specified ExpectedException: provided (normally provided in 
 * comments at the head of a stylesheet test).  Multiple 
 * ExpectedExceptions: may be provided, and we only check that 
 * toString() of the actual exception contains one of the 
 * ExpectedExceptions, not the full text (this makes maintaining 
 * the expected data easier).
 * Note also: we do nothing with the output files, so currently 
 * we can't validate tests that both throw an exception and 
 * produce an output file (that's the subject of another test...)
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class StylesheetErrorTestlet extends TestletImpl
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.StylesheetErrorTestlet"; }


    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new StylesheetDatalet(); }


    /** Token in xsl file denoting the text of an expected exception.  */
    public static final String EXPECTED_EXCEPTION = "ExpectedException:";
    public static final String EXPECTED_EXCEPTION_END = "-->";

    /**
     * Accesor method for a brief description of this test.  
     *
     * @return String describing what this StylesheetErrorTestlet does.
     */
    public String getDescription()
    {
        return "StylesheetErrorTestlet - use stylesheet w/error and verify exception thrown";
    }


    /**
     * Run this StylesheetErrorTestlet: execute it's test and return.
     *
     * @param Datalet to use as data point for the test.
     */
    public void execute(Datalet d)
    {
        StylesheetDatalet datalet = null;
        try
        {
            datalet = (StylesheetDatalet)d;
        }
        catch (ClassCastException e)
        {
            logger.checkErr("Datalet provided is not a StylesheetDatalet; cannot continue with " + d);
            return;
        }

        logger.logMsg(Logger.STATUSMSG, "about to test: " 
                      + (null == datalet.inputName
                         ? datalet.xmlName
                         : datalet.inputName));
        //@todo validate our Datalet - ensure it has valid 
        //  and/or existing files available.

        // Cleanup outName only if asked to - delete the file on disk
        // Optimization: this takes extra time and often is not 
        //  needed, so only do this if the option is set
        if ("true".equalsIgnoreCase(datalet.options.getProperty("deleteOutFile")))
        {
            try
            {
                boolean btmp = (new File(datalet.outputName)).delete();
                logger.logMsg(Logger.TRACEMSG, "Deleting OutFile of::" + datalet.outputName
                                     + " status: " + btmp);
            }
            catch (SecurityException se)
            {
                logger.logMsg(Logger.WARNINGMSG, "Deleting OutFile of::" + datalet.outputName
                                       + " threw: " + se.toString());
                // But continue anyways...
            }
        }

        // Create a new TransformWrapper of appropriate flavor
        //  null arg is unused liaison for TransformWrapper
        //@todo allow user to pass in pre-created 
        //  TransformWrapper so we don't have lots of objects 
        //  created and destroyed for every file
        TransformWrapper transformWrapper = null;
        try
        {
            transformWrapper = TransformWrapperFactory.newWrapper(datalet.flavor);
            transformWrapper.newProcessor(null);
        }
        catch (Throwable t)
        {
            logger.logThrowable(Logger.ERRORMSG, t, getDescription() + " newWrapper/newProcessor threw");
            logger.checkErr(getDescription() + " newWrapper/newProcessor threw: " + t.toString());
            return;
        }

        // Read in expectedExecption from datalet or stylesheet file
        Vector expectedException = getExpectedException(datalet);

        // Store local copies of XSL, XML references to avoid 
        //  potential for changing datalet            
        String inputName = datalet.inputName;
        String xmlName = datalet.xmlName;

        //@todo Should we log a custom logElement here instead?
        // Be sure to log everything before we start the test!
        logger.logMsg(Logger.TRACEMSG, "executing with: inputName=" + inputName
                      + " xmlName=" + xmlName + " flavor="  + datalet.flavor
                      + " num" + EXPECTED_EXCEPTION + "=" 
                      + (null != expectedException
                        ? expectedException.size()
                        : 0));

        // Test our supplied input file, and validate exceptions
        try
        {
            // Simply have the wrapper do all the transforming
            //  or processing for us - we handle either normal .xsl 
            //  stylesheet tests or just .xml embedded tests
            if (null == datalet.inputName)
            {
                // Note: we expect an exception to be thrown!
                // presume it's an embedded test
                transformWrapper.transformEmbedded(xmlName, datalet.outputName); // throwaway returned value
            }
            else
            {
                // Note: we expect an exception to be thrown!
                // presume it's a normal stylesheet test
                transformWrapper.transform(xmlName, inputName, datalet.outputName);  // throwaway returned value
            }

            // Note: Error tests, by our definitions, are expected 
            //  to throw an exception: so if we get here, we should 
            //  actually report a fail!

            // Log a custom element with all the file refs first
            //  Closely related to viewResults.xsl select='fileref"
            Hashtable attrs = new Hashtable();
            attrs.put("idref", (new File(datalet.inputName)).getName());
            attrs.put("inputName", datalet.inputName);
            attrs.put("xmlName", datalet.xmlName);
            attrs.put("outputName", datalet.outputName);
            attrs.put("goldName", datalet.goldName);
            logger.logElement(Logger.STATUSMSG, "fileref", attrs, "Conformance error test file references");
            // Then log the failure reason
            logger.checkFail(datalet.getDescription() + " did not throw any exception");
        }
        catch (Throwable t)
        {
            // Validate against some expected exception(s) 
            if (null == expectedException)
            {
                logger.logThrowable(Logger.ERRORMSG, t, getDescription() + " " + datalet.getDescription());
                logger.checkAmbiguous(datalet.getDescription() 
                                      + " no expected exception available to validate against!");
                return;
                
            }
            //@todo improve how we take in data about what's expected
            //  -- an actual Throwable subclass to .equals
            //  -- some part of Throwable.toString()
            //  -- -- compared to string in datalet
            //  -- -- compared to string in xsl file
            //  -- some part of the stack traceback?
            boolean foundExpected = false;
            for (Enumeration enum = expectedException.elements();
                 enum.hasMoreElements(); 
                 /* no increment portion */)
            {
                // Note cast is safe since we only put Strings in 
                //  there in our own worker method
                String expExc = (String)enum.nextElement();
                if (t.toString().indexOf(expExc) > -1)
                {
                    foundExpected = true;
                    break;
                }
            }
            if (foundExpected)
            {
                logger.checkPass(datalet.getDescription() 
                                 + " expectedly threw: " + t.toString());
            }
            else
            {
                // Put the logThrowable first, so it appears before 
                //  the Fail record, and gets color-coded
                logger.logThrowable(Logger.ERRORMSG, t, getDescription() + " " + datalet.getDescription());
                logger.checkFail(datalet.getDescription() 
                                + " unexpectedly threw: " + t.toString());
            }
        }
    }

    /**
     * Worker method to get expected exception text about a stylesheet.
     * 
     * Currently parses the inputDir stylesheet for a line that contains 
     * EXPECTED_EXCEPTION inside an xsl comment, on a single line, and 
     * trims off the closing comment -->.
     * Future work: allow options on datalet to specify some other 
     * expected data in another format - a whole Throwable object to 
     * compare to, or a stacktrace, etc.
     * 
     * @author Shane Curcuru
     * @param d Datalet that contains info about the exception
     * @return Vector of Strings denoting toString of exception(s)
     * we might expect - any one of them will pass; null if error
     */
    protected Vector getExpectedException(StylesheetDatalet d)
    {
        Vector v = null;
        // Read in the testName file to see if it's expecting something        
        try
        {
            FileReader fr = new FileReader(d.inputName);
            BufferedReader br = new BufferedReader(fr);
            for (;;)
            {
                String inbuf = br.readLine();

                if (inbuf == null)
                    break;  // end of file, break out and return default (false)

                int idx = inbuf.indexOf(EXPECTED_EXCEPTION);

                if (idx < 0)
                    continue;  // not on this line, keep going

                // The expected exception.getMessage is the rest of the line...
                String expExc = inbuf.substring(idx + EXPECTED_EXCEPTION.length(),
                                         inbuf.length());

                // ... less the trailing " -->" comment end; trimmed
                int endComment = expExc.indexOf(EXPECTED_EXCEPTION_END);
                if (endComment > -1)
                    expExc = expExc.substring(0, endComment).trim();
                else
                    expExc = expExc.trim();

                if (null == v)
                    v = new Vector(); // only create if needed
                v.addElement(expExc);

                // Continue reading the file for more potential
                //  expected exception strings - read them all
                //@todo optimization: stop parsing after xx lines?

            }  // end for (;;)
        }
        catch (java.io.IOException ioe)
        {
            logger.logMsg(Logger.ERRORMSG, "getExpectedException() threw: "
                                   + ioe.toString());
            return null;
        }
        return v;
    }

}  // end of class StylesheetErrorTestlet

