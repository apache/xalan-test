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
package org.apache.qetest.xsl;

import java.io.FilenameFilter;
import java.io.File;

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Returns directories that are either on an inclusion list, or
 * just ones that don't begin with [x|X], or are 'CVS'.
 * Rudimentary multiple inclusion dirs are now supported.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class ConformanceDirRules implements FilenameFilter
{

    /** Initialize for defaults (not using inclusion list) no-op. */
    public ConformanceDirRules(){}

    /**
     * Initialize with a case-sensitive Hash of directory names to include.  
     *
     * @param iDirs hash of inclusion dirs
     */
    public ConformanceDirRules(Hashtable iDirs)
    {
        setIncludeDirs(iDirs);
    }

    /**
     * Initialize with a case-insensitive String directory name(s) to include.  
     *
     * @param incDir semicolon-delimited string of inclusion dir(s)
     */
    public ConformanceDirRules(String incDir)
    {
        setIncludeDirs(incDir);
    }

    /**
     * Hash of directory names to include.
     * <p>Keys are dir names, values in hash are ignored. Note that
     * directory names are case-sensitive.  If list is not set somehow,
     * then we return all dirs that don't begin with [x|X].</p>
     */
    protected Hashtable includeDirs = null;

    /** Exclude CVS repository dirs always. */
    public static final String CVS = "CVS";

    /**
     * Accessor methods for case-sensitive Hash of directory names to include.  
     *
     * @param iDirs hash of inclusion dirs
     */
    public void setIncludeDirs(Hashtable iDirs)
    {

        if (iDirs != null)
            includeDirs = (Hashtable) iDirs.clone();
        else
            includeDirs = null;
    }

    /**
     * Accessor methods for case-sensitive Hash of directory names to include.  
     *
     * @return clone of our hash of inclusion dirs
     */
    public Hashtable getIncludeDirs()
    {

        if (includeDirs != null)
        {
            Hashtable tempHash = (Hashtable) includeDirs.clone();

            return (tempHash);
        }
        else
        {
            return null;
        }
    }

    /**
     * Accessor method to set a case-insensitive String directory name to include.  
     *
     * @param incDir semicolon-delimited string of inclusion dir(s)
     */
    public void setIncludeDirs(String incDir)
    {
        setIncludeDirs(incDir, false);
    }

    /**
     * Accessor method to set an optionally case-sensitive String directory name to include.
     * <p><b>Note:</b> simply uses .toUpperCase() and .toLowerCase() on the input string;
     * does not do full case-checking on the entire string!</p>
     *
     * @param incDir semicolon-delimited string of inclusion dir(s)
     * @param caseSensitive - should be obvious, shouldn't it?
     */
    public void setIncludeDirs(String incDir, boolean caseSensitive)
    {

        if ((incDir != null) && (incDir != ""))
        {
            includeDirs = null;
            includeDirs = new Hashtable();

            StringTokenizer st = new StringTokenizer(incDir, ";");
            while (st.hasMoreTokens())
            {
                String tmp = st.nextToken();
                // Value in hash is ignored
                includeDirs.put(tmp, Boolean.TRUE);
                if (!caseSensitive)
                {
                    includeDirs.put(tmp.toUpperCase(), Boolean.TRUE);
                    includeDirs.put(tmp.toLowerCase(), Boolean.TRUE);
                }
            }
        }
        else
        {
            includeDirs = null;
        }
    }

    /**
     * Tests if a specified file should be included in a file list.
     * Returns only directories that are on our inclusion list.
     * Currently may be case-sensitive or insensitive, depending on
     * how/if our inclusion list was set.
     * @param dir the directory in which the file was found.
     * @param name the name of the file.
     * @return <code>true</code> if the name should be included in the file list; <code>false</code> otherwise.
     * @since JDK1.0
     */
    public boolean accept(File dir, String name)
    {

        // Shortcut to only look at directories
        File file = new File(dir, name);

        if (!file.isDirectory())
            return (false);

        // If we have an inclusion list, just look at that
        if (includeDirs != null)
        {
            if (includeDirs.containsKey(name))
                return (true);
            else
                return (false);
        }

        // Otherwise, exclude any other names that begin with [x|X]
        char firstChar = name.charAt(0);

        if ((firstChar == 'x') || (firstChar == 'X'))
            return (false);
        else if (CVS.equals(name))  // ALSO: exclude "CVS" dirs from our source control
            return (false);
        else
            return (true);
    }
}
