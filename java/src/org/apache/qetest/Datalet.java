/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights 
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
 * Datalet.java
 *
 */
package org.apache.qetest;

import java.util.Hashtable;

/**
 * Minimal interface defining a datalet, a single set of data 
 * for a simple test case.
 * <p>A Datalet defines a single group of data that a matching 
 * Testlet needs to execute it's simple test case.  Normally,
 * Testlets and Datalets are matched.</p>
 *
 * <p>This makes creating data-driven tests simpler, by separating 
 * the test algorithim from the definition of the test data.  Note 
 * that logging what happened during the test is already separated 
 * out into the Logger interface.</p>
 *
 * <p>Normally Datalets will simply be collections of public members
 * that can be written or read by anyone.  An enclosing test can 
 * simply create or load a number of Datalets and then pass them 
 * to a Testlet to execute the whole bunch of them.</p>
 *
 * <p>One way to look at a Datalet is basically an intelligent 
 * Properties / Hashtable / InputSource, designed for a 
 * specific set of data.  They can be loaded from a variety of 
 * inputs and will intelligently create the right kind of data 
 * that their corresponding Testlet is expecting.
 * For example, and DOM-based InputSource Datalet might be 
 * loaded from a number of Strings denoting filenames or URLs.
 * The Datalet would then know how to parse each of the files 
 * into a DOM, which a Testlet could then use directly.</p>
 *
 * <p>//@todo what metaphor is best? I.e. Should Datalets break OO
 * paradigms and just have public members, or should they work more 
 * like a Hashtable with some Properties-like features thrown in?
 * Or: what are the most important 
 * things to optimize: syntactic sugar-like simplicity for callers; 
 * more functionality for different datatypes; ease of use by 
 * inexperienced coders (perhaps testers who are just learning Java);
 * ease of use by experienced coders?</p>
 * 
 *
 * <p>//@todo Should we add a getParameterInfo() method?</p>
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public interface Datalet
{

    /**
     * Accesor method for a brief description of this Datalet.  
     *
     * @return String describing the specific set of data 
     * this Datalet contains (can often be used as the description
     * of any check() calls made from the Testlet).
     */
    public abstract String getDescription();


    /**
     * Accesor method for a brief description of this Datalet.  
     * Datalets must have this as a read/write property, since many 
     * users will programmatically construct Datalets.
     *
     * @param s description to use for this Datalet.
     */
    public abstract void setDescription(String s);


    /**
     * Load fields of this Datalet from a Hashtable.
     * Note many datalets might take in a Properties block 
     * instead for simple String-valued data.
     *
     * @param Hashtable to load; Datalet should attempt to fill 
     * in as many member variables as possible from this, leaving 
     * any non-specified variables null or some default; behavior 
     * if null is passed is undefined
     */
    public abstract void load(Hashtable h);


    /**
     * Load fields of this Datalet from an array.
     * This allows most Datalets to be initialized from a main()
     * command line or the like.  For any String-valued types, or 
     * types that can easily be derived from a String, this makes 
     * running ad-hoc tests very easy.
     *
     * @param args array of Strings, as if from the command line;
     * Datalet should attempt to fill in as many member variables 
     * as possible from this, defining the order itself, leaving 
     * any non-specified variables null or some default; behavior 
     * if null is passed is undefined
     */
    public abstract void load(String[] args);

}  // end of class Datalet

