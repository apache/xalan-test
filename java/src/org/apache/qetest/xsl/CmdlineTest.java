/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.qetest.xsl;

import org.apache.qetest.*;
import org.apache.qetest.xslwrapper.ProcessorWrapper;  // Merely for the ERROR constant

import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;
import java.util.Vector;

import org.apache.xalan.xslt.Process;

//-------------------------------------------------------------------------

/**
 * New, Improved! CmdlineTest for Xalan's Process class.
 * Note this test works identically for Xalan-J 1.x and Xalan-J 2.x!
 * @author shane_curcuru@lotus.com
 */
public class CmdlineTest extends XSLDirectoryIterator
{

    /** Array of 'command line' args for Process.main(args) calls. */
    protected String[] pargs = null;

    /** If we should precompile (and serialize) stylesheets separately first. */
    protected boolean precompile = false;

    /** Just initialize test name, comment, numTestCases. */
    public CmdlineTest()
    {

        testName = "CmdlineTest";
        testComment = "Iterates over all conf test dirs and validates outputs USING Process.main()";
    }

    /**
     * Initialize this test - create vector of command line args for Process.  
     *
     * @param p Properties to initialize from (if needed)
     * @return false if we should abort the test; true otherwise
     */
    public boolean doTestFileInit(Properties p)
    {

        Vector vec = new Vector();

        // Set up pargs with global args from our inputs; used in every process
        // Standard args that XLTest supports:
        if ((liaison == null) || ("".equals(liaison)))
        {
            // no-op, don't use null or blank liaison
        }
        else
        {
            // setup args for liaison - note must be fully qualified classname
            // Note: may not be applicable for Xalan-J 2.x
            vec.addElement("-PARSER");
            vec.addElement(liaison);
        }

        // Only pass the indent if it was explicitly set
        if (indentLevel > NO_INDENT)
        {
            vec.addElement("-INDENT");
            vec.addElement(Integer.toString(indentLevel));
        }

        // precompile is supported in processOneFile
        // Unsupported: flavor - not very useful!
        // Unsupported: diagName
        // Unsupported: noReuse
        // Gadzillions of args that Process supports:
        setSingleArg("E", testProps, vec);  //"   [-E (Do not expand entity refs)]");
        setSingleArg("V", testProps, vec);  //"   [-V (Version info)]");
        setSingleArg("QC", testProps, vec);  //"   [-QC (Quiet Pattern Conflicts Warnings)]");
        setSingleArg("Q", testProps, vec);  //"   [-Q  (Quiet Mode)]");
        setSingleArg("LF", testProps, vec);  //"   [-LF (Use linefeeds only on output {default is CR/LF})]");
        setSingleArg("CR", testProps, vec);  //"   [-CR (Use carriage returns only on output {default is CR/LF})]");
        setSingleArg("TT", testProps, vec);  //"   [-TT (Trace the templates as they are being called.)]");
        setSingleArg("TG", testProps, vec);  //"   [-TG (Trace each generation event.)]");
        setSingleArg("TS", testProps, vec);  //"   [-TS (Trace each selection event.)]");
        setSingleArg("TTC", testProps, vec);  //"   [-TTC (Trace the template children as they are being processed.)]");
        setSingleArg("VALIDATE", testProps, vec);  //"   [-VALIDATE (Set whether validation occurs.  Validation is off by default.)]");
        setSingleArg("XML", testProps, vec);  //"   [-XML (Use XML formatter and add XML header.)]");
        setSingleArg("TEXT", testProps, vec);  //"   [-TEXT (Use simple Text formatter.)]");
        setSingleArg("HTML", testProps, vec);  //"   [-HTML (Use HTML formatter.)]");
        setSingleArg("SX", testProps, vec);  //"   [-SX (Xerces serializers)]");

        // FIXME: Args not yet tested    
        //"   [-ESCAPE (Which characters to escape {default is <>&\"\'\\r\\n}]");
        //"   [-TCLASS (TraceListener class for trace extensions.)]");
        //"   [-EDUMP {optional filename} (Do stackdump on error.)]");
        //"   [-PARAM name expression (Set a stylesheet parameter)]");
        // Copy the vector into array
        int vecSize = vec.size();
        pargs = new String[vecSize];

        vec.copyInto(pargs);

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < vecSize; i++)
        {
            buf.append(vec.elementAt(i));
            buf.append(" "); // separate with spaces
        }
        reporter.logInfoMsg("Default arguments vector1: " + buf.toString());
        reporter.logInfoMsg("Default arguments vector2: " + vec.toString());

