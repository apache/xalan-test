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

package org.apache.qetest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.Properties;

/**
 * Simply does .readLine of each file into string buffers and then String.equals().
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class SimpleFileCheckService implements CheckService
{

    /**
     * Compare two objects for equivalence, and return appropriate result.
     *
     * @param logger to dump any output messages to
     * @param actual (current) File to check
     * @param reference (gold, or expected) File to check against
     * @param description of what you're checking
     * @param msg comment to log out with this test point
     * @param id ID tag to log out with this test point
     * @return Logger.*_RESULT code denoting status; each method may 
     * define it's own meanings for pass, fail, ambiguous, etc.
     */
    public int check(Logger logger, Object actual, Object reference,
                     String msg, String id)
    {

        if (!((actual instanceof File) & (reference instanceof File)))
        {

            // Must have File objects to continue
            logger.checkErr(msg + " :check() objects were not Files", id);

            return Logger.ERRR_RESULT;
        }

        String fVal1 = readFileIntoString(logger, (File) actual);

        // Fail if Actual file doesn't exist
        if (fVal1 == null)
        {
            logger.checkFail(msg + " :Actual file null", id);

            return Logger.FAIL_RESULT;
        }

        String fVal2 = readFileIntoString(logger, (File) reference);

        // Ambiguous if gold or reference file doesn't exist
        if (fVal2 == null)
        {
            logger.checkAmbiguous(msg + " :Gold file null", id);

            return Logger.AMBG_RESULT;
        }

        // Pass if they're equal, fail otherwise        
        if (fVal1.equals(fVal2))
        {
            logger.checkPass(msg, id);

            return Logger.PASS_RESULT;
        }
        else
        {
            logger.checkFail(msg, id);

            return Logger.FAIL_RESULT;
        }
    }

    /**
     * Compare two objects for equivalence, and return appropriate result.
     *
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
     * Read text file into string line-by-line.  
     * @param logger to dump any messages to
     * @param f File object to read
     * @return String of file's contents
     */
    private String readFileIntoString(Logger logger, File f)
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
            if (logger != null)
            {
                logger.logMsg(Logger.ERRORMSG, "SimpleFileCheckService(" + f.getPath()
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
     * Gets extended information about the last checkFiles call: NONE AVAILABLE.
     * @return null, since we don't support this
     */
    public String getExtendedInfo()
    {
        return null;
    }

    /**
     * Allows the user to set specific attributes on the testing 
     * utility or it's underlying product object under test.
     * 
     * No-op; this class does not have any supported attributes.
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
        /* no-op */        
    }

    /**
     * Allows the user to set specific attributes on the testing 
     * utility or it's underlying product object under test.
     * 
     * No-op; this class does not have any supported attributes.
     * 
     * @param attrs Props of various name, value attrs.
     */
    public void applyAttributes(Properties attrs)
    {
        /* no-op */        
    }

    /**
     * Allows the user to retrieve specific attributes on the testing 
     * utility or it's underlying product object under test.
     *
     * @param name The name of the attribute.
     * @return null, no attributes supported.
     * @throws IllegalArgumentException thrown if the underlying
     * implementation doesn't recognize the attribute and wants to 
     * inform the user of this fact.
     */
    public Object getAttribute(String name)
        throws IllegalArgumentException
    {
        return null;
    }

    /**
     * Description of what this testing utility does.  
     * 
     * @return String description of extension
     */
    public String getDescription()
    {
        return ("Reads in text files line-by-line as strings (ignoring newlines) and does String.equals()");
    }

}  // end of class SimpleFileCheckService

