/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
package org.apache.qetest.dtm;

import java.io.StringReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.xpath.objects.XMLStringFactoryImpl;

import org.apache.xml.dtm.*;
import org.apache.xml.dtm.ref.*;

import org.apache.qetest.dtm.dtmWSStripper;


/**
 * Unit test for DTMManager/DTM
 *
 * Loads an XML document from a file (or, if no filename is supplied,
 * an internal string), then dumps its contents. Replaces the old
 * version, which was specific to the ultra-compressed implementation.
 * (Which, by the way, we probably ought to revisit as part of our ongoing
 * speed/size performance evaluation.)
 *
 * %REVIEW% Extend to test DOM2DTM, incremental, DOM view of the DTM, 
 * whitespace-filtered, indexed/nonindexed, ...
 * */
public class TimeDTMTraverserFlat {

static final String[] TYPENAME=
  { "NULL",
    "ELEMENT",
    "ATTRIBUTE",
    "TEXT",
    "CDATA_SECTION",
    "ENTITY_REFERENCE",
    "ENTITY",
    "PROCESSING_INSTRUCTION",
    "COMMENT",
    "DOCUMENT",
    "DOCUMENT_TYPE",
    "DOCUMENT_FRAGMENT",
    "NOTATION",
    "NAMESPACE"
  };

  public static void main(String argv[])
  {
  	System.out.println("\nTesting Traversal of FLAT documents.");
    try
    {
		// Pick our input source
		Source source=null;
		if(argv.length<1)
		{
			String defaultSource=
				"<?xml version=\"1.0\"?>\n"+
				"<Doc>"+
				"<Aa/><Ab/><Ac/><Ad/>"+
				"<Ae/><Af/><Ag/><Ah/><Ai/><Aj/><Ak/><Al/><Am/><An/><Ao/>"+
				"<Ap/><Aq/><Ar/><As/><At/><Au/><Av/><Aw/><Ax/><Ay/><Az/>"+
				"</Doc>";

			source=new StreamSource(new StringReader(defaultSource));
		}
		else if (argv.length>1 &&  "X".equalsIgnoreCase(argv[1]))
		{
			// XNI stream startup goes here
			// Remember to perform Schema validation, to obtain PSVI annotations
		}
		else
		{
			// Read from a URI via whatever mechanism the DTMManager prefers
			source=new StreamSource(argv[0]);
		}
	
      // Get a DTM manager, and ask it to load the DTM "uniquely",
      // with no whitespace filtering, nonincremental, but _with_
      // indexing (a fairly common case, and avoids the special
      // mode used for RTF DTMs).

	  // For testing with some of David Marston's files I do want to strip whitespace.
	  dtmWSStripper stripper = new dtmWSStripper();

      DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      DTM dtm=manager.getDTM(source, true, stripper, false, true);

	  // Get various nodes to use as context nodes.
	  int dtmRoot = dtm.getDocument();					// #document
	  String dtmRootName = dtm.getNodeName(dtmRoot);	// Used for output

	  int DNode = dtm.getFirstChild(dtmRoot);			// <Doc>
	  String DNodeName = dtm.getNodeName(DNode);

	  int ANode = dtm.getFirstChild(DNode);				// <Aa>
	  String ANodeName = dtm.getNodeName(ANode);

	  int lastNode = 0;						// holds last node from traversal
	  int[] rtData = {0,0,0};				// holds return data from timing routine

	  // Get a traverser for Child:: axis.
	  System.out.println("\n#### CHILD from "+"<"+DNodeName+">");
	  timeAxis(dtm, Axis.CHILD, DNode, rtData);
	  System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	  // Get a traverser for Following:: axis.
	  System.out.println("\n#### FOLLOWING from "+"<"+ANodeName+">");
	  timeAxis(dtm, Axis.FOLLOWING,ANode, rtData);
	  System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	  // Get a traverser for Following-sibling:: axis.
	  System.out.println("\n#### FOLLOWINGSIBLING from "+"<"+ANodeName+">");
	  timeAxis(dtm, Axis.FOLLOWING,ANode, rtData);
	  System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	  // Get a traverser for Descendant:: axis.
	  System.out.println("\n#### DESCENDANT from "+"<"+DNodeName+">");
	  timeAxis(dtm, Axis.DESCENDANT, DNode, rtData);
	  System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	  // Use last node from Descendant traverse as Context node for subsequent traversals
	  lastNode = rtData[1];
	  String lastNodeName = dtm.getNodeName(lastNode);

	  // Get a traverser for Ancestor:: axis.
	  System.out.println("\n#### ANCESTOR from "+"<"+lastNodeName+">");	
	  timeAxis(dtm, Axis.ANCESTOR, lastNode, rtData);
	  System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	  // Get a traverser for Preceding:: axis.
	  System.out.println("\n#### PRECEDING from "+"<"+lastNodeName+">");			   
	  timeAxis(dtm, Axis.PRECEDING, lastNode, rtData);
	  System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	  // Get a traverser for Preceding:: axis.
	  System.out.println("\n#### PRECEDING-SIBLING from "+"<"+lastNodeName+">");			   
	  timeAxis(dtm, Axis.PRECEDINGSIBLING, lastNode, rtData);
	  System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	}
    catch(Exception e)
      {
        e.printStackTrace();
      }
  }
  

 static void timeAxis(DTM dtm, int axis, int context, int[] rtdata)
  {	
	// Define some local variables
    long startTime = 0;
    long travTime = 0;
	int atNode = 0;
	int lastNode = 0;
	int x =0;

	// Get the traverser for the given axis
  	DTMAxisTraverser at = dtm.getAxisTraverser(axis);

	// Time the traversal.
	startTime = System.currentTimeMillis();

    for (atNode = at.first(context); DTM.NULL != atNode;
                  atNode = at.next(context, atNode))
		{ //printNode(dtm, atNode, " ");
          lastNode = atNode;
		  x = x + 1;
    	}

    travTime = System.currentTimeMillis() - startTime;

	// Print the last node so we can manually check that the traverse was 
	// correct.
	printNode(dtm, lastNode, " ");
	rtdata[0] = (int)travTime;
	rtdata[1] = lastNode;
	rtdata[2] = x;


  }

  static void printNode(DTM dtm,int nodeHandle,String indent)
  {
    // Display basic node info.
    // Formatting hack -- suppress quotes when value is null, to distinguish
    // it from "null".
    String value=dtm.getNodeValue(nodeHandle);
    String vq=(value==null) ? "" : "\"";

    // Skip outputing of text nodes. In most cases they clutter the output, 
	// besides I'm only interested in the elemental structure of the dtm. 
    if( TYPENAME[dtm.getNodeType(nodeHandle)] != "TEXT" )
	{
    	System.out.println(indent+
		       +nodeHandle+": "+
		       TYPENAME[dtm.getNodeType(nodeHandle)]+" "+
			   dtm.getNodeName(nodeHandle)+" "+
			   " Level=" + dtm.getLevel(nodeHandle)
		       ); 
	}
  }
  
}