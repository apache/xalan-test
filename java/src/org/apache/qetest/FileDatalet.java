/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights 
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
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Datalet representing set of paths to files: input, output, gold.
 * 
 * This is a fairly simplistic Datalet implementation that's 
 * useful for the many programs where the test requires reading 
 * an input file, performing an operation with the program, and 
 * then verifying an output file.
 *
 * We normally operate on local path/filenames, since the Java 
 * SDK's implementation of URLs and File objects is so poor at 
 * handling proper URI/URL's according to the RFCs.
 * 
 * @author Shane_Curcuru@us.ibm.com
 * @version $Id$
 */
public class FileDatalet implements Datalet
{
    /** Path of the location to get input resources from.  */
    protected String input = "tests/defaultInput";

    /** Accessor method for input.  */
    public String getInput() { return input; }

    /** Path to put actual output resources into.  */
    protected String output = "output/defaultOutput";

    /** Accessor method for output.  */
    public String getOutput() { return output; }

    /** Path of the location to get gold or reference resources from.  */
    protected String gold = "gold/defaultGold";

    /** Accessor method for gold.  */
    public String getGold() { return gold; }


    /** 
     * Worker method to validate the files/dirs we represent.  
     * 
     * By default, ensures that the input already exists in some 
     * format, and for both the output and gold, attempts to create 
     * them if they don't already exist.
     *
     * @param strict if true, requires that output and gold must 
     * be created; otherwise they're optional
     * @return false if input doesn't already exist; true otherwise
     */
    public boolean validate(boolean strict)
    {
        File f = new File(getInput());
        if (!f.exists())
            return false;

        f = new File(getOutput());
        if (!f.exists())
        {
            if (!f.mkdirs())
            {
                // Only fail if asked to be strict
                if (strict)
                    return false;
            }
        }        

        f = new File(getGold());
        if (!f.exists())
        {
            if (!f.mkdirs())
            {
                // Only fail if asked to be strict
                if (strict)
                    return false;
            }
        }        
        // If we get here, we're happy either way
        return true;
    }


    /** 
     * Generic placeholder for any additional options.  
     * 
     * This allows FileDatalets to support additional kinds 
     * of tests, like performance tests, without having to change 
     * this data model.  These options can serve as a catch-all 
     * for any new properties or options or what-not that new 
     * tests need, without having to change how the most basic 
     * member variables here work.
     * Note that while this needs to be a Properties object to 
     * take advantage of the parent/default behavior in 
     * getProperty(), this doesn't necessarily mean they can only 
     * store Strings; however only String-valued items can make 
     * use of the default properties mechanisim.
     *
     * Default is a null object.
     */
    protected Properties options = null;

    /** Accessor method for optional properties.  */
    public Properties getOptions() { return options; }

    /** Accessor method for optional properties; settable.  */
    public void setOptions(Properties p) { options = p; }


    /** Description of what this Datalet tests.  */
    protected String description = "FileDatalet: default description";

    /**
     * Accesor method for a brief description of this Datalet.  
     *
     * @return String describing the specific set of data 
     * this Datalet contains (can often be used as the description
     * of any check() calls made from the Testlet).
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Accesor method for a brief description of this Datalet.  
     *
     * @param s description to use for this Datalet.
     */
    public void setDescription(String s)
    {
        description = s;
    }


    /**
     * Worker method to auto-set the description of this Datalet.  
     */
    protected void setDescription()
    {
        setDescription("input=" + input 
                       + " output=" + output 
                       + " gold=" + gold); 
    }


    /**
     * No argument constructor is a no-op; not very useful.  
     */
    public FileDatalet() { /* no-op */ }


    /**
     * Initialize this datalet from another FileDatalet which 
     * serves as a 'base' location, and a filename.  
     * 
     * We set each of our input, output, gold to be concatenations 
     * of the base + File.separator + fileName.
     *
     * @param base FileDatalet object to serve as base directories
     * @param fileName to concatenate for each of input/output/gold
     */
    public FileDatalet(FileDatalet base, String fileName)
    {
        if ((null == base) || (null == fileName))
            throw new IllegalArgumentException("FileDatalet(base, fileName) called with null base!");

        StringBuffer buf = new StringBuffer(File.separator + fileName);
        input = base.getInput() + buf;
        output = base.getOutput() + buf;
        gold = base.getGold() + buf;
        
        setDescription(); 
    }


    /**
     * Initialize this datalet from a list of paths.  
     * 
     * @param i path for input
     * @param o path for output
     * @param g path for gold
     */
    public FileDatalet(String i, String o, String g)
    {
        input = i;
        output = o;
        gold = g;
        
        setDescription(); 
    }


    /**
     * Load fields of this Datalet from a Hashtable.  
     * Caller must provide data for all of our fields.
     * 
     * @param Hashtable to load
     */
    public void load(Hashtable h)
    {
        if (null == h)
            return; //@todo should this have a return val or exception?

        input = (String)h.get("input");
        output = (String)h.get("output");
        gold = (String)h.get("gold");
    }


    /**
     * Load fields of this Datalet from an array.  
     * Order: input, output, gold
     * If too few args, then fields at end of list are left at default value.
     * @param args array of Strings
     */
    public void load(String[] args)
    {
        if (null == args)
            return; //@todo should this have a return val or exception?

        try
        {
            input = args[0];
            output = args[2];
            gold = args[3];
        }
        catch (ArrayIndexOutOfBoundsException  aioobe)
        {
            // No-op, leave remaining items as default
        }
    }

}  // end of class FileDatalet

