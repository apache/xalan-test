<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: axes59 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2 Axes-->
  <!-- Creator: David Marston -->
  <!-- Purpose: Check the namespace axis. -->

<xsl:template match="/">
  <out>
    <xsl:text>&#010;</xsl:text>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="doc">
  <xsl:for-each select="namespace::*">
    <xsl:sort select="name(.)"/>
    <xsl:element name="{name(.)}"><xsl:value-of select="."/></xsl:element><xsl:text>,&#010;</xsl:text>
  </xsl:for-each>
</xsl:template>

<xsl:template match="text()"/><!-- To suppress empty lines -->

</xsl:stylesheet>
