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

import org.apache.qetest.CheckService;
import org.apache.qetest.Datalet;
import org.apache.qetest.Logger;
import org.apache.qetest.LoggingHandler;
import org.apache.qetest.QetestUtils;
import org.apache.qetest.TestletImpl;
import org.apache.qetest.trax.LoggingErrorListener;
import org.apache.qetest.xslwrapper.TransformWrapper;
import org.apache.qetest.xslwrapper.TransformWrapperFactory;
import org.apache.qetest.xslwrapper.TraxWrapperUtils; // semi-HACK: should make this more generic

import javax.xml.transform.ErrorListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.lang.reflect.Constructor;

/**
 * Testlet for testing of xsl stylesheets using a custom 
 * JAXP ErrorHandler.
 *
 * This class provides the testing algorithim used for verifying 
 * how a XSLT processor handles stylesheets with known expected 
 * errors conditions in them using a JAXP ErrorHandler.  Note that 
 * this testlet is effectively only applicable with 
 * TransformWrappers that wrap JAXP-compatible implementations.
 *
 * Attempts to separate validation between stylesheet parse/build 
 * errors and transform errors.
 *
 * //@todo better doc on our algorithim
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class ErrorHandlerTestlet extends StylesheetTestlet
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.ErrorHandlerTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new StylesheetDatalet(); }


    /**
     * Accesor method for a brief description of this test.  
     * @return String describing what this ErrorHandlerTestlet does.
     */
    public String getDescription()
    {
        return "ErrorHandlerTestlet";
    }


    /**
     * Our testing state: during stylesheet build or transform.
     */
    protected boolean duringXSLBuild = true;


    /** 
     * Worker method to actually perform the transform: overridden.  
     *
     * Explicitly builds a stylesheet first, then does transform.  
     * With duringXSLBuild state above, we can then validate 
     * when exceptions/errors are thrown.
     * Note: Does not properly handle embedded tests yet!
     *
     * @param datalet to test with
     * @param transformWrapper to have perform the transform
     * @throws allows any underlying exception to be thrown
     */
    protected void testDatalet(StylesheetDatalet datalet, TransformWrapper transformWrapper)
            throws Exception
    {
        //@todo Should we log a custom logElement here instead?
        logger.logMsg(Logger.TRACEMSG, "executing with: inputName=" + datalet.inputName
                      + " xmlName=" + datalet.xmlName + " outputName=" + datalet.outputName
                      + " goldName=" + datalet.goldName + " flavor="  + datalet.flavor);

        // Simply have the wrapper do all the transforming
        //  or processing for us - we handle either normal .xsl 
        //  stylesheet tests or just .xml embedded tests
        long retVal = 0L;
        if (null == datalet.inputName)
        {
            // presume it's an embedded test
            //@todo make this handle duringXSLBuild state!
            long [] times = transformWrapper.transformEmbedded(datalet.xmlName, datalet.outputName);
            retVal = times[TransformWrapper.IDX_OVERALL];
        }
        else
        {
            // presume it's a normal stylesheet test
            // First build the stylesheet
            duringXSLBuild = true;
            long[] times = transformWrapper.buildStylesheet(datalet.inputName);
            duringXSLBuild = false;
            times = transformWrapper.transformWithStylesheet(datalet.xmlName, datalet.outputName);
        }
    }


    /** 
     * Worker method to get a TransformWrapper: overridden.  
     *
     * @param datalet to test with
     * @return TransformWrapper to use with this datalet
     */
    protected TransformWrapper getTransformWrapper(StylesheetDatalet datalet)
    {
        try
        {
            TransformWrapper transformWrapper = TransformWrapperFactory.newWrapper(datalet.flavor);
            // Set our datalet's options as options in the wrapper
            // PLUS put in special key for our ErrorListener - this 
            //  will log any errors to our logger
            //@todo add expected data here as well so that we can 
            //  actually validate the specific errors logged
            ErrorListener listener = (ErrorListener)getLoggingHandler(datalet);
            datalet.options.put(TransformWrapper.SET_PROCESSOR_ATTRIBUTES + TraxWrapperUtils.SET_ERROR_LISTENER, 
                                listener);
            transformWrapper.newProcessor(datalet.options);
            return transformWrapper;
        }
        catch (Throwable t)
        {
            logger.logThrowable(Logger.ERRORMSG, t, getDescription() + " newWrapper/newProcessor threw");
            logger.checkErr(getCheckDescription(datalet) + " newWrapper/newProcessor threw: " + t.toString());
            return null;
        }
    }


    /** 
     * Worker method to get a specific ErrorListener for a datalet.  
     *
     * @param datalet to test with
     * @return LoggingHandler presumably suitable for use as 
     * a JAXP ErrorListener or SAX ErrorHandler
     */
    protected LoggingHandler getLoggingHandler(StylesheetDatalet datalet)
    {
        try
        {
            Class clazz = QetestUtils.testClassForName(datalet.options.getProperty("errorListener"), 
                                                        QetestUtils.defaultPackages, 
                                                        "org.apache.qetest.trax.LoggingErrorListener");
                                                
            // Get the class, find appropriate constructor, 
            //  munge together appropriate ctor args, and 
            //  call the constructor to get a LoggingHandler
            Class[] ctorTypes = new Class[1];
            ctorTypes[0] = Logger.class;
            Constructor ctor = clazz.getConstructor(ctorTypes);

            Object[] ctorArgs = new Object[1];
            ctorArgs[0] = (Object) logger;
            LoggingHandler handler = (LoggingHandler) ctor.newInstance(ctorArgs);
            if ((handler instanceof LoggingErrorListener) || 
                (handler instanceof LoggingSAXErrorHandler))
            {
                // Mimic DefaultErrorHandler behavior
                ((LoggingErrorListener)handler).setThrowWhen(LoggingErrorListener.THROW_ON_ERROR & LoggingErrorListener.THROW_ON_FATAL);
            }
            return handler;
        }
        catch (Throwable t)
        {
            logger.logThrowable(Logger.ERRORMSG, t, getDescription() + " newWrapper/newProcessor threw");
            logger.checkErr(getCheckDescription(datalet) + " newWrapper/newProcessor threw: " + t.toString());
            return null;
        }
    }
}  // end of class ErrorHandlerTestlet

