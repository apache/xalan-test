/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
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
import java.io.IOException;

/**
 * Utility class for reading buffered streams on a thread.
 *
 * <p>Common usage case:
 * <pre>
 * ThreadedStreamReader errReader = new ThreadedStreamReader();
 * Process p = Runtime.exec(cmdLine, environment);
 * errReader.setInputStream(
 *     new BufferedReader(
 *         new InputStreamReader(proc.getErrorStream()), bufSize));
 * errReader.start();
 * proc.waitFor();
 * errReader.join();
 * String sysErr = errReader.getBuffer().toString();
 * </pre>
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class ThreadedStreamReader extends Thread
{

    /** Reader we pull data from.  */
    BufferedReader is = null;

    /** Buffer we store the data in.  */
    StringBuffer sb = null;

    /**
     * Asks us to capture data from the provided stream.
     * @param set BufferedReader we should pull data from
     */
    public void setInputStream(BufferedReader set)
    {
        is = set;
    }

    /**
     * Implement Thread.run().  
     * This asks us to start reading data from our setInputStream()
     * and storing it in our getBuffer() area.  Note you probably 
     * should have called setInputStream() first.
     */
    public void run()
    {
        sb = new StringBuffer();
        sb.append(READER_START_MARKER);

        if (null == is)
        {
            sb.append("ERROR! setInputStream(null)");
            sb.append(READER_END_MARKER);
            return;
        }

        String i = null;
        try
        {
            i = is.readLine();
        }
        catch (IOException ioe1)
        {
            sb.append("ERROR! no data to read, threw: " + ioe1.toString());
            i = null;
        }

        while (i != null)
        {
            sb.append(i);
            // Note: also append the implicit newline that readLine grabbed
            sb.append('\n');

            try
            {
                i = is.readLine();
            }
            catch (IOException ioe2)
            {
                sb.append("WARNING! readLine() threw: " + ioe2.toString());
                i = null;
            }
        }
        sb.append(READER_END_MARKER);
    }

    /**
     * Get the buffer of data we've captured. 
     *
     * @return our internal StringBuffer; will be null if we 
     * have not been run yet.
     */
    public StringBuffer getBuffer()
    {
        return sb;
    }

    public static final String READER_START_MARKER = "<stream>\n";
    public static final String READER_END_MARKER = "</stream>\n";

}  // end of class ThreadedStreamReader

