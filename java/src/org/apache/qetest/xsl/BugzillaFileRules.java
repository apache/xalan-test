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

import java.io.FilenameFilter;
import java.io.File;

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Bugzilla-specific file filter: .java or .xsl files.
 * Has crude support for an excludes list of filename bases.
 * @see #accept(File, String)
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class BugzillaFileRules implements FilenameFilter
{

    /** Initialize for defaults (not using exclusion list) no-op. */
    public BugzillaFileRules(){}

    /**
     * Initialize with a case-sensitive Hash of file names to exclude.  
     *
     * @param excludesHash - keys are basenames of files to exclude
     */
    public BugzillaFileRules(Hashtable excludesHash)
    {
        setExcludes(excludesHash);
    }

    /**
     * Initialize with a case-insensitive semicolon-delimited String of file names to exclude.  
     *
     * @param excludesStr is specific file names to exclude
     */
    public BugzillaFileRules(String excludesStr)
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
     * @param exFiles hash keys are filenames to exclude
     */
    public void setExcludes(Hashtable exFiles)
    {

        if (exFiles != null)
            excludeFiles = (Hashtable) exFiles.clone();
        else
            excludeFiles = null;
    }

    /**
     * Accessor methods to set a case-sensitive Hash of file names to exclude.  
     *
     * @return clone of our excludes hash
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
     * @param exFiles are specific file names to exclude
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
     * @param exFiles is specific file names to exclude
     * @param caseSensitive is we should attempt to be
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
     * <p>Returns true for: filenames that begin with the directory 
     * name, and: are *.java, or are *.xsl.  If one of each exists, 
     * then we only return true for the *.java file (and not for the 
     * *.xsl file).</p>
     * <p>The essence here is that we return only one file for each 
     * conceptual 'test' that the user wrote.  If they only wrote a 
     * .xsl stylesheet, we return that.  If they only wrote a .java 
     * file (presumably a Testlet), we return that.  If they wrote 
     * both, only return the .java file, since it should have all the 
     * logic necessary to run the test, including hardcoded .xsl 
     * file name.  In this case, it might not be a valid test to 
     * simply transform the .xsl file because the Testlet may 
     * expect that parameters are set, etc.</p>
     * <p><b>Except:</b> if any filenames contain an item 
     * in excludeFiles.</p>
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
        // Skip any dirs
        if (file.isDirectory())
            return false;
    
        // Only accept files that start with 'bugzilla'
        // HACK: we should really look at the last part of the 
        //  directory name here, but in this one case it's much 
        //  easier to just hard-code the name (this is in 
        //  response to specifying inputDir=tests/bugzilla 
        //  on a Windows platform)
        if (!(name.toLowerCase().startsWith("bugzilla")))
            return false;
            
        // Accept any .java files
        if (name.toLowerCase().endsWith("java"))
            return true;
            
        // If it's a .xsl file..
        if (name.toLowerCase().endsWith("xsl"))
        {
            // Construct matching foo.java from foo.xsl (xsl len = 3)
            File matchingJava = new File(dir, 
                    name.substring(0, (name.length() - 3)) + "java");
            // ..Only accept if matchingJava does not exist        
            return !matchingJava.exists();
        }
            
        // Fall-through: doesn't match, return false
        return false;
    }
}
