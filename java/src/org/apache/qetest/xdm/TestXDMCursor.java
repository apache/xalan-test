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
package org.apache.qetest.xdm;
import java.io.StringReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.xpath.objects.XMLStringFactoryImpl;

//import org.apache.xml.dtm.*;
//import org.apache.xml.dtm.ref.*;
import org.apache.xml.xdm.*;
import org.apache.xml.xdm.ref.*;


/**
 * Unit test for XDMCursor, currently hardwired to the initial DTM implementation
 *
 * Loads an XML document from a file (or, if no filename is supplied,
 * an internal string), then dumps its contents.
 *
 * %REVIEW% Extend to test other XDMs, incremental, DOM view of the DTM, 
 * whitespace-filtered, indexed/nonindexed, ...
 * */
public class TestXDMCursor {

  public static void main(String argv[])
  {
  	System.out.println("\nTestXDMCursor -- Initial unit test of singletons and axes\n");
    try
    {
	    // Pick our input source
		Source source=null;
		if(argv.length<1)
		{
			String defaultSource=
 		"<?xml version=\"1.0\"?>\n"+
 		"<Document xmlns:d=\"www.d.com\" a1=\"hello\" a2=\"goodbye\">"+
 		"<!-- Default test document -->"+
 		"<?api a1=\"yes\" a2=\"no\"?>"+
 		"<A><!-- A Subtree --><B><C><D><E><F xmlns:f=\"www.f.com\" a1=\"down\" a2=\"up\"/></E></D></C></B></A>"+
 		"<Aa/><Ab/><Ac><Ac1/></Ac>"+
 		"<Ad xmlns:Ad=\"www.Ad.com\" xmlns:y=\"www.y.com\" xmlns:z=\"www.z.com\">"+
 		"<Ad1/></Ad>"+
 		"</Document>";

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
	  xdmWSStripper stripper = new xdmWSStripper();

      XDMManager manager= new XDMManagerDTM().newInstance(new XMLStringFactoryImpl());
      XDMCursor doc=manager.getXDM(source, true, stripper, false, true);

	  // Get various nodes to use as context nodes.
	  //
	  // XDM supports only axis navigation, not tree navigation;
	  // the equivalent of getFirstChild() or getNextSibling() is
	  // to get the children/siblings cursor, which will be
	  // initialized to point to the first instance. Note that
	  // these remain mutable -- if you want to protect them you
	  // need to clone the axis cursor or make a SELF cursor
	  // to that node.
	  XDMCursor xdmRoot = doc;					// #document
	  String xdmRootName = xdmRoot.getNodeName();	// Used for output
	  XDMCursor DNode = xdmRoot.getAxisCursor(Axis.CHILD);	// <Document>
	  String DNodeName = DNode.getNodeName();
	  XDMCursor CNode = DNode.getAxisCursor(Axis.CHILD); // <Comment>

	  // Use the cursor as an iterator, pulling a SELF 
	  // cursor off the PINode to save state.
	  XDMCursor cNodeSibs=CNode.getAxisCursor(Axis.FOLLOWINGSIBLING);
	  XDMCursor PINode = cNodeSibs.singleNode();			// <PI>
	  cNodeSibs.nextNode();
	  XDMCursor ANode = cNodeSibs;			// <A>
	  String ANodeName = ANode.getNodeName();
      

	  // Get a Iterator for CHILD:: axis, starting from DNode
	  XDMCursor iter=DNode.getAxisCursor(Axis.CHILD);
	  // SELF cursor -- mutable, so we can reset it each time.
	  XDMCursor lastNode=iter.singleNode();

	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("#### CHILD from "+"<"+DNodeName+">, Doc-ordered:" + iter.isDocOrdered());			   
	  
	  // Iterate the axis and print out node info.
	  // Note the need to check isEmpty before starting the loop
	  // ... this is slightly ugly, but seems to be unavoidable
	  // in the general case.
	  //
	  // %REVIEW% I hate to say it, but having nextNode return
	  // an object rather than a boolean *may* have been
	  // suboptimal.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
			lastNode.setIterationRoot(iter,null);
		}
	  
	  String lastNodeName = lastNode.getNodeName();

	  // Get iterator for PARENT:: Axis
	  iter = lastNode.getAxisCursor(Axis.PARENT);

	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### PARENT from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   
  
	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
		}

	  // Get iterator for SELF:: Axis
	  iter = lastNode.getAxisCursor(Axis.SELF);
	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### SELF from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   

	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
		}

/**** Not Implemented? 
	  // Get iterator for NAMESPACEDECLS:: Axis
	  iter = doc.getAxisIterator(Axis.NAMESPACEDECLS);
	  iter.setStartNode(lastNode);
	  System.out.println("\n#### NAMESPACEDECLS from "+"<"+lastNodeName+">, Reverse Axis:" + iter.isReverse());	   

	  // Iterate the axis and print out node info.
      for (int itNode = iter.next(); doc.NULL != itNode;
              itNode = iter.next())
		  printNode(xdm, itNode, " ");
****/

