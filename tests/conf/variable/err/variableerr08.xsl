<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr08 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.5 -->
  <!-- Purpose: Put xsl:param somewhere other than first in a template. -->
  <!-- ExpectedException: xsl:param elements must be first inside xsl:template -->
  <!-- Author: David Marston -->

<xsl:template match="doc">
  <out>
    <xsl:text>Output before setting param1...</xsl:text>
    <xsl:param name="param1" select="item" />
    <xsl:value-of select="$param1"/>
  </out>
</xsl:template>

</xsl:stylesheet>