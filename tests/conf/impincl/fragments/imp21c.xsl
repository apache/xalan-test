<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- FileName: imp21c -->
<!-- Purpose: Used indirectly by impincl21 and impincl25 -->

<xsl:template match="foo">
  <C>
    <xsl:value-of select="name(.)"/>
    <xsl:apply-templates select="bar"/>
  </C>
</xsl:template>

<xsl:template match="goo">
  <C>
    <xsl:value-of select="name(.)"/>
    <xsl:apply-templates select="bar"/>
  </C>
</xsl:template>

<xsl:template match="bar">
  <xsl:text> - match on bar in imp21c.xsl</xsl:text>
</xsl:template>

<xsl:template match="blob"><!-- no such node -->
  <xsl:apply-imports/>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
