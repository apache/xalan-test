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
 * TestableExtension.java
 *
 */
package org.apache.qetest.xsl;
import org.apache.qetest.Logger;
import org.apache.qetest.CheckService;
import org.apache.qetest.xsl.StylesheetDatalet;


/**
 * Simple base class defining test hooks for Java extensions.  
 *
 * Currently provides generic but limited verification for 
 * basic extensions in the context of being called from standard 
 * transformations.  I would have preferred an interface, but then 
 * methods couldn't be static, which makes validation simpler 
 * since the test doesn't have to try to grab the same instance of 
 * an extension that the transformer used.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class TestableExtension
{
    /** 
     * Perform and log any pre-transformation info.  
     *
     * The extension should log out to the logger brief info 
     * about what the extension does.  It may also use items in 
     * the datalet to perform additional validation (like ensuring 
     * that counters are zeroed, etc.).
     *
     * TestableExtensions should validate any output files needed in 
     * their postCheck method; ExtensionTestlets simply rely on this 
     * method to both validate the internal actions of the extension 
     * as well as any output files, as needed.
     *
     * This should only return false if some horrendous error with 
     * the extension appears to have occoured; it will likely prevent 
     * any callers from completing the test.
     * 
     * @return true if OK; false if any fatal error occoured
     * @param logger Logger to dump any info to
     * @param datalet Datalet of current stylesheet test
     */
    public static boolean preCheck(Logger logger, StylesheetDatalet datalet) 
    {
        /* Must be overridden */
        return false;
    }
    

    /** 
     * Perform and log any post-transformation info.  
     * 
     * The extension should validate that it's extension was 
     * properly called.  It should also validate any output files
     * specified in the datalet, etc.  It may also 
     * validate against extension-specific data in the options 
     * (like validating that some extension call was hit a 
     * certain number of times or the like).
     * 
     * @param logger Logger to dump any info to
     * @param datalet Datalet of current stylesheet test
     */
    public static void postCheck(Logger logger, StylesheetDatalet datalet)
    {
        /* Must be overridden */
        return;
    }


    /**
     * Description of what this extension does.  
     * @return String description of extension
     */
    public static String getDescription()
    {
        return "Must be overridden";
    }

}  // end of class TestableExtension

