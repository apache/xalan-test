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
 * XHTComparator.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.Reporter;  // Only for PASS_RESULT, etc.

import java.io.PrintWriter;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.StringTokenizer;

// DOM imports from Xerces
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;

// Xerces imports
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.html.dom.HTMLBuilder;

// SAX2 imports from Xerces
import org.xml.sax.InputSource;

/**
 * Uses an XML/HTML/Text diff comparator to check or diff two files.
 * <p>Given two files, an actual test result and a known good or 'gold'
 * test result, diff the two files to see if they are equal; if not, provide
 * some very basic info on where they differ.</p>
 * <p>Attempts to parse each file as an XML document using Xerces;
 * if that fails, attempt to parse each as an HTML document using
 * <i>NEED NEW HTML PARSER</i>; if that fails, pretend to parse each
 * doc as a single text node.</p>
 * @todo document whitespace difference handling better -sc
 * @todo check how namespaces are handled and diff'd -sc
 * @todo check how XML decls are handled (or not) -sc
 * @todo check how files of different encodings are handled in each parse type -sc
 * @todo Allow param to define the type of parse we do (i.e. if a
 * testwriter knows their output file will be XML, we should only
 * attempt to parse it as XML, not other types)
 * @author Scott_Boag@lotus.com
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class XHTComparator
{

    /** NEEDSDOC Field MAX_VALUE_DISPLAY_LEN          */
    static final int MAX_VALUE_DISPLAY_LEN = 511;  // arbitrary length, for convenience

    /** NEEDSDOC Field maxDisplayLen          */
    private int maxDisplayLen = MAX_VALUE_DISPLAY_LEN;

    /**
     * NEEDSDOC Method setMaxDisplayLen 
     *
     *
     * NEEDSDOC @param i
     */
    public void setMaxDisplayLen(int i)
    {
        if (i > 0)
            maxDisplayLen = i;
    }

    /** Constants for reporting out reason for failed diffs. */
    public static final String SEPARATOR = ";";

    /** NEEDSDOC Field LBRACKET          */
    public static final String LBRACKET = "[";

    /** NEEDSDOC Field RBRACKET          */
    public static final String RBRACKET = "]";

    /** NEEDSDOC Field TEST          */
    public static final String TEST = "test";

    /** NEEDSDOC Field GOLD          */
    public static final String GOLD = "gold";

    /** NEEDSDOC Field PARSE_TYPE          */
    public static final String PARSE_TYPE = "-parse-type" + SEPARATOR;  // postpended to TEST or GOLD

    /** NEEDSDOC Field OTHER_ERROR          */
    public static final String OTHER_ERROR = "other-error" + SEPARATOR;

    /** NEEDSDOC Field WARNING          */
    public static final String WARNING = "warning" + SEPARATOR;

    /** NEEDSDOC Field MISMATCH_NODE          */
    public static final String MISMATCH_NODE = "mismatch-node" + SEPARATOR;

    /** NEEDSDOC Field MISSING_TEST_NODE          */
    public static final String MISSING_TEST_NODE = "missing-node-" + TEST
                                                       + SEPARATOR;

    /** NEEDSDOC Field MISSING_GOLD_NODE          */
    public static final String MISSING_GOLD_NODE = "missing-node-" + GOLD
                                                       + SEPARATOR;

    /** NEEDSDOC Field MISMATCH_ATTRIBUTE          */
    public static final String MISMATCH_ATTRIBUTE = "mismatch-attribute"
                                                        + SEPARATOR;

    /** NEEDSDOC Field MISMATCH_VALUE          */
    public static final String MISMATCH_VALUE = "mismatch-value" + SEPARATOR;

    /** NEEDSDOC Field MISSING_TEST_VALUE          */
    public static final String MISSING_TEST_VALUE = "missing-value-" + TEST
                                                        + SEPARATOR;

    /** NEEDSDOC Field MISSING_GOLD_VALUE          */
    public static final String MISSING_GOLD_VALUE = "missing-value-" + GOLD
                                                        + SEPARATOR;

    /** NEEDSDOC Field WHITESPACE_DIFF          */
    public static final String WHITESPACE_DIFF = "whitespace-diff;";

    /**
     * Compare two files by parsing into DOMs and comparing trees.
     * @param goldFileName expected file
     * @param testFileName actual file
     * @param reporter PrintWriter to dump status info to
     * @param array of warning flags (for whitespace diffs, I think?)
     * NEEDSDOC @param warning
     * @return true if they match, false otherwise
     */
    public boolean compare(String goldFileName, String testFileName,
                           PrintWriter reporter, boolean[] warning)
    {

        // parse the gold doc
        Document goldDoc = parse(goldFileName, reporter, GOLD);

        // parse the test doc only if gold doc was parsed OK
        Document testDoc = (null != goldDoc)
                           ? parse(testFileName, reporter, TEST) : null;

        if (null == goldDoc)
        {
            reporter.println(OTHER_ERROR + GOLD + SEPARATOR
                             + "document null");

            return false;
        }
        else if (null == testDoc)
        {
            reporter.println(OTHER_ERROR + TEST + SEPARATOR
                             + "document null");

            return false;
        }

        return diff(goldDoc, testDoc, reporter, warning);
    }

    // Reporter format:
    // REASON_CONSTANT;gold val;test val;reason description

    /**
     * The contract is: when you enter here the gold and test nodes are the same type,
     * both non-null, and both in the same basic position in the tree.
     * @todo verify caller really performs for the contract -sc
     *
     * NEEDSDOC @param gold
     * NEEDSDOC @param test
     * NEEDSDOC @param reporter
     * NEEDSDOC @param warning
     *
     * NEEDSDOC ($objectName$) @return
     */
    boolean diff(Node gold, Node test, PrintWriter reporter,
                 boolean[] warning)
    {

        String name1 = gold.getNodeName();
        String name2 = test.getNodeName();

        // If both there but not equal, fail
        if ((null != name1) && (null != name2) &&!name1.equals(name2))
        {
            reporter.print(MISMATCH_NODE + nodeTypeString(gold) + SEPARATOR
                           + nodeTypeString(test) + SEPARATOR
                           + "name does not equal test node");

            return false;
        }
        else if ((null != name1) && (null == name2))
        {
            reporter.print(MISSING_TEST_NODE + nodeTypeString(gold)
                           + SEPARATOR + nodeTypeString(test) + SEPARATOR
                           + "name missing on test");

            return false;
        }
        else if ((null == name1) && (null != name2))
        {
            reporter.print(MISSING_GOLD_NODE + nodeTypeString(gold)
                           + SEPARATOR + nodeTypeString(test) + SEPARATOR
                           + "name missing on gold");

            return false;
        }

        String value1 = gold.getNodeValue();
        String value2 = test.getNodeValue();

        if ((null != value1) && (null != value2) &&!value1.equals(value2))
        {
            System.err.println("value1:");
            System.err.println(value1);
            System.err.println("value2:");
            System.err.println(value2);
            reporter.print(MISMATCH_VALUE + nodeTypeString(gold) + "-"
                           + value1.length() + SEPARATOR
                           + nodeTypeString(test) + "-" + value2.length()
                           + SEPARATOR + "lengths do not match");

            // Ignore old conditional printing; always print out values, even with newlines -sc

            /**
             *     // semi-HACK: only report out stuff that's short enough
             *     // TODO This should be a settable property for the test harness! -sc
             *     if((value1.length() < maxDisplayLen)
             *        && (value2.length() < maxDisplayLen))
             *     {
             *       boolean doPrintValue = true;
             *       // TODO wouldn't it be more efficient to do indexOf's? -sc
             *       for(int i = 0; i < value1.length(); i++)
             *       {
             *         char c = value1.charAt(i);
             *         if((c == 0x0A) || (c == 0x0D) || (c == '\n'))
             *         {
             *           doPrintValue = false;
             *           break;
             *         }
             *       }
             *       if(doPrintValue)
             *       {
             *         for(int i = 0; i < value2.length(); i++)
             *         {
             *           char c = value2.charAt(i);
             *           if((c == 0x0A) || (c == 0x0D) || (c == '\n'))
             *           {
             *             doPrintValue = false;
             *             break;
             *           }
             *         }
             *       }
             *       if(doPrintValue)
             *       {
             * / Ignore old conditional printing; always print out values, even with newlines -sc
             */
            reporter.println();
            reporter.print(MISMATCH_VALUE + value1 + SEPARATOR + value2
                           + SEPARATOR + "values do not match");

            return false;
        }
        else if ((null != value1) && (null == value2))
        {
            reporter.print(MISSING_TEST_VALUE + nodeTypeString(gold) + "-"
                           + value1 + SEPARATOR + nodeTypeString(test)
                           + SEPARATOR + "test no value");

            return false;
        }
        else if ((null == value1) && (null != value2))
        {
            reporter.print(MISSING_GOLD_VALUE + nodeTypeString(gold)
                           + SEPARATOR + nodeTypeString(test) + "-" + value2
                           + SEPARATOR + "gold no value");

            return false;
        }

        switch (gold.getNodeType())
        {
        case Node.DOCUMENT_NODE :
        {

            // Why don't we do anything here? -sc
        }
        break;
        case Node.ELEMENT_NODE :
        {

            // Explicitly ignore attribute ordering
            // TODO do we need to make this settable for testing purposes? -sc
            NamedNodeMap goldAttrs = gold.getAttributes();
            NamedNodeMap testAttrs = test.getAttributes();

            if ((null != goldAttrs) && (null == testAttrs))
            {
                reporter.print(MISMATCH_ATTRIBUTE + nodeTypeString(gold)
                               + SEPARATOR + nodeTypeString(test) + SEPARATOR
                               + "test no attrs");

                return false;
            }
            else if ((null == goldAttrs) && (null != testAttrs))
            {
                reporter.print(MISMATCH_ATTRIBUTE + nodeTypeString(gold)
                               + SEPARATOR + nodeTypeString(test) + SEPARATOR
                               + "gold no attrs");

                return false;
            }

            int gn = goldAttrs.getLength();
            int tn = testAttrs.getLength();

            if (gn != tn)
            {
                reporter.print(MISMATCH_ATTRIBUTE + nodeTypeString(gold)
                               + "-" + gn + SEPARATOR + nodeTypeString(test)
                               + "-" + tn + SEPARATOR
                               + "attribte count mismatch");

                // TODO: add output of each set of attrs for comparisons
                return false;
            }

            // TODO verify this checks the full list of attributes both ways, 
            //      from gold->test and from test->gold -sc
            for (int i = 0; i < gn; i++)
            {
                Attr goldAttr = (Attr) goldAttrs.item(i);
                String goldAttrName = goldAttr.getName();
                Node testAttr = testAttrs.getNamedItem(goldAttrName);

                if (null == testAttr)
                {
                    reporter.print(MISMATCH_ATTRIBUTE + nodeTypeString(gold)
                                   + "-" + goldAttrName + SEPARATOR
                                   + nodeTypeString(test) + SEPARATOR
                                   + "missing attribute on test");

                    return false;
                }

                if (!diff(goldAttr, testAttr, reporter, warning))
                {
                    return false;
                }
            }
        }
        break;
        case Node.CDATA_SECTION_NODE :{}
        break;
        case Node.ENTITY_REFERENCE_NODE :{}
        break;
        case Node.ATTRIBUTE_NODE :{}
        break;
        case Node.COMMENT_NODE :{}
        break;
        case Node.ENTITY_NODE :{}
        break;
        case Node.NOTATION_NODE :{}
        break;
        case Node.PROCESSING_INSTRUCTION_NODE :{}
        break;
        case Node.TEXT_NODE :{}
        break;
        default :{}
        }

        Node try2[] = new Node[2];
        Node goldChild = gold.getFirstChild();
        Node testChild = test.getFirstChild();

        if (!basicChildCompare(goldChild, testChild, reporter, warning, try2))
            return false;

        goldChild = try2[0];
        testChild = try2[1];

        while (null != goldChild)
        {
            if (!diff(goldChild, testChild, reporter, warning))
                return false;

            goldChild = goldChild.getNextSibling();
            testChild = testChild.getNextSibling();

            if (!basicChildCompare(goldChild, testChild, reporter, warning,
                                   try2))
                return false;

            goldChild = try2[0];
            testChild = try2[1];
        }

        return true;
    }  // end of diff()

    /**
     * NEEDSDOC Method isWhiteSpace 
     *
     *
     * NEEDSDOC @param s
     *
     * NEEDSDOC (isWhiteSpace) @return
     */
    boolean isWhiteSpace(String s)
    {

        int n = s.length();

        for (int i = 0; i < n; i++)
        {
            if (!Character.isWhitespace(s.charAt(i)))
                return false;
        }

        return true;
    }  // end of isWhiteSpace()

    /**
     * NEEDSDOC Method tryToAdvancePastWhitespace 
     *
     *
     * NEEDSDOC @param n
     * NEEDSDOC @param reporter
     * NEEDSDOC @param warning
     * NEEDSDOC @param next
     * NEEDSDOC @param which
     *
     * NEEDSDOC (tryToAdvancePastWhitespace) @return
     */
    Node tryToAdvancePastWhitespace(Node n, PrintWriter reporter,
                                    boolean[] warning, Node next[], int which)
    {

        if (n.getNodeType() == Node.TEXT_NODE)
        {
            String data = n.getNodeValue();

            if (null != data)
            {
                if (isWhiteSpace(data))
                {
                    warning[0] = true;

                    reporter.print(WHITESPACE_DIFF + " ");  // TODO check the format of this; maybe use println -sc

                    n = n.getNextSibling();
                    next[which] = n;
                }
            }
        }

        return n;
    }  // end of tryToAdvancePastWhitespace()

    /**
     * NEEDSDOC Method basicChildCompare 
     *
     *
     * NEEDSDOC @param gold
     * NEEDSDOC @param test
     * NEEDSDOC @param reporter
     * NEEDSDOC @param warning
     * NEEDSDOC @param next
     *
     * NEEDSDOC (basicChildCompare) @return
     */
    boolean basicChildCompare(Node gold, Node test, PrintWriter reporter,
                              boolean[] warning, Node next[])
    {

        next[0] = gold;
        next[1] = test;

        boolean alreadyTriedToAdvance = false;

        if ((null != gold) && (null == test))
        {
            gold = tryToAdvancePastWhitespace(gold, reporter, warning, next,
                                              0);
            alreadyTriedToAdvance = true;

            if ((null != gold) && (null == test))
            {
                reporter.print(MISSING_TEST_NODE + nodeTypeString(gold)
                               + SEPARATOR + SEPARATOR
                               + "missing node on test");

                return false;
            }
        }
        else if ((null == gold) && (null != test))
        {
            test = tryToAdvancePastWhitespace(test, reporter, warning, next,
                                              1);
            alreadyTriedToAdvance = true;

            if ((null == gold) && (null != test))
            {
                reporter.print(MISSING_GOLD_NODE + SEPARATOR
                               + nodeTypeString(test) + SEPARATOR
                               + "missing node on gold");

                return false;
            }
        }

        if ((null != gold) && (gold.getNodeType() != test.getNodeType()))
        {
            Node savedGold = gold;
            Node savedTest = test;

            if (!alreadyTriedToAdvance)
            {
                gold = tryToAdvancePastWhitespace(gold, reporter, warning,
                                                  next, 0);

                if (gold == savedGold)
                {
                    test = tryToAdvancePastWhitespace(test, reporter,
                                                      warning, next, 1);
                }
            }

            if ((null != gold) && (gold.getNodeType() != test.getNodeType()))
            {
                gold = savedGold;
                test = savedTest;

                reporter.print(MISMATCH_NODE + nodeTypeString(gold)
                               + SEPARATOR + nodeTypeString(test) + SEPARATOR
                               + "node type mismatch");

                return false;
            }
        }

        return true;
    }  // end of basicChildCompare()

    /**
     * Cheap-o text printout of a node.  By Scott.  
     *
     * NEEDSDOC @param n
     *
     * NEEDSDOC ($objectName$) @return
     */
    String nodeTypeString(Node n)
    {

        String s;

        switch (n.getNodeType())
        {
        case Node.DOCUMENT_NODE :
            s = "DOCUMENT_NODE(" + n.getNodeName() + ")";
            break;
        case Node.ELEMENT_NODE :
            s = "ELEMENT_NODE(" + n.getNodeName() + ")";
            break;
        case Node.CDATA_SECTION_NODE :
            s = "CDATA_SECTION_NODE(" + n.getNodeName() + ")";
            break;
        case Node.ENTITY_REFERENCE_NODE :
            s = "ENTITY_REFERENCE_NODE(" + n.getNodeName() + ")";
            break;
        case Node.ATTRIBUTE_NODE :
            s = "ATTRIBUTE_NODE(" + n.getNodeName() + ")";
            break;
        case Node.COMMENT_NODE :
            s = "COMMENT_NODE(" + n.getNodeName() + ")";
            break;
        case Node.ENTITY_NODE :
            s = "ENTITY_NODE(" + n.getNodeName() + ")";
            break;
        case Node.NOTATION_NODE :
            s = "NOTATION_NODE(" + n.getNodeName() + ")";
            break;
        case Node.PROCESSING_INSTRUCTION_NODE :
            s = "PROCESSING_INSTRUCTION_NODE(" + n.getNodeName() + ")";
            break;
        case Node.TEXT_NODE :
            s = "TEXT_NODE(" + n.getNodeName() + ")";
            break;
        default :
            s = "UNKNOWN_NODE(" + n.getNodeName() + ")";
        }

        return s;
    }  // end of nodeTypeString()

    /**
     * Parameter: force use of URI's for Xerces 1.1.2 or leave filenames alone?
     */
    protected boolean useURI = true;

    /**
     * NEEDSDOC Method parse 
     *
     *
     * NEEDSDOC @param filename
     * NEEDSDOC @param reporter
     * NEEDSDOC @param which
     *
     * NEEDSDOC (parse) @return
     */
    Document parse(String filename, PrintWriter reporter, String which)
    {

        // Force filerefs to be URI's if needed: note this is independent of any other files
        // Remember: this only applies to the wacky Xerces parser, which we're presumably
        //  using as our default DOMParser() below
        String xercesFilename = filename;

        if (useURI)
        {
            try
            {

                // Use static worker method to get the correct format
                // Note: this is copied straight from Xalan 1.x's org.apache.xalan.xslt.Process
                // TODO verify this is the most correct and simplest way to munge the filename
                xercesFilename = getURLFromString(filename,
                                                  null).toExternalForm();
            }
            catch (Exception e)
            {
                reporter.print(WARNING + e.toString() + "\n");
            }
        }

        String parseType = which + PARSE_TYPE + "[xml];";
        DOMParser parser = new DOMParser();
        Document doc = null;

        try
        {

            // Use the Xerces-munged name specifically here!
            parser.parse(new InputSource(xercesFilename));

            doc = parser.getDocument();
        }
        catch (Throwable se)
        {
            reporter.print(WARNING + se.toString() + "\n");

            parseType = which + PARSE_TYPE + "[html];";

            try
            {

                // @todo need to find an HTML to DOM parser we can use!!!
                // doc = someHTMLParser.parse(new InputSource(filename));
                throw new RuntimeException("We need an HTML to DOM parser!");
            }
            catch (Exception e)
            {
                try
                {
                    reporter.print(WARNING + e.toString() + "\n");

                    parseType = which + PARSE_TYPE + "[text];";

                    // Safer not to rely directly upon the DocumentImpl classes.
                    //   DocumentImpl docImpl = new DocumentImpl();
                    //   docImpl.appendChild(docElem);
                    //   DocumentImpl docImpl =
                    // Instead, use the DOM Level 2 createDocument call.
                    // Obtaining the DOMImplementation is still nonportable;
                    // DOM Level 3 may address that.
                    Document docImpl =
                        DOMImplementationImpl.getDOMImplementation().createDocument(
                            null, "out", null);
                    Element docElem = docImpl.getDocumentElement();

                    // Parse as text, line by line
                    //   Since we already know it should be text, this should 
                    //   work better than parsing by bytes.
                    FileReader fr = new FileReader(filename);
                    BufferedReader br = new BufferedReader(fr);
                    StringBuffer buffer = new StringBuffer();

                    for (;;)
                    {
                        String tmp = br.readLine();

                        if (tmp == null)
                        {
                            break;
                        }

                        buffer.append(tmp);
                        buffer.append("\n");  // Put in the newlines as well
                    }

                    Text tx = docImpl.createTextNode(buffer.toString());

                    // Note: will this always be a valid node?  If we're parsing 
                    //    in as text, will there ever be cases where the diff that's 
                    //    done later on will fail becuase some really garbage-like 
                    //    text has been put into a node?
                    docElem.appendChild(tx);

                    doc = docImpl;
                }
                catch (Throwable throwable)
                {
                    reporter.print(OTHER_ERROR + filename + SEPARATOR
                                   + "threw:" + throwable.toString() + "\n");
                }
            }
        }

        // Output a newline here for readability
        reporter.print(parseType + "\n");

        return doc;
    }  // end of parse()

    /**
     * Take a user string and try and parse XML, and also return the url.
     * <p>Note: this needs to be revisited.  It is only used to convert Strings
     * that represent filenames into the proper URL format for Xerces 1.1x parser.</p>
     * Note: this is copied straight from Xalan 1.x's org.apache.xalan.xslt.Process
     *
     * NEEDSDOC @param urlString
     * NEEDSDOC @param base
     *
     * NEEDSDOC ($objectName$) @return
     * @exception Exception thrown if we really really can't create the URL
     */
    public static URL getURLFromString(String urlString, String base)
            throws Exception
    {

        String origURLString = urlString;
        String origBase = base;

        // System.out.println("getURLFromString - urlString: "+urlString+", base: "+base);
        Object doc;
        URL url = null;
        int fileStartType = 0;

        try
        {
            if (null != base)
            {
                if (base.toLowerCase().startsWith("file:/"))
                {
                    fileStartType = 1;
                }
                else if (base.toLowerCase().startsWith("file:"))
                {
                    fileStartType = 2;
                }
            }

            boolean isAbsoluteURL;

            // From http://www.ics.uci.edu/pub/ietf/uri/rfc1630.txt
            // A partial form can be distinguished from an absolute form in that the
            // latter must have a colon and that colon must occur before any slash
            // characters. Systems not requiring partial forms should not use any
            // unencoded slashes in their naming schemes.  If they do, absolute URIs
            // will still work, but confusion may result.
            int indexOfColon = urlString.indexOf(':');
            int indexOfSlash = urlString.indexOf('/');

            if ((indexOfColon != -1) && (indexOfSlash != -1)
                    && (indexOfColon < indexOfSlash))
            {

                // The url (or filename, for that matter) is absolute.
                isAbsoluteURL = true;
            }
            else
            {
                isAbsoluteURL = false;
            }

            if (isAbsoluteURL || (null == base) || (base.length() == 0))
            {
                try
                {
                    url = new URL(urlString);
                }
                catch (MalformedURLException e){}
            }

            // The Java URL handling doesn't seem to handle relative file names.
            else if (!((urlString.charAt(0) == '.') || (fileStartType > 0)))
            {
                try
                {
                    URL baseUrl = new URL(base);

                    url = new URL(baseUrl, urlString);
                }
                catch (MalformedURLException e){}
            }

            if (null == url)
            {

                // Then we're going to try and make a file URL below, so strip 
                // off the protocol header.
                if (urlString.toLowerCase().startsWith("file:/"))
                {
                    urlString = urlString.substring(6);
                }
                else if (urlString.toLowerCase().startsWith("file:"))
                {
                    urlString = urlString.substring(5);
                }
            }

            if ((null == url) && ((null == base) || (fileStartType > 0)))
            {
                if (1 == fileStartType)
                {
                    if (null != base)
                        base = base.substring(6);

                    fileStartType = 1;
                }
                else if (2 == fileStartType)
                {
                    if (null != base)
                        base = base.substring(5);

                    fileStartType = 2;
                }

                File f = new File(urlString);

                if (!f.isAbsolute() && (null != base))
                {

                    // String dir = f.isDirectory() ? f.getAbsolutePath() : f.getParent();
                    // System.out.println("prebuiltUrlString (1): "+base);
                    StringTokenizer tokenizer = new StringTokenizer(base,
                                                    "\\/");
                    String fixedBase = null;

                    while (tokenizer.hasMoreTokens())
                    {
                        String token = tokenizer.nextToken();

                        if (null == fixedBase)
                        {

                            // Thanks to Rick Maddy for the bug fix for UNIX here.
                            if (base.charAt(0) == '\\'
                                    || base.charAt(0) == '/')
                            {
                                fixedBase = File.separator + token;
                            }
                            else
                            {
                                fixedBase = token;
                            }
                        }
                        else
                        {
                            fixedBase += File.separator + token;
                        }
                    }

                    // System.out.println("rebuiltUrlString (1): "+fixedBase);
                    f = new File(fixedBase);

                    String dir = f.isDirectory()
                                 ? f.getAbsolutePath() : f.getParent();

                    // System.out.println("dir: "+dir);
                    // System.out.println("urlString: "+urlString);
                    // f = new File(dir, urlString);
                    // System.out.println("f (1): "+f.toString());
                    // urlString = f.getAbsolutePath();
                    f = new File(urlString);

                    boolean isAbsolute = f.isAbsolute()
                                         || (urlString.charAt(0) == '\\')
                                         || (urlString.charAt(0) == '/');

                    if (!isAbsolute)
                    {

                        // Getting more and more ugly...
                        if (dir.charAt(dir.length() - 1)
                                != File.separator.charAt(0)
                                && urlString.charAt(0)
                                   != File.separator.charAt(0))
                        {
                            urlString = dir + File.separator + urlString;
                        }
                        else
                        {
                            urlString = dir + urlString;
                        }

                        // System.out.println("prebuiltUrlString (2): "+urlString);
                        tokenizer = new StringTokenizer(urlString, "\\/");

                        String rebuiltUrlString = null;

                        while (tokenizer.hasMoreTokens())
                        {
                            String token = tokenizer.nextToken();

                            if (null == rebuiltUrlString)
                            {

                                // Thanks to Rick Maddy for the bug fix for UNIX here.
                                if (urlString.charAt(0) == '\\'
                                        || urlString.charAt(0) == '/')
                                {
                                    rebuiltUrlString = File.separator + token;
                                }
                                else
                                {
                                    rebuiltUrlString = token;
                                }
                            }
                            else
                            {
                                rebuiltUrlString += File.separator + token;
                            }
                        }

                        // System.out.println("rebuiltUrlString (2): "+rebuiltUrlString);
                        if (null != rebuiltUrlString)
                            urlString = rebuiltUrlString;
                    }

                    // System.out.println("fileStartType: "+fileStartType);
                    if (1 == fileStartType)
                    {
                        if (urlString.charAt(0) == '/')
                        {
                            urlString = "file://" + urlString;
                        }
                        else
                        {
                            urlString = "file:/" + urlString;
                        }
                    }
                    else if (2 == fileStartType)
                    {
                        urlString = "file:" + urlString;
                    }

                    try
                    {

                        // System.out.println("Final before try: "+urlString);
                        url = new URL(urlString);
                    }
                    catch (MalformedURLException e)
                    {

                        // System.out.println("Error trying to make URL from "+urlString);
                    }
                }
            }

            if (null == url)
            {

                // The sun java VM doesn't do this correctly, but I'll 
                // try it here as a second-to-last resort.
                if ((null != origBase) && (origBase.length() > 0))
                {
                    try
                    {
                        URL baseURL = new URL(origBase);

                        // System.out.println("Trying to make URL from "+origBase+" and "+origURLString);
                        url = new URL(baseURL, origURLString);

                        // System.out.println("Success! New URL is: "+url.toString());
                    }
                    catch (MalformedURLException e)
                    {

                        // System.out.println("Error trying to make URL from "+origBase+" and "+origURLString);
                    }
                }

                if (null == url)
                {
                    try
                    {
                        String lastPart;

                        if (null != origBase)
                        {
                            File baseFile = new File(origBase);

                            if (baseFile.isDirectory())
                            {
                                lastPart =
                                    new File(baseFile,
                                             urlString).getAbsolutePath();
                            }
                            else
                            {
                                String parentDir = baseFile.getParent();

                                lastPart =
                                    new File(parentDir,
                                             urlString).getAbsolutePath();
                            }
                        }
                        else
                        {
                            lastPart = new File(urlString).getAbsolutePath();
                        }

                        // Hack
                        // if((lastPart.charAt(0) == '/') && (lastPart.charAt(2) == ':'))
                        //   lastPart = lastPart.substring(1, lastPart.length() - 1);
                        String fullpath;

                        if (lastPart.charAt(0) == '\\'
                                || lastPart.charAt(0) == '/')
                        {
                            fullpath = "file://" + lastPart;
                        }
                        else
                        {
                            fullpath = "file:" + lastPart;
                        }

                        url = new URL(fullpath);
                    }
                    catch (MalformedURLException e2)
                    {

                        // Below throw modified from original version
                        throw new Exception("Cannot create url for: "
                                            + urlString + " threw: "
                                            + e2.toString());

                        //XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_CREATE_URL, new Object[]{urlString}),e2); //"Cannot create url for: " + urlString, e2 );
                    }
                }
            }
        }
        catch (SecurityException se)
        {
            try
            {
                url = new URL("http://xml.apache.org/xslt/"
                              + java.lang.Math.random());  // dummy
            }
            catch (MalformedURLException e2)
            {

                // I give up
            }
        }

        // System.out.println("url: "+url.toString());
        return url;
    }
}
