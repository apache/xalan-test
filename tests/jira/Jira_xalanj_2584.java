/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
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

/** TODO: Consider going back to an XML/XSL file pair in a -fail directory.
 * @author manoranjan_das@hotmail.com
 * @author shane_curcuru@lotus.com
 * @author jkesselm@apache.org
 */
public class Jira_xalanj_2584 extends TestletImpl
{
  // Initialize our classname for TestletImpl's main() method - must be updated!
  // TODO: Consider switching to retrieving this via reflection.
  static { thisClassName = "Jira_xalanj_2584"; }

  /**
   * <a href="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=4286">
   * Link to Jira report</a>
   * @return description of test (typically Jira issue name and brief description)
   */
  public String getDescription()
  {
    return "Jira issue XALANJ-2584 (EXSLT function Date:Date throws IndexOutOfBounds Exception on empty input)";
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
            + "      <xsl:element name='date'>\n"
            + "        <xsl:value-of select='date:date(root/@anAttribute)'/>\n"
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

    try
    {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(stylesheet) ));
      StringWriter stringWriter = new StringWriter();
      logger.logMsg(Logger.STATUSMSG, "About to transform document into StringWriter");
      transformer.transform(new StreamSource(new StringReader(document)), 
          new StreamResult(stringWriter));
      String result=stringWriter.toString();
      logger.logMsg(Logger.INFOMSG, "StringWriter is: " + result);
      // GONK: We really should validate the output, on principle.
      // GONK: ...This one could just be xsl/xml/out/gold, couldn't it?
      logger.checkPass("Transform completed and returned");
    }
    catch (Exception e)
    {
      logger.logThrowable(Logger.ERRORMSG, e, "Transform threw non-expected");
      logger.checkFail("Transform threw non-expected: " + e.toString());
    }
  }

}  // end of class
