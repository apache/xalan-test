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
 * originally based on software copyright (c) 2000, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/*
 *
 * LoggingHandlerAPIJunit.java
 *
 */
package org.apache.qetest.qetesttest;

import org.apache.qetest.ConsoleLogger;
import org.apache.qetest.Logger;
import org.apache.qetest.LoggingHandler;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 * API Coverage test of LoggingHandler - very simplistic.  
 * 
 * <p>Note: to 'test the tests', so to speak, as an experiment 
 * I'm using the JUnit test framework to implement some tests 
 * of the base Qetest framework classes themselves.</p>
 * <p>You must download JUnit itself to compile/run this test, since 
 * it's not checked into the xml-xalan CVS see:
 * <a href="http://www.junit.org/">http://www.junit.org/</a></p>
 *
 * @author shane_curcuru@lotus.com
 * @version $Id$
 */
public class LoggingHandlerAPIJUnit extends TestCase
{
    /* Constructor calls superclass to run a named test.  */
    public LoggingHandlerAPIJUnit(String name)
    {
        super(name);
    }

    protected LoggingHandler m_loggingHandler = null;
    protected Logger m_logger = null;

    /* Testwide setup routine: creates a blank LoggingHandler.  */
    protected void setUp()
    {
        m_loggingHandler = new LoggingHandler();
        // How do you log out that the setUp went OK?
        assert((null != m_loggingHandler));
        m_logger = new ConsoleLogger();
        assert((null != m_logger));
    }

    /* Testwide cleanup routine: deletes blank LoggingHandler.  */
    protected void tearDown()
    {
        m_loggingHandler = null;
        m_logger = null;
    }

    /* Simple getCounters API coverage.  */
    public void testGetCounters()
    {
        int[] actual = m_loggingHandler.getCounters();
        int[] expected = LoggingHandler.NOTHING_HANDLED_CTR;
        assertEquals(actual[0], expected[0]);
        assertEquals(actual.length, expected.length);
    }

    /* Simple getLast API coverage.  */
    public void testGetLast()
    {
        assertEquals(m_loggingHandler.getLast(), LoggingHandler.NOTHING_HANDLED);
    }

    /* Simple getDescription API coverage.  */
    public void testGetDescription()
    {
        assertEquals(m_loggingHandler.getDescription(), "LoggingHandler: default implementation, does nothing");
    }

    /* Simple set/getDefaultHandler API coverage.  */
    public void testSetDefaultHandler()
    {
        String sampleObj = "sampleObj";
        assertNull(m_loggingHandler.getDefaultHandler());
        // This base class doesn't actually implement the setter
        try
        {
            m_loggingHandler.setDefaultHandler(sampleObj);
            fail("LoggingHandler.setDefaultHandler() should have thrown an exception");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(e.getMessage(), "LoggingHandler.setDefaultHandler() is unimplemented!");
        }
    }


    /* Basic set/getLogger API coverage.  */
    public void testSetGetLogger()
    {
        assertNull("getLogger null by default", m_loggingHandler.getLogger());
        m_loggingHandler.setLogger(null);
        Logger l = m_loggingHandler.getLogger();
        assertNotNull("getLogger not null after setLogger(null)", l);
        try
        {
            Class clazz = Class.forName(Logger.DEFAULT_LOGGER);
            assert("getLogger is correct default after setting to null", clazz.isInstance(l));
        }
        catch (ClassNotFoundException e)
        {
            fail("Can't load class " + Logger.DEFAULT_LOGGER + " threw: " + e.toString());
        }
        m_loggingHandler.setLogger(m_logger);
        assertEquals("set/getLogger coverage", m_logger, m_loggingHandler.getLogger());
    }

    /* Simple set/getLoggingLevel API coverage.  */
    public void testSetGetLoggingLevel()
    {
        assertEquals("default logging level correct", m_loggingHandler.getLoggingLevel(), Logger.DEFAULT_LOGGINGLEVEL);
        m_loggingHandler.setLoggingLevel(99);
        assertEquals("set/getLoggingLevel coverage", m_loggingHandler.getLoggingLevel(), 99);
    }

    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line args
     */
    public static void main(String[] args)
    {
        // Simply ask the framework to run us as-is
        junit.textui.TestRunner.run(LoggingHandlerAPIJUnit.class);
    }
}
