/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional STATUSrmation
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Common Qetest / Xalan testing imports
import org.apache.qetest.Datalet;
import org.apache.qetest.Logger;
import org.apache.qetest.TestletImpl;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.TimeZone;

/**
 * @author manoranjan_das@hotmail.com
 * @author shane_curcuru@lotus.com
 * @author jkesselm@apache.org
 */
public class Jira_xalanj_2623 extends TestletImpl
{
  // Initialize our classname for TestletImpl's main() method - must be updated!
  // TODO: Consider switching to retrieving this via reflection.
  static { thisClassName = "Jira_xalanj_2623"; }

  /**
   * <a href="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=4286">
   * Link to Jira report</a>
   * @return description of test (typically Jira issue name and brief description)
   */
  public String getDescription()
  {
    return "Jira issue XALANJ-2623 (dateTime() formatting minutes wrong for :30 offset).\n";
  }

  /**
   * @param d (optional) Datalet to use as data point for the test.
   */
  public void execute(Datalet d)
  {
    logger.logMsg(Logger.STATUSMSG, "Testing "+getDescription());
    String stylesheet=
        "<?xml version='1.0' encoding='UTF-8'?>\n"
            + "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'\n"
            + "                xmlns:date='http://exslt.org/dates-and-times'\n"
            + "                extension-element-prefixes='date'\n"
            + "                version='1.0'>\n"
            + "    <xsl:template match='/'>\n"
            + "      <xsl:element name='dateTime'>\n"
            + "        <xsl:value-of select='date:dateTime()'/>\n"
            + "      </xsl:element>\n"
            + "    </xsl:template>\n"
            + "</xsl:stylesheet>\n"
            ;
    String document=
        "<?xml version='1.0' encoding='UTF-8'?>\n"
            +"<root/>\n"
            ;
    logger.logMsg(Logger.INFOMSG,"stylesheet:\n"+stylesheet+'\n');
    logger.logMsg(Logger.INFOMSG,"document: \n"+document+'\n');

    // Formatting error appeared when timezone offset was not in full hours.
    // India Standard Time (+5:30) was the example given in the Jira Issue.
    // Temporarily set Java into that timezone for this test.
    // (I'm playing it safe and restoring the previous value before exiting.)
    //
    // Since I'm not sure I understand whether/how India implements DST or other
    // clock shifts, I'm explicitly requesting IST rather than Asia/Kolkata.
    String testzone="IST";
    logger.logMsg(Logger.INFOMSG,"Testing in timezone: '"+testzone+"'");
    TimeZone previousTimezone=TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone(testzone));

    try
    {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(stylesheet) ));
      StringWriter stringWriter = new StringWriter();
      logger.logMsg(Logger.INFOMSG, "About to transform document into StringWriter");
      transformer.transform(new StreamSource(new StringReader(document)), 
          new StreamResult(stringWriter));
      // Validate output, quick and dirty
      String result=stringWriter.toString();
      // Quick and dirty: Make sure output include
      if(result.indexOf("+05:30")<0)
      {
        logger.logMsg(Logger.STATUSMSG, "StringWriter is: " + result);
        logger.checkFail("Transform did not report time with +05:30 offset");
      } else {
        logger.checkPass("Transform completed with intended IST offset");
      }
    }
    catch (Exception e)
    {
      logger.logThrowable(Logger.ERRORMSG, e, "Transform threw non-expected");
      logger.checkFail("Transform threw non-expected: " + e.toString());
    }
    finally {
      logger.logMsg(Logger.INFOMSG,"Restoring previous timezone: "+previousTimezone.getDisplayName());
      TimeZone.setDefault(previousTimezone);
    }
  }

}  // end of class
