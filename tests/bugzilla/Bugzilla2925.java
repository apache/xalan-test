/*
 * Covered by The Apache Software License, Version 1.1
 * See xml-xalan/License
 */

// Common Qetest / Xalan testing imports
import org.apache.qetest.Datalet;
import org.apache.qetest.Logger;
import org.apache.qetest.TestletImpl;
import org.apache.qetest.xsl.XHTFileCheckService;
import org.apache.qetest.CheckService;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.apache.xalan.templates.*;
import org.apache.xalan.extensions.*;
import org.apache.xalan.transformer.*;
import org.apache.xpath.*;
import org.apache.xpath.objects.*;

import java.io.File;

/**
 * Testlet for reproducing
 * <a href="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=2925">bug #2925</a>
 * @author scott_boag@lotus.com
 */
public class Bugzilla2925 extends TestletImpl
{

  // Initialize our classname for TestletImpl's main() method - must be updated!
  static
  {
    thisClassName = "Bugzilla2925";
  }

  /**
   * Write Minimal code to reproduce your Bugzilla bug report.
   * Many Bugzilla tests won't bother with a datalet; they'll
   * just have the data to reproduce the bug encoded by default.
   * @param d (optional) Datalet to use as data point for the test.
   *
   * NEEDSDOC @param datalet
   */
  public void execute(Datalet datalet)
  {

    // Use logger.logMsg(...) instead of System.out.println(...)
    logger.logMsg(Logger.STATUSMSG, "Reproducing Bugzilla#2925");

    try
    {
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer t = tf.newTransformer(new StreamSource("Bugzilla2925.xsl"));
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse("Bugzilla2925Params.xml");

      t.setParameter("stylesheets", doc.getDocumentElement());
      t.transform(new StreamSource("bugzilla2925.xml"),
                  new StreamResult("bugzilla2925.xsr"));

      // If we get here, attempt to validate the contents of 
      //  the outputFile created
      CheckService fileChecker = new XHTFileCheckService();

      if (Logger.PASS_RESULT
              != fileChecker.check(logger, new File("bugzilla2925.xsr"),
                                   new File("bugzilla2925.out"),
                                   getDescription())){}
    }
    catch (Exception e)
    {
      logger.checkFail(e.getMessage());
    }

    // Optional: use the Datalet d if supplied
    // Call code to reproduce the bug here
    // Call logger.checkFail("desc") (like Junit's assert(true, "desc")
    //  or logger.checkPass("desc")  (like Junit's assert(false, "desc")
    //  to report the actual bug fail/pass status
  }

  /**
   * <a href="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=2925">
   * Link to Bugzilla report</a>
   * @return "Parameter set from DOM Node, broken".
   */
  public String getDescription()
  {
    return "http://nagoya.apache.org/bugzilla/show_bug.cgi?id=2925";
  }
}  // end of class Bugzilla2925

