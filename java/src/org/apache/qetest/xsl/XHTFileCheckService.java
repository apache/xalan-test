/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000, 2001 The Apache Software Foundation.  All rights 
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
import java.util.Hashtable;

/**
 * Uses an XML/HTML/Text diff comparator to check or diff two files.
 * @see #check(Logger logger, Object actual, Object reference, String msg, String id)
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
     * @param logger to dump any output messages to
     * @param actual (current) Object to check
     * @param reference (gold, or expected) Object to check against
     * @param description of what you're checking
     * @param msg comment to log out with this test point
     * @param id ID tag to log out with this test point
     * @return Logger.*_RESULT code denoting status; each method may 
     * define it's own meanings for pass, fail, ambiguous, etc.
     */
    public int check(Logger logger, Object actual, Object reference,
                     String msg, String id)
    {
        // Create our 'extended info' stuff now, so it will 
        //  always reflect the most recent call to this method
        sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        if (((null == actual) || (null == reference )))
        {
            pw.println("XHTFileCheckService actual or reference was null!");
            pw.flush();
            logFileCheckElem(logger, "null", "null", msg, id, sw.toString());
            logger.checkErr(msg, id);
            return logger.ERRR_RESULT;
        }
        if (!((actual instanceof File) & (reference instanceof File)))
        {
            // Must have File objects to continue
            pw.println("XHTFileCheckService only takes File objects!");
            pw.flush();
            logFileCheckElem(logger, actual.toString(), reference.toString(), msg, id, sw.toString());
            logger.checkErr(msg, id);
            return logger.ERRR_RESULT;
        }

        File actualFile = (File) actual;
        File referenceFile = (File) reference;

        // Fail if Actual file doesn't exist or is 0 len
        if ((!actualFile.exists()) || (actualFile.length() == 0))
        {
            pw.println("actual(" + actualFile.toString() + ") did not exist or was 0 len");
            pw.flush();
            logFileCheckElem(logger, actualFile.toString(), referenceFile.toString(), msg, id, sw.toString());
            logger.checkFail(msg, id);
            return logger.FAIL_RESULT;
        }

        // Ambiguous if gold file doesn't exist or is 0 len
        if ((!referenceFile.exists()) || (referenceFile.length() == 0))
        {
            pw.println("reference(" + referenceFile.toString() + ") did not exist or was 0 len");
            pw.flush();
            logFileCheckElem(logger, actualFile.toString(), referenceFile.toString(), msg, id, sw.toString());
            logger.checkAmbiguous(msg, id);
            return Logger.AMBG_RESULT;
        }

        boolean warning[] = new boolean[1];  // TODO: should use this!
        warning[0] = false;
        boolean isEqual = false;

        try
        {
            // Note calling order (gold, act) is different than checkFiles()
            isEqual = comparator.compare(referenceFile.getCanonicalPath(),
                                         actualFile.getCanonicalPath(), pw,
                                         warning, attributes);
            // Side effect: fills in pw/sw with info about the comparison
        }
        catch (Throwable t)
        {
            // Add any exception info to pw/sw; this will automatically 
            //  get logged out later on via logFileCheckElem
            pw.println("XHTFileCheckService threw: " + t.toString());
            t.printStackTrace(pw);
            isEqual = false;
        }

        // If not equal at all, fail
        if (!isEqual)
        {
            pw.println("XHTFileCheckService files were not equal");
            pw.flush();
            logFileCheckElem(logger, actualFile.toString(), referenceFile.toString(), msg, id, sw.toString());
            logger.checkFail(msg, id);
            return Logger.FAIL_RESULT;
        }
        // If whitespace-only diffs, then pass/fail based on allowWhitespaceDiff
        else if (warning[0])
        {
            pw.println("XHTFileCheckService whitespace diff warning!");
            pw.flush();
            if (allowWhitespaceDiff)
            {
                logger.logMsg(Logger.STATUSMSG, "XHTFileCheckService whitespace diff warning, passing!");
                logger.checkPass(msg, id);
                return Logger.PASS_RESULT;
            }
            else
            {
                logFileCheckElem(logger, actualFile.toString(), referenceFile.toString(), msg, id, 
                        "XHTFileCheckService whitespace diff warning, failing!\n" + sw.toString());
                logger.checkFail(msg, id);
                return Logger.FAIL_RESULT;
            }
        }
        // Otherwise we were completely equal, so pass
        else
        {
            pw.println("XHTFileCheckService files were equal");
            pw.flush();
            // For pass case, we *dont* call logFileCheckElem
            logger.checkPass(msg, id);
            return Logger.PASS_RESULT;
        }
    }

    /**
     * Logs a custom element about the current check() call.  
     * <pre>
     * <fileCheck level="40"
     * reference="tests\conf-gold\match\match16.out"
     * reportedBy="XHTFileCheckService"
     * actual="results-alltest\dom\match\match16.out"
     * >
     * StylesheetTestlet match16.xsl(null) 
     * XHTFileCheckService threw: java.io.IOException: The process cannot access the file because it is being used by another process
     * java.io.IOException: The process cannot access the file because it is being used by another process
     * 	at java.io.Win32FileSystem.canonicalize(Native Method)
     *     etc...
     * XHTFileCheckService files were not equal
     * 
     * </fileCheck>
     * </pre>     
     * @param logger to dump any output messages to
     * @param name of actual (current) File to check
     * @param name of reference (gold, or expected) File to check against
     * @param msg comment to log out with this test point
     * @param id to log out with this test point
     * @param additional log info from PrintWriter/StringWriter
     */
    protected void logFileCheckElem(Logger logger, 
            String actualFile, String referenceFile,
            String msg, String id, String logs)
    {
        Hashtable attrs = new Hashtable();
        attrs.put("actual", actualFile);
        attrs.put("reference", referenceFile);
        attrs.put("reportedBy", "XHTFileCheckService");
        String elementBody = msg + "(" + id + ") \n" + logs;
        // HACK: escapeString(elementBody) so that it's legal XML
        //  for cases where we have XML output.  This isn't 
        //  necessarily a 'hack', I'm just not sure what the 
        //  cleanest place to put this is (here or some sort 
        //  of intelligent logic in XMLFileLogger)
        elementBody = XMLFileLogger.escapeString(elementBody);
        logger.logElement(Logger.STATUSMSG, "fileCheck", attrs, elementBody);
    }

    /**
     * Compare two objects for equivalence, and return appropriate result.
     *
     * @see #check(Logger logger, Object actual, Object reference, String msg, String id)
     * @param logger to dump any output messages to
     * @param actual (current) File to check
     * @param reference (gold, or expected) File to check against
     * @param description of what you're checking
     * @param msg comment to log out with this test point
     * @return Logger.*_RESULT code denoting status; each method may 
     * define it's own meanings for pass, fail, ambiguous, etc.
     */
    public int check(Logger logger, Object actual, Object reference,
                     String msg)
    {
        return check(logger, actual, reference, msg, null);
    }

    /**
     * Whether whitespace differences will cause a fail or not.  
     */
    public static final String ALLOW_WHITESPACE_DIFF = "urn:XHTFileCheckService:allowWhitespaceDiff";

    /**
     * Whether whitespace differences will cause a fail or not.  
     * setAttribute("allow-whitespace-diff", true|false)
     * true=whitespace-only diff will pass;
     * false, whitespace-only diff will fail
     */
    protected boolean allowWhitespaceDiff = false;  // default; backwards compatible

    /**
     * Hash of parser-like attributes that have been set.  
     */
    protected Hashtable attributes = null;

    /**
     * Allows the user to set specific attributes on the testing 
     * utility or it's underlying product object under test.
     * 
     * Supports basic JAXP DocumentBuilder attributes, plus our own 
     * "allow-whitespace-diff" attribute.
     * 
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     * @throws IllegalArgumentException thrown if the underlying
     * implementation doesn't recognize the attribute and wants to 
     * inform the user of this fact.
     */
    public void setAttribute(String name, Object value)
        throws IllegalArgumentException
    {
        // Check for our own attributes first
        if (ALLOW_WHITESPACE_DIFF.equals(name))
        {
            //@todo set allowWhitespaceDiff based on value here
        }
        else
        {
            if (null == attributes)
            {
                attributes = new Hashtable();
            }
            attributes.put(name, value);
        }
    }

    /**
     * Allows the user to retrieve specific attributes on the testing 
     * utility or it's underlying product object under test.
     *
     * @param name The name of the attribute.
     * @return value of supported attributes or null if not recognized.
     * @throws IllegalArgumentException thrown if the underlying
     * implementation doesn't recognize the attribute and wants to 
     * inform the user of this fact.
     */
    public Object getAttribute(String name)
        throws IllegalArgumentException
    {
        // Check for our own attributes first
        if (ALLOW_WHITESPACE_DIFF.equals(name))
        {
            return new Boolean(allowWhitespaceDiff);
            
        }
        else if (null != attributes)
        {
            return attributes.get(name);
        }
        else
            return null;
    }

    /**
     * Description of what this testing utility does.  
     * 
     * @return String description of extension
     */
    public String getDescription()
    {
        return ("Uses an XML/HTML/Text diff comparator to check or diff two files.");
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

}  // end of class XHTFileCheckService

