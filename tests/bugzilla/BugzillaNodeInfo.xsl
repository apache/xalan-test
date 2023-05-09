<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:java="http://xml.apache.org/xslt/java"
                xmlns:xalan="http://xml.apache.org/xalan"
                exclude-result-prefixes="java">

  <!-- FileName: javaNodeInfo01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 14 Extensions -->
  <!-- Purpose: Testing Xalan custom extension "NodeInfo", implemented in lib/NodeInfo.java. -->
 
  <!-- jkesselm May 2023: What this test seems to be demonstrating is that the
       custom TrAX property SOURCE_LOCATION, which records the data needed
       for NodeInfo to report node location, is not implemented for
       Temporary Trees filled from stylesheet literal content. One can debate
       whether we should be attempting to do so.

       I would recommend considering this a Feature Request rather than a bug,
       unless the definition of "http://xml.apache.org/xalan/features/source_location"
       says otherwise. (There is not currently a documentation page at that
       URI; we'd need to dig into Xalan docs to establish this.)
  -->

  <xsl:strip-space elements="*"/>
  <xsl:output indent="yes"/>
             
<xsl:template match="/">
  <out>
    <xsl:variable name="rtf">
      <docelem>
        <elem1/>
        <elem2>
          <elem3>content</elem3>
        </elem2>
      </docelem>
    </xsl:variable>
    <global>
      <xsl:element name="lineNumber">
        <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.lineNumber()"/>
      </xsl:element>
      <xsl:element name="columnNumber">
        <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.columnNumber()"/>
      </xsl:element>
      <xsl:element name="systemId">
        <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.systemId()"/>
      </xsl:element>
      <xsl:element name="publicId">
        <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.publicId()"/>
      </xsl:element>
    </global>
    <rtf>
      <!-- From the documentation:
	  NOTE: Xalan does not normally record location information
	  for each node.  To obtain it, you must set the custom TrAX
	  attribute
	  "http://xml.apache.org/xalan/features/source_location" true
	  in the TransformerFactory before generating the Transformer
	  and executing the stylesheet. Storage cost per node will be
	  noticably increased in this mode.

	When unknown, returns -1.

	The BugzillaNodeInfo.java driver *does* set this property.
	However, the node in question is coming from a
	stylesheet-literal element in a Temporary Tree rather than the
	input document, and it appears we have not implemented this
	feature for this case.
      -->
      <xsl:element name="lineNumber">
        <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.lineNumber((xalan:nodeset($rtf)))"/>
      </xsl:element>
      <xsl:element name="columnNumber">
        <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.columnNumber((xalan:nodeset($rtf)))"/>
      </xsl:element>

      <!-- SystemID of a nodeset is that of the first node in the set.
	   See above re location information of Temporary Trees; it appears
	   we have not implemented that.
      -->
      <xsl:element name="systemId">
        <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.systemId((xalan:nodeset($rtf)))"/>
      </xsl:element>

      <!-- Xalan does not currently record publicID. The function returns
	   null
      -->
      <xsl:element name="publicId">
        <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.publicId((xalan:nodeset($rtf)))"/>
      </xsl:element>
    </rtf>
    <elems>
      <xsl:apply-templates />
    </elems>
  </out>
</xsl:template>
<xsl:template match="elem | subelem">
  <elem>
    <xsl:element name="lineNumber">
      <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.lineNumber(.)"/>
    </xsl:element>
    <xsl:element name="columnNumber">
      <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.columnNumber(.)"/>
    </xsl:element>
    <xsl:element name="systemId">
      <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.systemId(.)"/>
    </xsl:element>
    <xsl:element name="publicId">
      <xsl:value-of select="java:org.apache.xalan.lib.NodeInfo.publicId(.)"/>
    </xsl:element>
  </elem>
  <xsl:apply-templates />
</xsl:template>
  

  <!--
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
  -->

</xsl:stylesheet>
