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
 * OutputNameManager.java
 *
 */
package org.apache.qetest;

/**
 * Simple utility class to manage tests with multiple output names.
 * <p>Starts with a base name and extension, and returns
 * nextName()s like:<br>
 * baseName_1.ext<br>
 * baseName_2.ext<br>
 * baseName_3.ext<br>
 * ...<br>
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class OutputNameManager
{

    // defaults are provided for everything for the terminally lazy

    /** NEEDSDOC Field extension          */
    protected String extension = ".out";

    /** NEEDSDOC Field baseName          */
    protected String baseName = "OutputFile";

    /** NEEDSDOC Field currentName          */
    protected String currentName = "currentUnset";

    /** NEEDSDOC Field previousName          */
    protected String previousName = "previousUnset";

    /** NEEDSDOC Field counter          */
    protected int counter = 0;

    /** NEEDSDOC Field SEPARATOR          */
    public static final String SEPARATOR = "_";

    /**
     * Construct with just a basename.  
     *
     * NEEDSDOC @param base
     */
    public OutputNameManager(String base)
    {
        baseName = base;
    }

    /**
     * Construct with a basename and extension.  
     *
     * NEEDSDOC @param base
     * NEEDSDOC @param ext
     */
    public OutputNameManager(String base, String ext)
    {
        baseName = base;
        extension = ext;
    }

    /**
     * Construct with a basename, extension, and set the counter.  
     *
     * NEEDSDOC @param base
     * NEEDSDOC @param ext
     * NEEDSDOC @param ctr
     */
    public OutputNameManager(String base, String ext, int ctr)
    {

        baseName = base;
        extension = ext;

        setCounter(ctr);
    }

    /** Reset the counter to zero and update current, previous names. */
    public void reset()
    {

        previousName = currentName;
        currentName = null;
        counter = 0;  // Set to 0 since we always call nextOutName() first
    }

    /**
     * Increment counter and get next name.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String nextName()
    {

        setCounter(counter++);  // Updates names

        return currentName();
    }

    /**
     * Just get the current name.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String currentName()
    {
        return currentName;
    }

    /**
     * Get the previous name, even past a reset().  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String previousName()
    {
        return previousName;
    }

    /**
     * Get the current counter number.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public int currentCounter()
    {
        return counter;
    }

    /**
     * Set the current counter number, including names.  
     *
     * NEEDSDOC @param ctr
     */
    public void setCounter(int ctr)
    {

        counter = ctr;
        previousName = currentName;
        currentName = baseName + SEPARATOR + counter + extension;
    }
}  // end of class OutputNameManager

