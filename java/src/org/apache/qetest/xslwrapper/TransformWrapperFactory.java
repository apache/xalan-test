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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.qetest.xslwrapper;

import org.apache.qetest.QetestUtils;

import java.io.InputStream;
import java.io.IOException;

import java.util.Properties;

/**
 * Factory static class for creating TransformWrappers.
 *
 * Includes a 'wrapperMapper' functionality to simplify specifying 
 * the 'flavor' of a wrapper to create.  This is optional, but will 
 * allow a user to specify newWrapper("trax.file") and get back an 
 * instance of an org.apache.qetest.xslwrapper.TraxFileWrapper.
 *
 * @author Shane Curcuru
 * @version $Id$
 */
public abstract class TransformWrapperFactory
{

    /**
     * Currently known list of implemented wrappers.
     *
     * Allows specification of a simple name for each wrapper,
     * so clients don't necessarily have to know the FQCN or
     * fully qualified classname of their wrapper.</p>
     *
     * <ul>Where:
     * <li>key = String simple name = xalan|trax|etc...</li>
     * <li>value = String FQCN of wrapper implementation</li>
     * <li>Supports: See TransformWrapperFactory.properties</li>
     * </ul>
     */
    protected static Properties wrapperMapper = null;


    /** 
     * Static initializer for wrapperMapper.  
     * Attempts to load TransformWrapperFactory.properties into 
     * our wrapperMapper.
     */
    static
    {
        wrapperMapper = new Properties();

        // Add a default trax flavor, since it's widely used
        // This should be overwritten below if the properties 
        //  file can be loaded
        wrapperMapper.put("trax", "org.apache.qetest.xslwrapper.TraxFileWrapper");

        try
        {
            InputStream is =
                TransformWrapperFactory.class.getResourceAsStream(
                    "TransformWrapperFactory.properties");

            wrapperMapper.load(is);
            is.close();
        }
        catch (Exception e)
        {
            System.err.println(
                "TransformWrapperFactory.properties.load() threw: "
                + e.toString());
            e.printStackTrace();
        }
    };


    /**
     * Accessor for our wrapperMapper.
     *
     * @return Properties block of wrapper implementations
     * that we implicitly know about; may be null if we have none
     */
    public static final Properties getDescriptions()
    {
        return wrapperMapper;
    }


    /**
     * Return an instance of a TransformWrapper of requested flavor.
     *
     * This static factory method creates TransformWrappers for the 
     * user.  Our 'wrapperMapper' functionality allows users to 
     * either specify short names listed in 
     * TransformWrapperFactory.properties (which are mapped to the 
     * appropriate FQCN's) or to directly supply the FQCN of a 
     * wrapper to create.
     *
     * @param flavor to create, either a wrapperMapped one or FQCN
     *
     * @return instance of a wrapper; will throw an 
     * IllegalArgumentException if we cannot find the asked-for 
     * wrapper class anywhere
     */
    public static final TransformWrapper newWrapper(String flavor)
    {

        // Attempt to lookup the flavor: if found, use the 
        //  value we got, otherwise default to the same value
        String className = wrapperMapper.getProperty(flavor, flavor);
        
        try
        {
            // Allow people to use bare classnames in popular packages
            Class clazz = QetestUtils.testClassForName(className, 
                               QetestUtils.defaultPackages,
                               "Wrapper Not Found");
            
            TransformWrapper wrapper = (TransformWrapper) clazz.newInstance();
            return wrapper;
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("newWrapper(" + flavor + ") threw: " + e.toString());
        }
    }


    /**
     * Simplistic command line support merely prints out wrapperMapper.
     *
     * @param args command line args - unused
     */
    public static void main(String[] args)
    {
        Properties p = getDescriptions();
        System.out.println("TransformWrapperFactory: installed flavors=wrappers");
        p.list(System.out);
    }
}
