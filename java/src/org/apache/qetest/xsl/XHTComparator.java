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

import org.apache.qetest.Logger;  // Only for PASS_RESULT, etc.
import org.apache.qetest.QetestUtils;

import java.io.PrintWriter;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Properties;
import java.util.StringTokenizer;

// DOM imports
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;

// Xerces imports - use JAXP interface instead
// Needed JAXP classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// SAX2 imports
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
 * //@todo document whitespace difference handling better -sc
 * //@todo check how namespaces are handled and diff'd -sc
 * //@todo check how XML decls are handled (or not) -sc
 * //@todo check how files of different encodings are handled in each parse type -sc
 * //@todo Allow param to define the type of parse we do (i.e. if a
 * testwriter knows their output file will be XML, we should only
 * attempt to parse it as XML, not other types)
 * @author Scott_Boag@lotus.com
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class XHTComparator
{

    /** Default value for maximum output length of diff'd values.  */
    static final int MAX_VALUE_DISPLAY_LEN = 511;  // arbitrary length, for convenience

    /** 
     * Maximum output length we may log for differing values.  
     * When two nodes have mismatched values, we output the first 
     * two values that were mismatched.  In some cases, this may be 
     * extremely long, so limit how much we output for convenience.
     */
    private int maxDisplayLen = MAX_VALUE_DISPLAY_LEN;

    /**
     * Accessor method for maxDisplayLen.
     * @param i maximum length we log out
     */
    public void setMaxDisplayLen(int i)
    {
        if (i > 0)
            maxDisplayLen = i;
    }

    /** Constants for reporting out reason for failed diffs. */
    public static final String SEPARATOR = ";";

    /** LBRACKET '['  */
    public static final String LBRACKET = "[";

    /** RBRACKET ']'  */
    public static final String RBRACKET = "]";

    /** TEST 'test', for the actual value.  */
    public static final String TEST = "test";

    /** GOLD 'gold' for the gold or expected value.  */
    public static final String GOLD = "gold";

    /** PARSE_TYPE '-parse-type' */
    public static final String PARSE_TYPE = "-parse-type" + SEPARATOR;  // postpended to TEST or GOLD

    /** OTHER_ERROR 'other-error'  */
    public static final String OTHER_ERROR = "other-error" + SEPARATOR;

    /** WARNING 'warning'  */
    public static final String WARNING = "warning" + SEPARATOR;

    /** MISMATCH_NODE  */
    public static final String MISMATCH_NODE = "mismatch-node" + SEPARATOR;

    /** MISSING_TEST_NODE  */
    public static final String MISSING_TEST_NODE = "missing-node-" + TEST
                                                       + SEPARATOR;

    /** MISSING_GOLD_NODE  */
    public static final String MISSING_GOLD_NODE = "missing-node-" + GOLD
                                                       + SEPARATOR;

    /** MISMATCH_ATTRIBUTE */
    public static final String MISMATCH_ATTRIBUTE = "mismatch-attribute"
                                                        + SEPARATOR;

    /** MISMATCH_VALUE  */
    public static final String MISMATCH_VALUE = "mismatch-value" + SEPARATOR;

    /** MISMATCH_VALUE  */
    public static final String MISMATCH_VALUE_GOLD = "mismatch-value-gold" + SEPARATOR;

    /** MISMATCH_VALUE  */
    public static final String MISMATCH_VALUE_TEXT = "mismatch-value-text" + SEPARATOR;

    /** MISSING_TEST_VALUE  */
    public static final String MISSING_TEST_VALUE = "missing-value-" + TEST
                                                        + SEPARATOR;

    /** MISSING_GOLD_VALUE  */
    public static final String MISSING_GOLD_VALUE = "missing-value-" + GOLD
                                                        + SEPARATOR;

    /** WHITESPACE_DIFF  */
    public static final String WHITESPACE_DIFF = "whitespace-diff;";

    /**
     * Compare two files by parsing into DOMs and comparing trees.
     * @param goldFileName expected file
     * @param testFileName actual file
     * @param reporter PrintWriter to dump status info to
     * @param array of warning flags (for whitespace diffs, I think?)
     * NEEDSDOC @param warning
     * @param attributes to attempt to set onto parsers
     * @return true if they match, false otherwise
     */
    public boolean compare(String goldFileName, String testFileName,
                           PrintWriter reporter, boolean[] warning,
                           Properties attributes)
    {

        // parse the gold doc
        Document goldDoc = parse(goldFileName, reporter, GOLD, attributes);

        // parse the test doc only if gold doc was parsed OK
        Document testDoc = (null != goldDoc)
                           ? parse(testFileName, reporter, TEST, attributes) : null;

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
     * //@todo verify caller really performs for the contract -sc
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
            reporter.println(MISMATCH_NODE + nodeTypeString(gold) + SEPARATOR
                           + nodeTypeString(test) + SEPARATOR
                           + "name does not equal test node");

            return false;
        }
        else if ((null != name1) && (null == name2))
        {
            reporter.println(MISSING_TEST_NODE + nodeTypeString(gold)
                           + SEPARATOR + nodeTypeString(test) + SEPARATOR
                           + "name missing on test");

            return false;
        }
        else if ((null == name1) && (null != name2))
        {
            reporter.println(MISSING_GOLD_NODE + nodeTypeString(gold)
                           + SEPARATOR + nodeTypeString(test) + SEPARATOR
                           + "name missing on gold");

            return false;
        }

        String value1 = gold.getNodeValue();
        String value2 = test.getNodeValue();

        if ((null != value1) && (null != value2) &&!value1.equals(value2))
        {
            reporter.println(MISMATCH_VALUE + nodeTypeString(gold) + "len="
                           + value1.length() + SEPARATOR
                           + nodeTypeString(test) + "len=" + value2.length()
                           + SEPARATOR + "lengths do not match");

            // Limit length we output to logs; extremely long values 
            //  are more hassle than they're worth (at that point, 
            //  it's either obvious what the problem is, or it's 
            //  such a small problem that you'll need to manually
            //  compare the files separately
            if (value1.length() > maxDisplayLen)
                value1 = value1.substring(0, maxDisplayLen);
            if (value2.length() > maxDisplayLen)
                value2 = value2.substring(0, maxDisplayLen);
            reporter.println(MISMATCH_VALUE_GOLD + nodeTypeString(gold) + SEPARATOR + "\n" + value1);
            reporter.println(MISMATCH_VALUE_TEXT + nodeTypeString(test) + SEPARATOR + "\n" + value2);

            return false;
        }
        else if ((null != value1) && (null == value2))
        {
            reporter.println(MISSING_TEST_VALUE + nodeTypeString(gold) + "-"
                           + value1 + SEPARATOR + nodeTypeString(test)
                           + SEPARATOR + "test no value");

            return false;
        }
        else if ((null == value1) && (null != value2))
        {
            reporter.println(MISSING_GOLD_VALUE + nodeTypeString(gold)
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
                reporter.println(MISMATCH_ATTRIBUTE + nodeTypeString(gold)
                               + SEPARATOR + nodeTypeString(test) + SEPARATOR
                               + "test no attrs");

                return false;
            }
            else if ((null == goldAttrs) && (null != testAttrs))
            {
                reporter.println(MISMATCH_ATTRIBUTE + nodeTypeString(gold)
                               + SEPARATOR + nodeTypeString(test) + SEPARATOR
                               + "gold no attrs");

                return false;
            }

            int gn = goldAttrs.getLength();
            int tn = testAttrs.getLength();

            if (gn != tn)
            {
                reporter.println(MISMATCH_ATTRIBUTE + nodeTypeString(gold)
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
                    reporter.println(MISMATCH_ATTRIBUTE + nodeTypeString(gold)
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
     * Returns Character.isWhitespace
     * @param s String to check for whitespace
     * @return true if all whitespace; false otherwise
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
                reporter.println(MISSING_TEST_NODE + nodeTypeString(gold)
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
                reporter.println(MISSING_GOLD_NODE + SEPARATOR
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

                reporter.println(MISMATCH_NODE + nodeTypeString(gold)
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
    Document parse(String filename, PrintWriter reporter, String which, Properties attributes)
    {
        // Force filerefs to be URI's if needed: note this is independent of any other files
        String docURI = filename;
        if (useURI)
        {
            // Use static worker method to get the correct format
            docURI = QetestUtils.filenameToURL(filename);
        }

        // Use JAXP instead of Xerces-specific calls
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        // Always set namespaces on
        dfactory.setNamespaceAware(true);
        // Set other attributes here as needed
        applyAttributes(dfactory, attributes);
        
        String parseType = which + PARSE_TYPE + "[xml];";
        Document doc = null;
        try
        {
            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            doc = docBuilder.parse(new InputSource(docURI));
        }
        catch (Throwable se)
        {
            // We couldn't parse as XML, attempt parse as HTML
            reporter.println(WARNING + se.toString());
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
                    // We couldn't parse as HTML, just compare as strings
                    reporter.println(WARNING + e.toString());
                    parseType = which + PARSE_TYPE + "[text];";

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

                    DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
                    doc = docBuilder.newDocument();
                    Element outElem = doc.createElement("out");
                    Text textNode = doc.createTextNode(buffer.toString());

                    // Note: will this always be a valid node?  If we're parsing 
                    //    in as text, will there ever be cases where the diff that's 
                    //    done later on will fail becuase some really garbage-like 
                    //    text has been put into a node?
                    outElem.appendChild(textNode);
                    doc.appendChild(outElem);
                }
                catch (Throwable throwable)
                {
                    reporter.println(OTHER_ERROR + filename + SEPARATOR
                                   + "threw:" + throwable.toString());
                    // throwable.printStackTrace();
                }
            }
        }

        // Output a newline here for readability
        reporter.println(parseType);

        return doc;
    }  // end of parse()
    
    /**
     * Pass applicable attributes onto our DocumentBuilderFactory.  
     *
     * Only passes thru attributes we explicitly know about and 
     * are constants from XHTFileCheckService.
     * 
     * @param dbf factory to attempt to set* onto
     * @param attrs various attributes we should try to set
     */
    protected void applyAttributes(DocumentBuilderFactory dfactory, Properties attributes)
    {
        if ((null == attributes) || (null == dfactory))
            return;

        String tmp = attributes.getProperty(XHTFileCheckService.SETVALIDATING);
        if (null != tmp)
        {
            dfactory.setValidating(new Boolean(tmp).booleanValue());
        }
        tmp = attributes.getProperty(XHTFileCheckService.SETIGNORINGELEMENTCONTENTWHITESPACE);
        if (null != tmp)
        {
            dfactory.setIgnoringElementContentWhitespace(new Boolean(tmp).booleanValue());
        }
        tmp = attributes.getProperty(XHTFileCheckService.SETEXPANDENTITYREFERENCES);
        if (null != tmp)
        {
            dfactory.setExpandEntityReferences(new Boolean(tmp).booleanValue());
        }
        tmp = attributes.getProperty(XHTFileCheckService.SETIGNORINGCOMMENTS);
        if (null != tmp)
        {
            dfactory.setIgnoringComments(new Boolean(tmp).booleanValue());
        }
        tmp = attributes.getProperty(XHTFileCheckService.SETCOALESCING);
        if (null != tmp)
        {
            dfactory.setCoalescing(new Boolean(tmp).booleanValue());
        }
        /* Unknown attributes are ignored! */
    }

}
