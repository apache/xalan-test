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
 * StylesheetTestlet.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.CheckService;
import org.apache.qetest.Datalet;
import org.apache.qetest.Logger;
import org.apache.qetest.QetestUtils;
import org.apache.qetest.TestletImpl;
import org.apache.qetest.xslwrapper.ProcessorWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;

/**
 * Testlet for conformance testing of xsl stylesheet files.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class StylesheetTestlet extends TestletImpl
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.StylesheetTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new StylesheetDatalet(); }

    /**
     * Accesor method for a brief description of this test.  
     *
     * @return String describing what this StylesheetTestlet does.
     */
    public String getDescription()
    {
        return "StylesheetTestlet";
    }


    /**
     * Run this StylesheetTestlet: execute it's test and return.
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
            logger.checkErr("Datalet provided is not a StylesheetDatalet; cannot continue");
            return;
        }
        
        //@todo validate our Datalet - ensure it has valid 
        //  and/or existing files available.

        // Cleanup outputName - delete the file on disk
        //  Ensure other files or previous test runs don't 
        //  interfere with our results
        try
        {
            File outFile = new File(datalet.outputName);
            boolean btmp = outFile.delete();
        }
        catch (SecurityException se)
        {
            logger.logMsg(Logger.WARNINGMSG, "Deleting OutFile of:" + datalet.outputName
                                   + " threw: " + se.toString());
            // But continue anyways...
        }

        // Test our supplied input file, and compare with gold
        try
        {
            // Create a new ProcessorWrapper of appropriate flavor
            //  null arg is unused liaison for ProcessorWrapper
            //@todo allow user to pass in pre-created 
            //  ProcessorWrapper so we don't have lots of objects 
            //  created and destroyed for every file
            ProcessorWrapper processorWrapper = ProcessorWrapper.getWrapper(datalet.flavor);
            if (null == processorWrapper.createNewProcessor(null))
            {
                logger.checkErr("ERROR: could not create processorWrapper, aborting.");
                return;
            }

            // Store local copies of XSL, XML references for 
            //  potential change to URLs            
            String inputName = datalet.inputName;
            String xmlName = datalet.xmlName;
            if (datalet.useURL)
            {
                // inputName may not exist if it's an embedded test
                if (null != inputName)
                    inputName = QetestUtils.filenameToURL(inputName);
                xmlName = QetestUtils.filenameToURL(xmlName);
            }

            //@todo do we really want to log all of this out here?
            logger.logMsg(Logger.TRACEMSG, "executing with: inputName=" + inputName
                          + " xmlName=" + xmlName + " outputName=" + datalet.outputName
                          + " goldName=" + datalet.goldName + " flavor="  + datalet.flavor);

            // Simply have the wrapper do all the transforming
            //  or processing for us - we handle either normal .xsl 
            //  stylesheet tests or just .xml embedded tests
            long retVal = ProcessorWrapper.ERROR;
            if (null == datalet.inputName)
            {
                // presume it's an embedded test
                retVal = processorWrapper.processEmbeddedToFile(xmlName, datalet.outputName);
            }
            else
            {
                // presume it's a normal stylesheet test
                retVal = processorWrapper.processToFile(xmlName, inputName, datalet.outputName);
            }

            if (ProcessorWrapper.ERROR == retVal)
            {
                //@todo Should validate potential expectedExceptions here
                logger.checkFail(getDescription() + " " + datalet.getDescription()
                                 + "unexpected processToFile problem");

                return;
            }
            //@todo report out timing data for overall use?
        }
        catch (FileNotFoundException fnfe)
        {
            logger.checkFail(getDescription() + " " + datalet.getDescription() 
                             + " threw: " + fnfe.toString());
            // Don't bother logging the stacktrace here; not worth it
            return;
        }
        catch (Throwable t)
        {
            logger.checkFail(getDescription() + " " + datalet.getDescription() 
                             + " threw: " + t.toString());
            logThrowable(t, getDescription() + " " + datalet.getDescription());
            return;
        }

        // If we get here, attempt to validate the contents of 
        //  the last outputFile created
        //@todo allow passing in of preexisting checkService
        CheckService fileChecker = new XHTFileCheckService();
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
}  // end of class StylesheetTestlet

