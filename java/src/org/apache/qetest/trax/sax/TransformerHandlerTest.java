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
 * TransformerHandlerTest.java
 *
 */
package org.apache.qetest.trax.sax;

import java.io.File;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.qetest.FileBasedTest;
import org.apache.qetest.Logger;
import org.apache.qetest.OutputNameManager;

//-------------------------------------------------------------------------

/**
 * Basic functionality test for the TransformerHandler class of TRAX.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class TransformerHandlerTest extends FileBasedTest
{

    /** Provides nextName(), currentName() functionality for output files.  */
    protected OutputNameManager outNames;


    /** Just initialize test name, comment, numTestCases. */
    public TransformerHandlerTest()
    {
        numTestCases = 1;  // REPLACE_num
        testName = "TransformerHandlerTest";
        testComment = "Basic functionality test for the TransformerHandler class of TRAX";
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
        final String outSubDirName = outputDir + File.separator + "trax" 
                                     + File.separator + "sax";
        File outSubDir = new File(outSubDirName);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outSubDirName + File.separator + testName, ".out");
        return true;
    }


    /**
     * TransformerHandler tests in error conditions.
     * Inspired by http://nagoya.apache.org/bugzilla/show_bug.cgi?id=2310
     * @author scott_boag@lotus.com
     * @author shane_curcuru@lotus.com
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Basic API coverage of set/get methods");

        // No public constructor available: you must always ask 
        //  a SAXTransformerFactory to give you one
        TransformerFactory factory = null;
        SAXTransformerFactory saxFactory = null;

        // Simply use strings instead of files to keep data together
        String xmlErrorStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root1></root2>"; // note mismatched tags
        String xmlGoodStr  = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>Hello world</root>";
        String xslStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
                + "<xsl:template match=\"/\">" 
                + "<xsl:copy-of select=\".\"/>"
                + "</xsl:template>" 
                + "</xsl:stylesheet>";

        // Use arbitrary so strings don't get escaped for XML output
        reporter.logArbitrary(Logger.STATUSMSG, "xmlErrorStr is: " + xmlErrorStr);
        reporter.logArbitrary(Logger.STATUSMSG, "xmlGoodStr is: " + xmlErrorStr);
        reporter.logArbitrary(Logger.STATUSMSG, "xslStr is: " + xmlErrorStr);
        try
        {
            factory = TransformerFactory.newInstance();
            saxFactory = (SAXTransformerFactory)factory;

            boolean gotExpectedException = false;
            Transformer reusedTransformer = null; // This is reused later; not sure if ordering is important
            try
            {
                reporter.logInfoMsg("About to newTransformer(xslStr)");
                reusedTransformer = saxFactory.newTransformer(
                                    new StreamSource(new java.io.StringReader(xslStr)));
                reporter.logInfoMsg("About to transform(xmlErrorStr, " + outNames.nextName() + ")");
                reusedTransformer.transform(new StreamSource(new java.io.StringReader(xmlErrorStr)),
                            new StreamResult(outNames.currentName()));
            }
            catch (TransformerException te)
            {
                // Transformer should only throw a TransformerException - pass
                gotExpectedException = true;
                reporter.logThrowable(Logger.INFOMSG, te, "Normal transform of xmlErrorStr correctly threw");
                // Also validate part of the string, since we know root1 should be in there somewhere
                reporter.check((te.toString().indexOf("root1") > -1), true, "Exception.toString correctly had root1 in it");
                //sb reporter.logInfoMsg("transformation 1 failed above (it is supposed to)");
            }
            catch (Throwable t)
            {
                // Any other Throwables - fail
                gotExpectedException = false;
                reporter.logThrowable(Logger.ERRORMSG, t, "Normal transform of xmlErrorStr threw unexpected Throwable");
                //sb reporter.logInfoMsg("transformation 1 failed above (it is supposed to)");
            }
            reporter.check(gotExpectedException, true, "Got expected exception from previous operation");
            gotExpectedException = false;


            // Note this is reused several times; I'm not sure if the reuse is important
            TransformerHandler thandler = null;
            try
            {
                reporter.logInfoMsg("About to newTransformerHandler(xslStr)");
                thandler = saxFactory.newTransformerHandler(
                                                new StreamSource(new java.io.StringReader(xslStr)));

                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                SAXParser parser = spf.newSAXParser();
                org.xml.sax.XMLReader reader = parser.getXMLReader();
                reporter.logTraceMsg("Got a SAXParser, now setting ContentHandler, ErrorHandler");
                reader.setContentHandler(thandler);
                reader.setErrorHandler((org.xml.sax.ErrorHandler)thandler);
                reporter.logInfoMsg("Now setResult to " + outNames.nextName()); // Note nextName here
                thandler.setResult(new StreamResult(outNames.currentName()));
                reporter.logInfoMsg("About to reader.parse(xmlErrorStr...)");
                reader.parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlErrorStr)));
            }
            catch (Exception e)
            {
                gotExpectedException = true;
                reporter.logThrowable(Logger.INFOMSG, e, "TransformerHandler of xmlErrorStr correctly threw");
                // Validate that it had root1, which we know is part of the error
                //  Note: we could also validate that it was a SAXParseException, but 
                //  I'm not sure that's worth the maintenance here 
                reporter.check((e.toString().indexOf("root1") > -1), true, "Exception.toString correctly had root1 in it");
                //sb reporter.logInfoMsg("transformation 2 failed above (it is supposed to)");
            }
            reporter.check(gotExpectedException, true, "Got expected exception from previous operation");
            gotExpectedException = false;

            try
            {
                reporter.logInfoMsg("About to newTransformerHandler(xslStr)");
                thandler = saxFactory.newTransformerHandler(
                            new StreamSource(new java.io.StringReader(xslStr)));

                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                SAXParser parser = spf.newSAXParser();
                org.xml.sax.XMLReader reader = parser.getXMLReader();
                reporter.logTraceMsg("Got a SAXParser");

                reporter.logInfoMsg("Now setResult to " + outNames.nextName()); // Note nextName here
                thandler.setResult(new StreamResult(outNames.currentName()));

                reporter.logTraceMsg("Got a SAXParser, now setting ContentHandler, ErrorHandler");
                reader.setContentHandler(thandler);
                reader.setErrorHandler((org.xml.sax.ErrorHandler)thandler);

                reporter.logInfoMsg("About to reader.parse(xmlGoodStr...)");
                reader.parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlGoodStr)));
                reporter.checkPass("TransformerHandler did not throw");
            }
            catch (Exception e)
            {
                reporter.logThrowable(Logger.INFOMSG, e, "TransformerHandler should not have thrown");
                reporter.checkFail("TransformerHandler should not have thrown");
                //sb reporter.logInfoMsg("transformation 3 failed above (bad)");
            }
            gotExpectedException = false;



            try
            {
                reporter.logInfoMsg("About to newTransformer(xslStr)");
                reusedTransformer = ((TransformerFactory) TransformerFactory.newInstance())
                                    .newTransformer(new StreamSource(new java.io.StringReader(xslStr)));
                reporter.logInfoMsg("About to transform(xmlGoodStr, " + outNames.nextName() + ")");
                reusedTransformer.transform(new StreamSource(new java.io.StringReader(xmlGoodStr)),
                            new StreamResult(outNames.currentName()));
                reporter.checkPass("Transformer did not throw");
            }
            catch (Exception e)
            {
                reporter.logThrowable(Logger.INFOMSG, e, "TransformerHandler should not have thrown");
                reporter.checkFail("TransformerHandler should not have thrown");
                //sb reporter.logInfoMsg("transformation 4 failed above (bad)");
            }
            gotExpectedException = false;


        }
        catch (Throwable t)
        {
            reporter.checkFail("Problem with testCase1");
            reporter.logThrowable(reporter.ERRORMSG, t, "testCase1");
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
        return ("Common [optional] options supported by TransformerHandlerTest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        TransformerHandlerTest app = new TransformerHandlerTest();
        app.doMain(args);
    }
}
