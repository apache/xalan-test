/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights 
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

package org.apache.qetest;

import java.io.File;
import java.util.Hashtable;

/**
 * Testlet superclass for testing URI or file based resources.
 *
 * This class is broken up into common worker methods to make 
 * subclassing easier for alternate testing algoritims.  
 * Individual subclasses may be implemented to test various 
 * programs that somehow can be tested by taking an input file 
 * and processing it, creating an output file, and then comparing 
 * the output to a known good gold file.
 *
 * @author Shane_Curcuru@us.ibm.com
 * @version $Id$
 */
public class FileTestlet extends TestletImpl
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.FileTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new FileDatalet(); }

    /**
     * Accesor method for a brief description of this test.  
     *
     * @return String describing what this FileTestlet does.
     */
    public String getDescription()
    {
        return "FileTestlet";
    }


    /**
     * Run this FileTestlet: execute it's test and return.
     *
     * Calls various worker methods to perform the basic steps of:
     * initDatalet, testDatalet, checkDatalet;  
     * catch... {handleException}
     *
     * @param Datalet to use as data point for the test.
     */
    public void execute(Datalet d)
	{
        // Ensure we have the correct kind of datalet
        FileDatalet datalet = null;
        try
        {
            datalet = (FileDatalet)d;
        }
        catch (ClassCastException e)
        {
            logger.checkErr("Datalet provided is not a FileDatalet; cannot continue with " + d);
            return;
        }

        // Perform any general setup needed...
        if (!initDatalet(datalet))
            return;
            
        try
        {
            // Perform the operation on the product under test..
            testDatalet(datalet);

            // ...and compare with gold data
            checkDatalet(datalet);
        }
        // Handle any exceptions from the testing
        catch (Throwable t)
        {
            handleException(datalet, t);
            return;
        }
	}


    /** 
     * Worker method to perform any initialization needed.  
     *
     * Subclasses might pre-verify the existence of the input and 
     * gold files or delete any pre-existing outputs in the 
     * datalet before testing.
     *
     * @param datalet to test with
     * @return true if OK, false if test should be aborted; if 
     * false, this method must log a fail or error
     */
    protected boolean initDatalet(FileDatalet datalet)
    {
        //@todo validate our Datalet - ensure it has valid 
        //  and/or existing files available.

        // Cleanup outName only if asked to - delete the file on disk
        // Optimization: this takes extra time and often is not 
        //  needed, so only do this if the option is set
        if ("true".equalsIgnoreCase(datalet.getOptions().getProperty("deleteOutFile")))
        {
            String output = datalet.getOutput();
            try
            {
                boolean btmp = (new File(output)).delete();
                logger.logMsg(Logger.TRACEMSG, "Deleting OutFile of::" + output
                                     + " status: " + btmp);
            }
            catch (SecurityException se)
            {
                logger.logMsg(Logger.WARNINGMSG, "Deleting OutFile of::" + output
                                       + " threw: " + se.toString());
            }
        }
        return true;
    }


    /** 
     * Worker method to actually perform the test operation.  
     *
     * Subclasses must (obviously) override this.  They should 
     * perform whatever actions are needed to process the input 
     * into an output, logging any status along the way.  
     * Note that validation of the output file is handled later 
     * in checkDatalet.
     *
     * @param datalet to test with
     * @throws allows any underlying exception to be thrown
     */
    protected void testDatalet(FileDatalet datalet)
            throws Exception
    {
        logger.logMsg(Logger.TRACEMSG, getCheckDescription(datalet));

        // Perform the test operation here - you must subclass this!
    }


    /** 
     * Worker method to validate output resource with gold.  
     *
     * Logs out applicable info while validating output file.  
     * Attempts to use datalet.getOptions.get("fileCheckerImpl")
     * or datalet.getOptions.getProperty("fileChecker") to get 
     * a CheckService for the output; if that fails, it uses a 
     * default QetestFactory.newCheckService().
     *
     * @param datalet to test with
     * @throws allows any underlying exception to be thrown
     */
    protected void checkDatalet(FileDatalet datalet)
            throws Exception
    {
        // See if the datalet already has a fileChecker to use...
        CheckService fileChecker = (CheckService)datalet.getOptions().get("fileCheckerImpl");
        // ...if not, look for a default classname to use...
        if (null == fileChecker)
        {
            String clazzName = datalet.getOptions().getProperty("fileChecker");
            if (!(null == clazzName))
            {
                // ...find and create a class of the default classname given
                Class fClazz = QetestUtils.testClassForName(clazzName, QetestUtils.defaultPackages, null);
                fileChecker = (CheckService)fClazz.newInstance();
            }
            else
            {
                //...If all else failed, simply get a default one from the factory
                fileChecker = QetestFactory.newCheckService(logger, QetestFactory.TYPE_FILES);
            }
            // Apply any testing options to the fileChecker
            fileChecker.applyAttributes(datalet.getOptions());    
        }

        int result = fileChecker.check(logger,
                                 new File(datalet.getOutput()), 
                                 new File(datalet.getGold()), 
                                 getCheckDescription(datalet));

        //@todo if needed, we can put additional processing here 
        //  to output special logging in case of results that are fail                                 
    }


    /** 
     * Worker method to validate or log exceptions thrown by testDatalet.  
     *
     * Provided so subclassing is simpler; our implementation merely 
     * calls checkErr and logs the exception.
     *
     * @param datalet to test with
     * @param e Throwable that was thrown
     */
    protected void handleException(FileDatalet datalet, Throwable t)
    {
        // Put the logThrowable first, so it appears before 
        //  the Fail record, and gets color-coded
        logger.logThrowable(Logger.ERRORMSG, t, getCheckDescription(datalet) + " threw");
        logger.checkErr(getCheckDescription(datalet) 
                         + " threw: " + t.toString());
    }


    /** 
     * Worker method to construct a description.  
     *
     * Simply concatenates useful info to override getDescription().
     *
     * @param datalet to test with
     * @return simple concatenation of our desc and datalet's desc
     */
    protected String getCheckDescription(FileDatalet datalet)
    {
        return getDescription() 
                + ": "
                + datalet.getDescription();
    }
}  // end of class FileTestlet

