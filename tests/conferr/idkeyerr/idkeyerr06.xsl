<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.2 -->
  <!-- Purpose: Test for two use attributes in xsl:key. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: Attribute "use" was already specified for element "xsl:key". -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: Attribute "use" was already specified for element "xsl:key". -->

<xsl:output indent="yes"/>

<xsl:key name="mykey" use="foo" match="div" use="title"/>

<xsl:template match="doc">
  <xsl:value-of select="key('mykey', 'Introduction')/p"/>
  <xsl:value-of select="key('mykey', 'Stylesheet Structure')/p"/>
  <xsl:value-of select="key('mykey', 'Expressions')/p"/>
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
