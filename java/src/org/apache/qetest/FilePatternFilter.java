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
package org.apache.qetest;

import java.io.File;

/**
 * FilenameFilter supporting includes/excludes returning files
 * that match a specified pattern.
 *
 * <p>Returns files of *ext that match our includes/excludes.</p>
 *
 * @author shane_curcuru@us.ibm.com
 * @version $Id$
 */
public class FilePatternFilter extends IncludeExcludeFilter
{

    /**
     * Initialize with some include(s)/exclude(s) and a pattern.  
     *
     * @param inc semicolon-delimited string of inclusion name(s)
     * @param exc semicolon-delimited string of exclusion name(s)
     * @param glob simple pattern to look for
     */
    public FilePatternFilter(String inc, String exc, String glob)
    {
        setIncludes(inc);
        setExcludes(exc);
        setPattern(glob);
        setUseIncludesOnly(false);
    }

    /**
     * Simple globbing char for pattern is "*".  
     */
    public static final String GLOB_CHAR = "*";

    /**
     * Simple starting pattern to include.  
     */
    private String startPattern = "";

    /**
     * Simple ending pattern to include.  
     */
    private String endPattern = "";

    /**
     * Accessor method to set file pattern we're looking for.  
     *
     * @param glob file pattern we're looking for; if null, we 
     * set it to a blank string
     */
    public void setPattern(String glob)
    {
        if (null != glob)
        {
            int idx = glob.indexOf(GLOB_CHAR);
            if (idx > -1)
            {
                startPattern = glob.substring(0, idx);
                endPattern = glob.substring(idx + 1, glob.length());
            }
            else
            {
                // Derivitave case: no glob char, so exact match 
                //  only - you might as well use includes
                startPattern = glob;
                endPattern = null; //special case
            }
        }
        else
        {
            // A blank string matches either
            startPattern = "";
            endPattern = "";
        }
    }

    /**
     * Return files that match our pattern.
     *
     * @param dir the directory in which the file was found.
     * @param name the name of the file.
     * @return <code>true</code> if it's a file that has our pattern.
    */
    public boolean acceptOverride(File dir, String name)
    {
        // Only return files...
        if (!(new File(dir, name)).isFile())
            return false;

        if (null != endPattern)
        {
            return name.startsWith(startPattern)
                   && name.endsWith(endPattern);
        }
        else
        {
            // Derivitave case: no glob char, so exact match 
            //  only - you might as well use includes
            return name.equals(startPattern);
        }
    }
}
