<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namespace137 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.2 Creating Elements -->
  <!-- Creator: Santiago Pericas-Geertsen -->
  <!-- Purpose: Test for resetting of an unspecified default namespace by copy-of. -->

<xsl:output method="xml" indent="no" encoding="UTF-8"/>

<xsl:template match = "/">
  <!-- create an RTF with no namespace nodes -->
  <xsl:variable name="x"><hello/></xsl:variable>
  <out>
    <xsl:element name="literalName" namespace="http://literalURI">
      <xsl:copy-of select="$x"/>
    </xsl:element>
  </out>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
