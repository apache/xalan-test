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
package org.apache.qetest.dtm;

import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTM;


public class TimeDTMIterator
{

public static void main(String argv[])
{

  	System.out.println("\n#### Timing Iterations of DEEP documents. ####");

	StringBuffer buf = new StringBuffer();
	int lastNode = 0;
	String lastNodeName;
	int[] rtData = {0,0,0};		// returns Traversal time, last node, number of nodes traversed 


	// Preload once to prime the JVM.
	DTM dtm = QeDtmUtils.createDTM(0, QeDtmUtils.deepFile, buf);

	// Time the creation of the dtm	

	buf.setLength(0);
	dtm = QeDtmUtils.createDTM(0, QeDtmUtils.deepFile, buf);
	buf.setLength(0);
	dtm = QeDtmUtils.createDTM(0, QeDtmUtils.deepFile, buf);
	buf.setLength(0);
	dtm = QeDtmUtils.createDTM(0, QeDtmUtils.deepFile, buf);

	// Get various nodes to use as context nodes.
	int dtmRoot = dtm.getDocument();				// #document
	String dtmRootName = dtm.getNodeName(dtmRoot);	// Used for output
	int DNode = dtm.getFirstChild(dtmRoot);			// <Doc>
	String DNodeName = dtm.getNodeName(DNode);
	int ANode = dtm.getFirstChild(DNode);			// <A>
	String ANodeName = dtm.getNodeName(ANode);

	// Get a traverser for Descendant:: axis.
	System.out.println("\n* DESCENDANT from "+"<"+DNodeName+">");
	QeDtmUtils.timeAxisIterator(dtm, Axis.DESCENDANT, DNode, rtData);
	System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	// Get a traverser for Descendant:: axis.
	System.out.println("\n* DESCENDANT-OR-SELF from "+"<"+DNodeName+">");
	QeDtmUtils.timeAxisIterator(dtm, Axis.DESCENDANTORSELF, DNode, rtData);
	System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	// Use last node from Child traverse as Context node for subsequent traversals
	lastNode = rtData[1];
	lastNodeName = dtm.getNodeName(lastNode);

	// Get a traverser for Ancestor:: axis.
	System.out.println("\n* ANCESTOR from "+"<"+lastNodeName+">");	
	QeDtmUtils.timeAxisIterator(dtm, Axis.ANCESTOR, lastNode, rtData);
	System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	// Get a traverser for Ancestor:: axis.
	System.out.println("\n* ANCESTOR-OR-SELF from "+"<"+lastNodeName+">");	
	QeDtmUtils.timeAxisIterator(dtm, Axis.ANCESTORORSELF, lastNode, rtData);
	System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);
	
  	System.out.println("\n#### Testing Iteration of FLAT documents. ####");

	buf.setLength(0);
	DTM dtm2 = QeDtmUtils.createDTM(0, QeDtmUtils.flatFile, buf);
	buf.setLength(0);
	dtm2 = QeDtmUtils.createDTM(0, QeDtmUtils.flatFile, buf);
	buf.setLength(0);
	dtm2 = QeDtmUtils.createDTM(0, QeDtmUtils.flatFile, buf);
	buf.setLength(0);
	dtm2 = QeDtmUtils.createDTM(0, QeDtmUtils.flatFile, buf);
	buf.setLength(0);
	dtm2 = QeDtmUtils.createDTM(0, QeDtmUtils.flatFile, buf);
																
	// Get various nodes to use as context nodes.
	dtmRoot = dtm2.getDocument();				// #document
	dtmRootName = dtm2.getNodeName(dtmRoot);	// Used for output
	DNode = dtm2.getFirstChild(dtmRoot);		// <Doc>
	DNodeName = dtm2.getNodeName(DNode);
	int fiNode = dtm2.getFirstChild(DNode);			// first <item>
	String fiNodeName = dtm2.getNodeName(fiNode);

	// Get a traverser for Child:: axis.
	System.out.println("\n* CHILD from "+"<"+DNodeName+"> " + DNode);
	QeDtmUtils.timeAxisIterator(dtm2, Axis.CHILD, DNode, rtData);
	System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	// Get a traverser for Following:: axis.
	System.out.println("\n* FOLLOWING from "+"<"+fiNodeName+"> " + fiNode);
	QeDtmUtils.timeAxisIterator(dtm2, Axis.FOLLOWING, fiNode, rtData);
	System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	// Get a traverser for Following-sibling:: axis.
	System.out.println("\n* FOLLOWINGSIBLING from "+"<"+fiNodeName+"> " + fiNode);
	QeDtmUtils.timeAxisIterator(dtm2, Axis.FOLLOWINGSIBLING, fiNode, rtData);
	System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	// Get a traverser for Descendant:: axis.
	System.out.println("\n* DESCENDANT from "+"<"+DNodeName+"> " + DNode);
	QeDtmUtils.timeAxisIterator(dtm2, Axis.DESCENDANT, DNode, rtData);
	System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	// Use last node from Descendant traverse as Context node for subsequent traversals
	lastNode = rtData[1];
	lastNodeName = dtm2.getNodeName(lastNode);

	// Get a traverser for Ancestor:: axis.
	System.out.println("\n* ANCESTOR from "+"<"+lastNodeName+"> " + lastNode);	
	QeDtmUtils.timeAxisIterator(dtm2, Axis.ANCESTOR, lastNode, rtData);
	System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	// Get a traverser for Preceding:: axis.
	System.out.println("\n* PRECEDING-SIBLING from "+"<"+lastNodeName+"> " + lastNode);			   
	QeDtmUtils.timeAxisIterator(dtm2, Axis.PRECEDINGSIBLING, lastNode, rtData);
	System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);

	// Get a traverser for Preceding:: axis.
	System.out.println("\n* PRECEDING from "+"<"+lastNodeName+"> " + lastNode);			   
	QeDtmUtils.timeAxisIterator(dtm2, Axis.PRECEDING, lastNode, rtData);
	System.out.println("Time="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);


}



}
