<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkey43 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Use variables in match pattern in id() argument and predicate. -->

<xsl:strip-space elements="a b c"/>

<xsl:variable name="pick" select="'id2'"/>
<xsl:variable name="major" select="'b'"/>

<xsl:template match="/">
  <out>
    <xsl:apply-templates select="doc/a//text()"/>
    <xsl:text>&#10;</xsl:text>
  </out>
</xsl:template>

<xsl:template match="id($pick)/*[name()=$major]/text()" priority="2">
  <xsl:text>&#10;</xsl:text>
  <bee><xsl:value-of select="../@id"/></bee>
</xsl:template>

<xsl:template match="id($pick)//text()">
  <xsl:text>&#10;</xsl:text>
  <x><xsl:value-of select="../@id"/></x>
</xsl:template>

<xsl:template match="text()">
  <xsl:text>&#10;</xsl:text>
  <other><xsl:value-of select="."/></other>
</xsl:template>

</xsl:stylesheet>
