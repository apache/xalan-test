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

package org.apache.qetest;

import java.io.File;
import java.io.IOException;

/**
 * Static utility class for both general-purpose testing methods 
 * and a few XML-specific methods.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class QetestUtils
{

    /**
     * Utility method to translate a String filename to URL.  
     * Note: This method is not necessarily proven to get the 
     * correct URL for every possible kind of filename; it should 
     * be improved.  It handles the most common cases that we've 
     * encountered when running Conformance tests on Xalan.
     * Also note, this method does not handle other non-file:
     * flavors of URLs at all.
     * 
     * @param String local path\filename of a file
     * @return a file:/// URL, or null if error
     */
    public static String filenameToURL(String filename)
    {
        // null begets null - something like the commutative property
        if (null == filename)
            return null;

        // Don't translate a string that already looks like 
        //  a file: URL
        if (filename.startsWith("file:///"))
            return filename;

        File f = new File(filename);
        String tmp = null;
        try
        {
            // This normally gives a better path
            tmp = f.getCanonicalPath();
        }
        catch (IOException ioe)
        {
            // But this can be used as a backup, for cases 
            //  where the file does not exist, etc.
            tmp = f.getAbsolutePath();
        }

        // URLs must explicitly use only forward slashes
	    if (File.separatorChar == '\\') {
	        tmp = tmp.replace('\\', '/');
	    }
        // Note the presumption that it's a file reference
        return "file:///" + tmp;
    }


    /**
     * Utility method to get a testing Class object.  
     * This is mainly a bit of syntactic sugar to allow users 
     * to specify only the end parts of a package.classname 
     * and still have it loaded.  It basically does a 
     * Class.forName() search, starting with the provided 
     * classname, and if not found, searching through a list 
     * of root packages to try to find the class.
     *
     * Note the inherent danger when there are same-named 
     * classes in different packages, where the behavior will 
     * depend on the order of searchPackages.
     *
     * Commonly called like: 
     * <code>testClassForName("PerformanceTestlet", 
     * new String[] {"org.apache.qetest", "org.apache.qetest.xsl" },
     * "org.apache.qetest.StylesheetTestlet");</code>
     * 
     * @param String classname FQCN or partially specified classname
     * that you wish to load
     * @param String[] rootPackages a list of packages to search 
     * for the classname specified in array order
     * @param String defaultClassname a default known-good FQCN to 
     * return if the classname was not found
     * @return Class object asked for if one found by combining 
     * clazz with one of the rootPackages; if none, a Class of 
     * defaultClassname; or null if an error occoured
     */
    public static Class testClassForName(String classname, 
                                         String[] rootPackages,
                                         String defaultClassname)
    {
        // Ensure we have a valid classname, and try it
        if ((null != classname) && (classname.length() > 0))
        {
            // Try just the specified classname, in case it's a FQCN
            try
            {
                return Class.forName(classname);
            }
            catch (Exception e)
            {
                /* no-op, fall through */
            }

            // Now combine each of the rootPackages with the classname
            //  and see if one of them gets loaded
            if (null != rootPackages)
            {
                for (int i = 0; i < rootPackages.length; i++)
                {
                    try
                    {
                        return Class.forName(rootPackages[i] + "." + classname);
                    }
                    catch (Exception e)
                    {
                        /* no-op, continue */
                    }
                } // end for
            } // end if rootPackages...
        } // end if classname...

        // If we fell out here, try the defaultClassname
        try
        {
            return Class.forName(defaultClassname);
        }
        catch (Exception e)
        {
            // You can't always get you what you want
            return null;
        }
    }


    /** 
     * Constructor is private on purpose; this class 
     * provides static utility methods only. 
     */
    private QetestUtils() { /* no-op */ }
}
