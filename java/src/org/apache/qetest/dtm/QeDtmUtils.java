/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
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


package org.apache.qetest.dtm;

import java.io.FileOutputStream;
import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.qetest.Reporter;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xpath.objects.XMLStringFactoryImpl;

/**
 * Static utility class for both general-purpose testing methods 
 * and a few XML-specific methods.  
 * Also provides a simplistic Test/Testlet launching helper 
 * functionality.  Simply execute this class from the command 
 * line with a full or partial classname (in the org.apache.qetest 
 * area, obviously) and we'll load and execute that class instead.
 * @author paul_dick@us.ibm.com
 * @version $Id$
 */
public abstract class QeDtmUtils
{
    // abstract class cannot be instantiated


/** Subdirectory under test\tests\api for our xsl/xml files.  */
public static final String DTM_SUBDIR = "dtm";
public static final String DTM_Prefix = "DTM_";
public static final String deepFile = "/tests/perf/xtestdata/elem10kdeep.xml";
public static final String flatFile = "/tests/perf/xtestdata/words-repeat.xml";


public static final String defaultSource=
 	"<?xml version=\"1.0\"?>\n"+
 	"<Document xmlns:d=\"www.d.com\" a1=\"hello\" a2=\"goodbye\">"+
 	"<!-- Default test document -->"+
 	"<?api a1=\"yes\" a2=\"no\"?>"+
 	"<A><!-- A Subtree --><B><C><D><E><F xmlns:f=\"www.f.com\" a1=\"down\" a2=\"up\"/></E></D></C></B></A>"+
 	"<Aa/><Ab/><Ac><Ac1/></Ac>"+
 	"<Ad xmlns:Ad=\"www.Ad.com\" xmlns:y=\"www.y.com\" xmlns:z=\"www.z.com\">"+
 	"<Ad1/></Ad>"+
 	"</Document>";
 
public static final String defaultSource2=
	"<?xml version=\"1.0\"?>\n"+
	"  <bdd:dummyDocument xmlns:bdd=\"www.bdd.org\" version=\"99\">\n"+
	"  <!-- Default test document -->&#09;&amp;"+
	"  <?api attrib1=\"yes\" attrib2=\"no\"?>"+
	"   <A>\n"+
	"    <B hat=\"new\" car=\"Honda\" dog=\"Boxer\">Life is good</B>\n"+
	"   </A>\n"+
	"   <C>My Anaconda<xyz:D xmlns:xyz=\"www.xyz.org\"/>Words</C>\n"+
	"  	   Want a more interesting docuent, provide the URI on the command line!\n"+
 	"   <Sub-Doc xmlns:d=\"www.d.com\" a1=\"hello\" a2=\"goodbye\">"+
 	"   <!-- Default test Subdocument -->"+
 	"   <?api a1=\"yes\" a2=\"no\"?>"+
 	"   <A><!-- A Subtree --><B><C><D><E><f:F xmlns:f=\"www.f.com\" a1=\"down\" a2=\"up\"/></E></D></C></B></A>"+
 	"   <Aa/><Ab/><Ac><Ac1/></Ac>"+
 	"   <Ad:Ad xmlns:Ad=\"www.Ad.com\" xmlns:y=\"www.y.com\" xmlns:z=\"www.z.com\">"+
 	"   <Ad1/></Ad:Ad>"+
 	"   </Sub-Doc>"+
	"  </bdd:dummyDocument>\n";

public static final String simpleFlatFile=
 	"<?xml version=\"1.0\"?>\n"+
 	"<Doc>\n"+
	"<item>XSLT</item>\n"+
	"<item>processors</item>\n"+
	"<item>must</item>\n"+
	"<item>use</item>\n"+
	"<item>XML</item>\n"+
	"<item>Namespaces</item>\n"+
	"<item>mechanism</item>\n"+
 	"</Doc>";


public static final String[] TYPENAME=
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

// This routine generates a new DTM for each testcase
      // Get a DTM manager, and ask it to load the DTM "uniquely",
      // with no whitespace filtering, nonincremental, but _with_
      // indexing (a fairly common case, and avoids the special
      // mode used for RTF DTMs).
public static DTM createDTM(int method, String theSource, StringBuffer buf)
{
	dtmWSStripper stripper = new dtmWSStripper();
	long dtmStart = 0;
	long dtmStop = 0;
	Source source;
	long startMem, startTotMem, stopMem, stopTotMem, postGCfree, postGCtotal;


	// Create DTM and generate initial context
	if (method == 1)
	{
		source = new StreamSource(new StringReader(theSource));
	}
	else 
	{
		source=new StreamSource(theSource);
	}

	startMem = Runtime.getRuntime().freeMemory();
	startTotMem = Runtime.getRuntime().totalMemory();

	dtmStart = System.currentTimeMillis();
	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
	DTM dtm=manager.getDTM(source, true, stripper, false, true);
   	dtmStop = System.currentTimeMillis();

	stopMem = Runtime.getRuntime().freeMemory();
	stopTotMem = Runtime.getRuntime().totalMemory();

	Runtime.getRuntime().gc();

	postGCfree = Runtime.getRuntime().freeMemory();
	postGCtotal = Runtime.getRuntime().totalMemory();

	buf.append("\nPre-DTM free memory:\t" + startMem + "/" + startTotMem); 
	buf.append("\nPost-DTM free memory:\t" + stopMem + "/" + stopTotMem);
	buf.append("\nPost-GC free memory:\t" + postGCfree + "/" + postGCtotal);


	buf.append("\nInit of DTM took: \t"+ (dtmStop-dtmStart) + "\n");
	System.out.println(buf);
	return dtm;
}

