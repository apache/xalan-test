<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: axes68 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2 Axes-->
  <!-- Creator: David Marston -->
  <!-- Purpose: Check that namespace axis includes all namespaces in scope. -->

<xsl:template match="/">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="*">
  <xsl:text>Namespaces for </xsl:text><xsl:value-of select="name(.)"/><xsl:text>:
</xsl:text>
  <xsl:for-each select="namespace::*">
    <xsl:value-of select="name(.)"/><xsl:text>=</xsl:text><xsl:value-of select="."/><xsl:text>,&#010;</xsl:text>
  </xsl:for-each>
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
