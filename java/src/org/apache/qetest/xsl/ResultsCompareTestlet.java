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
 * ResultsCompareTestlet.java
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
 * Testlet for just comparing results of test runs.
 *
 * This class provides a simple way to compare two result 
 * trees, without actually invoking the processor.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class ResultsCompareTestlet extends StylesheetTestlet
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.ResultsCompareTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new StylesheetDatalet(); }

    /**
     * Accesor method for a brief description of this test.  
     *
     * @return String describing what this ResultsCompareTestlet does.
     */
    public String getDescription()
    {
        return "ResultsCompareTestlet";
    }

    /** 
     * Worker method merely compares results.  
     *
     * Logs out applicable info; ignores transformation, 
     * compares results in goldDir with outputDir.
     *
     * @param datalet to test with
     * @param transformWrapper to have perform the transform
     * @throws allows any underlying exception to be thrown
     */
    protected void testDatalet(StylesheetDatalet datalet, TransformWrapper transformWrapper)
            throws Exception
    {
        //@todo Should we log a custom logElement here instead?
        logger.logMsg(Logger.TRACEMSG, "ResultsCompare of: outputName=" + datalet.outputName
                      + " goldName=" + datalet.goldName);

        // If we get here, attempt to validate the contents of 
        //  the last outputFile created
        CheckService fileChecker = (CheckService)datalet.options.get("fileCheckerImpl");
        // Supply default value
        if (null == fileChecker)
            fileChecker = new XHTFileCheckService();
        // Apply any testing options to the fileChecker
        // Note: for the overall conformance test case, this is 
        //  a bit inefficient, but we don't necessarily know if 
        //  the checkService has already been setup or not    
        fileChecker.applyAttributes(datalet.options);    
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
            // No longer need to log failure reason, this kind 
            //  of functionality should be kept in checkServices
        }
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
    protected void handleException(StylesheetDatalet datalet, Throwable t)
    {
        // Put the logThrowable first, so it appears before 
        //  the Fail record, and gets color-coded
        logger.logThrowable(Logger.ERRORMSG, t, getDescription() + " " + datalet.getDescription());
        logger.checkFail(getDescription() + " " + datalet.getDescription() 
                         + " threw: " + t.toString());
    }
}  // end of class ResultsCompareTestlet

