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
import org.apache.qetest.xsl.StylesheetTestlet;
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
public class ExtensionTestlet extends StylesheetTestlet
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
     * Worker method to perform any pre-processing needed.  
     *
     * This optionally does deleteOutFile, then attempts to load 
     * a matching TestableExtension class that matches the datalet's 
     * stylesheet.  If one is found, we call preCheck on that too.
     *
     * @param datalet to test with
     */
    protected void testletInit(StylesheetDatalet datalet)
    {
        // Simply grab any superclass functionality first
        super.testletInit(datalet);
        
        // Now do custom initialization for extensions

        // See if we have a Java-based extension class
        // Side effect: fills in datalet.options
        findExtensionClass(datalet);

        // If found, ask the class to validate
        Class extensionClazz = (Class)datalet.options.get(TESTABLE_EXTENSION);
        if (null != extensionClazz)
        {
            boolean ignored = invokeMethodOn(extensionClazz, "preCheck", datalet);
        }
        else
        {
            logger.logMsg(Logger.TRACEMSG, "No extension class found");
        }
    }


    /** 
     * Worker method to validate output file with gold.  
     *
     * Logs out applicable info while validating output file.
     * Most commonly will call the underlying TestableExtension's 
     * postCheck method to get validation done.
     *
     * @param datalet to test with
     * @throws allows any underlying exception to be thrown
     */
    protected void checkDatalet(StylesheetDatalet datalet)
            throws Exception
    {
        // If we have an associated extension class, call postCheck
        // If found, ask the class to validate
        Class extensionClazz = (Class)datalet.options.get(TESTABLE_EXTENSION);
        if (null != extensionClazz)
        {
            boolean ignored = invokeMethodOn(extensionClazz, "postCheck", datalet);
        }
        else
        {
            // Have our parent class do it's own validation
            super.checkDatalet(datalet);
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

