<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- FileName: imp26d -->
<!-- Purpose: Used indirectly by impincl26 -->
<!-- No template for middle -->

<xsl:template match="outer">
  <D>
    <xsl:value-of select="name(.)"/>
    <xsl:text> Switching to middle...
</xsl:text>
    <xsl:apply-templates select="middle"/>
  </D>
</xsl:template>

<xsl:template match="inner">
  <D>
    <xsl:value-of select="name(.)"/>
  </D>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
