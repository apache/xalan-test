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
import java.util.Hashtable;

/**
 * Simple file filter; returns *.xsl non-dir files that start with the directory name.
 * Has crude support for an excludes list of filename bases.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class GoldFileRules extends ConformanceFileRules
{

    /** Initialize for defaults (not using inclusion list) no-op. */
    public GoldFileRules(){}

    /**
     * Initialize with a case-sensitive Hash of file names to exclude.  
     *
     * NEEDSDOC @param excludesHash
     */
    public GoldFileRules(Hashtable excludesHash)
    {
    	super(excludesHash);
    }

    /**
     * Initialize with a case-insensitive semicolon-delimited String of file names to exclude.  
     *
     * NEEDSDOC @param excludesStr
     */
    public GoldFileRules(String excludesStr)
    {
        super(excludesStr);
    }

    /**
     * Tests if a specified file should be included in a file list.
     * <p>Returns true only for *.xsl files whose names start with
     * the name of the directory, case-insensitive (uses .toLowerCase()).
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

        return (!file.isDirectory()) && name.toLowerCase().endsWith(".out")
               && name.toLowerCase().startsWith(dir.getName().toLowerCase());
    }
}
