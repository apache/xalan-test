/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.qetest.xsltc;

import java.io.StringReader;
import java.io.IOException;
import java.io.File;

import java.net.MalformedURLException; 

import org.apache.xalan.xsltc.runtime.DefaultSAXOutputHandler;
import org.apache.xalan.xsltc.runtime.TextOutput;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.DOM;


import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import org.apache.xalan.xsltc.compiler.Constants;
import org.apache.xalan.xsltc.dom.DTDMonitor;


import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.dom.DOMImpl;
import org.apache.xalan.xsltc.dom.Axis;
import org.apache.xalan.xsltc.NodeIterator;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;


public class TimeXSLTCDom 
{

private DTDMonitor _dtdMonitor = null;

public static void main(String[] args) 
{
	TimeXSLTCDom proc = new TimeXSLTCDom();
    proc.run(args);
}

public void run(String[] args) 
{
	Source source=null;
    if (args.length != 1) 
    {
	  String defaultSource=
		"<?xml version=\"1.0\"?>\n"+
		"<Doc>"+
		"<Aa/><Ab/><Ac/><Ad/>"+
		"<Ae/><Af/><Ag/><Ah/><Ai/><Aj/><Ak/><Al/><Am/><An/><Ao/>"+
		"<Ap/><Aq/><Ar/><As/><At/><Au/><Av/><Aw/><Ax/><Ay/><Az/>"+
		"</Doc>";

	  source=new StreamSource(new StringReader(defaultSource));
/*      
      System.err.println("Usage:\n\tprocessor <xmlfile> <xslfile>" +
            "\n\n\twhere <xmlfile> = xml input filename, and" +
            "\n\t      <xslfile> = stylesheet filename.");
      System.exit(1);
*/
    }
    String xmldocFilename     = args[0];
    //String stylesheetFilename = args[1];

    // Compile the stylesheet to a translet
    //Translet translet = compileStylesheet(stylesheetFilename);

    // Parse the input XML document
    DOMImpl dom = getDOM(xmldocFilename);
	//NodeIterator axis = dom.getAxisIterator(Axis.CHILD);

    // Transform the XML document against the translet
    //doTransform(translet, dom);
    System.exit(0);
}

private Translet compileStylesheet(String stylesheetName) 
{
    Translet retval = null;
    String transletName = stylesheetName.substring(0,
                              stylesheetName.indexOf('.'));
    try 
    {
      XSLTC xsltc = new XSLTC();
      xsltc.init();
      File stylesheet = new File(stylesheetName);
      xsltc.compile(stylesheet.toURL());
      Class clazz = Class.forName(transletName);
      retval = (Translet)clazz.newInstance();
    } 
    
    catch (MalformedURLException e) 
    {
      System.err.println("Could not create URL to stylesheet file: " +
                          stylesheetName + ".\n" + e.getMessage());
      System.exit(1);
    } 
    
    catch (ClassNotFoundException e) 
    {
      System.err.println("Could not find class file: " +
                          transletName + ".class .\n" + e.getMessage());
      System.exit(1);
    } 
    
    catch (InstantiationException e) 
    {
      System.err.println("Could not instantiate class file: " +
                          transletName + ".class .\n" + e.getMessage());
      System.exit(1);
    } 
    
    catch (IllegalAccessException e) 
    {
      System.err.println("Could not instantiate class file: " +
            transletName + ".class, illegal access.\n" + e.getMessage());
      System.exit(1);
    }
    
    return retval;
}



private DOMImpl getDOM(String xmldocname) 
{
	// Define some local variables
    final SAXParserFactory factory = SAXParserFactory.newInstance();
    long startTime = 0;
    long domCreation =0;	
	long travDom =0;
    
    try 
    {
      factory.setFeature(Constants.NAMESPACE_FEATURE, true);
    } 
    
    catch (Exception e) 
    {
      factory.setNamespaceAware(true);
    }
    
	// Time the Dom creation.
	startTime = System.currentTimeMillis();

    final DOMImpl dom = new DOMImpl();
    dom.setDocumentURI(xmldocname);
    XMLReader reader = null;
    SAXParser parser = null;
    _dtdMonitor = new DTDMonitor();
    
    try 
    {
      parser = factory.newSAXParser();
      reader = parser.getXMLReader();
      reader.setContentHandler(dom.getBuilder());
      _dtdMonitor.handleDTD(reader);
      reader.parse(xmldocname);
    } 
            
    catch (ParserConfigurationException e) 
    {
      System.err.println("SAX Parser is not configured properly.\n"+
                          e.getMessage());
      System.exit(1);
    } 
    
    catch (SAXException e ) 
    {
      System.err.println("SAX Parser could not be created.\n"+
                          e.getMessage());
      System.exit(1);
    } 
    
    catch (IOException e ) 
    {
      System.err.println("XML Reader could not read xml document '"+
                          xmldocname + "'," + e.getMessage());
      System.exit(1);
    }
	
    domCreation = System.currentTimeMillis() - startTime;

	System.out.println("Walking the XSLTC DOM ");
	System.out.println("Creation took: " + domCreation);
 	System.out.println("Size of DOM: " + dom.getSize());
	System.out.println("DocumentURI: " + dom.getDocumentURI());
	
	// Get a Axis Iterator to start with, based on ROOTNODE.
	NodeIterator axis = dom.getAxisIterator(Axis.CHILD);
	System.out.println("Reverse axis? " + axis.isReverse());
	axis.setStartNode(DOM.ROOTNODE);

	// Get Document Element and iterate from there.
	int doc = axis.next();

	// Get new AxisIterator and Time the traversal. 
	startTime = System.currentTimeMillis();
	axis = dom.getAxisIterator(Axis.CHILD);
	axis.setStartNode(doc);
	
	for (int itNode = axis.next(); DOM.NULL != itNode;
              itNode = axis.next())
	{ 
		System.out.print("\n Name: " + dom.getNodeName(itNode)+" "+
						 " Type:  \""+dom.getType(itNode)+"\" "+
						 " Position: "+axis.getPosition());
		//if (dom.getType(itNode) != 1)
		//System.out.print(" Value: \""+dom.getElementValue(itNode)+"\""); 
	}
    
    travDom = System.currentTimeMillis() - startTime;
	System.out.println("\nChild Axis Traversal took: " + travDom);
    return dom;
}



private void doTransform(Translet translet, DOMImpl dom) 
{
    DefaultSAXOutputHandler outputhandlr = null;
    TextOutput textoutput = null;
    try 
    {
      outputhandlr= new DefaultSAXOutputHandler(System.out, "utf-8");
      textoutput = new TextOutput(outputhandlr, "utf-8");
    } 
    
    catch (IOException e) 
    {
      System.err.println("Could not create SAX Output Handler."+
                          e.getMessage());
      System.exit(1);
    }
  
    // for XSL keys
    AbstractTranslet absTranslet = (AbstractTranslet)translet;
    absTranslet.setIndexSize(dom.getSize());
    _dtdMonitor.buildIdIndex(dom, 0, absTranslet);

    try 
    {
      absTranslet.transform(dom, textoutput);
    } 
    
    catch (TransletException e) 
    {
      System.err.println("Could not transform XML document."+
                          e.getMessage());
      System.exit(1);
    }
}

}