/*
 * Covered by The Apache Software License, Version 1.1
 * See xml-xalan/java/License
 */

// explicitly packageless

import org.apache.qetest.CheckService;
import org.apache.qetest.Logger;
import org.apache.qetest.xsl.StylesheetDatalet;
import org.apache.qetest.xsl.TestableExtension;
import org.apache.qetest.xsl.XHTFileCheckService;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Hashtable;

/**
 * Extension for testing xml-xalan/samples/extensions.  
 */
public class javaBugzilla3722 extends TestableExtension
{
    static Hashtable counters = new Hashtable ();
    
    static private Logger extnLogger = null;

    /** Extension method from Bugzilla3722.  */
    public String dumpConfig(NodeList conf)
    {
        counter++;
        if (conf != null) 
        {
            for (int i=0; i<conf.getLength(); i++) 
            {
                Node node = conf.item(i);
                if (node!=null) 
                {
                    if (node.hasChildNodes())
                    {
                        // getLogger().debug("<" + node.getNodeName() + ">");
                        try 
                        {
                            // Below line throws DTMDOMException on CVS code 21-Sep-01
                            NodeList subList = node.getChildNodes();
                            this.dumpConfig(subList);
                        } 
                        catch (Exception e) 
                        {
                            extnLogger.logThrowable(Logger.ERRORMSG, e, "dumpConfig threw:");
                            extnLogger.checkFail("dumpConfig threw unexpected exception");
                        }
                    } else 
                    {
                        extnLogger.logMsg(Logger.INFOMSG, "<" + node.getNodeName() + "/>");
                    }
                }
            }
        }
        return "dumpConfig-done";
    }

    //// Implementations of TestableExtension
    /** Plain counter of number of times called.  */    
    private static int counter = 0;
    

    /** 
     * Perform and log any pre-transformation info.  
     * @return true if OK; false if any fatal error occoured
     * @param datalet Datalet of current stylesheet test
     */
    public static boolean preCheck(Logger logger, StylesheetDatalet datalet)
    {
        logger.logMsg(Logger.INFOMSG, "javaBugzilla3722.preCheck; counter=" + counter);
        extnLogger = logger;
        return true;        
    }
    

    /** 
     * Perform and log any post-transformation info.  
     * 
     * The extension should validate that it's extension was 
     * properly called; we also validate output file.
     * 
     * @param logger Logger to dump any info to
     * @param datalet Datalet of current stylesheet test
     */
    public static void postCheck(Logger logger, StylesheetDatalet datalet)
    {
        // Dump out our hashtable for user analysis
        logger.logHashtable(Logger.STATUSMSG, counters, "javaBugzilla3722.postCheck() counters");

        // Verify that we've been called at least once
        //@todo update to verify specific number of calls and hash entries
        if (counter > 0)
            logger.checkPass("javaBugzilla3722 has been called " + counter + " times");
        else
            logger.checkFail("javaBugzilla3722 has not been called");

        // We also validate the output file the normal way
        CheckService fileChecker = (CheckService)datalet.options.get("fileCheckerImpl");
        // Supply default value
        if (null == fileChecker)
            fileChecker = new XHTFileCheckService();
        if (Logger.PASS_RESULT
            != fileChecker.check(logger,
                                 new File(datalet.outputName), 
                                 new File(datalet.goldName), 
                                 "Extension test of " + datalet.getDescription())
           )
        {
            // Log a custom element with all the file refs first
            // Closely related to viewResults.xsl select='fileref"
            //@todo check that these links are valid when base 
            //  paths are either relative or absolute!
            Hashtable attrs = new Hashtable();
            attrs.put("idref", (new File(datalet.inputName)).getName());
            attrs.put("inputName", datalet.inputName);
            attrs.put("xmlName", datalet.xmlName);
            attrs.put("outputName", datalet.outputName);
            attrs.put("goldName", datalet.goldName);
            logger.logElement(Logger.STATUSMSG, "fileref", attrs, "Extension test file references");
        }
    }


    /**
     * Description of what this extension does.  
     * @return String description of extension
     */
    public static String getDescription()
    {
        return "Reproduce Bugzilla # 3722";
    }
}