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
 * ExtensionTestlet.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.CheckService;
import org.apache.qetest.Datalet;
import org.apache.qetest.Logger;
import org.apache.qetest.QetestUtils;
import org.apache.qetest.TestletImpl;
import org.apache.qetest.xsl.StylesheetDatalet;
import org.apache.qetest.xsl.XHTFileCheckService;
import org.apache.qetest.xslwrapper.TransformWrapper;
import org.apache.qetest.xslwrapper.TransformWrapperFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * Testlet for testing xsl stylesheet extensions.  
 *
 * This class provides the testing algorithim used for verifying 
 * Xalan-specific extensions, primarily by transforming stylesheets 
 * that use extensions and optionally by allowing any Java-based 
 * extension classes to verify themselves and log out info.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class ExtensionTestlet extends TestletImpl
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.ExtensionTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new StylesheetDatalet(); }

    /** Convenience constant: Property key for Java classnames.  */
    public static final String JAVA_CLASS_NAME = "java.class.name";

    /** Convenience constant: Property key for TestableExtension objects.  */
    public static final String TESTABLE_EXTENSION = "testable.extension";

    /**
     * Accesor method for a brief description of this test.  
     * @return String describing what this ExtensionTestlet does.
     */
    public String getDescription()
    {
        return "ExtensionTestlet";
    }


    /**
     * Run this ExtensionTestlet: execute it's test and return.
     *
     * @param Datalet to use as data point for the test.
     */
    public void execute(Datalet d)
	{
        // Common: ensure cast to StylesheetDatalet
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

        // Common: generic pre-logging
        logger.logMsg(Logger.STATUSMSG, "About to test: " 
                      + (null == datalet.inputName
                         ? datalet.xmlName
                         : datalet.inputName));

        // Continue if our Datalet is OK, and after we've performed 
        //  any other pre-transform steps...
        if (preCheck(datalet))
        {
            // ... then just perform a transform ...
            if (doTransform(datalet))
            {
                // ... and if that's OK, then do verification
                postCheck(datalet);
            }
        }
	}


    /**
     * Perform any pre-transform validation or logging.  
     * This optionally does deleteOutFile, then attempts to load 
     * a matching TestableExtension class that matches the datalet's 
     * stylesheet.  If one is found, we call preCheck on that too.
     * 
     * @param d datalet to use for testing
     */
    protected boolean preCheck(StylesheetDatalet datalet)
    {
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

        // See if we have a Java-based extension class
        // Side effect: fills in datalet.options
        findExtensionClass(datalet);

        // If found, ask the class to validate
        Class extensionClazz = (Class)datalet.options.get(TESTABLE_EXTENSION);
        if (null != extensionClazz)
        {
            return invokeMethodOn(extensionClazz, "preCheck", datalet);
        }
        else
        {
            logger.logMsg(Logger.TRACEMSG, "No extension class found");
            return true; // This currently isn't fatal; you can 
                         //  still run these tests
        }
    }


    /**
     * Perform just the transformation itself.  
     * This is generic to most testlets and was just copied from 
     * the body of StylesheetTestlet.
     * 
     * Accesses our class member logger.
     * @param d datalet to use for testing
     */
    protected boolean doTransform(StylesheetDatalet datalet)
    {
        // Just perform the transform and log it; don't verify yet
        TransformWrapper transformWrapper = null;
        try
        {
            transformWrapper = TransformWrapperFactory.newWrapper(datalet.flavor);
            // Set our datalet's options as options in the wrapper
            transformWrapper.newProcessor(datalet.options);
        }
        catch (Throwable t)
        {
            logger.logThrowable(Logger.ERRORMSG, t, getDescription() + " newWrapper/newProcessor threw");
            logger.checkErr(getDescription() + " newWrapper/newProcessor threw: " + t.toString());
            return false;
        }

        // Transform our supplied input file
        try
        {
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
                long [] times = transformWrapper.transformEmbedded(datalet.xmlName, datalet.outputName);
                retVal = times[TransformWrapper.IDX_OVERALL];
            }
            else
            {
                // presume it's a normal stylesheet test
                long[] times = transformWrapper.transform(datalet.xmlName, datalet.inputName, datalet.outputName);
                retVal = times[TransformWrapper.IDX_OVERALL];
            }
            return true;
        }
        catch (Throwable t)
        {
            // Put the logThrowable first, so it appears before 
            //  the Fail record, and gets color-coded
            logger.logThrowable(Logger.ERRORMSG, t, getDescription() + " " + datalet.getDescription());
            logger.checkFail(getDescription() + " " + datalet.getDescription() 
                             + " threw: " + t.toString());
            return false;
        }
    }


    /**
     * Perform any post-transform validation or logging.  
     * 
     * Accesses our class member logger.
     * @param d datalet to use for testing
     */
    protected boolean postCheck(StylesheetDatalet datalet)
    {
        // If we have an associated extension class, call postCheck
        // If found, ask the class to validate
        Class extensionClazz = (Class)datalet.options.get(TESTABLE_EXTENSION);
        if (null != extensionClazz)
        {
            return invokeMethodOn(extensionClazz, "postCheck", datalet);
        }
        else
        {
            // Simply validate the output files ourselves, as normal
            CheckService fileChecker = (CheckService)datalet.options.get("fileCheckerImpl");
            // Supply default value
            if (null == fileChecker)
                fileChecker = new XHTFileCheckService();
            if (Logger.PASS_RESULT
                != fileChecker.check(logger,
                                     new File(datalet.outputName), 
                                     new File(datalet.goldName), 
                                     "Extension test of " + datalet.getDescription())
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
                logger.logElement(Logger.STATUSMSG, "fileref", attrs, "Extension test file references");
            }
            
            return true; // This currently isn't fatal; you can 
                         //  still run these tests
        }
    }


    /**
     * Worker method: Try to find a matching .class file for this .xsl.  
     * 
     * Accesses our class member logger.
     * @param d datalet to use for testing
     */
    protected void findExtensionClass(StylesheetDatalet datalet)
    {
        // Find the basename of the stylesheet
        String classname = null;
        if (null != datalet.inputName)
        {
            classname = datalet.inputName.substring(0, datalet.inputName.indexOf(".xsl"));
        }
        else
        {
            classname = datalet.xmlName.substring(0, datalet.xmlName.indexOf(".xml"));
        }
        
        // Also rip off any pathing info if it's found
        classname = classname.substring(classname.lastIndexOf(File.separator) + 1);
            
        try
        {
            //@todo future work: since these Java extensions are all 
            //  packageless, figure out a better way to reduce name 
            //  collisions - perhaps allow as org.apache.qetest.something
            Class extensionClazz = Class.forName(classname);
            logger.logMsg(Logger.TRACEMSG, "findExtensionClass found for " 
                    + classname + " which is " + extensionClazz.getName());

            // Ensure the class is a TestableExtension
            if ((TestableExtension.class).isAssignableFrom((Class)extensionClazz))
            {
                // Store info about class in datalet
                datalet.options.put(JAVA_CLASS_NAME, extensionClazz.getName());
                datalet.options.put(TESTABLE_EXTENSION, extensionClazz);
            }
            else
            {
                logger.logMsg(Logger.STATUSMSG, "findExtensionClass was not a TestableExtension, was: " + extensionClazz);
            }
        } 
        catch (Exception e)
        {
            logger.logMsg(Logger.INFOMSG, "findExtensionClass not found for " + classname);
        }
    }


    /**
     * Worker method: Call a method on this extension.  
     * Only works for preCheck/postCheck, since they have the 
     * proper method signatures.
     * 
     * Accesses our class member logger.
     * @param extensionClazz Class that's assumed to be a TestableExtension
     * @param methodName method to invoke
     * @param datalet to pass to method
     */
    protected boolean invokeMethodOn(Class extensionClazz, 
            String methodName, StylesheetDatalet datalet)
    {
        try
        {
            Class[] parameterTypes = new Class[2];
            parameterTypes[0] = Logger.class;
            parameterTypes[1] = StylesheetDatalet.class;
            Method method = extensionClazz.getMethod(methodName, parameterTypes);
    
            // Call static method to perform pre-transform validation
            // Pass on the datalet's options in case it uses them
            Object[] parameters = new Object[2];
            parameters[0] = logger;
            parameters[1] = datalet;
            Object returnValue = method.invoke(null, parameters);
            // If the method returned something, return that ..
            if ((null != returnValue)
                && (returnValue instanceof Boolean))
            {
                return ((Boolean)returnValue).booleanValue();
            }
            else
            {
                // .. otherwise just return true by default
                return true;
            }
        }
        catch (Exception e)
        {
            logger.logThrowable(Logger.WARNINGMSG, e, "invokeMethodOn(" + methodName + ") threw");
            logger.checkErr("invokeMethodOn(" + methodName + ") threw: " + e.toString());
            return false;
        }
    }

}  // end of class ExtensionTestlet