	  // Get iterator for NAMESPACE:: Axis
	  iter = lastNode.getAxisCursor(Axis.NAMESPACE);
	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### NAMESPACE from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   

	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
		}

	  // Get iterator for PRECEDING:: Axis
	  iter = lastNode.getAxisCursor(Axis.PRECEDING);
	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### PRECEDING from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   

	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
		}


	  // Get iterator for PRECEDINGSIBLING:: Axis
	  iter = lastNode.getAxisCursor(Axis.PRECEDINGSIBLING);
	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### PRECEDINGSIBLING from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   

	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
		}

	  // Get iterator for ATTRIBUTE:: Axis
	  iter = lastNode.getAxisCursor(Axis.ATTRIBUTE);
	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### ATTRIBUTE from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   

	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
		}


	  // Get iterator for FOLLOWING:: Axis
	  iter = lastNode.getAxisCursor(Axis.FOLLOWING);
	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### FOLLOWING from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   

	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
		}

	  // Get iterator for FOLLOWINGSIBLING:: Axis
	  iter = lastNode.getAxisCursor(Axis.FOLLOWINGSIBLING);
	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### FOLLOWINGSIBLING from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   

	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
		}

	  // Get iterator for DESCENDANT:: Axis
	  iter = lastNode.getAxisCursor(Axis.DESCENDANT);
	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### DESCENDANT from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   

	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
		}

	  // Get iterator for DESCENDANTORSELF:: Axis
	  iter = lastNode.getAxisCursor(Axis.DESCENDANTORSELF);
	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### DESCENDANTORSELF from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   

	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
			lastNode.setIterationRoot(iter,null);
		}

	  // %BUG% In DTM, uncommenting provoked Bugzilla 7885
	  //lastNode = iter.getLast();	 
      lastNodeName = lastNode.getNodeName();
	  
	  // %BUG% The output from Ancestor and Ancestor-or-self is the topic
	  // of Bugzilla 7886
	  

	  // Get iterator for ANCESTOR:: Axis
	  iter = lastNode.getAxisCursor(Axis.ANCESTOR);
	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### ANCESTOR from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   

	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
		}


	  // Get iterator for ANCESTORORSELF:: Axis
	  iter = lastNode.getAxisCursor(Axis.ANCESTORORSELF);
	  // isReverse not currently supported, so I'm changing this message:
	  System.out.println("\n#### ANCESTORORSELF from "+"<"+lastNodeName+">, Doc-ordered:" + iter.isDocOrdered());	   

	  // Iterate the axis and print out node info.
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ printNode(iter, " ");
		}


/**** Absolute axis (ALL, DESCENDANTSFROMROOT, or DESCENDANTSORSELFFROMROOT) not implemented.  
	  // Get iterator for ALL:: Axis
	  // of previous iterator, i.e. lastNode.
	  iter = doc.getAxisIterator(Axis.ALL);
	  iter.setStartNode(lastNode);
	  System.out.println("\n#### ALL from "+"<"+lastNodeName+">, Reverse Axis:" + iter.isReverse());	   

	  // Iterate the axis and print out node info.
      for (int itNode = iter.next(); doc.NULL != itNode;
              itNode = iter.next())
		  printNode(xdm, itNode, " ");
****/
    }
    catch(Exception e)
      {
        e.printStackTrace();
      }
  }
  
static void printNode(XDMCursor node, String indent)
  {
    // Briefly display this node
    // Don't bother displaying namespaces or attrs; we do that at the
    // next level up.


    // Skip outputing of text nodes. In most cases they clutter the output, 
	// besides I'm only interested in the elemental structure of the node. 
    if( node.getNodeType() != XDMCursor.TEXT_NODE )
	{
    	System.out.println(indent+node.toString());
	}
  }

}