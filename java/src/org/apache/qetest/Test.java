/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights 
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
 * Test.java
 *
 */
package org.apache.qetest;

import java.util.Properties;

/**
 * Minimal interface defining a test.
 * Supplying a separate interface from the most common default
 * implementation makes it simpler for external harnesses or
 * automation methods to handle lists of Tests.
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public interface Test
{

    /**
     * Accesor method for the name of this test.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public abstract String getTestName();

    /**
     * Accesor method for a brief description of this test.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public abstract String getTestDescription();

    /**
     * Accesor methods for our Reporter.
     * Tests will either have a Logger (for very simple tests)
     * or a Reporter (for most tests).
     * <p>Providing both API's in the interface allows us to run
     * the two styles of tests nearly interchangeably.</p>
     * @todo document this better; how to harnesses know which to use?
     * @param r the Reporter to have this test use for logging results
     */
    public abstract void setReporter(Reporter r);

    /**
     * Accesor methods for our Reporter.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public abstract Reporter getReporter();

    /**
     * Accesor methods for our Logger.
     * Tests will either have a Logger (for very simple tests)
     * or a Reporter (for most tests).
     * <p>Providing both API's in the interface allows us to run
     * the two styles of tests nearly interchangeably.</p>
     * @todo document this better; how to harnesses know which to use?
     * @param l the Logger to have this test use for logging results
     */
    public abstract void setLogger(Logger l);

    /**
     * Accesor methods for our Logger.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public abstract Logger getLogger();

    /**
     * Accesor methods for our abort flag.
     * If this flag is set during a test run, then we should simply
     * not bother to run the rest of the test.  In all other cases,
     * harnesses or Tests should attempt to continue running the
     * entire test including cleanup routines.
     * @param a true if we should halt processing this test
     */
    public abstract void setAbortTest(boolean a);

    /**
     * Accesor methods for our abort flag.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public abstract boolean getAbortTest();

    /**
     * Token used to pass command line as initializer.
     * Commonly tests may be run as applications - this token is
     * used as the name for the entry in the Properties block
     * that will contain the array of Strings that was the command
     * line for the application.
     * <p>This allows external test harnesses or specific test
     * implementations to easily pass in their command line using
     * the Properties argument in many Test methods.</p>
     */
    public static final String MAIN_CMDLINE = "test.CmdLine";

    /**
     * Run this test: main interface to cause the test to run itself.
     * A major goal of the Test class is to separate the act and
     * process of writing a test from it's actual runtime
     * implementation.  Testwriters should not generally need to
     * know how their test is being executed.
     * <ul>They should simply focus on defining:
     * <li>doTestFileInit: what setup has to be done before running
     * the testCases: initializing the product under test, etc.</li>
     * <li>testCase1, 2, ... n: individual, independent test cases</li>
     * <li>doTestFileClose: what cleanup has to be done after running
     * the test, like restoring product state or freeing test resources</li>
     * </ul>
     * <p>This method returns a simple boolean status as a convenience.
     * In cases where you have a harness that runs a great many
     * tests that normally pass, the harness can simply check this
     * value for each test: if it's true, you could even delete any
     * result logs then, and simply print out a meta-log stating
     * that the test passed.  Note that this does not provide any
     * information about why a test failed (or caused an error, or
     * whatever) - that's what the info in any reports/logs are for.</p>
     * <p>If a test is aborted, then any containing harness needs not
     * finish executing the test.  Otherwise, even if part of a test fails,
     * you should let the whole test run through.  Note that aborting
     * a test may result in the reporter or logger output being
     * incomplete, which may make an invalid report file (in the case
     * of XMLFileLogger, for example).</p>
     * @todo Maybe return TestResult instead of boolean flag?
     * @todo pass in a set of options for the test
     * @author Shane_Curcuru@lotus.com
     * @param Properties block used for initialization
     *
     * NEEDSDOC @param p
     * @return status - true if test ran to completion and <b>all</b>
     * cases passed, false otherwise
     */
    public abstract boolean runTest(Properties p);

    /**
     * Initialize this test - called once before running testcases.
     * @todo does this need to be in the interface? Shouldn't external
     * callers simply use the runTest() interface?
     * @author Shane_Curcuru@lotus.com
     * @param Properties block used for initialization
     *
     * NEEDSDOC @param p
     * @return true if setup and Reporter creation successful, false otherwise
     */
    public abstract boolean testFileInit(Properties p);

    /**
     * Run all of our testcases.
     * This should cause each testCase in the test to be executed
     * independently, and then return true if and only if all
     * testCases passed successfully.  If any testCase failed or
     * caused any unexpected errors, exceptions, etc., it should
     * return false.
     * @todo Maybe return TestResult instead of boolean flag?
     * @todo does this need to be in the interface? Shouldn't external
     * callers simply use the runTest() interface?
     * @author Shane_Curcuru@lotus.com
     * @param Properties block used for initialization
     *
     * NEEDSDOC @param p
     * @return true if all testCases passed, false otherwise
     */
    public abstract boolean runTestCases(Properties p);

    /**
     * Cleanup this test - called once after running testcases.
     * @todo does this need to be in the interface? Shouldn't external
     * callers simply use the runTest() interface?
     * @author Shane_Curcuru@lotus.com
     * @param Properties block used for initialization
     *
     * NEEDSDOC @param p
     * @return true if cleanup successful, false otherwise
     */
    public abstract boolean testFileClose(Properties p);
}  // end of class Test

