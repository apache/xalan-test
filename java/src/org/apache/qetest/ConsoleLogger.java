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
 * ConsoleLogger.java
 *
 */
package org.apache.qetest;

import java.io.PrintStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Logger that prints human-readable output to System.out.
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class ConsoleLogger implements Logger
{

    //-----------------------------------------------------
    //-------- Class members --------
    //-----------------------------------------------------

    /** Our output stream - currently hard-coded to System.out. */
    protected PrintStream outStream = System.out;

    /** If we're ready to start outputting yet. */
    protected boolean ready = false;

    /** If we should indent sub-results or not. */
    protected boolean indent = true;

    /** Level (number of spaces?) to indent sub-results. */
    protected StringBuffer sIndent = new StringBuffer();

    /** Generic properties for this Logger; sort-of replaces instance variables. */
    protected Properties loggerProps = null;

    //-----------------------------------------------------
    //-------- Control and utility routines --------
    //-----------------------------------------------------

    /** Simple constructor, does not perform initialization. */
    public ConsoleLogger()
    { /* no-op */
    }

    /**
     * Constructor calls initialize(p).
     * @param p Properties block to initialize us with.
     */
    public ConsoleLogger(Properties p)
    {
        ready = initialize(p);
    }

    /**
     * Return a description of what this Logger does.
     * @return "reports results to System.out".
     */
    public String getDescription()
    {
        return ("org.apache.qetest.ConsoleLogger - reports results to System.out.");
    }

    /**
     * Returns information about the Property name=value pairs that
     * are understood by this Logger/Reporter.
     * @return same as {@link java.applet.Applet.getParameterInfo}.
     */
    public String[][] getParameterInfo()
    {

        String pinfo[][] =
        {
            { OPT_INDENT, "boolean", "If reporter should indent sub-results" }
        };

        return pinfo;
    }

    /**
     * Accessor methods for our properties block.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public Properties getProperties()
    {
        return loggerProps;
    }

    /**
     * Accessor methods for our properties block.
     * @param p Properties to set (is cloned).
     */
    public void setProperties(Properties p)
    {

        if (p != null)
        {
            loggerProps = (Properties) p.clone();
        }
    }

    /**
     * Call once to initialize this Logger/Reporter from Properties.
     * @param Properties block to initialize from.
     * @param status, true if OK, false if an error occoured.
     *
     * NEEDSDOC @param p
     *
     * NEEDSDOC ($objectName$) @return
     */
    public boolean initialize(Properties p)
    {

        setProperties(p);

        String i = loggerProps.getProperty(OPT_INDENT);

        if (i != null)
        {
            if (i.toLowerCase().equals("no")
                    || i.toLowerCase().equals("false"))
                indent = false;
            else if (i.toLowerCase().equals("yes")
                     || i.toLowerCase().equals("true"))
                indent = true;
        }

        ready = true;

        return true;
    }

    /**
     * Is this Logger/Reporter ready to log results?
     * @return status - true if it's ready to report, false otherwise
     */
    public boolean isReady()
    {
        return ready;
    }

    /**
     * Is this Logger/Reporter still running OK?
     * @return false - ConsoleLoggers never have errors
     */
    public boolean checkError()
    {
        return false;
    }

    /**
     * Flush this Logger/Reporter - no-op for ConsoleLogger.
     */
    public void flush()
    { /* no-op */
    }

    /**
     * Close this Logger/Reporter - essentially no-op for ConsoleLogger.
     */
    public void close()
    {

        flush();

        ready = false;
    }

    /** Simplistic indenting - two spaces. */
    protected void indent()
    {
        if (indent)
            sIndent.append("  ");
    }

    /** Simplistic outdenting - two spaces. */
    protected void outdent()
    {
        if ((indent) && (sIndent.length() >= 2))
            sIndent.setLength(sIndent.length() - 2);
    }

    //-----------------------------------------------------
    //-------- Testfile / Testcase start and stop routines --------
    //-----------------------------------------------------

    /**
     * Report that a testfile has started.
     * @param name file name or tag specifying the test.
     * @param comment comment about the test.
     */
    public void testFileInit(String name, String comment)
    {
        outStream.println(sIndent + "TestFileInit " + name + ":" + comment);
        indent();
    }

    /**
     * Report that a testfile has finished, and report it's result.
     * @param msg message or name of test to log out
     * @param result result of testfile
     */
    public void testFileClose(String msg, String result)
    {
        outdent();
        outStream.println(sIndent + "TestFileClose(" + result + ") " + msg);
    }

    /**
     * Report that a testcase has started.
     * @param comment short description of this test case's objective.
     */
    public void testCaseInit(String comment)
    {
        outStream.println(sIndent + "TestCaseInit " + comment);
        indent();
    }

    /**
     * Report that a testcase has finished, and report it's result.
     * @param msg message of name of test case to log out
     * @param result result of testfile
     */
    public void testCaseClose(String msg, String result)
    {
        outdent();
        outStream.println(sIndent + "TestCaseClose(" + result + ") " + msg);
    }

    //-----------------------------------------------------
    //-------- Test results logging routines --------
    //-----------------------------------------------------

    /**
     * Report a comment to result file with specified severity.
     * ConsoleLoggers ignore message severities.
     * @param level severity or class of message.
     * @param msg comment to log out.
     */
    public void logMsg(int level, String msg)
    {
        outStream.println(sIndent + msg);
    }

    /**
     * Report an arbitrary String to result file with specified severity.
     * Log out the String provided exactly as-is.
     * @param severity or class of message.
     * @param arbitrary String to log out.
     *
     * NEEDSDOC @param level
     * NEEDSDOC @param msg
     */
    public void logArbitrary(int level, String msg)
    {
        outStream.println(msg);
    }

    /**
     * Logs out statistics to result file with specified severity.
     * @param severity of message.
     *
     * NEEDSDOC @param level
     * @param lVal statistic in long format.
     * @param dVal statistic in double format.
     * @param msg comment to log out.
     */
    public void logStatistic(int level, long lVal, double dVal, String msg)
    {
        outStream.println(sIndent + msg + " l: " + lVal + " d: " + dVal);
    }

    /**
     * Logs out a element to results with specified severity.
     * Simply indents and dumps output as string like so:
     * <pre>
     *    element
     *    attr1=value1
     *    ...
     *    msg.toString()
     * </pre>
     * @param level severity of message.
     * @param element name of enclosing element
     * @param attrs hash of name=value attributes
     * @param msg Object to log out; up to reporters to handle
     * processing of this; usually logs just .toString().
     */
    public void logElement(int level, String element, Hashtable attrs,
                           Object msg)
    {

        indent();
        outStream.println(sIndent + element);
        indent();

        for (Enumeration enum = attrs.keys();
                enum.hasMoreElements(); /* no increment portion */ )
        {
            Object key = enum.nextElement();

            outStream.println(sIndent + key.toString() + "="
                              + attrs.get(key).toString());
        }

        outdent();
        outStream.println(sIndent + msg.toString());
        outdent();
    }

    /**
     * Logs out contents of a Hashtable with specified severity.
     * @param level severity or class of message.
     * @param hash Hashtable to log the contents of.
     * @param msg decription of the Hashtable.
     */
    public void logHashtable(int level, Hashtable hash, String msg)
    {

        indent();
        outStream.println(sIndent + "HASHTABLE: " + msg);
        indent();

        if (hash == null)
        {
            outStream.println(sIndent + "hash == null, no data");
        }
        else
        {
            try
            {

                // Fake the Properties-like output
                for (Enumeration enum = hash.keys();
                        enum.hasMoreElements(); /* no increment portion */ )
                {
                    Object key = enum.nextElement();

                    outStream.println(sIndent + key.toString() + "="
                                      + hash.get(key).toString());
                }
            }
            catch (Exception e)
            {

                // No-op: should ensure we have clean output
            }
        }

        outdent();
        outdent();
    }

    //-----------------------------------------------------
    //-------- Test results reporting check* routines --------
    //-----------------------------------------------------

    /**
     * Writes out a Pass record with comment.
     * @param comment comment to log with the pass record.
     */
    public void checkPass(String comment)
    {
        outStream.println(sIndent + "PASS!  " + comment);
    }

    /**
     * Writes out an ambiguous record with comment.
     * @param comment comment to log with the ambg record.
     */
    public void checkAmbiguous(String comment)
    {
        outStream.println(sIndent + "AMBG   " + comment);
    }

    /**
     * Writes out a Fail record with comment.
     * @param comment comment to log with the fail record.
     */
    public void checkFail(String comment)
    {
        outStream.println(sIndent + "FAIL   " + comment);
    }

    /**
     * Writes out a Error record with comment.
     * @param comment comment to log with the error record.
     */
    public void checkErr(String comment)
    {
        outStream.println(sIndent + "ERROR  " + comment);
    }

    /* EXPERIMENTAL: have duplicate set of check*() methods 
       that all output some form of ID as well as comment. 
       Leave the non-ID taking forms for both simplicity to the 
       end user who doesn't care about IDs as well as for 
       backwards compatibility.
    */

    /**
     * Writes out a Pass record with comment and ID.
     * @author Shane_Curcuru@lotus.com
     * @param comment comment to log with the pass record.
     * @param ID token to log with the pass record.
     */
    public void checkPass(String comment, String id)
    {
        if (id != null)
            outStream.println(sIndent + "PASS!  (" + id + ") " + comment);
        else
            outStream.println(sIndent + "PASS!  " + comment);
    }

    /**
     * Writes out an ambiguous record with comment and ID.
     * @author Shane_Curcuru@lotus.com
     * @param comment to log with the ambg record.
     * @param ID token to log with the pass record.
     */
    public void checkAmbiguous(String comment, String id)
    {
        if (id != null)
            outStream.println(sIndent + "AMBG   (" + id + ") " + comment);
        else
            outStream.println(sIndent + "AMBG   " + comment);
    }

    /**
     * Writes out a Fail record with comment and ID.
     * @author Shane_Curcuru@lotus.com
     * @param comment comment to log with the fail record.
     * @param ID token to log with the pass record.
     */
    public void checkFail(String comment, String id)
    {
        if (id != null)
            outStream.println(sIndent + "FAIL!  (" + id + ") " + comment);
        else
            outStream.println(sIndent + "FAIL!  " + comment);
    }

    /**
     * Writes out an Error record with comment and ID.
     * @author Shane_Curcuru@lotus.com
     * @param comment comment to log with the error record.
     * @param ID token to log with the pass record.
     */
    public void checkErr(String comment, String id)
    {
        if (id != null)
            outStream.println(sIndent + "ERROR  (" + id + ") " + comment);
        else
            outStream.println(sIndent + "ERROR  " + comment);
    }
}  // end of class ConsoleLogger

