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
     * Implementers should provide the details of their "equals"
     * algorithim in getCheckMethod().
     * Note that the order of actual, reference is usually important
     * important in determining the result.
     * <li>Typically:
     * <ul>any unexpected Exceptions thrown -> ERRR_RESULT</ul>
     * <ul>actual does not exist -> FAIL_RESULT</ul>
     * <ul>reference does not exist -> AMBG_RESULT</ul>
     * <ul>actual is equivalent to reference -> PASS_RESULT</ul>
     * <ul>actual is not equivalent to reference -> FAIL_RESULT</ul>
     * </li>
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

        if (!((actual instanceof File) & (reference instanceof File)))
        {

            // Must have File objects to continue
            reporter.checkErr("XHTFileCheckService only takes files, with: "
                              + msg, id);

            return reporter.ERRR_RESULT;
        }

        File actualFile = (File) actual;
        File referenceFile = (File) reference;

        // Fail if Actual file doesn't exist or is 0 len
        if ((!actualFile.exists()) || (actualFile.length() == 0))
        {
            reporter.checkFail(msg, id);

            return reporter.FAIL_RESULT;
        }

        // Ambiguous if gold file doesn't exist or is 0 len
        if ((!referenceFile.exists()) || (referenceFile.length() == 0))
        {
            reporter.checkAmbiguous(msg, id);

            return Reporter.AMBG_RESULT;
        }

        sw = new StringWriter();

        PrintWriter pw = new PrintWriter(sw);
        boolean warning[] = new boolean[1];  // TODO: should use this!

        warning[0] = false;

        boolean isEqual = false;

        try
        {

            // Note calling order (gold, act) is different than checkFiles()
            isEqual = comparator.compare(referenceFile.getCanonicalPath(),
                                         actualFile.getCanonicalPath(), pw,
                                         warning);

            // Side effect: fills in sw with info about the comparison
        }
        catch (Exception e)
        {
            pw.println("comparator threw: " + e.toString());

            isEqual = false;
        }

        pw.flush();

        if (!isEqual)
        {

            // We fail, obviously!
            reporter.checkFail(msg, id);

            return Reporter.FAIL_RESULT;
        }
        else if (warning[0])
        {
            pw.println("comparator whitespace diff warning!");
            pw.flush();
            reporter.checkFail(msg, id);

            return Reporter.FAIL_RESULT;
        }
        else
        {
            reporter.checkPass(msg, id);

            return Reporter.PASS_RESULT;
        }
    }

    /**
     * Compare two objects for equivalence, and return appropriate result.
     *
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
     * Gets extended information about the last checkFiles call.
     * @return String describing any additional info about the last two files that were checked
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
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String getDescription()
    {
        return ("Uses an XML/HTML/Text diff comparator to check or diff two files.");
    }
}  // end of class XHTFileCheckService

