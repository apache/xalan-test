<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr20 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.6 -->
  <!-- Purpose: Try to set same param twice inside a template, after setting via with-param. -->
  <!-- ExpectedException: Variable is already declared in this template -->
  <!-- Author: David Marston -->

<xsl:template match="doc">
  <out>
    <xsl:call-template name="secondary">
      <xsl:with-param name="a" select="'zero'"/><!-- Two sets of quotes make it a string -->
    </xsl:call-template>
  </out>
</xsl:template>

<xsl:template name="secondary">
  <xsl:param name="a" select="'first'" />
  <xsl:param name="a" select="'second'" />
  <xsl:value-of select="$a" />
</xsl:template>

</xsl:stylesheet>