 public static void timeAxisIterator(DTM dtm, int axis, int context, int[] rtdata)
  {	
    long startTime = 0;
    long iterTime = 0;
	int atNode = 0;
	int lastNode = 0;							
	int numOfNodes =0;

	// Time creation and iteration.
	startTime = System.currentTimeMillis();

    DTMAxisIterator iter = dtm.getAxisIterator(axis);
    iter.setStartNode(context);

    for (atNode = iter.next(); DTM.NULL != atNode; atNode = iter.next())
		{ 
          lastNode = atNode;
		  numOfNodes = numOfNodes + 1;	// Need to know that we Iterated the whole tree
    	}

    iterTime = System.currentTimeMillis() - startTime;

	if (lastNode != 0)
		getNodeInfo(dtm, lastNode, " ");

	rtdata[0] = (int)iterTime;
	rtdata[1] = lastNode;
	rtdata[2] = numOfNodes;
  }

  
static void timeAxisTraverser(DTM dtm, int axis, int context, int[] rtdata)
  {	
    long startTime = 0;
    long travTime = 0;
	int atNode = 0;
	int lastNode = 0;
	int numOfNodes =0;

	// Time the creation and traversal.
	startTime = System.currentTimeMillis();

  	DTMAxisTraverser at = dtm.getAxisTraverser(axis);

    for (atNode = at.first(context); DTM.NULL != atNode; atNode = at.next(context, atNode))
		{ 
          lastNode = atNode;
		  numOfNodes = numOfNodes + 1;
    	}

    travTime = System.currentTimeMillis() - startTime;

	if (lastNode != 0)
		getNodeInfo(dtm, lastNode, " ");

	rtdata[0] = (int)travTime;
	rtdata[1] = lastNode;
	rtdata[2] = numOfNodes;

 }

// This routine gathers up all the important info about a node, concatenates
// in all together into a single string and returns it. 
public static String getNodeInfo(DTM dtm, int nodeHandle, String indent)
{
    // Formatting hack -- suppress quotes when value is null, to distinguish
    // it from "null" (JK).
	String buf = new String("null");
    String value=dtm.getNodeValue(nodeHandle);
    String vq = (value==null) ? "" : "\"";

    // Skip outputing of text nodes. In most cases they clutter the output, 
	// besides I'm only interested in the elemental structure of the dtm. 
    if( TYPENAME[dtm.getNodeType(nodeHandle)] != "TEXT" )
	{ 
    	buf = new String(indent+
		       nodeHandle+": "+
		       TYPENAME[dtm.getNodeType(nodeHandle)]+" "+
			   dtm.getNodeName(nodeHandle)+" "+
			   " Level=" + dtm.getLevel(nodeHandle)+" "+
		       "\tValue=" + vq + value + vq	+ "\n"
		       ); 
	}
	
	return buf;
}

// This routine generates the output file stream.
public static FileOutputStream openFileStream(String name, Reporter reporter)
{
	FileOutputStream fos = null;

	try
	{  fos = new FileOutputStream(name); }

	catch (Exception e)
	{  reporter.checkFail("Failure opening output file."); }

	return fos;
}

// This routine writes the results to the output file.
public static void writeClose(FileOutputStream fos, StringBuffer buf, Reporter reporter)
{
	// Write results and close output file.
	try
	{
		fos.write(buf.toString().getBytes());
		fos.close();
	}

	catch (Exception e)
	{  reporter.checkFail("Failure writing output."); 	}
 }
    


}
