<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters  -->
  <!-- Purpose: Test for xsl:param with both content and select. -->
  <!-- ExpectedException: xsl:param cannot have both content and select attribute. -->
  <!-- Author: David Marston -->

<xsl:param name="n" select="3">2</xsl:param>

<xsl:template match="doc">
  <out>
    <xsl:value-of select="item[$n]"/>
  </out>
</xsl:template>

</xsl:stylesheet>