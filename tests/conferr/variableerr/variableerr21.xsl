<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr21 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.6 Passing Parameters to Templates  -->
  <!-- Purpose: Test for xsl:with-param with both content and select. -->
  <!-- ExpectedException: xsl:with-param element must not have both content and a select attribute. -->
  <!-- Author: Richard Cao -->

<xsl:template match="doc">
  <xsl:call-template name="foo">
    <xsl:with-param name="bar" select="3">2</xsl:with-param>
  </xsl:call-template>
</xsl:template>

<xsl:template name="foo">
  <xsl:param name="bar" select="0"/>
  <out>
    <xsl:value-of select="$bar"/>
  </out>
</xsl:template>

</xsl:stylesheet>
