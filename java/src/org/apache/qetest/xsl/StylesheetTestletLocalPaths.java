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
 * StylesheetTestletLocalPaths.java
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;

/**
 * Testlet for conformance testing of xsl stylesheet files.
 *
 * HACK: Forces use of local pathnames.
 * 
 * This class provides the testing algorithim used for verifying 
 * Xalan's conformance to the XSLT spec.  It works in conjunction 
 * with StylesheetTestletLocalPathsDriver, which supplies the logic for 
 * choosing the testfiles to iterate over, and with 
 * TransformWrapper, which provides an XSL  processor- and 
 * method-independent way to process files (i.e. different 
 * flavors of TransformWrapper may be different products, as well 
 * as different processing models, like SAX, DOM or Streams).
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class StylesheetTestletLocalPaths extends TestletImpl
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.StylesheetTestletLocalPaths"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new StylesheetDatalet(); }

    /**
     * Accesor method for a brief description of this test.  
     *
     * @return String describing what this StylesheetTestletLocalPaths does.
     */
    public String getDescription()
    {
        return "StylesheetTestletLocalPaths";
    }


    /**
     * Run this StylesheetTestletLocalPaths: execute it's test and return.
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

        logger.logMsg(Logger.STATUSMSG, "About to test: " 
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
            logThrowable(t, getDescription() + " newWrapper/newProcessor threw");
            logger.checkErr(getDescription() + " newWrapper/newProcessor threw: " + t.toString());
            return;
        }

        // Test our supplied input file, and compare with gold
        try
        {
            // Store local copies of XSL, XML references for 
            //  potential change to URLs            
            String inputName = datalet.inputName;
            String xmlName = datalet.xmlName;

            // * HACK: Forces use of local pathnames.

            //@todo Should we log a custom logElement here instead?
            logger.logMsg(Logger.TRACEMSG, "executing with: inputName=" + inputName
                          + " xmlName=" + xmlName + " outputName=" + datalet.outputName
                          + " goldName=" + datalet.goldName + " flavor="  + datalet.flavor);

            // Simply have the wrapper do all the transforming
            //  or processing for us - we handle either normal .xsl 
            //  stylesheet tests or just .xml embedded tests
            long retVal = 0L;
            if (null == datalet.inputName)
            {
                // presume it's an embedded test
                long [] times = transformWrapper.transformEmbedded(xmlName, datalet.outputName);
                retVal = times[TransformWrapper.IDX_OVERALL];
            }
            else
            {
                // presume it's a normal stylesheet test
                long[] times = transformWrapper.transform(xmlName, inputName, datalet.outputName);
                retVal = times[TransformWrapper.IDX_OVERALL];
            }

            // If we get here, attempt to validate the contents of 
            //  the last outputFile created
            CheckService fileChecker = (CheckService)datalet.options.get("fileCheckerImpl");
            // Supply default value
            if (null == fileChecker)
                fileChecker = new XHTFileCheckService();
            if (Logger.PASS_RESULT
                != fileChecker.check(logger,
                                     new File(datalet.outputName), 
                                     new File(datalet.goldName), 
                                     getDescription() + " " + datalet.getDescription())
               )
            {
                // Log a custom element with all the file refs first
                // Closely related to viewResults.xsl select='fileref"
                //@todo check that these links are valid when base 
                //  paths are either relative or absolute!
                Hashtable attrs = new Hashtable();
                attrs.put("idref", (new File(datalet.inputName)).getName());
                attrs.put("inputName", datalet.inputName);
                attrs.put("xmlName", datalet.xmlName);
                attrs.put("outputName", datalet.outputName);
                attrs.put("goldName", datalet.goldName);
                logger.logElement(Logger.STATUSMSG, "fileref", attrs, "Conformance test file references");
                // Then log the failure reason
                logger.logArbitrary(Logger.STATUSMSG, (new File(datalet.inputName)).getName() 
                                    + " failure reason: " + fileChecker.getExtendedInfo());
            }
        }
        // Note that this class can only validate positive test 
        //  cases - we don't handle ExpectedExceptions
        catch (Throwable t)
        {
            // Put the logThrowable first, so it appears before 
            //  the Fail record, and gets color-coded
            logThrowable(t, getDescription() + " " + datalet.getDescription());
            logger.checkFail(getDescription() + " " + datalet.getDescription() 
                             + " threw: " + t.toString());
            return;
        }
	}


    /**
     * Logs out throwable.toString() and stack trace to our Logger.
     * //@todo Copied from Reporter; should probably be moved into Logger.
     * @param throwable thrown throwable/exception to log out.
     * @param msg description of the throwable.
     */
    protected void logThrowable(Throwable throwable, String msg)
    {
        StringWriter sWriter = new StringWriter();
        sWriter.write(msg + "\n");

        PrintWriter pWriter = new PrintWriter(sWriter);
        throwable.printStackTrace(pWriter);

        logger.logArbitrary(Logger.STATUSMSG, sWriter.toString());
    }
}  // end of class StylesheetTestletLocalPaths

