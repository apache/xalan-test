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

package org.apache.qetest;
import java.util.Properties;

/**
 * Interface with basic configuration API's.  
 *
 * A simple interface allowing configuration of testing utilities; 
 * generally by passing generic information to the testing 
 * utility and having it pass it to any underlying objects.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public interface Configurable
{
    /**
     * Allows the user to set specific attributes on the testing 
     * utility or it's underlying product object under test.
     * 
     * This method may or may not throw an IllegalArgumentException 
     * if an attribute that it does not recognize is passed in.  
     * This decision is up to the individual testing utility, and 
     * may include deciding to either trap underlying product 
     * exceptions for unknown attributes, or may re-throw them as 
     * IllegalArgumentExceptions.
     * 
     * Modeled after JAXP's various setAttribute()/setFeature() 
     * methods.
     * 
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     * @throws IllegalArgumentException thrown if the underlying
     * implementation doesn't recognize the attribute and wants to 
     * inform the user of this fact.
     */
    public void setAttribute(String name, Object value)
        throws IllegalArgumentException;


    /**
     * Allows the user to set specific attributes on the testing 
     * utility or it's underlying product object under test.
     * 
     * This method should attempt to set any applicable attributes 
     * found in the given attrs onto itself, and will ignore any and 
     * all attributes it does not recognize.  It should never 
     * throw exceptions.  This method may overwrite any previous 
     * attributes that were set.  Currently since this takes a 
     * Properties block you may only be able to set objects that 
     * are Strings, although individual implementations may 
     * attempt to use Hashtable.get() on only the local part.
     * 
     * @param attrs Props of various name, value attrs.
     */
    public void applyAttributes(Properties attrs);


    /**
     * Allows the user to retrieve specific attributes on the testing 
     * utility or it's underlying product object under test.
     *
     * @param name The name of the attribute.
     * @return value The value of the attribute.
     * @throws IllegalArgumentException thrown if the underlying
     * implementation doesn't recognize the attribute and wants to 
     * inform the user of this fact.
     */
    public Object getAttribute(String name)
        throws IllegalArgumentException;


    /**
     * Description of what this testing utility does.  
     * 
     * @return String description of extension
     */
    public String getDescription();

}  // end of class Configurable

