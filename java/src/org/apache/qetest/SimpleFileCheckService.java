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
 * SimpleFileCheckService.java
 *
 */
package org.apache.qetest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

/**
 * Simply does .readLine of each file into string buffers and then String.equals().
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class SimpleFileCheckService implements CheckService
{

    /**
     * Compare two objects for equivalence, and return appropriate result.
     * Implementers should provide the details of their "equals"
     * algorithim in getCheckMethod().
     * Note that the order of actual, reference is usually important
     * important in determining the result.
     * <li>Typically:
     * <ul>any unexpected Exceptions thrown -> ERRR_RESULT</ul>
     * <ul>either object is not a File -> ERRR_RESULT</ul>
     * <ul>actual does not exist -> FAIL_RESULT</ul>
     * <ul>reference does not exist -> AMBG_RESULT</ul>
     * <ul>actual is equivalent to reference -> PASS_RESULT</ul>
     * <ul>actual is not equivalent to reference -> FAIL_RESULT</ul>
     * </li>
     *
     * @param reporter to dump any output messages to
     * @param actual (current) File to check
     * @param reference (gold, or expected) File to check against
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
            reporter.checkErr(
                "SimpleFileCheckService only takes files, with: " + msg, id);

            return reporter.ERRR_RESULT;
        }

        String fVal1 = readFileIntoString(reporter, (File) actual);

        // Fail if Actual file doesn't exist
        if (fVal1 == null)
        {
            reporter.checkFail(msg, id);

            return Reporter.FAIL_RESULT;
        }

        String fVal2 = readFileIntoString(reporter, (File) reference);

        // Ambiguous if gold or reference file doesn't exist
        if (fVal2 == null)
        {
            reporter.checkAmbiguous(msg, id);

            return Reporter.AMBG_RESULT;
        }

        // Pass if they're equal, fail otherwise        
        if (fVal1.equals(fVal2))
        {
            reporter.checkPass(msg, id);

            return Reporter.PASS_RESULT;
        }
        else
        {
            reporter.checkFail(msg, id);

            return Reporter.FAIL_RESULT;
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
     * Compare two files for equivalence, and return appropriate *_RESULT flag.
     * <p>Uses appropriate values from Reporter for return values.</p>
     * @param file1 Actual (current) file to check
     * @param file2 Reference (gold, or expected) file to check against
     * @return PASS if equal, FAIL if not, AMBG if gold does not exist
     */
    public int checkFiles(Reporter reporter, File file1, File file2)
    {

        String fVal1 = readFileIntoString(reporter, file1);

        // Fail if Actual file doesn't exist
        if (fVal1 == null)
            return (Reporter.FAIL_RESULT);

        String fVal2 = readFileIntoString(reporter, file2);

        // Ambiguous if gold or reference file doesn't exist
        if (fVal2 == null)
            return (Reporter.AMBG_RESULT);

        // Pass if they're equal, fail otherwise        
        if (fVal1.equals(fVal2))
            return (Reporter.PASS_RESULT);
        else
            return (Reporter.FAIL_RESULT);
    }

    /**
     * Compare two files for equivalence, and return appropriate *_RESULT flag.
     * For backwards compatibility.
     * @param file1 Actual (current) file to check
     * @param file2 Reference (gold, or expected) file to check against
     * @return PASS if equal, FAIL if not, AMBG if gold does not exist
     */
    public int checkFiles(File file1, File file2)
    {

        return checkFiles(null, file1, file2);
    }

    /**
     * Read text file into string line-by-line.  
     * @param f File object to read
     * @return String of file's contents
     */
    private String readFileIntoString(Reporter reporter, File f)
    {

        StringBuffer sb = new StringBuffer();

        try
        {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            for (;;)
            {
                String inbuf = br.readLine();

                if (inbuf == null)
                    break;

                sb.append(inbuf);
            }
        }
        catch (Exception e)
        {
            if (reporter != null)
            {
                reporter.logErrorMsg("SimpleFileCheckService(" + f.getPath()
                                     + ") threw:" + e.toString());
            }
            else
                System.err.println("SimpleFileCheckService(" + f.getPath()
                                   + ") threw:" + e.toString());

            return null;
        }

        return sb.toString();
    }

    /**
     * Read text file into string line-by-line.  
     * For backwards compatibility.
     * @param f File object to read
     * @return String of file's contents
     */
    private String readFileIntoString(File f)
    {
        return readFileIntoString(null, f);
    }
    /**
     * Description of algorithim used to check file equivalence.  
     * @return String description of algorithim
     */
    public String getDescription()
    {
        return ("Reads in text files line-by-line as strings (ignoring newlines) and does String.equals()");
    }

    /**
     * Gets extended information about the last checkFiles call: NONE AVAILABLE.
     * @return null, since we don't support this
     */
    public String getExtendedInfo()
    {
        return null;
    }
}  // end of class SimpleFileCheckService

