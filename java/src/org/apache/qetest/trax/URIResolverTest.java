/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2001 The Apache Software Foundation.  All rights 
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
 * URIResolverTest.java
 *
 */
package org.apache.qetest.trax;

// Support for test reporting and harness classes
import org.apache.qetest.*;
import org.apache.qetest.xsl.*;

// Import all relevant TRAX packages
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

// Needed SAX, DOM, JAXP classes
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.w3c.dom.Node;

// java classes
import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;

//-------------------------------------------------------------------------

/**
 * Verify that URIResolvers are called properly.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class URIResolverTest extends FileBasedTest
{

    /** Provide sequential output names automatically.   */
    protected OutputNameManager outNames;


    /** 
     * A simple stylesheet with errors for testing in various flavors.  
     * Must be coordinated with templatesExpectedType/Value,
     * transformExpectedType/Value.
     */
    protected XSLTestfileInfo testFileInfo = new XSLTestfileInfo();

    /** Subdir name under test\tests\api for files.  */
    public static final String TRAX_SUBDIR = "trax";

    /** Just initialize test name, comment, numTestCases. */
    public URIResolverTest()
    {
        numTestCases = 1;  // REPLACE_num
        testName = "URIResolverTest";
        testComment = "Verify that URIResolvers are called properly.";
    }


    /**
     * Initialize this test  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        // Used for all tests; just dump files in trax subdir
        File outSubDir = new File(outputDir + File.separator + TRAX_SUBDIR);
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        outNames = new OutputNameManager(outputDir + File.separator + TRAX_SUBDIR
                                         + File.separator + testName, ".out");


        String testBasePath = inputDir 
                              + File.separator 
                              + TRAX_SUBDIR
                              + File.separator;
        String goldBasePath = goldDir 
                              + File.separator 
                              + TRAX_SUBDIR
                              + File.separator;

        testFileInfo.inputName = testBasePath + "URIResolverTest.xsl";
        testFileInfo.xmlName = testBasePath + "URIResolverTest.xml";
        testFileInfo.goldName = goldBasePath + "URIResolverTest.out";

        return true;
    }


    /**
     * Build a stylesheet/do a transform with lots of URIs to resolve.
     * Verify that the URIResolver is called properly.
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Build a stylesheet/do a transform with lots of URIs to resolve");
        LoggingURIResolver loggingURIResolver = new LoggingURIResolver((Logger)reporter);
        reporter.logTraceMsg("loggingURIResolver originally setup:" + loggingURIResolver.getQuickCounters());

        TransformerFactory factory = null;
        Templates templates = null;
        Transformer transformer = null;
        try
        {
            factory = TransformerFactory.newInstance();
            // Set the URIResolver and validate it
            factory.setURIResolver(loggingURIResolver);
            reporter.check((factory.getURIResolver() == loggingURIResolver),
                           true, "set/getURIResolver on factory");

            // Validate various URI's to be resolved during stylesheet 
            //  build with the loggingURIResolver
            String[] expectedXslUris = 
            {
                "{" + QetestUtils.filenameToURL(testFileInfo.inputName) + "}" + "impincl/SystemIdImport.xsl",
                "{" + QetestUtils.filenameToURL(testFileInfo.inputName) + "}" + "impincl/SystemIdInclude.xsl"
            };
            loggingURIResolver.setExpected(expectedXslUris);
            // Note that we don't currently have a default URIResolver, 
            //  so the LoggingURIResolver class will just attempt 
            //  to use the SystemIDResolver class instead
            // loggingURIResolver.setDefaultHandler(savedURIResolver);
            reporter.logInfoMsg("About to factory.newTemplates(" + QetestUtils.filenameToURL(testFileInfo.inputName) + ")");
            templates = factory.newTemplates(new StreamSource(QetestUtils.filenameToURL(testFileInfo.inputName)));
            reporter.logTraceMsg("loggingURIResolver after newTemplates:" + loggingURIResolver.getQuickCounters());

            // Clear out any setExpected or counters
            loggingURIResolver.reset();

            transformer = templates.newTransformer();
            reporter.logTraceMsg("default transformer's getURIResolver is: " + transformer.getURIResolver());
            // Set the URIResolver and validate it
            transformer.setURIResolver(loggingURIResolver);
            reporter.check((transformer.getURIResolver() == loggingURIResolver),
                           true, "set/getURIResolver on transformer"); 

            // Validate various URI's to be resolved during transform
            //  time with the loggingURIResolver
            String[] expectedXmlUris = 
            {
                "{" + QetestUtils.filenameToURL(testFileInfo.inputName) + "}" + "../impincl/SystemIdImport.xsl",
                "{" + QetestUtils.filenameToURL(testFileInfo.inputName) + "}" + "impincl/SystemIdImport.xsl",
                "{" + QetestUtils.filenameToURL(testFileInfo.inputName) + "}" + "systemid/impincl/SystemIdImport.xsl",
            };
            
            //@todo Bugzilla#2425 every document() call is resolved twice twice - two fails caused below MOVED to SmoketestOuttakes.java 02-Nov-01 -sc
            reporter.logWarningMsg("Bugzilla#2425 every document() call is resolved twice twice - two fails caused below MOVED to SmoketestOuttakes.java 02-Nov-01 -sc");
            // loggingURIResolver.setExpected(expectedXmlUris);
            //@todo Bugzilla#2425 every document() call is resolved twice twice - two fails caused below MOVED to SmoketestOuttakes.java 02-Nov-01 -sc

            reporter.logTraceMsg("about to transform(...)");
            transformer.transform(new StreamSource(QetestUtils.filenameToURL(testFileInfo.xmlName)), 
                                  new StreamResult(outNames.nextName()));
            reporter.logTraceMsg("after transform(...)");
            // Clear out any setExpected or counters
            loggingURIResolver.reset();

            // Validate the actual output file as well: in this case, 
            //  the stylesheet should still work
            fileChecker.check(reporter, 
                    new File(outNames.currentName()), 
                    new File(testFileInfo.goldName), 
                    "transform of URI-filled xsl into: " + outNames.currentName());
        }
        catch (Throwable t)
        {
            reporter.checkFail("URIResolver test unexpectedly threw: " + t.toString());
            reporter.logThrowable(Logger.ERRORMSG, t, "URIResolver test unexpectedly threw");
        }

        reporter.testCaseClose();
        return true;
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by URIResolverTest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        URIResolverTest app = new URIResolverTest();
        app.doMain(args);
    }
}
