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
 * StylesheetDatalet.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.Datalet;

import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Datalet for conformance testing of xsl stylesheet files.
 * Should serve as a base class for other XSLT related Datalets.
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class StylesheetDatalet implements Datalet
{
    /** URL of the stylesheet; default:.../identity.xsl.  */
    public String inputName = "tests/api/trax/identity.xsl";

    /** URL of the xml document; default:.../identity.xml.  */
    public String xmlName = "tests/api/trax/identity.xml";

    /** URL to put output into; default:StylesheetDatalet.out.  */
    public String outputName = "StylesheetDatalet.out";

    /** URL of the a gold file or data; default:.../identity.out.  */
    public String goldName = "tests/api-gold/trax/identity.out";

    /** Flavor of a ProcessorWrapper to use; default:trax.  */
    public String flavor = "trax"; //@todo should be ProcessorWrapper.DEFAULT_FLAVOR

    /** 
     * If we should force any local path\filenames to URLs.  
     * Note: This is not really the best place for this, but 
     * since it works with Xerces and Crimson and Xalan, it's 
     * good enough for now.  
     * Not currently settable by user; default:true
     */
    public boolean useURL = true;

    /** 
     * Generic placeholder for any additional options.  
     * I'm still undecided if I like this idea or not.  
     * This allows StylesheetDatalets to support additional kinds 
     * of tests, like performance tests, without having to change 
     * this data model.  These options can serve as a catch-all 
     * for any new properties or options or what-not that new 
     * tests need, without having to change how the most basic 
     * member variables here work.
     * Note that while this needs to be a Properties object to 
     * take advantage of the parent/default behavior in 
     * getProperty(), this doesn't necessarily mean they can only 
     * store Strings.
     */
    public Properties options = new Properties();

    /** Description of what this Datalet tests.  */
    protected String description = "StylesheetDatalet: String inputName, String xmlName, String outputName, String goldName, String flavor";


    /**
     * No argument constructor is a no-op.  
     */
    public StylesheetDatalet() { /* no-op */ }


    /**
     * Initialize this datalet from a string, perhaps from 
     * a command line.  
     * We will parse the command line with whitespace and fill
     * in our member variables in order:
     * <pre>inputName, xmlName, outputName, goldName, flavor</pre>, 
     * if there are too few tokens, remaining variables will default.
     */
    public StylesheetDatalet(String args)
    {
        load(args);
        setDescription("inputName=" + inputName 
                       + " xmlName=" + xmlName 
                       + " outputName=" + outputName 
                       + " goldName=" + goldName 
                       + " flavor=" + flavor);
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
     * //@todo design decision: only have load(Hashtable)
     * or load(Properties), not both.
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
        flavor = (String)h.get("flavor");
    }


    /**
     * Load fields of this Datalet from a Properties.  
     * Caller must provide data for all of our fields.
     * //@todo design decision: only have load(Hashtable)
     * or load(Properties), not both.
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
        flavor = (String)p.getProperty("flavor");
        // Also set our internal options to default to this Properties
        options = new Properties(p);
    }

    /**
     * Load fields of this Datalet from an array.  
     * Order: inputName, xmlName, outputName, goldName, flavor
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
        }
        catch (ArrayIndexOutOfBoundsException  aioobe)
        {
            // No-op, leave remaining items as default
        }
    }


    /**
     * Load fields of this Datalet from a whitespace-delimited String.  
     * Order: inputName, xmlName, outputName, goldName, flavor
     * If too few args, then fields at end of list are left at default value.
     * EXPERIMENTAL: takes any extra args as name value pairs and 
     * attempts to add them to our options
     * @param args array of Strings
     */
    public void load(String str)
    {
        if (null == str)
            return; //@todo should this have a return val or exception?

        StringTokenizer st = new StringTokenizer(str);

        // Fill in as many items as we can; leave as default otherwise
        // Note that order is important!
        if (st.hasMoreTokens())
        {
            inputName = st.nextToken();
            if (st.hasMoreTokens())
            {
                xmlName = st.nextToken();
                if (st.hasMoreTokens())
                {
                    outputName = st.nextToken();
                    if (st.hasMoreTokens())
                    {
                        goldName = st.nextToken();
            
                        if (st.hasMoreTokens())
                        {
                            flavor = st.nextToken();
                        }
                    }
                }
            }
        }
        // EXPERIMENTAL add extra name value pairs to our options
        while (st.hasMoreTokens())
        {
            String name = st.nextToken();
            if (st.hasMoreTokens())
            {
                options.put(name, st.nextToken());
            }
            else
            {
                // Just put it as 'true' for boolean options
                options.put(name, "true");
            }
        }

    }
}  // end of class StylesheetDatalet

