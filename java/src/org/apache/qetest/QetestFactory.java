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

import java.util.Properties;

/**
 * Factory constructor for various qetest-related objects.  
 *
 * Currently only supports finding an appropriate instance 
 * of a file-based CheckService.
 *
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public abstract class QetestFactory
{

    /** Constant denoting a default CheckService for Files.  */
    public static final String TYPE_FILES = "QetestFactory.FILECHECK";

    /** Default TYPE_FILES implementation class.  */
    public static final String DEFAULT_TYPE_FILES = "org.apache.qetest.xsl.XHTFileCheckService";

    /**
     * Get a new CheckService of a specified type.
     * 
     * Currently only supports the TYPE_FILES or your own FQCN.  
     *
     * @param type FQCN or constant type of CheckService needed
     * @return CheckService of the appropriate type; or a fallback 
     * type if not found and a fallback is available.
     */
    public static CheckService newCheckService(Logger logger, String type)
    {
        CheckService service = null;
        if (null == type)
        {
            logger.logMsg(Logger.ERRORMSG, "Warning: no type specified for newCheckService!");
            return null;
        }
        else if (TYPE_FILES.equals(type))
        {
            // Return our default impl
            Class fClazz = QetestUtils.testClassForName(DEFAULT_TYPE_FILES, null, null);
            if (null == fClazz)
            {
                logger.logMsg(Logger.ERRORMSG, "Warning: no default fileChecker is available: " + DEFAULT_TYPE_FILES);
                return null; //@todo should we throw exception instead?
            }

            try
            {
                service = (CheckService)fClazz.newInstance();
                //logger.logMsg(Logger.TRACEMSG, TYPE_FILES + " is " + service);
            } 
            catch (Exception e)
            {
                logger.logThrowable(Logger.ERRORMSG, e, "newCheckService(" + TYPE_FILES + ") threw");
            }
            return service;
        }
        else // Assume a classname of your impl
        {
            // Return our default impl
            Class fClazz = QetestUtils.testClassForName(type, QetestUtils.defaultPackages, null);
            if (null == fClazz)
            {
                logger.logMsg(Logger.ERRORMSG, "Warning: no fileChecker is available of type: " + type);
                return null; //@todo should we throw exception instead?
            }

            try
            {
                service = (CheckService)fClazz.newInstance();
                logger.logMsg(Logger.TRACEMSG, TYPE_FILES + " is " + service);
            } 
            catch (Exception e)
            {
                logger.logThrowable(Logger.ERRORMSG, e, "newCheckService(" + TYPE_FILES + ") threw");
            }
            return service;
        }
    }


    /**
     * Get a new Reporter with some defaults.
     * 
     * Will attempt to initialize the appropriate Reporter
     * depending on the options passed in; if all else fails, will 
     * return at least a ConsoleLogger.  
     *
     * @param options to create Reporter from
     * @return appropriate Reporter instance, or a default one.
     */
    public static Reporter newReporter(Properties options)
    {
        Reporter reporter = null;
        if (null == options)
        {
            // Return a default Reporter
            reporter = new Reporter(null);
            reporter.addDefaultLogger();  // add default logger automatically
            return reporter;
        }

        // Setup appropriate defaults for the Reporter
        // Ensure we have an XMLFileLogger if we have a logName
        String logF = options.getProperty(Logger.OPT_LOGFILE);

        if ((logF != null) && (!logF.equals("")))
        {

            // We should ensure there's an XMLFileReporter
            String r = options.getProperty(Reporter.OPT_LOGGERS);

            if (r == null)
            {
                // Create the property if needed...
                options.put(Reporter.OPT_LOGGERS,
                              "org.apache.qetest.XMLFileLogger");
            }
            else if (r.indexOf("XMLFileLogger") <= 0)
            {
                // ...otherwise append to existing list
                options.put(Reporter.OPT_LOGGERS,
                              r + Reporter.LOGGER_SEPARATOR
                              + "org.apache.qetest.XMLFileLogger");
            }
        }

        // Ensure we have a ConsoleLogger unless asked not to
        // @todo improve and document this feature
        String noDefault = options.getProperty("noDefaultReporter");

        if (noDefault == null)
        {

            // We should ensure there's an XMLFileReporter
            String r = options.getProperty(Reporter.OPT_LOGGERS);

            if (r == null)
            {
                options.put(Reporter.OPT_LOGGERS,
                              "org.apache.qetest.ConsoleLogger");
            }
            else if (r.indexOf("ConsoleLogger") <= 0)
            {
                options.put(Reporter.OPT_LOGGERS,
                              r + Reporter.LOGGER_SEPARATOR
                              + "org.apache.qetest.ConsoleLogger");
            }
        }

        // Pass our options directly to the reporter
        //  so it can use the same values in initialization
        // A Reporter will auto-initialize from the values
        //  in the properties block
        reporter = new Reporter(options);
        return reporter;
    }


    /**
     * Get a new Logger with some defaults.
     * 
     * Currently a redirect to call (Logger)newReporter(options).  
     *
     * @param options to create Logger from
     * @return appropriate Logger instance, or a default one.
     */
    public static Logger newLogger(Properties options)
    {
        return (Logger)newReporter(options);
    }


    /** Prevent instantiation - private constructor.  */
    private QetestFactory() { /* no-op */ }

}  // end of class CheckService

