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
 * TraxDatalet.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.Datalet;
import org.apache.qetest.QetestUtils;

import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringReader;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Datalet for holding TrAX-like Sources and Results.
 *
 * Allows tester to set either a filename, URL, or Node for 
 * both the xml and xsl sources, and the application simply 
 * requests a Source object each time.
 * We apply a simplistic algorithim to determine which kind of 
 * Source object we return.
 * <b>Note:</b> Currently only supports StreamSources of various types.
 *
 * Note: should probably be moved to the org.apache.qetest.trax 
 * package, but I'm leaving it in the xsl package for now.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class TraxDatalet implements Datalet
{
    /** URL of XSL source to use on disk.  */
    protected String xslURL = null;

    /** String of characters to use as XSL source in a StringReader.  */
    protected String xslString = null;

    /** @param s the local path/filename of the XSL to use.  */
    public void setXSLName(String s)
    {
        xslURL = QetestUtils.filenameToURL(s);
    }

    /** @param s the URL/URI of the XSL to use.  */
    public void setXSLURL(String s)
    {
        xslURL = s;
    }

    /** @param s String of XSL to use in a StringReader, etc..  */
    public void setXSLString(String s)
    {
        xslString = s;
    }

    /** 
     * Return a Source object representing our XSL.
     * This may be any kind of source and is determined by which 
     * kinds of setXSL*() methods you've called.
     * <ul>
     * <li>if xslString is set, return new StreamSource(new StringReader(xslString))</li>
     * <li>if xslURL/Name is set, return new StreamSource(xslURL)</li>
     * <li>More types TBD</li>
     * </ul>
     * @return Source object representing our XSL, or an 
     * IllegalStateException if we don't have any XSL
     */
    public Source getXSLSource()
    {
        if (null != xslString)
            return new StreamSource(new StringReader(xslString));
        else if (null != xslURL)
            return new StreamSource(xslURL);
        else
            throw new IllegalStateException("TraxDatalet.getXSLSource() with no XSL!");
    }


    /** URL of XML source to use on disk.  */
    protected String xmlURL = null;

    /** String of characters to use as XML source in a StringReader.  */
    protected String xmlString = null;

    /** @param s the local path/filename of the XML to use.  */
    public void setXMLName(String s)
    {
        xmlURL = QetestUtils.filenameToURL(s);
    }

    /** @param s the URL/URI of the XML to use.  */
    public void setXMLURL(String s)
    {
        xmlURL = s;
    }

    /** @param s String of XML to use in a StringReader, etc..  */
    public void setXMLString(String s)
    {
        xmlString = s;
    }

    /** 
     * Return a Source object representing our XML.
     * This may be any kind of source and is determined by which 
     * kinds of setXML*() methods you've called.
     * <ul>
     * <li>if xmlString is set, return new StreamSource(new StringReader(xmlString))</li>
     * <li>if xmlURL/Name is set, return new StreamSource(xmlURL)</li>
     * <li>More types TBD</li>
     * </ul>
     * @return Source object representing our XML, or an 
     * IllegalStateException if we don't have any XML
     */
    public Source getXMLSource()
    {
        if (null != xmlString)
            return new StreamSource(new StringReader(xmlString));
        else if (null != xmlURL)
            return new StreamSource(xmlURL);
        else
            throw new IllegalStateException("TraxDatalet.getXMLSource() with no XML!");
    }


    /** 
     * Convenience method: set both XML and XSL names at once.  
     *
     * @param baseDir path/filename to your inputDir where 
     * your matched xml, xsl files are
     * @param baseName base portion of filename (not including 
     * extension, which is automatically .xml and .xsl)
     */
    public void setNames(String baseDir, String baseName)
    {
        // Note forward slash, since 
        String baseURL = QetestUtils.filenameToURL(baseDir) + "/";
        xslURL = baseURL + baseName + ".xsl";
        xmlURL = baseURL + baseName + ".xml";
    }


    /** Old-way: name to put output into; default:TraxDatalet.out.  */
    public String outputName = "TraxDatalet.out";

    /** Old-way: name of the a gold file or data; default:no-gold-file.out.  */
    public String goldName = "no-gold-file.out";


    /** 
     * Generic placeholder for any additional options.  
     * @see StylesheetDatalet#options
     */
    public Properties options = new Properties();


    /** 
     * A block of objects to validate.  
     * Users may put in various objects that they will use as 
     * expected data later on.  You can access this as a Properties 
     * block or as a Hashtable; it's up to each user to define this.
     */
    public Properties expected = new Properties();


    /** No argument constructor is a no-op.  */
    public TraxDatalet() { /* no-op */ }


    /** Description of what this Datalet tests.  */
    protected String description = "TraxDatalet: javax.xml.transform Source holder";


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
    public void load(Hashtable h)
    {
        if (null == h)
            return;
        xslURL = (String)h.get("xslURL");
        xslString = (String)h.get("xslString");
        xmlURL = (String)h.get("xmlURL");
        xmlString = (String)h.get("xmlString");
        outputName = (String)h.get("outputName");
        goldName = (String)h.get("goldName");
        description = (String)h.get("description");
        try
        {
            options = (Properties)h.get("options");
        }
        catch (Exception e) { /* no-op, ignore */ }
        try
        {
            expected = (Properties)h.get("expected");
        }
        catch (Exception e) { /* no-op, ignore */ }
    }


    /**
     * Load fields of this Datalet from a String[].  
     * Order: xslURL, xmlURL, outputName, goldName, description
     * If too few args, then fields at end of list are left at default value.
     * 
     * @param s String array to load
     */
    public void load(String[] args)
    {
        if (null == args)
            return; //@todo should this have a return val or exception?

        try
        {
            xslURL = args[0];
            xmlURL = args[1];
            outputName = args[2];
            goldName = args[3];
            description = args[4];
        }
        catch (ArrayIndexOutOfBoundsException  aioobe)
        {
            // No-op, leave remaining items as default
        }
    }
}  // end of class TraxDatalet

