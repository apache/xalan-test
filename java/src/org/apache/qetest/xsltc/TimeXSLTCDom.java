/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.apache.qetest.xsltc;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.compiler.Constants;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.dom.Axis;
import org.apache.xalan.xsltc.dom.DOMImpl;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.serializer.ToTextStream;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


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
    SerializationHandler textoutput;
    
    textoutput = new ToTextStream();
    textoutput.setOutputStream(System.out);
    textoutput.setEncoding("utf-8");
  
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
