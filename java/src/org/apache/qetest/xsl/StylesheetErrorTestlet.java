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

package org.apache.qetest.xsl;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.qetest.Datalet;
import org.apache.qetest.Logger;

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
public class StylesheetErrorTestlet extends StylesheetTestlet
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.StylesheetErrorTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new StylesheetDatalet(); }


    /**
     * Accesor method for a brief description of this test.  
     * @return String describing what this StylesheetErrorTestlet does.
     */
    public String getDescription()
    {
        return "StylesheetErrorTestlet";
    }


    /** 
     * Worker method to validate output file with gold; overridden
     * to call checkFail, since our definition of error tests 
     * is that they should have thrown an exception!  
     *
     * Logs out applicable info while validating output file.
     *
     * @param datalet to test with
     * @throws allows any underlying exception to be thrown
     */
    protected void checkDatalet(StylesheetDatalet datalet)
            throws Exception
    {
        // Just log a fail; error tests must throw exceptions
        logger.checkFail(datalet.getDescription() + " did not throw any exception");
        // Log a custom element with all the file refs
        // Closely related to viewResults.xsl select='fileref"
        //@todo check that these links are valid when base 
        //  paths are either relative or absolute!
        Hashtable attrs = new Hashtable();
        attrs.put("idref", (new File(datalet.inputName)).getName());
        try
        {
            attrs.put("baseref", System.getProperty("user.dir"));
        } 
        catch (Exception e) { /* no-op, ignore */ }
        
        attrs.put("inputName", datalet.inputName);
        attrs.put("xmlName", datalet.xmlName);
        attrs.put("outputName", datalet.outputName);
        attrs.put("goldName", datalet.goldName);
        logger.logElement(Logger.STATUSMSG, "fileref", attrs, "Conformance test file references");
    }


    /** 
     * Worker method to validate exceptions thrown by testDatalet.  
     *
     * Our implementation compares t.toString() with 
     * expectedException data from the stylesheet test.
     *
     * @param datalet to test with
     * @param e Throwable that was thrown
     */
    protected void handleException(StylesheetDatalet datalet, Throwable t)
    {
        // Get gold or reference info from the datalet
        Vector expectedExceptions = StylesheetDataletManager.getInfoItem(logger, datalet, StylesheetDataletManager.INFOITEM_EXPECTED_EXCEPTION);
        if ((null == expectedExceptions) || (0 == expectedExceptions.size()))
        {
            logger.logThrowable(Logger.INFOMSG, t, getDescription() + " " + datalet.getDescription());
            // No gold info available; report ambiguous and return
            logger.checkAmbiguous(getDescription() + " " + datalet.getDescription() 
                            + " no " + StylesheetDataletManager.INFOITEM_EXPECTED_EXCEPTION + " available!");
        }
        else
        {
            // Attempt to see if our throwable matches any gold data
            boolean foundExpected = false;
            for (Enumeration elements = expectedExceptions.elements();
                 elements.hasMoreElements(); 
                 /* no increment portion */)
            {
                // Maintenance Note: Ensure this will always be String data
                String expExc = (String)elements.nextElement();
                if (t.toString().indexOf(expExc) > -1)
                {
                    foundExpected = true;
                    break;
                }
            }
            if (foundExpected)
            {
                logger.checkPass(getDescription() + " " + datalet.getDescription() 
                                 + " expectedly threw: " + t.toString());
            }
            else
            {
                // Put the logThrowable first, so it appears before 
                //  the Fail record, and gets color-coded
                logger.logThrowable(Logger.ERRORMSG, t, getDescription() + " " + datalet.getDescription());
                logger.checkFail(getDescription() + " " + datalet.getDescription() 
                                 + " unexpectedly threw: " + t.toString());
            }
        }
    }

}  // end of class StylesheetErrorTestlet

