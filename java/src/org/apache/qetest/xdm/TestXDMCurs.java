/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights 
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
 *
 * TestXDMCurs.java
 *
 */
package org.apache.qetest.xdm;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// java classes
import java.io.File;
import java.io.StringReader;
import java.io.FileOutputStream;
import java.util.Properties;

// Needed SAX, DOM, JAXP, Xalan classes
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.xml.xdm.*;
import org.apache.xml.xdm.ref.*;
import org.apache.xpath.objects.XMLStringFactoryImpl;

//-------------------------------------------------------------------------

/**
* This test creates an XDM and then walks it with axisIterators 
* for each axis within XPATH
* - execute 'build package.trax', 'traxapitest TestXDMCurs.java'
* - a bunch of convenience variables/initializers are included, 
*   use or delete as is useful
* @author Paul Dick
* @author Joe Kesselman (adapted from DTM to XDM)
* @version $Id$
*/
public class TestXDMCurs extends FileBasedTest
{
    /**
     * Provides nextName(), currentName() functionality for tests 
     * that may produce any number of output files.
     */
    protected OutputNameManager outNames;

    /** 
     * Information about an xsl/xml file pair for transforming.  
     * Public members include inputName (for xsl); xmlName; goldName; etc.
     * If you don't use an .xml file on disk, you don't actually need this.
     */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();

    /** Subdirectory under test\tests\api for our xsl/xml files.  */
    public static final String XDM_SUBDIR = "xdm";
	public static final String CURS_Prefix = "Curs_";

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
 
