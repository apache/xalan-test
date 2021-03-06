<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: impincl12 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.6 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Show that apply-imports really means imports, not includes -->

<xsl:template match="/">
  <out><xsl:apply-templates select="doc/*" /></out>
</xsl:template>

<xsl:template match="author">
  MAIN author: <xsl:value-of select="."/>
</xsl:template>

<!-- INCLUDES can be anywhere and are positionally significant -->
<xsl:include href="c.xsl"/>

<xsl:template match="title"><!-- This has import precedence over e.xsl, plus it's last -->
  MAIN title: <xsl:value-of select="."/>
  <xsl:apply-imports/>
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
