<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conflictres04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.6 -->
  <!-- Purpose: Test for nodetest override of default priority. Also, node selected is attribute instead of element. -->
  <!-- Creator: Paul Dick -->
  <!-- No conflict warnings should be seen. -->

<xsl:template match="doc" priority="10">
  <out>
    <xsl:apply-templates select="foo/@test"/>
  </out>
</xsl:template>

<xsl:template match="foo">
  <xsl:text>Match-of-qualified-name</xsl:text>
</xsl:template>

<xsl:template match="*">
  <xsl:text>Match-of-wildcard</xsl:text>
</xsl:template>

<xsl:template match="node()" priority="1">
  <xsl:text>Match-of-node-type</xsl:text>
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
