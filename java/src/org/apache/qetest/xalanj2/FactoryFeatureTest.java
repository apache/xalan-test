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
 * FactoryFeatureTest.java
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

// Needed SAX, DOM, JAXP classes

// java classes
import java.io.File;
import java.util.Properties;
import java.util.Vector;

//-------------------------------------------------------------------------

/**
 * Basic functionality test of various Factory configuration APIs.
 * Testing TransformerFactoryImpl.setAttribute, getFeature, etc.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class FactoryFeatureTest extends FileBasedTest
{

    /** Provides nextName(), currentName() functionality.   */
    protected OutputNameManager outNames;

    /** Marker for datalet.options that notes setAttribute val.   */
    protected static final String SET_ATTRIBUTE = "setAttribute";

    /** Just initialize test name, comment, numTestCases. */
    public FactoryFeatureTest()
    {
        numTestCases = 3;  // REPLACE_num
        testName = "FactoryFeatureTest";
        testComment = "Basic functionality test of various Factory configuration APIs";
    }


    /**
     * Initialize this test - create output dir, outNames.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {
        File outSubDir = new File(outputDir + File.separator + "xalanj2");
        if (!outSubDir.mkdirs())
            reporter.logWarningMsg("Could not create output dir: " + outSubDir);
        // Initialize an output name manager to that dir with .out extension
        outNames = new OutputNameManager(outputDir + File.separator + "xalanj2"
                                         + File.separator + testName, ".out");
        return true;
    }


    /**
     * Default values/settings of known features; simple error cases.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase1()
    {
        reporter.testCaseInit("Default values/settings of known features; simple error cases");

        TransformerFactory factory = null;
        try
        {
            factory = TransformerFactory.newInstance();

            try
            {
                reporter.logStatusMsg("Calling: factory.getAttribute(FEATURE_INCREMENTAL)");
                Object o = factory.getAttribute(TransformerFactoryImpl.FEATURE_INCREMENTAL);
                reporter.checkPass("factory.getAttribute(FEATURE_INCREMENTAL) returned a value");
                reporter.logWarningMsg("//@todo also validate default value:false");                
            }
            catch (IllegalArgumentException iae1)
            {
                // Note Xalan-J 2.2D06 does not fully implement this yet!
                reporter.checkPass("factory.getAttribute(FEATURE_INCREMENTAL) threw an expected IllegalArgumentException");
            }

            try
            {
                reporter.logStatusMsg("Calling: factory.getAttribute(FEATURE_OPTIMIZE)");
                Object o = factory.getAttribute(TransformerFactoryImpl.FEATURE_OPTIMIZE);
                reporter.checkPass("factory.getAttribute(FEATURE_OPTIMIZE) returned a value");
                reporter.logWarningMsg("//@todo also validate default value:true");                
            }
            catch (IllegalArgumentException iae1)
            {
                // Note Xalan-J 2.2D06 does not fully implement this yet!
                reporter.checkPass("factory.getAttribute(FEATURE_OPTIMIZE) threw an expected IllegalArgumentException");
            }

            // Set each value to it's non-default value and ensure no exceptions thrown
            reporter.logStatusMsg("Calling: factory.setAttribute(FEATURE_INCREMENTAL, Boolean.TRUE)");
            factory.setAttribute(TransformerFactoryImpl.FEATURE_INCREMENTAL, Boolean.TRUE);
            reporter.checkPass("factory.setAttribute(FEATURE_INCREMENTAL, Boolean.TRUE) returned OK");

            reporter.logStatusMsg("Calling: factory.setAttribute(FEATURE_OPTIMIZE, Boolean.FALSE)");
            factory.setAttribute(TransformerFactoryImpl.FEATURE_OPTIMIZE, Boolean.FALSE);
            reporter.checkPass("factory.setAttribute(FEATURE_OPTIMIZE, Boolean.FALSE) returned OK");

            // Try setting a non-recognized attribute
            try
            {
                reporter.logTraceMsg("Calling: factory.setAttribute(unrecognized-attribute, Boolean.FALSE)");
                factory.setAttribute("unrecognized-attribute", Boolean.FALSE);
                reporter.checkFail("factory.setAttribute(unrecognized-attribute,...) did not throw expected exception");
            }
            catch (IllegalArgumentException iae1)
            {
                reporter.checkPass("factory.setAttribute(unrecognized-attribute,...) properly threw: " + iae1.toString());
            }

            // Try setting a recognized attribute to a bad value
            try
            {
                reporter.logTraceMsg("Calling: factory.setAttribute(..., bad-object-value)");
                // Note: pass any sort of non-String, non-Boolean Object, since 
                //  this attribute only accepts ture|false as Strings or Booleans
                factory.setAttribute(TransformerFactoryImpl.FEATURE_OPTIMIZE, factory);
                reporter.checkFail("factory.setAttribute(..., bad-object-value) did not throw expected exception");
            }
            catch (IllegalArgumentException iae2)
            {
                reporter.checkPass("factory.setAttribute(..., bad-object-value) properly threw: " + iae2.toString());
            }
        }
        catch (Throwable t)
        {
            reporter.checkFail("setAttribute() tests threw: " + t.toString());
            reporter.logThrowable(reporter.ERRORMSG, t, "setAttribute() tests threw");
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
        reporter.testCaseInit("Validate transforms with FEATURE_INCREMENTAL on/off");

        // Get our list of xsl datalets to test
        Vector datalets = buildDatalets(null, new File(inputDir), 
                                        new File(outputDir), new File(goldDir));

        // Skip Validating datalets - they're hard-coded
        int numDatalets = datalets.size();
        reporter.logInfoMsg("processFileList-equivalent() with " + numDatalets
                            + " potential tests");

        TransformerFactory factory = null;
        // Iterate over every datalet and test it with both feature settings
        for (int ctr = 0; ctr < numDatalets; ctr++)
        {
            TraxDatalet datalet = (TraxDatalet)datalets.elementAt(ctr);
            try
            {
                // Get a new factory for each datalet - not strictly necessary
                factory = TransformerFactory.newInstance();

                // Test both a Boolean object..
                reporter.logStatusMsg("Calling: factory.setAttribute(FEATURE_INCREMENTAL, Boolean.TRUE)");
                factory.setAttribute(TransformerFactoryImpl.FEATURE_INCREMENTAL, Boolean.TRUE);
                datalet.options.put(SET_ATTRIBUTE, "FEATURE_INCREMENTAL, Boolean.TRUE");
                datalet.outputName = outNames.nextName();
                transformAndCheck(factory, datalet);

                reporter.logStatusMsg("Calling: factory.setAttribute(FEATURE_INCREMENTAL, Boolean.FALSE)");
                factory.setAttribute(TransformerFactoryImpl.FEATURE_INCREMENTAL, Boolean.FALSE);
                datalet.options.put(SET_ATTRIBUTE, "FEATURE_INCREMENTAL, Boolean.FALSE");
                datalet.outputName = outNames.nextName();
                transformAndCheck(factory, datalet);

                // .. and a String representation of true|false
                reporter.logStatusMsg("Calling: factory.setAttribute(FEATURE_INCREMENTAL, 'true')");
                factory.setAttribute(TransformerFactoryImpl.FEATURE_INCREMENTAL, "true");
                datalet.options.put(SET_ATTRIBUTE, "FEATURE_INCREMENTAL, 'true'");
                datalet.outputName = outNames.nextName();
                transformAndCheck(factory, datalet);

                reporter.logStatusMsg("Calling: factory.setAttribute(FEATURE_INCREMENTAL, 'false')");
                factory.setAttribute(TransformerFactoryImpl.FEATURE_INCREMENTAL, "false");
                datalet.options.put(SET_ATTRIBUTE, "FEATURE_INCREMENTAL, 'false'");
                datalet.outputName = outNames.nextName();
                transformAndCheck(factory, datalet);
            } 
            catch (Throwable t)
            {
                reporter.logThrowable(Logger.ERRORMSG, t, datalet.getDescription() + "(" + ctr + ") threw");
                reporter.checkFail(datalet.getDescription() + "(" + ctr + ") threw: " + t.toString());
            }
        }  // of while...

        reporter.testCaseClose();
        return true;
    }


    /**
     * Validate transforms with FEATURE_OPTIMIZE on/off.
     *
     * @return false if we should abort the test; true otherwise
     */
    public boolean testCase3()
    {
        reporter.testCaseInit("Validate transforms with FEATURE_OPTIMIZE on/off");

        // Get our list of xsl datalets to test
        Vector datalets = buildDatalets(null, new File(inputDir), 
                                        new File(outputDir), new File(goldDir));

        // Skip Validating datalets - they're hard-coded
        int numDatalets = datalets.size();
        reporter.logInfoMsg("processFileList-equivalent() with " + numDatalets
                            + " potential tests");

        TransformerFactory factory = null;
        // Iterate over every datalet and test it with both feature settings
        for (int ctr = 0; ctr < numDatalets; ctr++)
        {
            TraxDatalet datalet = (TraxDatalet)datalets.elementAt(ctr);
            try
            {
                // Get a new factory for each datalet - not strictly necessary
                factory = TransformerFactory.newInstance();

                // Test both a Boolean object..
                reporter.logInfoMsg("Calling: factory.setAttribute(FEATURE_OPTIMIZE, Boolean.TRUE)");
                factory.setAttribute(TransformerFactoryImpl.FEATURE_OPTIMIZE, Boolean.TRUE);
                datalet.options.put(SET_ATTRIBUTE, "FEATURE_OPTIMIZE, Boolean.TRUE");
                datalet.outputName = outNames.nextName();
                transformAndCheck(factory, datalet);

                reporter.logInfoMsg("Calling: factory.setAttribute(FEATURE_OPTIMIZE, Boolean.FALSE)");
                factory.setAttribute(TransformerFactoryImpl.FEATURE_OPTIMIZE, Boolean.FALSE);
                datalet.options.put(SET_ATTRIBUTE, "FEATURE_OPTIMIZE, Boolean.FALSE");
                datalet.outputName = outNames.nextName();
                transformAndCheck(factory, datalet);

                // .. and a String representation of true|false
                reporter.logInfoMsg("Calling: factory.setAttribute(FEATURE_OPTIMIZE, 'true')");
                factory.setAttribute(TransformerFactoryImpl.FEATURE_OPTIMIZE, "true");
                datalet.options.put(SET_ATTRIBUTE, "FEATURE_OPTIMIZE, 'true'");
                datalet.outputName = outNames.nextName();
                transformAndCheck(factory, datalet);

                reporter.logInfoMsg("Calling: factory.setAttribute(FEATURE_OPTIMIZE, 'false')");
                factory.setAttribute(TransformerFactoryImpl.FEATURE_OPTIMIZE, "false");
                datalet.options.put(SET_ATTRIBUTE, "FEATURE_OPTIMIZE, 'false'");
                datalet.outputName = outNames.nextName();
                transformAndCheck(factory, datalet);
            } 
            catch (Throwable t)
            {
                reporter.logThrowable(Logger.ERRORMSG, t, datalet.getDescription() + "(" + ctr + ") threw");
                reporter.checkFail(datalet.getDescription() + "(" + ctr + ") threw: " + t.toString());
            }
        }  // of while...

        reporter.testCaseClose();
        return true;
    }


    /**
     * Create a vector of filled-in datalets to be tested.
     *
     * This currently is hard-coded to return a static Vector
     * of Datalets that are simply constructed here.
     * In the future we should add a way to read them in from disk.
     * The 'what' files we should test.
     * 
     * @param files Vector of local path\filenames to be tested
     * This is currently ignored
     * @param testLocation File denoting directory where all 
     * .xml/.xsl tests are found
     * @param outLocation File denoting directory where all 
     * output files should be put
     * @param goldLocation File denoting directory where all 
     * gold files are found
     * @return Vector of StylesheetDatalets that are fully filled in,
     * i.e. outputName, goldName, etc are filled in respectively 
     * to inputName
     */
    public Vector buildDatalets(Vector files, File testLocation, 
                                File outLocation, File goldLocation)
    {
        Vector v = new Vector();
        // identity transform
        // Info about the minitest: note gold file naming is different
        TraxDatalet d = new TraxDatalet();
        d.setDescription("Identity transform and:");
        d.setNames(inputDir + File.separator + "trax", "identity");
        d.goldName = goldDir + File.separator + "trax" + File.separator + "identity.out";
        v.addElement(d);

        // Info about the minitest: note gold file naming is different
        d = new TraxDatalet();
        d.setDescription("Basic Minitest file and:");
        d.setNames(inputDir, "Minitest");
        d.goldName = goldDir + File.separator + "Minitest-xalanj2.out";
        v.addElement(d);

        // All done: return full vector
        return v;
    }


    /**
     * Convenience method to do a transform and validate output.  
     * The 'how' of a specific test.
     * @param factory to use to create transformers
     * @param datalet to use (TraxDatalet)
     */
    protected void transformAndCheck(TransformerFactory factory, 
                                     TraxDatalet datalet)
    {
        // Validate arguments
        if ((null == factory) || (null == datalet))
        {
            reporter.checkErr(datalet.getDescription() + " with null args!");
            return;
        }
        final String desc = datalet.getDescription() + datalet.options.get(SET_ATTRIBUTE);

        try
        {
            reporter.logStatusMsg("transformAndCheck of: " + desc);
            Transformer transformer = factory.newTransformer(datalet.getXSLSource());
            reporter.logTraceMsg("About to transform...");
            transformer.transform(datalet.getXMLSource(), new StreamResult(datalet.outputName));

            if (Logger.PASS_RESULT 
                != fileChecker.check(reporter, 
                                     new File(datalet.outputName), 
                                     new File(datalet.goldName), 
                                     desc + " into " + datalet.outputName)
               )
                reporter.logInfoMsg(desc + " failure reason:" + fileChecker.getExtendedInfo());
        }
        catch (Throwable t)
        {
            reporter.logThrowable(reporter.ERRORMSG, t, desc + " threw");
            reporter.checkFail(desc + " threw: " + t.toString());
        }        
    }


    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by FactoryFeatureTest:\n"
                + "(Note: assumes inputDir=.\\tests\\api)\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        FactoryFeatureTest app = new FactoryFeatureTest();
        app.doMain(args);
    }
}
