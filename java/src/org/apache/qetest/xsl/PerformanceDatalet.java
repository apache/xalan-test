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

/*
 *
 * Datalet.java
 *
 */
package org.apache.qetest.xsl;
import org.apache.qetest.*;

import java.util.Hashtable;

/**
 * Minimal interface defining a datalet, a single set of data 
 * for a simple test case.
 * A Datalet defines a single group of data that a matching 
 * Testlet needs to execute it's simple test case.
 * For example:
 *  - We define a Datalet that processes an XML file with a 
 *    stylesheet in a certain manner - perhaps using a specific 
 *    set of SAX calls.  
 *  - The Datalet takes as an argument a matching Datalet, that 
 *    defines any parameters that may change  - like the names 
 *    of the XML file and the stylesheet file to use.
 *  - Test authors or users running a harness or the like can 
 *    then easily define a large set of Datalets for various 
 *    types of input files that they want to test, and simply 
 *    pass a list or Vector of the Datalets to the Datalet to 
 *    be iterated over.
 * Normally Datalets will simply be collections of public members
 * that can be written or read by anyone.  An enclosing test can 
 * simply
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class PerformanceDatalet extends DataletImpl
{
    /** URL of the stylesheet.  */
    public String inputName = "identity.xsl";

    /** URL of the xml document.  */
    public String xmlName = "identity.xml";

    /** URL to put output into.  */
    public String outputName = "identity.out";

    /** URL of the a gold file or data.  */
    public String goldName = "identity.gold";

    /** Flavor of a ProcessorWrapper to use.  */
    public String flavor = "trax";

    /** Number of loops to perform.  */
    public int iterations = 10;

    /** if we should preload a single process first.  */
    public boolean preload = true;

    /** Description of what this Datalet tests.  */
    protected String description = "PerformanceDatalet: String inputName, String xmlName, String outputName, String goldName, int iterations, boolean preload";

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
     * 
     * @param Hashtable to load
     */
    public void load(Hashtable p)
    {
        if (null == p)
            return; //@todo should this have a return val or exception?

        inputName = (String)p.get("inputName");
        xmlName = (String)p.get("xmlName");
        outputName = (String)p.get("outputName");
        goldName = (String)p.get("goldName");
        flavor = (String)p.get("flavor");
        iterations = ((Integer)p.get("iterations")).intValue();
        try
        {
            // Check string value first
            String tmp = (String)p.get("preload");
            // Only check for non-default case
            if ("false".equalsIgnoreCase(tmp) || "no".equalsIgnoreCase(tmp))
                preload = false;
        }
        catch (ClassCastException cce1)
        {
            try
            {
                // Check boolean value next
                Boolean tmp2 = (Boolean)p.get("preload");
                preload = tmp2.booleanValue();
            }
            catch (ClassCastException cce2) 
            {
                // No-op, leave as default
            }
        }
    }

    /**
     * Load fields of this Datalet from an array.  
     * Order: inputName, xmlName, outputName, goldName, flavor, iterations, preload
     * If too few args, then fields at end of list are left at default value.
     * @param args array of Strings
     */
    public void load(String[] args)
    {
        if (null == args)
            return; //@todo should this have a return val or exception?

        try
        {
            inputName = args[0];
            xmlName = args[1];
            outputName = args[2];
            goldName = args[3];
            flavor = args[4];
            try
            {
                iterations = Integer.parseInt(args[5]);
            }
            catch (NumberFormatException  nfe)
            {
                // No-op, leave as default and continue
            }
            String tmp = args[6];
            // Only check for non-default case
            if ("false".equalsIgnoreCase(tmp) || "no".equalsIgnoreCase(tmp))
                preload = false;
        }
        catch (ArrayIndexOutOfBoundsException  aioobe)
        {
            // No-op, leave remaining items as default
        }
    }

}  // end of class PerformanceDatalet

