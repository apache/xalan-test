/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights 
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

/*
 *
 * DTMDumpTest.java
 *
 */
package org.apache.qetest.xalanj2;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

// Relevant Xalan-J 2.x classes
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xml.dtm.*;
import org.apache.xml.dtm.ref.*;
import org.apache.xpath.NodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;

// Needed SAX, DOM, JAXP classes
import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;

// java classes
import java.io.File;
import java.util.Properties;
import java.util.Vector;

//-------------------------------------------------------------------------

/**
 * Simple unit test of various DTM and related apis.  
 * This class acts as it's own Xalan extension.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class DTMDumpTest extends FileBasedTest
{

    /** Provides nextName(), currentName() functionality.   */
    protected OutputNameManager outNames;

    /** Simple test with dumpDTM extension calls in.  */
    protected TraxDatalet testFileInfo = new TraxDatalet();

    /** Just initialize test name, comment, numTestCases. */
    public DTMDumpTest()
    {
        numTestCases = 2;  // REPLACE_num
        testName = "DTMDumpTest";
        testComment = "Simple unit test of various DTM and related apis";
    }


    /**
     * Initialize this test - create output dir, outNames.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        final String XALANJ2 = "xalanj2";
        File outSubDir = new File(outputDir + File.separator + XALANJ2);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + XALANJ2
                                         + File.separator + testName, ".out");
                                         
        testFileInfo.setDescription("Simple transform with dumpDTM extension call");
        testFileInfo.setNames(inputDir + File.separator + XALANJ2, "DTMDumpTest");
        testFileInfo.goldName = goldDir + File.separator + XALANJ2 + File.separator + "DTMDumpTest.out";
        return true;
    }


    /**
     * Simple dumping of DTM info from nodes.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Simple dumping of DTM info from nodes");

        TransformerFactory factory = null;
        Templates templates = null;
        Transformer transformer = null;
        try
        {
            factory = TransformerFactory.newInstance();
            // Transform a file that calls us as an extension
            templates = factory.newTemplates(testFileInfo.getXSLSource());
            transformer = templates.newTransformer();
            reporter.logInfoMsg("Before dtmBuf: " + dtmBuf.toString());
            reporter.logInfoMsg("About to create output: " + outNames.nextName()); 
            transformer.transform(testFileInfo.getXMLSource(),
                                  new StreamResult(outNames.currentName()));
            reporter.checkPass("Crash test only: returned from transform() call");
            reporter.logInfoMsg("After dtmBuf: " + dtmBuf.toString());
                
        }
        catch (Throwable t)
        {
            reporter.logThrowable(Logger.ERRORMSG, t, "Simple DTM test threw:");
            reporter.checkFail("Simple DTM test threw:");
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Validate transforms with FEATURE_INCREMENTAL on/off.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase2()
    {
        reporter.testCaseInit("Unused");
        reporter.checkPass("Unused");
        reporter.testCaseClose();
        return true;
    }


    /** Cheap way to pass info from extension methods to test.  */
    protected static StringBuffer dtmBuf = new StringBuffer();
    
    /** Cheap way to pass info from extension methods to test.  */
    protected static final String DTMBUFSEP = ";";

    /**
     * Implements a simple Xalan extension method.  
     *
     * Just a way to implement an extension and the test that calls 
     * it together in the same class.  Watch out for thread safety.
     * @param ExpressionContext from the transformer
     * @return String describing actions
     */
    public static String dumpDTM(ExpressionContext context)
    {
        Node contextNode = context.getContextNode();
        DTMNodeProxy proxy = (DTMNodeProxy)contextNode;
        dtmBuf.append(XalanDumper.dump(proxy, XalanDumper.DUMP_DEFAULT) + DTMBUFSEP);
        return XalanDumper.dump(proxy, XalanDumper.DUMP_NOIDS);
    }

    /**
     * Implements a simple Xalan extension method.  
     *
     * Just a way to implement an extension and the test that calls 
     * it together in the same class.  Watch out for thread safety.
     * @param context from the transformer
     * @param obj object to test; presumably an RTF
     * @return String describing actions
     */
    public static String dumpDTM(ExpressionContext context, Object rtf)
    {
        if (rtf instanceof NodeIterator)
        {
            NodeSet ns = new NodeSet((NodeIterator) rtf);
            Node first = ns.nextNode();
            DTMNodeProxy proxy = (DTMNodeProxy)first;
            dtmBuf.append("NI:" + XalanDumper.dump(proxy, XalanDumper.DUMP_DEFAULT) + DTMBUFSEP);
            return XalanDumper.dump(proxy, XalanDumper.DUMP_NOIDS);
        }
        else if (rtf instanceof NodeSet)
        {
            NodeSet ns = (NodeSet)rtf;
            Node first = ns.nextNode();
            DTMNodeProxy proxy = (DTMNodeProxy)first;
            dtmBuf.append("NS:" + XalanDumper.dump(proxy, XalanDumper.DUMP_DEFAULT) + DTMBUFSEP);
            return XalanDumper.dump(proxy, XalanDumper.DUMP_NOIDS);
        }
        else
        {
            dtmBuf.append("UK:" + rtf.toString() + DTMBUFSEP);
            return "UK:" + rtf.toString();
        }
    }

    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by DTMDumpTest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        DTMDumpTest app = new DTMDumpTest();
        app.doMain(args);
    }
}
