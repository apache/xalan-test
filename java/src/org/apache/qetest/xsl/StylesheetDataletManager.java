/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2001 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 2001, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.qetest.xsl;

// Support for test reporting and harness classes
import org.apache.qetest.*;

// java classes
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Simple services for getting lists of StylesheetDatalets.
 * 
 *
 * @author shane_curcuru@us.ibm.com
 * @version $Id$
 */
public abstract class StylesheetDataletManager // provide static services only
{
    /** '[' character, first char in first line of xsltmark fileList.  */
    public static final String XSLTMARK_CHAR = "[";

    /** '#' character, comment char in qetest fileList.  */
    public static final String QETEST_COMMENT_CHAR = "#";

    /**
     * Read in a file specifying a list of files to test.
     * <p>File format is determined from first line in file, 
     * which is a little bit dangerous!</p>
     * <p>If first line starts with '[', it's an xsltmark-style 
     * fileList, otherwise it's a qetest-style fileList.</p>
     *
     * @param logger to report problems to
     * @param fileName String; name of the file
     * @param desc description; caller's copy changed
     * @return Vector of StylesheetDatalets, or null if error
     */
    public static Vector readFileList(Logger logger, String fileName, String desc)
    {
        // Verify the file is there
        File f = new File(fileName);
        if (!f.exists())
        {
            logger.logMsg(Logger.ERRORMSG, "readFileList: " + fileName  
                          + " does not exist!");
            return null;
        }

        BufferedReader br = null;
        String line = null;
        try
        {
            br = new BufferedReader(new FileReader(f));
            line = br.readLine(); // read just first line
        }
        catch (IOException ioe)
        {
            logger.logMsg(Logger.ERRORMSG, "readFileList: " + fileName 
                          + " threw: " + ioe.toString());
            return null;
        }

        // Verify the first line
        if (line == null)
        {
            logger.logMsg(Logger.ERRORMSG, "readFileList: " + fileName
                          + " appears to be blank!");
            return null;
        }

        // Determine which kind of fileList this is
        //  we support the 'native' org.apache.qetest format, and 
        //  alternately the .ini file format used in xsltmark
        Vector vec = null;
        if (line.startsWith(XSLTMARK_CHAR))
        {
            // This is an xsltmark .ini style file
            vec = readXsltmarkFileList(logger, br, line, fileName, desc);
        }
        else if (line.startsWith(QETEST_COMMENT_CHAR))
        {
            // This is a native qetest style file
            vec = readQetestFileList(logger, br, line, fileName, desc);
        }
        else
        {
            logger.logMsg(Logger.WARNINGMSG, "readFileList: " + fileName
                          + " could not determine file type; assuming qetest!");
            vec = readQetestFileList(logger, br, line, fileName, desc);
        }

        if (vec.size() == 0)
        {
            logger.logMsg(Logger.ERRORMSG, "readFileList: " + fileName
                          + " did not have any non-comment lines!");
            return null;
        }
        return vec;
    }
    
    /**
     * Read in a qetest fileList specifying a list of files to test.
     * <p>File format is pretty simple:</p>
     * <ul>
     * <li># first line of comments is copied into desc</li>
     * <li># beginning a line is a comment</li>
     * <li># rest of lines are whitespace delimited filenames and options</li>
     * <li>inputName xmlName outName goldName flavor options...</li>
     * <li><b>Note:</b> see {@link StylesheetDatalet} for
     * details on how the file lines are parsed!</li>
     * </ul>
     * <p>Most items are optional, but not having them may result 
     * in validation oddities.  Future work would be to coordinate 
     * this with various Datalet's implementations of .load() so 
     * that Datalets can do better defaulting of non-provided 
     * items; or maybe so that a user can specific a default 'mask' 
     * of values to use for unspecified items.</p>
     *
     * @param logger to report problems to
     * @param br BufferedReader to read from
     * @param firstLine already read from br
     * @param fileName String; name of the file
     * @param desc to use of this file
     * @return Vector of StylesheetDatalets, or null if error
     */
    protected static Vector readQetestFileList(Logger logger, BufferedReader br, 
                                               String firstLine, String fileName, String desc)
    {
        final String ABSOLUTE = "absolute";
        final String RELATIVE = "relative";

        Vector vec = new Vector();
        String line = firstLine;
        // Check if the first line is a comment 
        if (line.startsWith(QETEST_COMMENT_CHAR))
        {
            // Save it as the description
            desc = line;
            // Parse the next line
            try
            {
                line = br.readLine();
            } 
            catch (IOException ioe)
            {
                logger.logMsg(Logger.ERRORMSG, "readQetestFileList: " 
                              + fileName + " threw: " + ioe.toString());
                return null;
            }
        }

        // Load each line into a StylesheetDatalet
        for (;;)
        {
            // Skip any lines beginning with # comment char or that are blank
            if ((!line.startsWith(QETEST_COMMENT_CHAR)) && (line.length() > 0))
            {
                // Create a Datalet and initialize with the line's contents
                StylesheetDatalet d = new StylesheetDatalet(line);

                //@todo Avoid spurious passes when output & gold not specified
                //  needs to detect when StylesheetDatalet doesn't 
                //  properly have outputName and goldName set

                // Add it to our vector
                vec.addElement(d);
            }

            // Read next line and loop
            try
            {
                line = br.readLine();
            }
            catch (IOException ioe2)
            {
                // Just force us out of the loop; if we've already 
                //  read part of the file, fine
                logger.logMsg(Logger.WARNINGMSG, "readQetestFileList: " 
                              + fileName + " threw: " + ioe2.toString());
                break;
            }

            if (line == null)
                break;
        } // end of for (;;)
        return vec;
    }

