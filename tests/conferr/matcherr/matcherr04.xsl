<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MATCHerr04 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.4 -->
  <!-- Purpose: Put content other than sort or param inside apply-templates. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: ElemTemplateElement error: Can not add #text to xsl:apply-templates -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: ElemTemplateElement error: Can not add #text to xsl:apply-templates -->
  <!-- ExpectedException: xsl:text is not allowed in this position in the stylesheet! -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="title"/><xsl:text>
</xsl:text>
    <xsl:apply-templates select="title">
      <xsl:sort select="."/>
      <xsl:text>This should not be inside apply-templates</xsl:text>
    </xsl:apply-templates>
  </out>
</xsl:template>

<xsl:template match="text()"><!-- To suppress empty lines --><xsl:apply-templates/></xsl:template>

<xsl:template match="*[@title]">
  <xsl:text>Found a node
</xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="p">
  <xsl:text>Found a P node; there should not be one!
</xsl:text>
  <xsl:apply-templates/>
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
