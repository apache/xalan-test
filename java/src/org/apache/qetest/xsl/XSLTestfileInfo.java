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
 * XSLTestfileInfo.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.TestfileInfo;

import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Simple data-holding class specifying info about one 'testfile'.
 * <p>Convenience class for XSL Processor testing, includes
 * additional fields and overrides some methods to change order
 * of initializer strings.</p>
 * <ul>Fields read in new order:
 * <li>inputName - xsl stylesheet</li>
 * <li>xmlName - xml data file <b>new</b></li>
 * <li>outputName - for results of transform</li>
 * <li>goldName</li>
 * <li>description</li>
 * <li>author</li>
 * <li>options</li>
 * </ul>
 * @author Shane Curcuru
 * @version $Id$
 */
public class XSLTestfileInfo extends TestfileInfo
{

    /** Name of the input XML data file. */
    public String xmlName = null;

    /** NEEDSDOC Field XMLNAME          */
    public static final String XMLNAME = "xmlName";

    /** No-arg constructor leaves everything null. */
    public XSLTestfileInfo(){}

    /**
     * Initialize members from name=value pairs in Properties block.
     * Default value for each field is null.
     * @param Properties block to initialize from
     *
     * NEEDSDOC @param p
     */
    public XSLTestfileInfo(Properties p)
    {
        load(p);
    }

    /**
     * Pass in a StringTokenizer-default-delimited string to initialize members.
     * <p>Members are read in order: inputName <i>xmlName</i> outputName goldName
     * author description options...
     * default value for each field is null</p>
     * @param String to initialize from
     *
     * NEEDSDOC @param inputStr
     */
    public XSLTestfileInfo(String inputStr)
    {

        StringTokenizer st = new StringTokenizer(inputStr);

        load(st, null);
    }

    /**
     * Pass in a StringTokenizer-default-delimited string to initialize members.
     * <p>Members are read in order: inputName <i>xmlName</i> outputName goldName
     * author description options...
     * default value for each field is user-specified String</p>
     * @param String to initialize from
     * @param String to use as default for any un-specified value
     *
     * NEEDSDOC @param inputStr
     * NEEDSDOC @param defaultVal
     */
    public XSLTestfileInfo(String inputStr, String defaultVal)
    {

        StringTokenizer st = new StringTokenizer(inputStr);

        load(st, defaultVal);
    }

    /**
     * Pass in a specified-delimited string to initialize members.
     * <p>Members are read in order: inputName <i>xmlName</i> outputName goldName
     * author description options...
     * default value for each field is user-specified String</p>
     * @param String to initialize from
     * @param String to use as default for any un-specified value
     * @param String to use as separator for StringTokenizer
     *
     * NEEDSDOC @param inputStr
     * NEEDSDOC @param defaultVal
     * NEEDSDOC @param separator
     */
    public XSLTestfileInfo(String inputStr, String defaultVal,
                           String separator)
    {

        StringTokenizer st = new StringTokenizer(inputStr, separator);

        load(st, defaultVal);
    }

    /**
     * Worker method to initialize members.
     *
     * NEEDSDOC @param st
     * NEEDSDOC @param defaultVal
     */
    public void load(StringTokenizer st, String defaultVal)
    {

        // Fill in as many items as are available; default the value otherwise
        // Note that order is important!
        if (st.hasMoreTokens())
            inputName = st.nextToken();
        else
            inputName = defaultVal;

        // Override from base class: put the xmlName next
        if (st.hasMoreTokens())
            xmlName = st.nextToken();
        else
            xmlName = defaultVal;

        if (st.hasMoreTokens())
            outputName = st.nextToken();
        else
            outputName = defaultVal;

        if (st.hasMoreTokens())
            goldName = st.nextToken();
        else
            goldName = defaultVal;

        if (st.hasMoreTokens())
            author = st.nextToken();
        else
            author = defaultVal;

        if (st.hasMoreTokens())
            description = st.nextToken();
        else
            description = defaultVal;

        if (st.hasMoreTokens())
        {
            options = st.nextToken();

            // For now, simply glom all additional tokens into the options, until the end of string
            // Leave separated with a single space char for readability
            while (st.hasMoreTokens())
            {
                options += " " + st.nextToken();
            }
        }
        else
            options = defaultVal;
    }

    /**
     * Initialize members from name=value pairs in Properties block.
     * Default value for each field is null.
     * @param Properties block to initialize from
     *
     * NEEDSDOC @param p
     */
    public void load(Properties p)
    {

        inputName = p.getProperty(INPUTNAME);
        xmlName = p.getProperty(XMLNAME);  // Override from base class
        outputName = p.getProperty(OUTPUTNAME);
        goldName = p.getProperty(GOLDNAME);
        author = p.getProperty(AUTHOR);
        description = p.getProperty(DESCRIPTION);
        options = p.getProperty(OPTIONS);
    }

    /**
     * Cheap-o debugging: return tab-delimited String of all our values.
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String dump()
    {

        return (inputName + '\t' + xmlName  // Override from base class
                + '\t' + outputName + '\t' + goldName + '\t' + author + '\t'
                + description + '\t' + options);
    }
}
