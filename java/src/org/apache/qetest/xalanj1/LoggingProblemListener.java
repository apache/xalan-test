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

/*
 *
 * LoggingProblemListener.java
 *
 */
package org.apache.qetest.xalanj1;

import org.apache.qetest.*;

import org.w3c.dom.Node;
import org.apache.xalan.xpath.xml.ProblemListener;

//-------------------------------------------------------------------------

/**
 * Cheap-o ProblemListener for use by API tests.
 * <p>Implements org.apache.xalan.xpath.xml.ProblemListener from
 * Xalan-J 1.x (and Xalan-J 2.x's compatibility layer).
 * Logs all info to our Logger; is settable as to when it halts.</p>
 * @todo try calling getLocator() and asking it for info directly
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class LoggingProblemListener implements ProblemListener
{

    /** No-op ctor seems useful. */
    public LoggingProblemListener(){}

    /**
     * Ctor that calls setLogger automatically.  
     * @param l Logger we should log to
     */
    public LoggingProblemListener(Logger l)
    {
        setLogger(l);
    }

    /** Our Logger, who we tell all our secrets to. */
    private Logger logger;

    /**
     * Accesor methods for our Logger.  
     * @param l Logger we should log to
     */
    public void setLogger(Logger l)
    {
        if (l != null)
            logger = l;
    }

    /**
     * Accesor methods for our Logger.  
     * @return Our Logger, who we tell all our secrets to
     */
    public Logger getLogger()
    {
        return logger;
    }

    /** Prefixed to all logger msg output. */
    private final String prefix = "LPL:";

    /** Constants determining our behavior w/r/t the XSLProcessor.  */
    public static final int DEFAULT_HALT = 0;   // Has a certain ring to it...
    /** Constants determining our behavior w/r/t the XSLProcessor.  */
    public static final int ALWAYS_HALT = 1;
    /** Constants determining our behavior w/r/t the XSLProcessor.  */
    public static final int NEVER_HALT = 2;

    /** If we should tell the XSLProcessor to halt on error.  */
    private int haltOnError = DEFAULT_HALT;
    
    /** 
     * Accesor methods for when we should halt.  
     * @param h when we should halt: DEFAULT_HALT|ALWAYS_HALT|NEVER_HALT
     */
    public void setHaltOnError(int h)
    {
        haltOnError = h;
    }

    /** 
     * Accesor methods for when we should halt.  
     * @return when we should halt: DEFAULT_HALT|ALWAYS_HALT|NEVER_HALT
     */
    public int getHaltOnError()
    {
        return haltOnError;
    }

    /** Counters for how many problems or messages we've processed.  */	
	private int problemCtr = 0;

    /** Counters for how many problems or messages we've processed.  */	
	private int messageCtr = 0;

    /** 
     * Accesor methods for counter incremented when problem() called.  
     * @return number of times problem() was called
     */
    public int getProblemCtr()
    {
        return problemCtr;
    }

    /** 
     * Accesor methods for counter incremented when problem() called.  
     * @return number of times problem() was called
     */
    public int getMessageCtr()
    {
        return messageCtr;
    }

    /** Cheap-o string representation of our state.  */
    public String getCounterString()
    {
        return("Problems: " + getProblemCtr() + ", Messages: " + getMessageCtr());
    }
	
    /** What loggingLevel to use for logger.logMsg().  */
    private int level = Logger.DEFAULT_LOGGINGLEVEL;
    
    /** 
     * Accesor methods for loggingLevel.  
     * @param l level for us to log at
     */
    public void setLoggingLevel(int l)
    {
        level = l;
    }

    /** 
     * Accesor methods for loggingLevel.  
     * @return level for us to log at
     */
    public int getLoggingLevel()
    {
        return level;
    }

    /** If we're expecting an error to come up (if so, then log a pass).  */
    public boolean expectProblem = false;

    /** 
     * If we're expecting an error to come up (if so, then log a pass).  
     * Note that we never re-set this, so it's up to the user to 
     * manage setting this when errors are/aren't expected.
     * @param b true if we're expecting an error, false otherwise
     */
    public void setExpectProblem(boolean b)
    {
        expectProblem = b;
    }

    /** 
     * If we're expecting an error to come up (if so, then log a pass).  
     * Note that we never re-set this, so it's up to the user to 
     * manage setting this when errors are/aren't expected.
     * @return true if we're expecting an error, false otherwise
     */
    public boolean getExpectProblem()
    {
        return expectProblem;
    }

    /** Constants copied from ProblemListenerDefault.  */
    public short expectWhere = 0;   // 0 (none) or XMLPARSER, XSLPROCESSOR, QUERYENGINE, XPATHPROCESSOR, or XPATHPARSER
    /** Constants copied from ProblemListenerDefault.  */
    public short expectClassification = 0;   // 0 (none) or WARNING or ERROR

    /** Constants copied from ProblemListenerDefault.  */
    public String errorHeader = "Error: ";
    /** Constants copied from ProblemListenerDefault.  */
    public String warningHeader = "Warning: ";
    /** Constants copied from ProblemListenerDefault.  */
    public String messageHeader = "";

    /** Constants copied from ProblemListenerDefault.  */
    public String xslHeader = "XSL ";
    /** Constants copied from ProblemListenerDefault.  */
    public String xmlHeader = "XML ";
    /** Constants copied from ProblemListenerDefault.  */
    public String queryHeader = "PATTERN ";
    
  /**
   * Testing implementation of a function that is called when a problem event occurs.  
   * <P>Javadoc copied from ProblemListener.</P>
   * <P>The testing implementation logs out info to a logger, which 
   * a user can analyze the results from later.</P>
   * 
   * @param   where             Either and XMLPARSER, XSLPROCESSOR, or QUERYENGINE.
   * @param   classification    Either ERROR or WARNING.
   * @param   styleNode         The style tree node where the problem
   *                            occurred.  May be null.
   * @param   sourceNode        The source tree node where the problem
   *                            occurred.  May be null.
   * @param   msg               A string message explaining the problem.
   * @param   lineNo            The line number where the problem occurred,  
   *                            if it is known. May be zero.
   * @param   charOffset        The character offset where the problem,  
   *                            occurred if it is known. May be zero.
   * 
   * @return  true if the return is an ERROR, in which case
   *          exception will be thrown.  Otherwise the processor will 
   *          continue to process.
   */
    public boolean problem(short where, short classification, 
                            Object styleNode, Node sourceNode,
                            String msg, String id, int lineNo, int charOffset)
    {
        problemCtr++;
        if(null != logger)
        {
            synchronized(this)
            {    
                // Yes, Virginia, this is essentially the same as ProblemListenerDefault
                logger.logMsg(level, prefix 
                                + ((XMLPARSER == where) ? xmlHeader : (QUERYENGINE == where) ? queryHeader : xslHeader)
                                + ((ERROR == classification) ? errorHeader : (WARNING == classification) ? warningHeader : messageHeader)
                                + msg
                                + ((null == styleNode)? "" : (", style tree node: "+styleNode.toString())) 
                                + ((null == sourceNode)? "" : (", source tree node: "+sourceNode.getNodeName()))
                                + ((null == id)? "" : (", Location "+id))
                                + ((0 == lineNo)? "" : (", line "+lineNo))
                                + ((0 == charOffset)? "" : (", offset "+charOffset)));
                if (expectProblem)
                {
                    // Try to validate the expectedProblem we got
                    logger.checkPass(prefix + "Problem when expected (see previous logMsg: not validated)");
                }
                else
                {
                    // User didn't expect a problem, so fail
                    logger.checkFail(prefix + "Problem when none expected (see previous logMsg)");
                }
            }
        }
        switch (haltOnError)
        {
            case DEFAULT_HALT:
                return classification == ERROR;
            case ALWAYS_HALT:
                return true;
            case NEVER_HALT:
                return false;
            default:
                return classification == ERROR;
        }
    }
    
    /**
     * Function that is called to issue a message.
     * Note: Needs better updating for logging!
     * @param msg A string message to output.
     */
    public boolean message(String msg)
    {
        messageCtr++;
	    if(null != logger)
        {    
            synchronized (this)
            {    
                logger.logMsg(level, prefix + msg);
            } 
        }
        return false; // we don't know this is an error 
    }
}
