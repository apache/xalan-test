/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights 
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
 * ConformanceTest.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.*;

import java.io.File;
import java.io.FilenameFilter;

import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * New, Improved! ConformanceTest.
 * <p>Note that other than setting up our name and comment, and determining
 * the processor's description, we simply use all the default implementations
 * of methods from XSLDirectoryIterator.</p>
 * @author shane_curcuru@lotus.com
 */
public class ConformanceTest extends XSLDirectoryIterator
{

    /**
     * Default constructor - initialize testName, Comment.
     */
    public ConformanceTest()
    {

        testName = "ConformanceTest";
        testComment =
            "Iterates over all conf test dirs and validates outputs";
    }

    /**
     * Initialize this test - setup description and createNewProcessor.  
     *
     * NEEDSDOC @param p
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean doTestFileInit(Properties p)
    {

        // Create a processor with our appropriate flavor, etc.
        if (!createNewProcessor())
        {
            reporter.logErrorMsg(
                "Could not createNewProcessor before testing; it won't work!");

            return false;
        }

        return true;
    }

    /**
     * Run through the directory given to us and run tests found
     * in subdirs; or run through our fileList.
     *
     * NEEDSDOC @param p
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean runTestCases(Properties p)
    {

        // The ConformanceTest simply uses all the default processing methods 
        //  from our parent XSLDirectoryIterator
        // Note that we skip executeTests and the number of 
        //  test cases we have, since we just iterate over 
        //  fileLists or directories
        processTestDir();

        return true;
    }

    /**
     * Main method to run test from the command line - can be left alone.  
     *
     * NEEDSDOC @param args
     */
    public static void main(String[] args)
    {

        ConformanceTest app = new ConformanceTest();

        app.doMain(args);
    }
}  // end of class ConformanceTest

