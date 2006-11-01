/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */

/*
 *
 * XPathASTTestlet.java
 *
 */
package org.apache.qetest.rwapi;

import org.apache.qetest.*;
import org.apache.qetest.xsl.StylesheetDatalet;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import org.apache.xpath.rwapi.XPathFactory;
import org.apache.xpath.rwapi.eval.Evaluator;
import org.apache.xpath.rwapi.expression.*;
import org.apache.xpath.rwapi.impl.parser.SimpleNode;
import org.apache.xpath.rwapi.impl.parser.XPath;
import org.apache.xpath.rwapi.impl.parser.XPathTreeConstants;
import java.io.StringReader;


/**
 * Testlet for testing XPath AST construction and manipulation.
 *
 * <p>NEEDSWORK: Crash test: little validation performed.  Meant to 
 * let us quickly see what kinds of AST's get built from various 
 * match= or select= patterns in our existing test suites.</p>
 *
 * <p><b>NOTE:</b> Feb-03 runs only with rwapi code from xslt20 branch!</p>
 *
 * @author Shane_Curcuru@us.ibm.com
 * @version $Id$
 */
public class XPathASTTestlet extends FileTestlet
{
    // Initialize our classname for TestletImpl's main() method
    static { thisClassName = "org.apache.qetest.xsl.XPathASTTestlet"; }

    // Initialize our defaultDatalet
    { defaultDatalet = (Datalet)new StylesheetDatalet(); }

    /**
     * Accesor method for a brief description of this test.  
     *
     * @return String describing what this XPathASTTestlet does.
     */
    public String getDescription()
    {
        return "XPathASTTestlet";
    }


    /**
     * Run this XPathASTTestlet: execute it's test and return.
     *
     * @param Datalet to use as data point for the test.
     */
    public void execute(Datalet d)
	{
        // Ensure we have the correct kind of datalet
        StylesheetDatalet datalet = null;
        try
        {
            datalet = (StylesheetDatalet)d;
        }
        catch (ClassCastException e)
        {
            logger.checkErr("Datalet provided is not a StylesheetDatalet; cannot continue with " + d);
            return;
        }

        // Perform any other general setup needed; 
        //  side effect of getting needed XPath data
        if (testletInit(datalet))
        {
            try
            {
                // Transform our supplied input file...
                testDatalet(datalet);
    
                // ...and compare with gold data
                checkDatalet(datalet);
            }
            // Handle any exceptions from the testing
            catch (Throwable t)
            {
                handleException(datalet, t);
                return;
            }
        }
	}

    // Since we're looking for attrs, they're not namespaced
    public static final String ATTR_URI = null;
    public static final String MATCH_PATTERNS = "match";
    public static final String SELECT_PATTERNS = "select";
    
    // Store collected patterns for our use
    private Vector matchpats = new Vector();
    private Vector selectpats = new Vector();

    /** 
     * Worker method to perform any pre-processing needed.  
     * <p>Overridden to gather XPaths from input stylesheets to test.</p>
     *
     * @param datalet to test with
     * @return false if we should abort; true otherwise
     */
    protected boolean testletInit(StylesheetDatalet datalet)
    {
        if (null == datalet.inputName)
        {
            logger.logMsg(logger.WARNINGMSG, "Datalet had null inputName for "
                    + datalet.xmlName);
            return false;
        }
        
        // Find and parse the datalet.inputName file to a DOM
        // Search whole DOM for match attrs and save; select attrs too
        Document doc;
        try
        {
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            doc = docBuilder.parse(new InputSource(datalet.inputName));

            NodeIterator iterator = ((DocumentTraversal)doc).
                createNodeIterator(
                    doc, 
                    NodeFilter.SHOW_ELEMENT, 
                    new NamedAttrFilter(ATTR_URI, MATCH_PATTERNS),
                    true);
            Node node = iterator.nextNode();
            while (null != node)
            {
                matchpats.add(((Element)node).getAttributeNS(ATTR_URI, MATCH_PATTERNS));
                node = iterator.nextNode();
            }
            iterator.detach();

            iterator = ((DocumentTraversal)doc).
                createNodeIterator(
                    doc, 
                    NodeFilter.SHOW_ELEMENT, 
                    new NamedAttrFilter(ATTR_URI, SELECT_PATTERNS),
                    true);
            node = iterator.nextNode();
            while (null != node)
            {
                selectpats.add(((Element)node).getAttributeNS(ATTR_URI, SELECT_PATTERNS));
                node = iterator.nextNode();
            }
            iterator.detach();
        }
        catch (Exception e)
        {
            logger.logThrowable(Logger.ERRORMSG, e, getCheckDescription(datalet));
            return false;
        }

        return true;
    }


