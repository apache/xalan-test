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
 * originally based on software copyright (c) 2000, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/*
 *
 * LoggingEntityResolver.java
 *
 */
package org.apache.qetest.xsl;

import org.apache.qetest.*;

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//-------------------------------------------------------------------------

/**
 * Implementation of EntityResolver that logs all calls.
 * Currently just provides default service; returns null.
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class LoggingEntityResolver implements EntityResolver
{

    /** No-op ctor since it's often useful to have one. */
    public LoggingEntityResolver(){}

    /**
     * Ctor that calls setReporter automatically.  
     *
     * NEEDSDOC @param r
     */
    public LoggingEntityResolver(Reporter r)
    {
        setReporter(r);
    }

    /** Our Reporter, who we tell all our secrets to. */
    private Reporter reporter;

    /**
     * Accesor methods for our Reporter.  
     *
     * NEEDSDOC @param r
     */
    public void setReporter(Reporter r)
    {
        if (r != null)
            reporter = r;
    }

    /**
     * Accesor methods for our Reporter.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public Reporter getReporter()
    {
        return (reporter);
    }

    /** Prefixed to all reporter msg output. */
    private String prefix = "ER:";

    /** Counters for how many entities we've 'resolved'. */
    private int entityCtr = 0;

    /**
     * Accesor methods for entity counter.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public int getEntityCtr()
    {
        return entityCtr;
    }

    /**
     * Cheap-o string representation of our state.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String getCounterString()
    {
        return (prefix + "Entities: " + getEntityCtr());
    }

    /** Cheap-o string representation of last entity we resolved. */
    private String lastEntity = null;

    /**
     * NEEDSDOC Method setLastEntity 
     *
     *
     * NEEDSDOC @param s
     */
    protected void setLastEntity(String s)
    {
        lastEntity = s;
    }

    /**
     * Accessor for string representation of last entity we resolved.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public String getLastEntity()
    {
        return lastEntity;
    }

    /** What loggingLevel to use for reporter.logMsg(). */
    private int level = Reporter.DEFAULT_LOGGINGLEVEL;

    /**
     * Accesor methods; don't think it needs to be synchronized.  
     *
     * NEEDSDOC @param l
     */
    public void setLoggingLevel(int l)
    {
        level = l;
    }

    /**
     * Accesor methods; don't think it needs to be synchronized.  
     *
     * NEEDSDOC ($objectName$) @return
     */
    public int getLoggingLevel()
    {
        return level;
    }

    /**
     * Implement this method: just returns null for now.
     * Also saves the last entity for later retrieval, and counts
     * how many entities we've 'resolved' overall.
     * @todo have a settable property to actually return as the InputSource
     *
     * NEEDSDOC @param publicId
     * NEEDSDOC @param systemId
     *
     * NEEDSDOC ($objectName$) @return
     * @exception SAXException never thrown
     * @exception IOException never thrown
     */
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException
    {

        entityCtr++;

        setLastEntity(publicId + ";" + systemId);

        if (reporter != null)
        {
            reporter.logMsg(level,
                            prefix + getLastEntity() + " "
                            + getCounterString());
        }

        return null;
    }
}
