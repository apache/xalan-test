/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights 
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

package org.apache.qetest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Simple services for getting lists of FileDatalets.
 * 
 * <p>Provides static worker methods for reading 
 * {@link FileTestletDriver#OPT_FILELIST -fileList} lists 
 * of input/output/gold files and creating lists of 
 * corresponding FileDatalets from them.  We provide the logic 
 * for reading the actual fileList line-by-line; the 
 * {@link FileDatalet} class provides the logic for initializing 
 * itself from a line of text.</p>
 *
 * @see FileTestletDriver
 * @see FileDatalet
 * @author shane_curcuru@us.ibm.com
 * @version $Id$
 */
public abstract class FileDataletManager // provide static services only
{

    /** '#' character, comment char in qetest fileList.  */
    public static final String QETEST_COMMENT_CHAR = "#";


    /**
     * Read in a file specifying a list of files to test.  
     *
     * <p>File format is fixed to be a qetest-style fileList; 
     * optional worker methods may allow other formats.  
     * Essentially we simply read the file line-by-line and 
     * create a FileDatalet from each line.</p>
     *
     * @param logger to report problems to
     * @param fileName String; name of the file
     * @param desc description; caller's copy changed
     * @param defaults default properties to potentially add to each datalet
     * @return Vector of FileDatalets; null if error
     */
    public static Vector readFileList(Logger logger, String fileName, 
            String desc, Properties defaults)
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
        Vector vec = null;
        if (line.startsWith(QETEST_COMMENT_CHAR))
        {
            // This is a native qetest style file
            vec = readQetestFileList(logger, br, line, fileName, desc, defaults);
        }
        else
        {
            //@todo: add a worker method that allows users to plug 
            //  in their own fileList reader that creates Datalets
            logger.logMsg(Logger.WARNINGMSG, "readFileList: " + fileName
                          + " could not determine file type; assuming qetest!");
            vec = readQetestFileList(logger, br, line, fileName, desc, defaults);
        }

        if ((null == vec) 
            || (vec.size() == 0))
        {
            logger.logMsg(Logger.ERRORMSG, "readFileList: " + fileName
                          + " did not have any non-comment lines!");
            // Explicitly return null so caller knows we had error
            return null;
        }
        return vec;
    }
    

    /**
     * Read in a qetest fileList specifying a list of files to test.
     *
     * <p>File format is pretty simple:</p>
     * <ul>
     * <li># first line of comments is copied into desc</li>
     * <li># beginning a line is a comment</li>
     * <li># rest of lines are whitespace delimited filenames and options</li>
     * <li>inputName outName goldName [options...]</li>
     * <li><b>Note:</b> see {@link FileDatalet} for
     * details on how the file lines are parsed!</li>
     * </ul>
     *
     * @param logger to report problems to
     * @param br BufferedReader to read from
     * @param firstLine already read from br
     * @param fileName String; name of the file
     * @param desc to use of this file
     * @param defaults default properties to potentially add to each datalet
     * @return Vector of FileDatalets, or null if error
     */
    protected static Vector readQetestFileList(Logger logger, BufferedReader br, 
                                               String firstLine, String fileName, 
                                               String desc, Properties defaults)
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
                logger.logMsg(Logger.ERRORMSG, "readQetestFileList-desc: " 
                              + fileName + " threw: " + ioe.toString());
                return null;
            }
        }

        // Load each line into a FileDatalet
        for (;;)
        {
            // Skip any lines beginning with # comment char or that are blank
            if ((!line.startsWith(QETEST_COMMENT_CHAR)) && (line.length() > 0))
            {
                // Create a Datalet and initialize with the line's 
                //  contents and default properties
                FileDatalet d = new FileDatalet(line, defaults);

                // Also pass over the global runId, if set
                //@todo this should be standardized and removed; 
                //  or at least have global constants for it
                d.getOptions().put("runId", defaults.getProperty("runId"));

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
                logger.logMsg(Logger.WARNINGMSG, "readQetestFileList-body: " 
                              + fileName + " threw: " + ioe2.toString());
                break;
            }

            if (line == null)
                break;
        } // end of for (;;)

        // Return our Vector of created datalets
        return vec;
    }

}
