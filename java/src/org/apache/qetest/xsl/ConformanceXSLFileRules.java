/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights 
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
import java.io.FilenameFilter;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Simple file filter; returns *.xsl non-dir files.
 * Has crude support for an excludes list of filename bases.
 * Copied from ConformanceFileRules, used for ad-hoc testing 
 * where you don't want to rename files to match dir names.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class ConformanceXSLFileRules implements FilenameFilter
{

    /** Initialize for defaults (not using inclusion list) no-op. */
    public ConformanceXSLFileRules(){}

    /**
     * Initialize with a case-sensitive Hash of file names to exclude.  
     *
     * @param excludesHash where keys are case-sensitive filenames 
     * to exclude; values are ignored
     */
    public ConformanceXSLFileRules(Hashtable excludesHash)
    {
        setExcludes(excludesHash);
    }

    /**
     * Initialize with a case-insensitive semicolon-delimited String of file names to exclude.  
     *
     * @param excludesStr well, mostly case-insensitive string
     */
    public ConformanceXSLFileRules(String excludesStr)
    {
        setExcludes(excludesStr);
    }

    /**
     * Hash of file name portions to exclude.
     * <p>Keys are base file names, values in hash are ignored. Note that
     * file names may be case-sensitive.</p>
     * <p>Note that we will exclude any filename in our excludes.</p>
     */
    protected Hashtable excludeFiles = null;

    /**
     * Accessor methods to set a case-sensitive Hash of file names to exclude.  
     *
     * @param exFiles where keys are case-sensitive filenames 
     * to exclude; values are ignored
     */
    public void setExcludes(Hashtable exFiles)
    {

        if (exFiles != null)
            excludeFiles = (Hashtable) exFiles.clone();
        else
            excludeFiles = null;
    }

    /**
     * Accessor methods to get a case-sensitive Hash of file names to exclude.  
     *
     * @return cloned Hashtable with keys of filenames to exclude
     */
    public Hashtable getExcludes()
    {

        if (excludeFiles != null)
        {
            Hashtable tempHash = (Hashtable) excludeFiles.clone();

            return tempHash;
        }
        else
        {
            return null;
        }
    }

    /**
     * Accessor method to set a list of case-insensitive String
     * directory name(s) to exclude.
     * Names should be separated by {@link #SEPARATOR semicolon}.
     *
     * @param excludesStr well, mostly case-insensitive string
     */
    public void setExcludes(String exFiles)
    {
        setExcludes(exFiles, false);
    }

    /** Semicolon separator for {@link #setExcludes(java.lang.String)}. */
    public static final String SEPARATOR = ";";

    /**
     * Accessor method to set an optionally case-sensitive String file name(s) to exclude.
     * <p><b>Note:</b> simply uses .toUpperCase() and .toLowerCase() on the input string(s);
     * does not do full case-checking on the entire string!</p>
     *
     * @param exFiles  well, mostly case-insensitive string
     * @param caseSensitive if we should add the 
     * toUpperCase() and toLowerCase() or not
     */
    public void setExcludes(String exFiles, boolean caseSensitive)
    {

        StringTokenizer st = new StringTokenizer(exFiles, SEPARATOR);

        excludeFiles = null;
        excludeFiles = new Hashtable();

        for (int i = 0; st.hasMoreTokens(); i++)
        {
            String fName = st.nextToken();

            excludeFiles.put(fName, "");

            if (!caseSensitive)
            {
                excludeFiles.put(fName.toUpperCase(), "");
                excludeFiles.put(fName.toLowerCase(), "");
            }
        }
    }

    /**
     * Tests if a specified file should be included in a file list.
     * <p>Returns true only for *.xsl files.
     * <b>Except:</b> if any filenames contain an item in excludeFiles.</p>
     * @param dir the directory in which the file was found.
     * @param name the name of the file.
     * @return <code>true</code> if the name should be included in the file list; <code>false</code> otherwise.
     * @since JDK1.0
     */
    public boolean accept(File dir, String name)
    {

        // Shortcuts for bogus filenames and dirs
        if (name == null || dir == null)
            return false;

        // Exclude any files that match an exclude rule
        if ((excludeFiles != null) && (excludeFiles.containsKey(name)))
            return false;

        File file = new File(dir, name);

        return (!file.isDirectory() && name.toLowerCase().endsWith(".xsl"));
    }
}
