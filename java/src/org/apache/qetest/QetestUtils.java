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
 * Also provides a simplistic Test/Testlet launching helper 
 * functionality.  Simply execute this class from the command 
 * line with a full or partial classname (in the org.apache.qetest 
 * area, obviously) and we'll load and execute that class instead.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public abstract class QetestUtils
{
    // abstract class cannot be instantiated

    /**
     * Utility method to translate a String filename to URL.  
     *
     * Note: This method is not necessarily proven to get the 
     * correct URL for every possible kind of filename; it should 
     * be improved.  It handles the most common cases that we've 
     * encountered when running Conformance tests on Xalan.
     * Also note, this method does not handle other non-file:
     * flavors of URLs at all.
     *
     * If the name is null, return null.
     * If the name starts with a common URI scheme (namely the ones 
     * found in the examples of RFC2396), then simply return the 
     * name as-is (the assumption is that it's already a URL)
     * Otherwise we attempt (cheaply) to convert to a file:/// URL.
     * 
     * @param String local path\filename of a file
     * @return a file:/// URL, the same string if it appears to 
     * already be a URL, or null if error
     */
    public static String filenameToURL(String filename)
    {
        // null begets null - something like the commutative property
        if (null == filename)
            return null;

        // Don't translate a string that already looks like a URL
        if (filename.startsWith("file:")
            || filename.startsWith("http:")
            || filename.startsWith("ftp:")
            || filename.startsWith("gopher:")
            || filename.startsWith("mailto:")
            || filename.startsWith("news:")
            || filename.startsWith("telnet:")
           )
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
        // Ensure we have the correct number of slashes at the 
        //  start: we always want 3 /// if it's absolute
        //  (which we should have forced above)
        if (tmp.startsWith("/"))
            return "file://" + tmp;
        else
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
     * for the classname specified in array order; if null then 
     * we don't search any additional packages
     * @param String defaultClassname a default known-good FQCN to 
     * return if the classname was not found
     *
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
     * Utility method to get a class name of a test.  
     * This is mainly a bit of syntactic sugar built on 
     * top of testClassForName.
     *
     * @param String classname FQCN or partially specified classname
     * that you wish to load
     * @param String[] rootPackages a list of packages to search 
     * for the classname specified in array order; if null then 
     * we don't search any additional packages
     * @param String defaultClassname a default known-good FQCN to 
     * return if the classname was not found
     *
     * @return name of class that testClassForName returns; 
     * or null if an error occoured
     */
    public static String testClassnameForName(String classname, 
                                         String[] rootPackages,
                                         String defaultClassname)
    {
        Class clazz = testClassForName(classname, rootPackages, defaultClassname);
        if (null == clazz)
            return null;
        else
            return clazz.getName();
    }


    /**
     * Utility method to create a unique runId.  
     * 
     * This is used to construct a theoretically unique Id for 
     * each run of a test script.  It is used later in some results 
     * analysis stylesheets to create comparative charts showing 
     * differences in results and timing data from one run of 
     * a test to another.
     *
     * Current format: MMddHHmm[;baseId]
     * where baseId is not used if null.
     * 
     * @param String Id base to start with
     * 
     * @return String Id to use; will include a timestamp
     */
    public static String createRunId(String baseId)
    {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat ("MMddHHmm");
        if (null != baseId)
            return formatter.format(new java.util.Date())+ ";" + baseId;
        else
            return formatter.format(new java.util.Date());
    }


    /**
     * Main method to run from the command line - this acts 
     * as a cheap launching mechanisim for Xalan tests.  
     * 
     * Simply finds the class specified in the first argument, 
     * instantiates one, and passes it any remaining command 
     * line arguments we were given.  
     * The primary motivation here is to provide a simpler 
     * command line for inexperienced  users.  You can either 
     * pass the FQCN, or just the classname and it will still 
     * get run.  Note the one danger is the order of package 
     * lookups and the potential for the wrong class to run 
     * when we have identically named classes in different 
     * packages - but this will usually work!
     * 
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.err.println("QetestUtils.main() ERROR in usage: must have at least one arg: classname [options]");
            return;
        }

        // Get the class specified by the first arg...
        Class clazz = QetestUtils.testClassForName(
                args[0], defaultPackages, null); // null = no default class
        if (null == clazz)
        {
            System.err.println("QetestUtils.main() ERROR: Could not find class:" + args[0]);
            return;
        }

        try
        {
            // ...find the main() method...
            Class[] parameterTypes = new Class[1];
            parameterTypes[0] = java.lang.String[].class;
            java.lang.reflect.Method main = clazz.getMethod("main", parameterTypes);
            
            // ...copy over our remaining cmdline args...
            final String[] appArgs = new String[(args.length) == 1 ? 0 : args.length - 1];
            if (args.length > 1)
            {
                System.arraycopy(args, 1, 
                                 appArgs, 0, 
                                 args.length - 1);
            }

            // ...and execute the method!
            Object[] mainArgs = new Object[1];
            mainArgs[0] = appArgs;
            main.invoke(null, mainArgs);
        }
        catch (Throwable t)
        {
            System.err.println("QetestUtils.main() ERROR: running " + args[0] 
                    + ".main() threw: " + t.toString());
            t.printStackTrace();
        }        
    }


    /** 
     * Default list of packages for xml-xalan tests.  
     * Technically this is Xalan-specific and should really be 
     * in some other directory, but I'm being lazy tonight.
     * This looks for Xalan-related tests in the following 
     * packages in <b>this order</b>:
     * <ul>
     * <li>org.apache.qetest.xsl</li>
     * <li>org.apache.qetest.xalanj2</li>
     * <li>org.apache.qetest.trax</li>
     * <li>org.apache.qetest.trax.dom</li>
     * <li>org.apache.qetest.trax.sax</li>
     * <li>org.apache.qetest.trax.stream</li>
     * <li>org.apache.qetest.xalanj1</li>
     * <li>org.apache.qetest</li>
     * <li>org.apache.qetest.qetesttest</li>
     * </ul>
     * Note the normal naming convention for automated tests 
     * is either *Test.java or *Testlet.java; although this is 
     * not required, it will make it easier to write simple 
     * test discovery mechanisims.
     */
    public static final String[] defaultPackages = 
    {
        "org.apache.qetest.xsl", 
        "org.apache.qetest.xalanj2", 
        "org.apache.qetest.trax", 
        "org.apache.qetest.trax.dom", 
        "org.apache.qetest.trax.sax", 
        "org.apache.qetest.trax.stream", 
        "org.apache.qetest.xalanj1", 
        "org.apache.qetest",
        "org.apache.qetest.qetesttest" 
    };

}
