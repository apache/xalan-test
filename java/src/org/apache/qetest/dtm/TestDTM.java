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
public class TestDTM extends FileBasedTest
{
/**
* This test creates a DTM and tests basic functionality of the DTM API
* - execute 'build package.trax', 'traxapitest TestDTMIter.java'
* - a bunch of convenience variables/initializers are included, 
*   use or delete as is useful
* @author Paul Dick
* @version $Id$
*
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
public static final String DTM_Prefix = "DTM_";

String defaultSource=
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

    /** Just initialize test name, comment, numTestCases. */
    public TestDTM()
    {
        numTestCases = 1;
        testName = "TestDTM";
        testComment = "Function test of DTM";
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
                              + File.separator
                              + DTM_Prefix;

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
	reporter.testCaseInit("Basic Functionality of DTM");
	StringBuffer buf = new StringBuffer();
	FileOutputStream fos = openFileStream(outNames.nextName());
    String gold = testFileInfo.goldName + "testcase1.out";

    // Create dtm and generate initial context
	DTM dtm = generateDTM();

    // DTM -- which will always be true for a node obtained this way, but
    // won't be true for "shared" DTMs used to hold XSLT variables
    int rootNode=dtm.getDocument();
	buf.append(" *** DOCUMENT PROPERTIES: *** "+
	  "\nDocURI=\""+dtm.getDocumentBaseURI()+"\" "+
	  "SystemID=\""+dtm.getDocumentSystemIdentifier(rootNode)+"\"\n"+
         // removed from test until implemented bugzilla 14753
         // "DocEncoding=\""+dtm.getDocumentEncoding(rootNode)+"\" "+
	  "StandAlone=\""+dtm.getDocumentStandalone(rootNode)+"\" "+
	  "DocVersion=\""+dtm.getDocumentVersion(rootNode)+"\""+
	  "\n\n");
      
    // Simple test: Recursively dump the DTM's content.
    // We'll want to replace this with more serious examples
	buf.append(" *** DOCUMENT DATA: *** ");
    recursiveDumpNode(dtm, rootNode, buf);
    
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
  
void recursiveDumpNode(DTM dtm, int nodeHandle, StringBuffer buf)
{
    // ITERATE over siblings
    for( ; nodeHandle!=DTM.NULL; nodeHandle=dtm.getNextSibling(nodeHandle) )
    {
    	buf.append(getNodeInfo(dtm,nodeHandle,""));
      
	    // List the namespaces, if any.
	    // Include only node's local namespaces, not inherited
	    // %ISSUE% Consider inherited?
	    int kid=dtm.getFirstNamespaceNode(nodeHandle,false);
	    if(kid!=DTM.NULL)
		{
			buf.append("\n\tNAMESPACES:");
			for( ; kid!=DTM.NULL; kid=dtm.getNextNamespaceNode(nodeHandle,kid,false))
			{
				buf.append(getNodeInfo(dtm,kid,"\t"));
			}
		}
      									
		// List the attributes, if any
		kid=dtm.getFirstAttribute(nodeHandle);
		if(kid!=DTM.NULL)
		{
			buf.append("\n\tATTRIBUTES:");
			for( ; kid!=DTM.NULL; kid=dtm.getNextSibling(kid))
			{
				buf.append(getNodeInfo(dtm,kid,"\t"));
			}
		}
      
		// Recurse into the children, if any
		recursiveDumpNode(dtm, dtm.getFirstChild(nodeHandle), buf);
	}
}

String getNodeInfo(DTM dtm, int nodeHandle, String indent)
{

    // Formatting hack -- suppress quotes when value is null, to distinguish
    // it from "null".
	String buf = new String("null");
    String value=dtm.getNodeValue(nodeHandle);
    String vq=(value==null) ? "" : "\"";

    // Skip outputing of text nodes. In most cases they clutter the output, 
	// besides I'm only interested in the elemental structure of the dtm. 
	{
    	buf = new String("\n" + indent+
		       nodeHandle+": "+
		       TYPENAME[dtm.getNodeType(nodeHandle)]+" "+
		       dtm.getNodeNameX(nodeHandle)+ " : " +
			   dtm.getNodeName(nodeHandle)+
		       "\" E-Type="+dtm.getExpandedTypeID(nodeHandle)+
			   " Level=" + dtm.getLevel(nodeHandle)+
		       " Value=" + vq + value + vq	+ "\n"+
		       indent+
			   "\tPrefix= "+"\""+dtm.getPrefix(nodeHandle)+"\""+
			   " Name= "+"\""+dtm.getLocalName(nodeHandle)+"\""+
			   " URI= "+"\""+dtm.getNamespaceURI(nodeHandle)+"\" "+
		       "Parent=" + dtm.getParent(nodeHandle) +
		       " 1stChild=" + dtm.getFirstChild(nodeHandle) +
		       " NextSib=" + dtm.getNextSibling(nodeHandle)
		       );

	} 
	return buf;
}
  
public String usage()
{
	return ("Common [optional] options supported by TestDTM:\n"
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

// This routine generates a new DTM for each testcase
DTM generateDTM()
{
	dtmWSStripper stripper = new dtmWSStripper();

	// Create DTM and generate initial context
	Source source = new StreamSource(new StringReader(defaultSource));
	DTMManager manager= new DTMManagerDefault().newInstance(new XMLStringFactoryImpl());
	DTM dtm=manager.getDTM(source, true, stripper, false, true);
   
	return dtm;
}

void writeClose(FileOutputStream fos, StringBuffer buf)
{
	// Write results and close output file.
	try
	{
               fos.write(buf.toString().getBytes("UTF-8"));
		fos.close();
	}

	catch (Exception e)
	{  reporter.checkFail("Failure writing output."); 	}
 }
    
/**
* Main method to run test from the command line - can be left alone.  
* @param args command line argument array
*/
public static void main(String[] args)
{
	TestDTM app = new TestDTM();
	app.doMain(args);
}


}
