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
 * TransformStateDatalet.java
 *
 */
package org.apache.qetest.xalanj2;

import org.apache.qetest.Datalet;

import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Datalet for holding ExpectedTransformState objects.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class TransformStateDatalet implements Datalet
{
    //// Items associated with the normal test
    /** URL of the stylesheet; default:.../identity.xsl.  */
    public String inputName = "tests/api/trax/identity.xsl";

    /** URL of the xml document; default:.../identity.xml.  */
    public String xmlName = "tests/api/trax/identity.xml";

    /** URL to put output into; default:TransformStateDatalet.out.  */
    public String outputName = "TransformStateDatalet.out";

    /** URL of the a gold file or data; default:.../identity.out.  */
    public String goldName = "tests/api-gold/trax/identity.out";

    /** 
     * A Hashtable of ExpectedTransformState objects to validate.  
     * Users should put ExpectedTransformState objects in here with 
     * some sort of hashKey that they want to use.
     */
/***********************************************
// Comment out validation using ExpectedObjects since they hang 28-Jun-01 -sc
    public Hashtable expectedTransformStates = new Hashtable();
// Comment out validation using ExpectedObjects since they hang 28-Jun-01 -sc
***********************************************/

    /** 
     * Cheap-o hash of items to validate for column 99.  
     * Temporary use until we solve problem in TransformStateTestlet:
     * "Comment out validation using ExpectedObjects since they hang 28-Jun-01 -sc".
     */
    public Hashtable validate99 = new Hashtable();


    /** Description of what this Datalet tests.  */
    protected String description = "TransformStateDatalet: String inputName, String xmlName, String outputName, String goldName, String flavor; plus second Templates object";


    /**
     * No argument constructor is a no-op.  
     */
    public TransformStateDatalet() { /* no-op */ }


    /**
     * Initialize this datalet from a string, perhaps from 
     * a command line.  
     * We will parse the command line with whitespace and fill
     * in our member variables in order:
     * <pre>inputName, xmlName, outputName, goldName, flavor</pre>, 
     * if there are too few tokens, remaining variables will default.
     */
    public TransformStateDatalet(String args)
    {
        load(args);
    }


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
     * Load fields of this Datalet from a Hashtable.  
     * Caller must provide data for all of our fields.
     * //@todo NOT FULLY IMPLEMENTED!.
     * 
     * @param Hashtable to load
     */
    public void load(Hashtable h)
    {
        if (null == h)
            return; //@todo should this have a return val or exception?

        inputName = (String)h.get("inputName");
        xmlName = (String)h.get("xmlName");
        outputName = (String)h.get("outputName");
        goldName = (String)h.get("goldName");
    }


    /**
     * Load fields of this Datalet from a Properties.  
     * Caller must provide data for all of our fields.
     * //@todo NOT FULLY IMPLEMENTED!.
     * 
     * @param Hashtable to load
     */
    public void load(Properties p)
    {
        if (null == p)
            return; //@todo should this have a return val or exception?

        inputName = (String)p.getProperty("inputName");
        xmlName = (String)p.getProperty("xmlName");
        outputName = (String)p.getProperty("outputName");
        goldName = (String)p.getProperty("goldName");
    }
    /**
     * Load fields of this Datalet from a String.  
     * NOT IMPLEMENTED! No easy way to load the Templates from string.  
     * 
     * @param s String to load
     */
    public void load(String s)
    {
        throw new RuntimeException("TransformStateDatalet.load(String) not implemented!");
    }
    /**
     * Load fields of this Datalet from a String[].  
     * NOT IMPLEMENTED! No easy way to load the Templates from string.  
     * 
     * @param s String array to load
     */
    public void load(String[] s)
    {
        throw new RuntimeException("TransformStateDatalet.load(String[]) not implemented!");
    }
}  // end of class TransformStateDatalet