	static final String[] TYPENAME=
 	{ 	"NULL",
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

	private XDMCursor lastNode;	// Set by first axis,  used by subsequent axis.
	private String lastName;

	private XDMCursor lastNode2;	// Set by DESCENDANTORSELF, used in 11 & 12 
	private String lastName2;

	private XDMCursor ANode;		// Used in testcase 7 - 10
	private String ANodeName;

	private static xdmWSStripper stripper = new xdmWSStripper();

    /** Just initialize test name, comment, numTestCases. */
    public TestXDMCurs()
    {
        numTestCases = 12;
        testName = "TestXDMCurs";
        testComment = "Function test of XDMCursors";
    }

    /**
     * Initialize this test - Set names of xml/xsl test files,
     * REPLACE_other_test_file_init.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        // Used for all tests; just dump files in xdm subdir
        File outSubDir = new File(outputDir + File.separator + XDM_SUBDIR);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);

        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + XDM_SUBDIR
                                         + File.separator + testName, ".out");

        String testBasePath = inputDir 
                              + File.separator 
                              + XDM_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator 
                              + XDM_SUBDIR
                              + File.separator
                              + CURS_Prefix;

        //testFileInfo.inputName = testBasePath + "REPLACE_xslxml_filename.xsl";
        //testFileInfo.xmlName = testBasePath + "REPLACE_xslxml_filename.xml";
        testFileInfo.goldName = goldBasePath;

        return true;
    }

    /**
     * Cleanup this test - REPLACE_other_test_file_cleanup.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileClose(Properties p)
    {
        // Often will be a no-op
        return true;
    }

   /**
    * Create AxisCursor and walk CHILD axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase1()
    {
		reporter.testCaseInit("Walk CHILD AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase1.out";

		// Create xdm and generate initial context
		XDMCursor doc = generateXDM();

	  	// Get various nodes to use as context nodes.
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
	  ANode = cNodeSibs;			// <A>
	  String ANodeName = ANode.getNodeName();


		// Get a Cursor for CHILD:: axis and query it's direction.
      	XDMCursor iter = DNode.getAxisCursor(Axis.CHILD);
      	lastNode=DNode.singleNode();

	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ 
			buf.append(getNodeInfo(iter, " "));
			lastNode.setIterationRoot(iter,null);			// Setting this GLOBAL IS BAD, but easy. Investigate!!
		}
		lastName = lastNode.getNodeName();

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
		LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
        myfilechecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase1"); 
        reporter.testCaseClose();
        return true;
    }

    /**
     * Create AxisCursor and walk PARENT axis.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
		reporter.testCaseInit("Walk PARENT AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase2.out";

		// Get a Cursor for PARENT:: axis.
      	XDMCursor iter = lastNode.getAxisCursor(Axis.PARENT);

	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
			 buf.append(getNodeInfo(iter, " "));
		 		
		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
		LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
        myfilechecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase2"); 

        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisCursor and walk SELF axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase3()
    {
		reporter.testCaseInit("Walk SELF AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase3.out";

		// Get a Cursor for CHILD:: axis.
      	XDMCursor iter = lastNode.getAxisCursor(Axis.SELF);

	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		  buf.append(getNodeInfo(iter, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
		LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
        myfilechecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase3"); 

        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisCursor and walk NAMESPACE axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase4()
    {
		reporter.testCaseInit("Walk NAMESPACE AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase4.out";

		// Get a Cursor for NAMESPACE:: axis.
      	XDMCursor iter = lastNode.getAxisCursor(Axis.NAMESPACE);

	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		     buf.append(getNodeInfo(iter, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
		LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
        myfilechecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase4"); 

        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisCursor and walk PRECEDING axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase5()
    {
		reporter.testCaseInit("Walk PRECEDING AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase5.out";

		// Get a Cursor for PRECEDING:: axis.
      	XDMCursor iter = lastNode.getAxisCursor(Axis.PRECEDING);


	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		     buf.append(getNodeInfo(iter, " "));

		// Write results and close output file.
		writeClose(fos, buf);


	    // Verify results		
	    LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
	    myfilechecker.check(reporter, new File(outNames.currentName()),
	        						  new File(gold),
	        						  "Testcase5");

        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisCursor and walk PRECEDINGSIBLING axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase6()
    {
		reporter.testCaseInit("Walk PRECEDINGSIBLING AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase6.out";

		// Get a Cursor for PRECEDINGSIBLING:: axis.
      	XDMCursor iter = lastNode.getAxisCursor(Axis.PRECEDINGSIBLING);

	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		     buf.append(getNodeInfo(iter, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
		LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
        myfilechecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase6"); 
        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisCursor and walk FOLLOWING axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase7()
    {
		reporter.testCaseInit("Walk FOLLOWING AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase7.out";

		// Get a Cursor for FOLLOWING:: axis.
      	XDMCursor iter = ANode.getAxisCursor(Axis.FOLLOWING);

	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		     buf.append(getNodeInfo(iter, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
		LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
        myfilechecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase7"); 
        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisCursor and walk FOLLOWINGSIBLING axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase8()
    {
		reporter.testCaseInit("Walk FOLLOWINGSIBLING AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase8.out";

		// Get a Cursor for FOLLOWINGSIBLING:: axis.
      	XDMCursor iter = ANode.getAxisCursor(Axis.FOLLOWINGSIBLING);

	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		{ buf.append(getNodeInfo(iter, " "));
		  //lastNode = itNode;
		}

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
		LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
        myfilechecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase8"); 
        reporter.testCaseClose();
        return true;
    }


   /**
    * Create AxisCursor and walk DESCENDANT axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase9()
    {
		reporter.testCaseInit("Walk DESCENDANT AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase9.out";

		// Get a Cursor for DESCENDANT:: axis.
      	XDMCursor iter = ANode.getAxisCursor(Axis.DESCENDANT);

	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
		     buf.append(getNodeInfo(iter, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
		LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
        myfilechecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase9"); 
        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisCursor and walk DESCENDANTORSELF axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase10()
    {
		reporter.testCaseInit("Walk DESCENDANTORSELF AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase10.out";

		// Get a Cursor for DESCENDANTORSELF:: axis.
      	XDMCursor iter = ANode.getAxisCursor(Axis.DESCENDANTORSELF);
		lastNode2=iter.singleNode();      	


	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
	   	{
	   		buf.append(getNodeInfo(iter, " "));
		  	lastNode2.setIterationRoot(iter,null);
		}

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
		LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
        myfilechecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase10"); 
        reporter.testCaseClose();
        return true;
    }


   /**
    * Create AxisCursor and walk ANCESTOR axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase11()
    {
		reporter.testCaseInit("Walk ANCESTOR AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase11.out";

		// Get a Cursor for ANCESTOR:: axis.
		lastName2 = lastNode2.getNodeName();
      	XDMCursor iter = lastNode2.getAxisCursor(Axis.ANCESTOR);

	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
			 buf.append(getNodeInfo(iter, " "));

		// Write results and close output file.
		writeClose(fos, buf);

	    // Verify results		
	    LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
	    myfilechecker.check(reporter, new File(outNames.currentName()),
	        						  new File(gold),
	        						  "Testcase11");        							
        							 
        reporter.testCaseClose();
        return true;
    }


   /**
    * Create AxisCursor and walk ANCESTORORSELF axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase12()
    {
		reporter.testCaseInit("Walk ANCESTORORSELF AxisCursor");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase12.out";

		// Get a Cursor for ANCESTORORSELF:: axis.
      	XDMCursor iter = lastNode2.getAxisCursor(Axis.ANCESTORORSELF);

	  	// scan the axis and write node info to output file
      for (boolean more=!iter.isEmpty();
      		more;
      		more=iter.nextNode())
			 buf.append(getNodeInfo(iter, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
		LinebyLineCheckService myfilechecker = new LinebyLineCheckService();
        myfilechecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase12"); 
        reporter.testCaseClose();
        return true;
    }

public String usage()
{
	return ("Common [optional] options supported by TestXDMCurs:\n"
             + "(Note: assumes inputDir=.\\tests\\api)\n");
}

FileOutputStream openFileStream(String name)
{
	FileOutputStream fos = null;

	try
	{  fos = new FileOutputStream(name); }

	catch (Exception e)
	{  reporter.checkFail("Failure opening output file."); }

	return fos;
}

// This routine generates a new root XDMCursor for each testcase
XDMCursor generateXDM()
{
	// Create XDM and generate initial context
	// %REVIEW% We'll need to generalize this.
	Source source = new StreamSource(new StringReader(defaultSource));
	XDMManager manager= new XDMManagerDTM().newInstance(new XMLStringFactoryImpl());
	XDMCursor xdm=manager.getXDM(source, true, stripper, false, true);
   
	return xdm;
}

void writeClose(FileOutputStream fos, StringBuffer buf)
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
    
String getNodeInfo(XDMCursor node, String indent)
{
    // Formatting hack -- suppress quotes when value is null, to distinguish
    // it from "null".
	String buf = new String("null");
    String value=node.getNodeValue();
    String vq = (value==null) ? "" : "\"";

    // Skip outputing of text nodes. In most cases they clutter the output, 
	// besides I'm only interested in the elemental structure of the xdm. 
    if( node.getNodeType() != node.TEXT_NODE )
	{
		// node.toString() does most of this better...
    	buf = new String(indent+
		       //nodeHandle+": "+
		       TYPENAME[node.getNodeType()]+" "+
			   node.getNodeName()+" "+
			   //" Level=" + xdm.getLevel(nodeHandle)+" "+
		       "\tValue=" + vq + value + vq	+ "\n"
		       ); 
	}
	return buf;
}

    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        TestXDMCurs app = new TestXDMCurs();
        app.doMain(args);
    }
}
