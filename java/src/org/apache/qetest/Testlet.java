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
 * Testlet.java
 *
 */
package org.apache.qetest;

/**
 * Minimal interface defining a testlet, a sort of mini-test.
 * A Testlet defines a single, simple test case that is completely
 * independent.  Commonly a Testlet will perform a single 
 * test operation and verify it, usually given a set of test 
 * data to perform the operation on.  
 *
 * <p>This makes creating data-driven tests simpler, by separating 
 * the test algorithim from the definition of the test data.  Note 
 * that logging what happened during the test is already separated 
 * out into the Logger interface.</p>
 * 
 * <p>Testlets are used with Datalets, which provide a single set 
 * of data to execute this test case with.
 * For example:</p>
 * <ul>
 *   <li>We define a Testlet that processes an XML file with a 
 *   stylesheet in a certain manner - perhaps using a specific 
 *   set of SAX calls.</li>
 *   <li>The Testlet takes as an argument a matching Datalet, that 
 *   defines any parameters that may change  - like the names 
 *   of the XML file and the stylesheet file to use.</li>
 *   <li>Test authors or users running a harness or the like can 
 *   then easily define a large set of Datalets for various 
 *   types of input files that they want to test, and simply 
 *   iterate over the set of Datalets, repeatedly calling 
 *   Testlet.execute().  Each execution of the Testlet will 
 *   be independent.</li>
 * </ul>
 * 
 * <p>Testlets may provide additional worker methods that allow them 
 * to be easily run in varying situations; for example, a 
 * testwriter may have a Test object that calls a number of Testlets 
 * for it's test cases.  If one of the Testlets finds a bug, the 
 * testwriter can simply reference the single Testlet and it's 
 * current Datalet in the bug report, without having to reference 
 * the enclosing Test file.  This makes it easier for others to 
 * reproduce the problem with a minimum of overhead.</p>
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public interface Testlet
{

    /**
     * Accesor method for a brief description of this Testlet.  
     * <p>Testlet implementers should provide a brief, one line 
     * description of the algorithim of their test case.</p>
     * //@todo do we need to define a setDescription() method?
     * Since Testlets are pretty self-sufficient, implementers 
     * should always just define this, and not let callers 
     * re-set them.
     *
     * @return String describing what this Testlet does.
     */
    public abstract String getDescription();


    /**
     * Accesor methods for our Logger.
     * <p>Testlets use simple Loggers that they rely on the caller 
     * to have setup.  This frees the Testlet and the Logger from 
     * having to store any other state about the Testlet.  It is 
     * the caller's responsibility to do any overall rolling-up 
     * or aggregating of results reporting, if needed.</p>
     *
     * @param l the Logger to have this test use for logging 
     * results; or null to use a default logger
     */
    public abstract void setLogger(Logger l);


    /**
     * Accesor methods for our Logger.  
     *
     * @return Logger we tell all our secrets to; may be null
     */
    public abstract Logger getLogger();


    /**
     * Get a default Logger for use with this Testlet.  
     * <p>Provided to allow subclasses to override this in different 
     * ways.  This would probably be called when setLogger(null) 
     * is called.  The most common implementation would be to 
     * return a Logger.DEFAULT_LOGGER (which simply 
     * logs things to the console).</p>
     *
     * //@todo this sort of functionality should really be provided 
     * by the Logger class itself - any caller should be able to ask 
     * Logger (or a Logger factory, if you want to get fancy) for a 
     * default Logger for use without having to supply any params.
     *
     * @return Logger suitable for passing to setLogger()
     */
    public abstract Logger getDefaultLogger();


    /**
     * Return this Testlet's default Datalet.  
     * <p>Every Testlet should have created a default Datalet that can 
     * be used with this test: i.e. the test case itself has a 
     * default, or sample set of data to execute the test with. This 
     * way a user can simply execute the Testlet on the fly without 
     * having to provide any input data.</p>
     * <p>If the Testlet can't provide a default Datalet, either 
     * the user must provide one somehow, or an error message 
     * should be printed out.  Note that the Testlet must still 
     * return a Datalet of the correct class from this method, even 
     * if the Datalet returned has no data set into it.  This would 
     * allow a harness with random test data generation capabilities 
     * to discover this Testlet, and then generate random Datalets to 
     * pass to it.</p>
     *
     * @return Datalet this Testlet can use as a default test case.
     */
    public abstract Datalet getDefaultDatalet();


    /**
     * Run this Testlet: execute it's test and return.
     * <p>The Testlet should perform it's test operation, logging 
     * information as needed to it's Logger, using the provided 
     * Datalet as a test point.</p>
     * <p>If the Datalet passed is null, the Testlet should use 
     * it's default Datalet as the test point data.</p>
     * <p>Testlets should not throw exceptions and should not 
     * return anything nor worry about checking their state or 
     * rolling up any overall results to their Logger.  Testlets 
     * should simply focus on performing their one test operation 
     * and outputting any simple pass/fail/other results to their 
     * Logger.  It is the responsibility of the caller to do any 
     * overall or rolled-up reporting that is desired.</p>
     *
     * @author Shane_Curcuru@lotus.com
     * @param Datalet to use as data points for the test; if null, 
     * will attempt to use getDefaultDatalet()
     */
    public abstract void execute(Datalet datalet);


}  // end of class Testlet

