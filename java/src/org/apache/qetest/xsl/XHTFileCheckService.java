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
 * XHTFileCheckService.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Uses an XML/HTML/Text diff comparator to check or diff two files.
 * @see #check(Reporter reporter, Object actual, Object reference, String msg, String id)
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class XHTFileCheckService implements CheckService
{

    /** XHTComparator tool to diff two files. */
    protected XHTComparator comparator = new XHTComparator();

    /** Stores last checkFile calls printed output. */
    private StringWriter sw = null;

    /**
     * Compare two objects for equivalence, and return appropriate result.
     * Note that the order of actual, reference is important
     * important in determining the result.
     * <li>Typically:
     * <ul>any unexpected Exceptions thrown -> ERRR_RESULT</ul>
     * <ul>actual does not exist -> FAIL_RESULT</ul>
     * <ul>reference does not exist -> AMBG_RESULT</ul>
     * <ul>actual is equivalent to reference -> PASS_RESULT</ul>
     * <ul>actual is not equivalent to reference -> FAIL_RESULT</ul>
     * </li>
     * Equvalence is first checked by parsing both files as XML to 
     * a DOM; if that has problems, we parse as HTML to a DOM; if 
     * that has problems, we create a DOM with a single text node 
     * after reading the file as text.  We then compare the two DOM 
     * trees for equivalence. Note that in XML mode differences in 
     * the XML header itself (i.e. standalone=no/yes) are not caught,
     * and will still report a pass (this is a defect in our 
     * comparison method).
     * Side effect: every call to check() fills some additional 
     * info about how the check() call was processed which is then 
     * returned from getExtendedInfo() - this happens no matter what 
     * the result of the check() call was.
     *
     * @param reporter to dump any output messages to
     * @param actual (current) Object to check
     * @param reference (gold, or expected) Object to check against
     * @param description of what you're checking
     * @param msg comment to log out with this test point
     * @param id ID tag to log out with this test point
     * @return Reporter.*_RESULT code denoting status; each method may define
     * it's own meanings for pass, fail, ambiguous, etc.
     */
    public int check(Reporter reporter, Object actual, Object reference,
                     String msg, String id)
    {
        // Create our 'extended info' stuff now, so it will 
        //  always reflect the most recent call to this method
        sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        if (!((actual instanceof File) & (reference instanceof File)))
        {

            // Must have File objects to continue
            reporter.checkErr("XHTFileCheckService only takes files, with: "
                              + msg, id);
            pw.println("XHTFileCheckService only takes files, with: " + msg);
            pw.println("   actual: " + actual);
            pw.println("reference: " + reference);
            pw.flush();
            return reporter.ERRR_RESULT;
        }

        File actualFile = (File) actual;
        File referenceFile = (File) reference;

        // Fail if Actual file doesn't exist or is 0 len
        if ((!actualFile.exists()) || (actualFile.length() == 0))
        {
            reporter.checkFail(msg, id);
            pw.println("actual(" + actualFile.toString() + ") did not exist or was 0 len");
            pw.flush();
            return reporter.FAIL_RESULT;
        }

        // Ambiguous if gold file doesn't exist or is 0 len
        if ((!referenceFile.exists()) || (referenceFile.length() == 0))
        {
            reporter.checkAmbiguous(msg, id);
            pw.println("reference(" + referenceFile.toString() + ") did not exist or was 0 len");
            pw.flush();
            return Reporter.AMBG_RESULT;
        }

        boolean warning[] = new boolean[1];  // TODO: should use this!
        warning[0] = false;
        boolean isEqual = false;

        try
        {

            // Note calling order (gold, act) is different than checkFiles()
            isEqual = comparator.compare(referenceFile.getCanonicalPath(),
                                         actualFile.getCanonicalPath(), pw,
                                         warning);

            // Side effect: fills in pw/sw with info about the comparison
        }
        catch (Throwable t)
        {
            // Add any exception info to pw/sw
            pw.println("XHTFileCheckService threw: " + t.toString());
            t.printStackTrace(pw);
            isEqual = false;
        }

        if (!isEqual)
        {
            // We fail, obviously!
            reporter.checkFail(msg, id);
            pw.println("XHTFileCheckService files were not equal");
            pw.flush();

            return Reporter.FAIL_RESULT;
        }
        else if (warning[0])
        {
            pw.println("XHTFileCheckService whitespace diff warning!");
            pw.flush();
            if (allowWhitespaceDiff)
            {
                reporter.logMsg(Logger.TRACEMSG, "XHTFileCheckService whitespace diff warning, passing!");
                reporter.checkPass(msg, id);
                return Reporter.PASS_RESULT;
            }
            else
            {
                reporter.logMsg(Logger.TRACEMSG, "XHTFileCheckService whitespace diff warning, failing!");
                reporter.checkFail(msg, id);
                return Reporter.FAIL_RESULT;
            }
        }
        else
        {
            reporter.checkPass(msg, id);
            pw.println("XHTFileCheckService files were equal");
            pw.flush();

            return Reporter.PASS_RESULT;
        }
    }

    /**
     * Compare two objects for equivalence, and return appropriate result.
     *
     * @see #check(Reporter reporter, Object actual, Object reference, String msg, String id)
     * @param reporter to dump any output messages to
     * @param actual (current) File to check
     * @param reference (gold, or expected) File to check against
     * @param description of what you're checking
     * @param msg comment to log out with this test point
     * @return Reporter.*_RESULT code denoting status; each method may define
     * it's own meanings for pass, fail, ambiguous, etc.
     */
    public int check(Reporter reporter, Object actual, Object reference,
                     String msg)
    {
        return check(reporter, actual, reference, msg, null);
    }

    /**
     * Gets extended information about the last check call.
     * This info is filled in for every call to check() with brief
     * descriptions of what happened; will return 
     * <code>XHTFileCheckService-no-info-available</code> if 
     * check() has never been called.
     * @return String describing any additional info about the 
     * last two files that were checked
     */
    public String getExtendedInfo()
    {

        if (sw != null)
            return sw.toString();
        else
            return "XHTFileCheckService-no-info-available";
    }

    /**
     * Description of algorithim used to check file equivalence.  
     * @return basic description of how we compare files
     */
    public String getDescription()
    {
        return ("Uses an XML/HTML/Text diff comparator to check or diff two files.");
    }

    /**
     * Whether whitespace differences will cause a fail or not.  
     * setFeature("allow-whitespace-diff", "true"|"false")
     * true=whitespace-only diff will pass;
     * false, whitespace-only diff will fail
     */
    protected boolean allowWhitespaceDiff = false;  // default; backwards compatible

    /**
     * Set a custom option or feature.  
     * @param feature name
     * @param feature value
     */
    public void setFeature(String name, String value)
    {
        if ("allow-whitespace-diff".equals(name))
        {
            if ("true".equals(value) || "yes".equals(value))
            {
                allowWhitespaceDiff = true;
            }
            else if ("false".equals(value) || "no".equals(value))
            {
                allowWhitespaceDiff = false;
            }
        }
    }
}  // end of class XHTFileCheckService

