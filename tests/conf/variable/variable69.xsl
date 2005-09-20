<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variable69 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.6 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Demonstrate various tests of nullness on global variables -->

<xsl:output method="xml" indent="no" encoding="UTF-8"/>

<xsl:variable name="NotSet"/>
<xsl:variable name="String" select="'string'"/>
<xsl:variable name="nString" select="''"/>
<xsl:variable name="Node" select="/doc/a"/>
<xsl:variable name="nNode" select="/doc/b"/><!-- No element of that name in source. -->

<xsl:template match="doc">
  <out>
    <xsl:text>NotSet: </xsl:text>
    <xsl:choose>
      <xsl:when test="$NotSet = ''"><xsl:text>equals empty</xsl:text></xsl:when>
      <xsl:when test="$NotSet != ''"><xsl:text>not empty</xsl:text></xsl:when>
      <xsl:otherwise><xsl:text>neither</xsl:text></xsl:otherwise>
    </xsl:choose>
    <xsl:text>
String: </xsl:text>
    <xsl:choose>
      <xsl:when test="$String = ''"><xsl:text>equals empty</xsl:text></xsl:when>
      <xsl:when test="$String != ''"><xsl:text>not empty</xsl:text></xsl:when>
      <xsl:otherwise><xsl:text>neither</xsl:text></xsl:otherwise>
    </xsl:choose>
    <xsl:text>
nString: </xsl:text>
    <xsl:choose>
      <xsl:when test="$nString = ''"><xsl:text>equals empty</xsl:text></xsl:when>
      <xsl:when test="$nString != ''"><xsl:text>not empty</xsl:text></xsl:when>
      <xsl:otherwise><xsl:text>neither</xsl:text></xsl:otherwise>
    </xsl:choose>
    <xsl:text>
Node: </xsl:text>
    <xsl:choose>
      <xsl:when test="$Node = ''"><xsl:text>equals empty</xsl:text></xsl:when>
      <xsl:when test="$Node != ''"><xsl:text>not empty</xsl:text></xsl:when>
      <xsl:otherwise><xsl:text>neither</xsl:text></xsl:otherwise>
    </xsl:choose>
    <xsl:text>
nNode: </xsl:text>
    <xsl:choose>
      <xsl:when test="$nNode = ''"><xsl:text>equals empty</xsl:text></xsl:when>
      <xsl:when test="$nNode != ''"><xsl:text>not empty</xsl:text></xsl:when>
      <xsl:otherwise><xsl:text>neither</xsl:text></xsl:otherwise>
    </xsl:choose>
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
