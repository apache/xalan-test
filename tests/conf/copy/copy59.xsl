<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: copy59 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.3 Using Values of Variables & Parameters with xsl:copy-of. -->
  <!-- Creator: David Bertoni -->
  <!-- Purpose: Use copy-of to put a node-set and RTF in a comment, where some members are text nodes. -->
  <!-- Invalid nodes (non-text) and their content should be ignored. -->

  <!-- Per XSLT 1.0 section 7.4, emphasis mine: "It is an error if
       instantiating the content of xsl:comment creates nodes other
       than text nodes. An XSLT processor may signal the error; if it
       does not signal the error, it must recover by ignoring the
       offending nodes TOGETHER WITH THEIR CONTENT." In other words,
       simply using string-value of the content is *not* correct. AS
       OF VERSION 2.7.3, XALAN IS GETTING THIS WRONG. -->

<xsl:output method="xml" indent="no" encoding="UTF-8"/>

<xsl:variable name="rTreeFrag">
  <xsl:copy-of select="/"/>
</xsl:variable>

<xsl:template match="doc">
  <out>
    <xsl:comment>
      <xsl:copy-of select="node()"/>
    </xsl:comment>
    <xsl:comment>
      <xsl:text> </xsl:text><!-- to separate delimiters -->
      <xsl:copy-of select="$rTreeFrag"/>
    </xsl:comment>
  </out>
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