    /** 
     * Worker method to actually perform the test.  
     *
     * <p>For each match/select, create an AST and dump.  Fail if we 
     * get a null or throw an exception, pass otherwise.</p>
     *
     * @param datalet to test with
     * @throws allows any underlying exception to be thrown
     */
    protected void testDatalet(StylesheetDatalet datalet)
            throws Exception
    {
        //@todo Should we log a custom logElement here instead?
        logger.logMsg(Logger.TRACEMSG, "executing with: inputName=" + datalet.inputName);

        // Cheap-o validation: ensure non-null, dump contents
        if (null != matchpats)
        {
            for (Enumeration elements = matchpats.elements();
                    elements.hasMoreElements(); /* no increment portion */ )
            {
                logXPath((String)elements.nextElement(), datalet.getDescription());
            }
        }
        if (null != selectpats)
        {
            for (Enumeration elements = selectpats.elements();
                    elements.hasMoreElements(); /* no increment portion */ )
            {
                logXPath((String)elements.nextElement(), datalet.getDescription());
            }
        }
    }


    /** 
     * Cheap-o Worker method to dump out XPath info.  
     *
     * <p>NEEDSWORK: Overrideen to put out a default pass record.</p>
     *
     * @param String to construct XPath from
     * @param String used as comment for where this test came from
     */
    protected void logXPath(String xpStr, String desc)
    {
        String comment = desc + " XPATH{" + xpStr + "}";
        try
        {
            // Construct AST, ensure non-null, and dump
            XPath parser = new XPath(new StringReader(xpStr));
            SimpleNode tree = parser.XPath2();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            tree.dump("|", new PrintStream(baos)); // NOTE encoding issues are not necessarily covered here!
            logger.logMsg(logger.STATUSMSG, comment + " creates " + baos.toString());

            Expr expr = (Expr) tree.jjtGetChild(0);
            // Note: asking to abbreviate or not does not let you 
            //  know what the original text format was.  Since 
            //  the majority of our pre-existing tests use the 
            //  abbreviated forms, do that

            String ab = expr.getString(true);

            if (xpStr.equals(ab))
                logger.checkPass(comment + " getString OK");
            else
                logger.checkFail(comment + " getString NOTOK: " + ab);
        } 
        catch (Throwable t)
        {
            logger.logThrowable(Logger.ERRORMSG, t, comment);
            logger.checkErr(comment 
                             + " threw: " + t.toString());
        }
    }


    /** 
     * Worker method to validate output file with gold.  
     *
     * <p>NEEDSWORK: Overrideen to put out a default pass record.</p>
     *
     * @param datalet to test with
     * @throws allows any underlying exception to be thrown
     */
    protected void checkDatalet(StylesheetDatalet datalet)
            throws Exception
    {
        // NEEDSWORK: once we have 'real' validation of the 
        //  contents of the AST, we want to update this!
        logger.checkPass(getCheckDescription(datalet));
    }


    /** 
     * Worker method to validate or log exceptions thrown by testDatalet.  
     *
     * Provided so subclassing is simpler; our implementation merely 
     * calls checkErr and logs the exception.
     *
     * @param datalet to test with
     * @param e Throwable that was thrown
     */
    protected void handleException(StylesheetDatalet datalet, Throwable t)
    {
        // Put the logThrowable first, so it appears before 
        //  the Fail record, and gets color-coded
        logger.logThrowable(Logger.ERRORMSG, t, getCheckDescription(datalet));
        logger.checkErr(getCheckDescription(datalet) 
                         + " threw: " + t.toString());
    }


    /** 
     * Worker method to construct a description.  
     *
     * Simply concatenates useful info to override getDescription().
     *
     * @param datalet to test with
     * @param e Throwable that was thrown
     */
    protected String getCheckDescription(StylesheetDatalet datalet)
    {
        return getDescription() + " "
                + datalet.getDescription();
    }

    /**
     * Inner class for walking DOM to find select= and match= attrs.
     */
    class NamedAttrFilter implements NodeFilter
    {
        private String namespaceURI;
        private String localName;

        public NamedAttrFilter(String ns, String lname)
        {
            namespaceURI = ns;
            localName = lname;
        }

        /** Implement NodeFilter method.  */
        public short acceptNode(Node n)
        {
            if ("" != ((Element)n).getAttributeNS(namespaceURI, localName))
                return FILTER_ACCEPT;
            else
                return FILTER_SKIP;
        }
    }

}  // end of class XPathASTTestlet