    /**
     * Read in an xsltmark fileList specifying a list of files to test.
     * <p>File format is an .ini file like so:</p>
     * <pre>
     * [avts]
     * input=db100.xml
     * stylesheet=avts.xsl
     * output=avts.out
     * reference=avts.ref
     * iterations=100
     * </pre>
     * <p>Note that additional attributes will be logged as warnings
     * and will be ignored.</p>
     *
     * @param logger to report problems to
     * @param br BufferedReader to read from
     * @param firstLine already read from br
     * @param fileName String; name of the file
     * @param desc to use of this file
     * @return Vector of StylesheetDatalets, or null if error
     */
    protected static Vector readXsltmarkFileList(Logger logger, BufferedReader br, 
                                                 String firstLine, String fileName, String desc)
    {
        Vector vec = new Vector();
        String line = firstLine;
        // Parse each line and build a datalet
        for (;;)
        {
            // If we're starting a section, parse the section to a datalet
            if (line.startsWith(XSLTMARK_CHAR))
            {
                StylesheetDatalet d = readXsltmarkDatalet(logger, br, line, fileName, desc);
                // Add datalet to our vector
                vec.addElement(d);
            }
            // Skip blank lines
            else if (line.length() == 0)
            {
                /* no-op */
            }
            // Ooops, readXsltmarkDatalet didn't work right
            else
            {
                logger.logMsg(Logger.WARNINGMSG, "readXsltmarkFileList parse error, unknown line: " 
                              + line);
            }

            // Read next line and loop
            try
            {
                line = br.readLine();
            }
            catch (IOException ioe2)
            {
                // Just force us out of the loop; if we've already 
                //  read part of the file, fine
                logger.logMsg(Logger.WARNINGMSG, "readXsltmarkFileList: " 
                              + fileName + " threw: " + ioe2.toString());
                break;
            }

            if (line == null)
                break;
        } // end of for (;;)
        return vec;
    }

    /**
     * Read in an xsltmark fileList specifying a list of files to test.
     * <p>File format is an .ini file</p>
     *
     * @param logger to report problems to
     * @param br BufferedReader to read from
     * @param firstLine already read from br
     * @param fileName String; name of the file
     * @param desc to use of this file
     * @return StylesheetDatalet with appropriate data, or null if error
     */
    private static StylesheetDatalet readXsltmarkDatalet(Logger logger, BufferedReader br, 
                                                String firstLine, String fileName, String desc)
    {
        final String STYLESHEET_MARKER = "stylesheet=";
        final String INPUT_MARKER = "input=";
        final String OUTPUT_MARKER = "output=";
        final String REFERENCE_MARKER = "reference=";
        final String ITERATIONS_MARKER = "iterations=";
        
        String line = firstLine;
        StylesheetDatalet d = new StylesheetDatalet();
        // Parse lines throughout the section to build the datalet
        for (;;)
        {
            // Each .ini file line starts with name of item to fill
            if (line.startsWith(STYLESHEET_MARKER))
            {
                d.inputName = line.substring(STYLESHEET_MARKER.length());
            } 
            else if (line.startsWith(INPUT_MARKER))
            {
                d.xmlName = line.substring(INPUT_MARKER.length());
            } 
            else if (line.startsWith(OUTPUT_MARKER))
            {
                d.outputName = line.substring(OUTPUT_MARKER.length());
            } 
            else if (line.startsWith(REFERENCE_MARKER))
            {
                d.goldName = line.substring(REFERENCE_MARKER.length());
            } 
            else if (line.startsWith(XSLTMARK_CHAR))
            {
                d.setDescription(line);
            } 
            else if (line.startsWith(ITERATIONS_MARKER))
            {
                d.options.put("iterations", line.substring(ITERATIONS_MARKER.length()));
            } 
            else if (line.length() == 0)
            {
                // Blank lines mean end-of-section; return datalet
                // This is the primary exit point for this method
                return d;
            }
            else
            {
                logger.logMsg(Logger.WARNINGMSG, "readXsltmarkDatalet, unknown line: " 
                              + line);
            }

            // Read next line and loop
            try
            {
                line = br.readLine();
            }
            catch (IOException ioe2)
            {
                // Just force us out of the loop; if we've already 
                //  read part of the file, fine
                logger.logMsg(Logger.WARNINGMSG, "readXsltmarkDatalet: " 
                              + fileName + " threw: " + ioe2.toString());
                break;
            }

            if (line == null)
                break;
        } // end of for (;;)
        logger.logMsg(Logger.ERRORMSG, "readXsltmarkDatalet: " + fileName
                      + " no data found!");
        return null;
    }
}