        return true;
    }

    /**
     * Worker method to add a single "-"arg if found in properties block.  
     *
     * @param name of argument
     * @param p properties block to search for name
     * @param v Vector changes caller's copy
     */
    protected void setSingleArg(String name, Properties p, Vector v)
    {
        final String tmp = (String) p.get(name);

        if (tmp != null)  // Note: adds it even if it's blank!
        {
            v.addElement("-" + name);
        }
    }

    /**
     * Subclassed callback to provide info about the processor you're testing.
     * @return description info from our processorWrapper.</P>
     */
    public String getDescription()
    {
        return "ProcessorVersion;command line Process.main():Xalan only";
    }

    /**
     * Run through the directory given to us and run tests found in subdirs.
     *
     * @param p Properties to initialize from, unused
     * @return false if serious error occours
     */
    public boolean runTestCases(Properties p)
    {
        // Use all the default directory processing methods 
        // from our parent XLDirectoryIterator
        processTestDir();
        return true;
    }

    /**
     * Run one xsl/xml file pair through the processor - overriden from XLDirectoryIterator.
     * <P>We simply call Process.main() with a constructed series of args[].</P>
     * <P>Note that timings are in no way comparable to the normal ConformanceTest.</P>
     *
     * @param XMLName path\filename of XML data file
     * @param XSLName path\filename of XSL Stylesheet file
     * @param OutName path\filename of desired output file
     * @return int status - pass, fail, expected exception or unexpected exception
     */
    public int processSingleFile(String XMLName, String XSLName,
                                 String OutName)
    {

        long fileTime = ProcessorWrapper.ERROR;

        try
        {
            if (precompile)
            {
                String lxcoutName = OutName + ".lxcout";

                // See if the serialized stylesheet previously exists first!!!
                File sFile = new File(lxcoutName);
                if (sFile.exists())
                {
                    reporter.logWarningMsg(
                        "WARNING: precompiled stylesheet already exists: "
                        + lxcoutName);
                }

                String args1[] = new String[pargs.length + 4];  // Need 4 extra slots for 
                // -xsl XSL -lxcout tempXSL
                args1[0] = "-xsl";  // Should be a constant
                args1[1] = QetestUtils.filenameToURL(XSLName);
                args1[2] = "-LXCOUT";
                args1[3] = lxcoutName;

                System.arraycopy(pargs, 0, args1, 4, pargs.length);

                // Special processing if we should precompile & serialize the stylesheets
                // Declare variables ahead of time to minimize latency
                long startTime = 0;
                long endTime = 0;

                startTime = System.currentTimeMillis();

                // Here, we're just preprocessing the stylesheet
                org.apache.xalan.xslt.Process.main(args1);

                endTime = System.currentTimeMillis();
                fileTime = endTime - startTime;

                // Separately verify that the stylesheet was serialized
                if (sFile.exists())
                {
                    reporter.logStatistic(reporter.INFOMSG,
                                          sFile.length(), 0,
                                          "Preprocessed stylesheet size: "
                                          + lxcoutName);
                }
                else
                {
                    reporter.logWarningMsg(
                        "WARNING: precompiled stylesheet does not exist: "
                        + lxcoutName);
                }

                // Now use the precompiled stylesheet, not the .XSL file
                String args2[] = new String[pargs.length + 6];  // Need 6 extra slots for 

                // -in XML -lxcin lxcfile -out OUT
                args2[0] = "-in";
                args2[1] = QetestUtils.filenameToURL(XMLName);
                args2[2] = "-LXCIN";
                args2[3] = lxcoutName;
                args2[4] = "-out";
                args2[5] = OutName;

                System.arraycopy(pargs, 0, args2, 6, pargs.length);

                startTime = System.currentTimeMillis();

                org.apache.xalan.xslt.Process.main(args2);

                endTime = System.currentTimeMillis();
                fileTime += endTime - startTime;  // Increment with the new time

                // Only report statistics after we're done with whole process
                if (fileTime != ProcessorWrapper.ERROR)
                {
                    dirTime += fileTime;

                    dirFilesProcessed++;

                    reporter.logTraceMsg("processOneFile(" + XSLName
                                         + ") no exceptions; time "
                                         + fileTime);
                }
                else
                {

                    // Do not increment performance counters if there's an error
                    reporter.logWarningMsg("processOneFile(" + XSLName
                                           + ") returned ERROR code!");
                }
            }  // of if (precompile)
            else
            {

                // Normal processing just like before
                String args[] = new String[pargs.length + 6];  // Need 6 extra slots for 

                // -in XML -xsl XSL -out OUT
                args[0] = "-in";
                args[1] = QetestUtils.filenameToURL(XMLName);
                args[2] = "-xsl";
                args[3] = QetestUtils.filenameToURL(XSLName);
                args[4] = "-out";
                args[5] = OutName;

                System.arraycopy(pargs, 0, args, 6, pargs.length);

                // Declare variables ahead of time to minimize latency
                long startTime = 0;
                long endTime = 0;

                startTime = System.currentTimeMillis();

                org.apache.xalan.xslt.Process.main(args);

                endTime = System.currentTimeMillis();
                fileTime = endTime - startTime;

                if (fileTime != ProcessorWrapper.ERROR)
                {
                    dirTime += fileTime;

                    dirFilesProcessed++;

                    reporter.logTraceMsg("processOneFile(" + XSLName
                                         + ") no exceptions; time "
                                         + fileTime);
                }
                else
                {

                    // Do not increment performance counters if there's an error
                    reporter.logWarningMsg("processOneFile(" + XSLName
                                           + ") returned ERROR code!");
                }
            }  // of if (precompile)
        }

        // Catch general Exceptions, check if they're expected, and restart
        catch (Exception e)
        {
            reporter.logStatusMsg("processOneFile(" + XSLName + ") threw: "
                                  + e.toString());

            int retVal = checkExpectedException(e, XSLName, OutName);
            return retVal;
        }

        // Catch any Throwable, check if they're expected, and restart
        catch (Throwable t)
        {
            reporter.logStatusMsg("processOneFile(" + XSLName + ") threw: "
                                  + t.toString());

            int retVal = checkExpectedException(t, XSLName, OutName);
            return retVal;
        }

        return PROCESS_OK;
    }

    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by CmdlineTest (none extra):\n"
                + super.usage());
    }

    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {
        CmdlineTest app = new CmdlineTest();
        app.doMain(args);
    }
}  // end of class CmdlineTest

