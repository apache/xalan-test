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

/**
 * Interface for 'check'ing (validating) equivalence of two items.
 *
 * Implementers provide their own algorithims for determining
 * equivalence, including custom algorithims for complex objects. 
 * CheckServices should log out both any pertinent information about 
 * the checking they've performed, then log out a checkPass or 
 * checkFail (or Ambg, etc.) record, as well as returning the 
 * appropriate result constant to the caller.
 *
 * The Configurable interface also allows callers to attempt to 
 * set attributes on either the CheckService or on any 
 * underlying validation algorithims.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public interface CheckService extends Configurable
{

    /**
     * Compare two objects for equivalence, and return appropriate result.
     * Implementers should provide the details of their "equals"
     * algorithim in getDescription().  They must also call the 
     * appropriate checkPass()/checkFail()/etc. method on the 
     * supplied Logger.
     * 
     * Note that the order of actual, reference is usually 
     * important in determining the result.
     * <li>Typically:
     * <ul>any unexpected Exceptions thrown -> ERRR_RESULT</ul>
     * <ul>actual does not exist -> FAIL_RESULT</ul>
     * <ul>reference does not exist -> AMBG_RESULT</ul>
     * <ul>actual is equivalent to reference -> PASS_RESULT</ul>
     * <ul>actual is not equivalent to reference -> FAIL_RESULT</ul>
     * </li>
     *
     * @param logger to dump any output messages to
     * @param actual (current) Object to check
     * @param reference (gold, or expected) Object to check against
     * @param description of what you're checking
     * @param msg comment to log out with this test point
     * @return Logger.*_RESULT code denoting status; each method may 
     * define it's own meanings for pass, fail, ambiguous, etc.
     */
    public int check(Logger logger, Object actual,
                     Object reference, String msg);

    /**
     * Compare two objects for equivalence, and return appropriate result.
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
    public int check(Logger logger, Object actual,
                     Object reference, String msg, String id);

    /**
     * Gets extended information about the last check call.
     * 
     * This is somewhat optional, and may be removed.  CheckServices 
     * should probably log out any additional info themselves to 
     * their logger before calling checkPass, etc., thus removing 
     * the need for callers to explicitly ask for this info.
     *
     * @return String describing any additional info about the last
     * two Objects that were checked, or null if none available
     */
    public String getExtendedInfo();

}  // end of class CheckService

