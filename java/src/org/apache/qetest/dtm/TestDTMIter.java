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
 * TestDTMIter.java
 *
 */
package org.apache.qetest.dtm;

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

import org.apache.xml.dtm.*;
import org.apache.xml.dtm.ref.*;
import org.apache.xpath.objects.XMLStringFactoryImpl;

//-------------------------------------------------------------------------

/**
* This test creates a DTM and then walks it with axisIterators 
* for each axis within XPATH
* - execute 'build package.trax', 'traxapitest TestDTMIter.java'
* - a bunch of convenience variables/initializers are included, 
*   use or delete as is useful
* @author Paul Dick
* @version $Id$
*/
public class TestDTMIter extends XSLProcessorTestBase
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
    public static final String DTM_SUBDIR = "dtm";

	public static final String defaultSource=
 		"<?xml version=\"1.0\"?>\n"+
 		"<Document xmlns:x=\"www.x.com\" a1=\"hello\" a2=\"goodbye\">"+
 		"<!-- Default test document -->"+
 		"<?api a1=\"yes\" a2=\"no\"?>"+
 		"<A><B><C><D><E><F/></E></D></C></B></A>"+
 		"<Aa/><Ab/><Ac><Ac1/></Ac>"+
 		"<Ad xmlns:xx=\"www.xx.com\" xmlns:y=\"www.y.com\" xmlns:z=\"www.z.com\">"+
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

	private int lastNode = 0;	// Set by first axis,  used by subsequent axis.
	private String lastName;

	private int lastNode2 = 0;	// Set by DESCENDANTORSELF, used in 11 & 12 
	private String lastName2;

	private int ANode = 0;		// Used in testcase 7 - 10
	private String ANodeName;

	private static dtmWSStripper stripper = new dtmWSStripper();

    /** Just initialize test name, comment, numTestCases. */
    public TestDTMIter()
    {
        numTestCases = 12;
        testName = "TestDTMIter";
        testComment = "Function test of DTM iterators";
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
        // Used for all tests; just dump files in dtm subdir
        File outSubDir = new File(outputDir + File.separator + DTM_SUBDIR);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);

        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + DTM_SUBDIR
                                         + File.separator + testName, ".out");

        String testBasePath = inputDir 
                              + File.separator 
                              + DTM_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator 
                              + DTM_SUBDIR
                              + File.separator;

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
    * Create AxisIterator and walk CHILD axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase1()
    {
		reporter.testCaseInit("Walk CHILD AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase1.out";

		// Create DTM and generate initial context
    	Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

	  	// Get various nodes to use as context nodes.
	  	int dtmRoot = dtm.getDocument();				// #document
	  	String dtmRootName = dtm.getNodeName(dtmRoot);	// Used for output
	  	int DNode = dtm.getFirstChild(dtmRoot);			// <Document>
	  	String DNodeName = dtm.getNodeName(DNode);
	  	int CNode = dtm.getFirstChild(DNode);			// <Comment>
	  	int PINode = dtm.getNextSibling(CNode);			// <PI>
	  	ANode = dtm.getNextSibling(PINode);				// <A>, used in testcase 7 - 10
	  	ANodeName = dtm.getNodeName(ANode);


		// Get a Iterator for CHILD:: axis and query it's direction.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.CHILD);
      	iter.setStartNode(DNode);
		buf.append("#### CHILD from "+DNodeName+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
		{ 
			buf.append(getNodeInfo(dtm, itNode, " "));
			lastNode = itNode;			// Setting this GLOBAL IS BAD, but easy. Investigate!!
		}
		lastName = dtm.getNodeName(lastNode);

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase1"); 
        reporter.testCaseClose();
        return true;
    }

    /**
     * Create AxisIterator and walk PARENT axis.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
		reporter.testCaseInit("Walk PARENT AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase2.out";

		// Create dtm and generate initial context
		Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

		// Get a Iterator for PARENT:: axis.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.PARENT);
      	iter.setStartNode(lastNode);

		// Print out info about the axis
		buf.append("#### PARENT from "+lastName+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
			 buf.append(getNodeInfo(dtm, itNode, " "));
		 		
		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase2"); 

        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisIterator and walk CHILD axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase3()
    {
		reporter.testCaseInit("Walk SELF AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase3.out";

		// Create dtm and generate initial context
    	Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

		// Get a Iterator for CHILD:: axis.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.SELF);
      	iter.setStartNode(lastNode);

		// Print out info about the axis
		buf.append("#### SELF from "+lastName+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
		  buf.append(getNodeInfo(dtm, itNode, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase3"); 

        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisIterator and walk NAMESPACE axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase4()
    {
		reporter.testCaseInit("Walk NAMESPACE AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase4.out";

		// Create dtm and generate initial context
    	Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

		// Get a Iterator for NAMESPACE:: axis.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.NAMESPACE);
      	iter.setStartNode(lastNode);

		// Print out info about the axis
		buf.append("#### NAMESPACE from "+lastName+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
		     buf.append(getNodeInfo(dtm, itNode, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase4"); 

        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisIterator and walk CHILD axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase5()
    {
		reporter.testCaseInit("Walk PRECEDING AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase5.out";

		// Create dtm and generate initial context
    	Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

		// Get a Iterator for CHILD:: axis.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.PRECEDING);
      	iter.setStartNode(lastNode);

		// Print out info about the axis
		buf.append("#### PRECEDING from "+lastName+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
		     buf.append(getNodeInfo(dtm, itNode, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase5"); 

        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisIterator and walk PRECEDINGSIBLING axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase6()
    {
		reporter.testCaseInit("Walk PRECEDINGSIBLING AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase6.out";

		// Create dtm and generate initial context
    	Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

		// Get a Iterator for PRECEDINGSIBLING:: axis.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.PRECEDINGSIBLING);
      	iter.setStartNode(lastNode);

		// Print out info about the axis
		buf.append("#### PRECEDINGSIBLING from "+lastName+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
		     buf.append(getNodeInfo(dtm, itNode, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase6"); 
        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisIterator and walk FOLLOWING axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase7()
    {
		reporter.testCaseInit("Walk FOLLOWING AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase7.out";

		// Create dtm and generate initial context
    	Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

		// Get a Iterator for FOLLOWING:: axis.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.FOLLOWING);
      	iter.setStartNode(ANode);

		// Print out info about the axis
		buf.append("#### FOLLOWING from "+ANodeName+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
		     buf.append(getNodeInfo(dtm, itNode, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase7"); 
        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisIterator and walk FOLLOWINGSIBLING axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase8()
    {
		reporter.testCaseInit("Walk FOLLOWINGSIBLING AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase8.out";

		// Create dtm and generate initial context
    	Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

		// Get a Iterator for FOLLOWINGSIBLING:: axis.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.FOLLOWINGSIBLING);
      	iter.setStartNode(ANode);

		// Print out info about the axis
		buf.append("#### FOLLOWINGSIBLING from "+ANodeName+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
		{ buf.append(getNodeInfo(dtm, itNode, " "));
		  //lastNode = itNode;
		}

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase8"); 
        reporter.testCaseClose();
        return true;
    }


   /**
    * Create AxisIterator and walk FOLLOWINGSIBLING axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase9()
    {
		reporter.testCaseInit("Walk DESCENDANT AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase9.out";

		// Create dtm and generate initial context
    	Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

		// Get a Iterator for DESCENDANT:: axis.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.DESCENDANT);
      	iter.setStartNode(ANode);

		// Print out info about the axis
		buf.append("#### DESCENDANT from "+ANodeName+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
		     buf.append(getNodeInfo(dtm, itNode, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase9"); 
        reporter.testCaseClose();
        return true;
    }

   /**
    * Create AxisIterator and walk FOLLOWINGSIBLING axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase10()
    {
		reporter.testCaseInit("Walk DESCENDANTORSELF AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase10.out";

		// Create dtm and generate initial context
    	Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

		// Get a Iterator for DESCENDANTORSELF:: axis.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.DESCENDANTORSELF);
      	iter.setStartNode(ANode);

		// Print out info about the axis
		buf.append("#### DESCENDANTORSELF from "+ANodeName+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
	   	{
	   		buf.append(getNodeInfo(dtm, itNode, " "));
		  	lastNode2 = itNode;
		}

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase10"); 
        reporter.testCaseClose();
        return true;
    }


   /**
    * Create AxisIterator and walk ANCESTOR axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase11()
    {
		reporter.testCaseInit("Walk ANCESTOR AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase11.out";

		// Create dtm and generate initial context
    	Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

		// Get a Iterator for ANCESTOR:: axis.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.ANCESTOR);
      	iter.setStartNode(lastNode2);

		lastName2 = dtm.getNodeName(lastNode2);

		// Print out info about the axis
		buf.append("#### ANCESTOR from "+lastName2+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
			 buf.append(getNodeInfo(dtm, itNode, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase11"); 
        reporter.testCaseClose();
        return true;
    }


   /**
    * Create AxisIterator and walk ANCESTORORSELF axis.
    * @return false if we should abort the test; true otherwise
    */
    public boolean testCase12()
    {
		reporter.testCaseInit("Walk ANCESTORORSELF AxisIterator");
		StringBuffer buf = new StringBuffer();
		FileOutputStream fos = openFileStream(outNames.nextName());
        String gold = testFileInfo.goldName + "testcase12.out";

		// Create dtm and generate initial context
    	Source source = new StreamSource(new StringReader(defaultSource));
      	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
      	DTM dtm=manager.getDTM(source, true, stripper, false, true);

		// Get a Iterator for ANCESTORORSELF:: axis.
      	DTMAxisIterator iter = dtm.getAxisIterator(Axis.ANCESTORORSELF);
      	iter.setStartNode(lastNode2);

		// Print out info about the axis
		buf.append("#### ANCESTORORSELF from "+lastName2+", Reverse Axis:" + iter.isReverse() + "\n");

	  	// Iterate the axis and write node info to output file
      	for (int itNode = iter.next(); DTM.NULL != itNode; itNode = iter.next())
			 buf.append(getNodeInfo(dtm, itNode, " "));

		// Write results and close output file.
		writeClose(fos, buf);

        // Verify results
        fileChecker.check(reporter, new File(outNames.currentName()),
        							new File(gold),
        							"Testcase12"); 
        reporter.testCaseClose();
        return true;
    }

public String usage()
{
	return ("Common [optional] options supported by TestDTMIter:\n"
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
    
String getNodeInfo(DTM dtm, int nodeHandle, String indent)
{
    // Formatting hack -- suppress quotes when value is null, to distinguish
    // it from "null".
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

    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        TestDTMIter app = new TestDTMIter();
        app.doMain(args);
    }
}
