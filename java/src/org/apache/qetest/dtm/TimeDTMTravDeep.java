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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.qetest.FileBasedTest;
import org.apache.qetest.OutputNameManager;
import org.apache.qetest.xsl.XSLTestfileInfo;
import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTM;


public class TimeDTMTravDeep extends FileBasedTest
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
	public static final String TIME_Prefix = "TimeTD_";

	//int[] metric = {0};
	int   lastNode;
	String lastNodeName;

    /** Just initialize test name, comment, numTestCases. */
    public TimeDTMTravDeep()
    {
        numTestCases = 4;
        testName = "TimeDTMTravDeep";
        testComment = "Time the creation and various axis traversers with a deep tree";
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
                              + TIME_Prefix;

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

public boolean testCase1()
{	  
	reporter.testCaseInit("Time Traversal of Descendant:: axis");
	StringBuffer buf = new StringBuffer();
	FileOutputStream fos = QeDtmUtils.openFileStream(outNames.nextName(), reporter);
	String gold = testFileInfo.goldName + "testcase1.out";

	buf.append("\nAxis is DESCENDANT");
	DTM dtm = QeDtmUtils.createDTM(0, QeDtmUtils.deepFile, buf);

	// Get various nodes to use as context nodes.
	int dtmRoot = dtm.getDocument();				// #document
	String dtmRootName = dtm.getNodeName(dtmRoot);	// Used for output
	int DNode = dtm.getFirstChild(dtmRoot);			// <Doc>
	String DNodeName = dtm.getNodeName(DNode);
	int[] rtData = {0,0,0};		// returns Traversal time, last node, number of nodes traversed 

	// Get a traverser for Descendant:: axis.
	buf.append("\n\tSTARTING from: "+ DNodeName);
	QeDtmUtils.timeAxisTraverser(dtm, Axis.DESCENDANT, DNode, rtData);
	buf.append("\n\tTime="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);
	
	// Write results and close output file.
	QeDtmUtils.writeClose(fos, buf, reporter);

    // Verify results
    fileChecker.check(reporter, new File(outNames.currentName()),
       							new File(gold),
       							"Testcase1"); 
    reporter.testCaseClose();
    return true;

}

public boolean testCase2()
{	  
	reporter.testCaseInit("Time Traverser of DESCENDANT-OR-SELF:: axis");
	StringBuffer buf = new StringBuffer();
	FileOutputStream fos = QeDtmUtils.openFileStream(outNames.nextName(), reporter);
	String gold = testFileInfo.goldName + "testcase2.out";

	buf.append("\nAxis is DESCENDANT-OR-SELF");
	DTM dtm = QeDtmUtils.createDTM(0, QeDtmUtils.deepFile, buf);

	// Get various nodes to use as context nodes.
	int dtmRoot = dtm.getDocument();				// #document
	String dtmRootName = dtm.getNodeName(dtmRoot);	// Used for output
	int DNode = dtm.getFirstChild(dtmRoot);			// <Doc>
	String DNodeName = dtm.getNodeName(DNode);
	int[] rtData = {0,0,0};		// returns Traversal time, last node, number of nodes traversed 

	// Get a traverser for Descendant:: axis.
	buf.append("\n\tSTARTING from: "+ DNodeName);
	QeDtmUtils.timeAxisTraverser(dtm, Axis.DESCENDANTORSELF, DNode, rtData);
	buf.append("\n\tTime="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);
	lastNode = rtData[1];
	lastNodeName = dtm.getNodeName(lastNode);

	// Write results and close output file.
	QeDtmUtils.writeClose(fos, buf, reporter);

    // Verify results
    fileChecker.check(reporter, new File(outNames.currentName()),
       							new File(gold),
       							"Testcase2"); 
    reporter.testCaseClose();
    return true;

}

public boolean testCase3()
{	  
	reporter.testCaseInit("Time Traverser of ANCESTOR:: axis");
	StringBuffer buf = new StringBuffer();
	FileOutputStream fos = QeDtmUtils.openFileStream(outNames.nextName(), reporter);
	String gold = testFileInfo.goldName + "testcase3.out";

	buf.append("\nAxis is ANCESTOR");
	DTM dtm = QeDtmUtils.createDTM(0, QeDtmUtils.deepFile, buf);
	buf.append("\n\tSTARTING from: "+ lastNodeName);
	int[] rtData = {0,0,0};		// returns Traversal time, last node, number of nodes traversed 

	// Get a traverser for ANCESTOR:: axis.
	QeDtmUtils.timeAxisTraverser(dtm, Axis.ANCESTOR, lastNode, rtData);
	buf.append("\n\tTime="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);
	
	// Write results and close output file.
	QeDtmUtils.writeClose(fos, buf, reporter);

    // Verify results
    fileChecker.check(reporter, new File(outNames.currentName()),
       							new File(gold),
       							"Testcase3"); 
    reporter.testCaseClose();
    return true;

}

public boolean testCase4()
{	  
	reporter.testCaseInit("Time Traverser of ANCESTOR-or-Self:: axis");
	StringBuffer buf = new StringBuffer();
	FileOutputStream fos = QeDtmUtils.openFileStream(outNames.nextName(), reporter);
	String gold = testFileInfo.goldName + "testcase4.out";

	buf.append("\nAxis is ANCESTOR-or-Self");
	DTM dtm = QeDtmUtils.createDTM(0, QeDtmUtils.deepFile, buf);
	int[] rtData = {0,0,0};		// returns Traversal time, last node, number of nodes traversed 

	// Get a traverser for ANCESTORORSELF:: axis.
	buf.append("\n\tSTARTING from: "+ lastNodeName);
	QeDtmUtils.timeAxisTraverser(dtm, Axis.ANCESTORORSELF, lastNode, rtData);
	buf.append("\n\tTime="+rtData[0] + " : " + "LastNode="+rtData[1]+" nodes="+rtData[2]);
	
	// Write results and close output file.
	QeDtmUtils.writeClose(fos, buf, reporter);

    // Verify results
    fileChecker.check(reporter, new File(outNames.currentName()),
       							new File(gold),
       							"Testcase4"); 
    reporter.testCaseClose();
    return true;

}


/**
* Main method to run test from the command line - can be left alone.  
* @param args command line argument array
*/
public static void main(String[] args)
{
	TimeDTMTravDeep app = new TimeDTMTravDeep();
	app.doMain(args);
}
 
}