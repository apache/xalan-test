/*
 * Covered by The Apache Software License, Version 1.1
 * See xml-xalan/License
 */
// Common Qetest / Xalan testing imports
import org.apache.qetest.Datalet;
import org.apache.qetest.Logger;
import org.apache.qetest.TestletImpl;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.apache.xalan.stree.DocumentImpl;
import org.apache.xalan.extensions.ExpressionContext;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.io.File;

/**
 * Testlet for reproducing Bugzilla reported bugs.
 * @author rylsky@hotmail.com (Vladimir Rylsky)
 * @author shane_curcuru@lotus.com
 */
public class Bugzilla1288 extends TestletImpl
{
    // Initialize our classname for TestletImpl's main() method - must be updated!
    static { thisClassName = "Bugzilla1288"; }

    /**
     * @param d (optional) Datalet to use as data point for the test.
     */
    public void execute(Datalet d)
	{
        // Use logger.logMsg(...) instead of System.out.println(...)
        logger.logMsg(Logger.STATUSMSG, "Reproducing Bugzilla#1288 TreeWalker.traverse goes to infinite loop (with extension)");
        // Just transform the stylesheet:
        logger.logMsg(Logger.CRITICALMSG, "WARNING! THIS TEST MAY HANG! (i.e. don't run in automation)");
        try
        {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Source transformerSource = new StreamSource(new File("Bugzilla1288.xsl"));
            Transformer transformer = transformerFactory.newTransformer(transformerSource);
            Source input = new StreamSource(new File("identity.xml"));
            Result output = new StreamResult(new File("Bugzilla1288.out"));
            logger.logMsg(Logger.STATUSMSG, "About to transform error.xml into Bugzilla1288.out");
            transformer.transform(input, output);
            logger.checkPass("Transform completed and returned (crash test)");
            logger.logMsg(Logger.STATUSMSG, "To-do: validate output!");
        }
        catch (Exception e)
        {
            logger.checkFail("Transform threw: " + e.toString());
            logger.logThrowable(Logger.ERRORMSG, e, "Transform threw");
        }
        logger.checkAmbiguous("Bug occours now: system hangs");
	}

    /**
     * <a href="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=1288">
     * Link to Bugzilla report</a>
     * @return TreeWalker.traverse goes to infinite loop (with extension).
     */
    public String getDescription()
    {
        return "TreeWalker.traverse goes to infinite loop (with extension)";
    }

    /* Extension function used in stylesheet */
    public Node run(ExpressionContext processor, Node context)
    {
      Document x_doc = null;
      Element n_tool;

      // Note must call public constructor! -sc
      x_doc = new DocumentImpl(1024);

      n_tool = (Element)x_doc.appendChild(x_doc.createElement("TOOL_NAME"));
      n_tool.setAttribute("date", "date-string-here" /* new Date().toString() */);

      Node n_result = n_tool.appendChild(x_doc.createElement("result"));

      if (context != null)
      {
        try
        {
          Transformer copier = TransformerFactory.newInstance().newTransformer();
          copier.transform(new DOMSource(n_tool), new DOMResult(context));
        }
        catch (TransformerException ex)
        {
          ex.printStackTrace();
        }
      }

      return n_tool;
     }    
}  // end of class Bugzilla1288
