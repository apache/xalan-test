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

package org.apache.qetest.xsl;

import org.apache.qetest.Logger;  // Only for PASS_RESULT, etc.
import org.apache.qetest.QetestUtils;

import java.io.PrintWriter;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Vector;
import java.util.Properties;
import java.util.StringTokenizer;

// DOM imports
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;

// Needed JAXP classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// SAX2 imports
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.w3c.tidy.*;
import java.net.URLConnection;

/**
 * Defines XSLTC's XML/HTML/Text diff comparator to check or diff two files.
 * This comparator uses the expanded name instead of the qname to compare
 * element names. Also, for simplicity, it ignores NS declaration attributes.
 *
 * //@todo Use expanded name for attributes as well
 *
 * @author Scott_Boag@lotus.com
 * @author Shane_Curcuru@lotus.com
 * @author Santiago.PericasGeertsen@sun.com
 * @version $Id$
 */
public class XHTComparatorXSLTC extends XHTComparator
{
    /**
     * Simple worker method to parse filename to a Document.  
     *
     * <p>Attempts XML parse, if that throws an exception, then 
     * we attempt an HTML parse (when parser available), if 
     * that throws an exception, then we parse as text: 
     * we construct a faux document element to hold it all, 
     * and then parse by readLine() and put each line of 
     * text into a &lt;line> element.</p>
     *
     * @param filename to parse as a local path
     * @param reporter PrintWriter to dump status info to
     * @param which either TEST or GOLD file being parsed
     * @param attributes name=value pairs to set on the 
     * DocumentBuilderFactory that we use to parse
     *
     * @return Document object with contents of the file; 
     * otherwise throws an unchecked RuntimeException if there 
     * is any fatal problem
     */
    Document parse(String filename, PrintWriter reporter, String which, Properties attributes)
    {
        // Force filerefs to be URI's if needed: note this is independent of any other files
        String docURI = QetestUtils.filenameToURL(filename);
        
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        // Always set namespaces on
        dfactory.setNamespaceAware(true);
        // Set other attributes here as needed
        applyAttributes(dfactory, attributes);
        
        // Local class: cheap non-printing ErrorHandler
        // This is used to suppress validation warnings which 
        //  would otherwise clutter up the console
        ErrorHandler nullHandler = new ErrorHandler() {
            public void warning(SAXParseException e) throws SAXException {}
            public void error(SAXParseException e) throws SAXException {}
            public void fatalError(SAXParseException e) throws SAXException 
            {
                throw e;
            }
        };

        String parseType = which + PARSE_TYPE + "[xml];";
        Document doc = null;
        try
        {
            // First, attempt to parse as XML (preferred)...
            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            docBuilder.setErrorHandler(nullHandler);
            doc = docBuilder.parse(new InputSource(docURI));
        }
        catch (Throwable se)
        {
            // ... if we couldn't parse as XML, attempt parse as HTML...
            reporter.println(WARNING + se.toString());
            parseType = which + PARSE_TYPE + "[html];";

            try
            {
	        Tidy tidy = new Tidy();
	        tidy.setXHTML(true);
	        tidy.setTidyMark(false);
	        tidy.setShowWarnings(false);
	        tidy.setQuiet(true);
	        doc  = tidy.parseDOM(new URL(docURI).openStream(), null);

                // @todo need to find an HTML to DOM parser we can use!!!
                // doc = someHTMLParser.parse(new InputSource(filename));
                // throw new RuntimeException("XHTComparator no HTML parser!");
            }
            catch (Exception e)
            {
                // ... if we can't parse as HTML, then just parse the text
                try
                {
                    reporter.println(WARNING + e.toString());
                    parseType = which + PARSE_TYPE + "[text];";

                    // First build a faux document with parent element
                    DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
                    doc = docBuilder.newDocument();
                    Element outElem = doc.createElement("out");

                    // Parse as text, line by line
                    //   Since we already know it should be text, this should 
                    //   work better than parsing by bytes.
                    FileReader fr = new FileReader(filename);
                    BufferedReader br = new BufferedReader(fr);
                    for (;;)
                    {
                        String tmp = br.readLine();

                        if (tmp == null)
                        {
                            break;
                        }
                        // An additional thing we could do would 
                        //  be to put in the line number in the 
                        //  file in here somehow, so when users 
                        //  view reports, they get that info
                        Element lineElem = doc.createElement("line");
                        outElem.appendChild(lineElem);
                        Text textNode = doc.createTextNode(tmp);
                        lineElem.appendChild(textNode);
                    }
                    // Now stick the whole element into the document to return
                    doc.appendChild(outElem);
                }
                catch (Throwable throwable)
                {
                    reporter.println(OTHER_ERROR + filename + SEPARATOR
                                   + "threw:" + throwable.toString());
                }
            }
        }

        // Output a newline here for readability
        reporter.println(parseType);

        return doc;
    }  // end of parse()


    /**
     * The contract is: when you enter here the gold and test nodes are the same type,
     * both non-null, and both in the same basic position in the tree.
     * //@todo verify caller really performs for the contract -sc
     *
     * @param gold gold or expected node
     * @param test actual node
     * @param reporter PrintWriter to dump status info to
     * @param warning[] if any whitespace diffs found
     *
     * @return true if pass, false if any problems encountered
     */
    boolean diff(Node gold, Node test, PrintWriter reporter,
                 boolean[] warning)
    {

        String name1 = gold.getLocalName();
        String name2 = test.getLocalName();

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

	String uri1 = gold.getNamespaceURI();
	String uri2 = test.getNamespaceURI();

        // If both there but not equal, fail
        if ((null != uri1) && (null != uri2) && !uri1.equals(uri2))
        {
            reporter.println(MISMATCH_NODE + nodeTypeString(gold) + SEPARATOR
                           + nodeTypeString(test) + SEPARATOR
                           + "namespace URI does not equal test node");

            return false;
        }
        else if ((null != uri1) && (null == uri2))
        {
            reporter.println(MISSING_TEST_NODE + nodeTypeString(gold)
                           + SEPARATOR + nodeTypeString(test) + SEPARATOR
                           + "namespace URI missing on test");

            return false;
        }
        else if ((null == uri1) && (null != uri2))
        {
            reporter.println(MISSING_GOLD_NODE + nodeTypeString(gold)
                           + SEPARATOR + nodeTypeString(test) + SEPARATOR
                           + "namespace URI missing on gold");

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

	    // Remove NS declarations from gold attribute list
	    Vector nsDeclarations = new Vector();
	    int length = goldAttrs.getLength();
	    for (int i = 0; i < length; i++) {
		final String name = goldAttrs.item(i).getNodeName();
		if (name.startsWith("xmlns")) {
		    nsDeclarations.addElement(name);
		}
	    }
	    length = nsDeclarations.size();
	    for (int i = 0; i < length; i++) {
		goldAttrs.removeNamedItem((String) nsDeclarations.elementAt(i));
	    }

	    // Remove NS declarations from test attribute list
	    nsDeclarations.clear();
	    length = testAttrs.getLength();
	    for (int i = 0; i < length; i++) {
		final String name = testAttrs.item(i).getNodeName();
		if (name.startsWith("xmlns")) {
		    nsDeclarations.addElement(name);
		}
	    }
	    length = nsDeclarations.size();
	    for (int i = 0; i < length; i++) {
		testAttrs.removeNamedItem((String) nsDeclarations.elementAt(i));
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
}
