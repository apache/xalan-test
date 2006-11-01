<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: whitespace35 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.4 -->
  <!-- Creator: Maynard Demmons (maynard@organic.com) -->
  <!-- Purpose: Test for whitespace stripping from source documents retrieved
                with the document() function. The spec is a little vague on this. -->
  <!-- Elaboration: doc/* would contain several whitespace nodes if not stripped.
       The built-in template for text would cause them to be emitted. -->

<xsl:strip-space elements="*" />

<xsl:template match="/">
  <out>
    <xsl:apply-templates/>
    <xsl:apply-templates select="document('docws35.xml')/doc"/>
  </out>
</xsl:template>

<xsl:template match="element">
  <xsl:element name="{(.)}"/>
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
