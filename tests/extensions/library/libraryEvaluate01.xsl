<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    exclude-result-prefixes="xalan">

  <!-- FileName: libraryEvaluate01.xsl -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Creator: Shane Curcuru -->
  <!-- Purpose: Basic test of evaluate() extension function -->

<xsl:param name="param1">sum(item)</xsl:param>

<xsl:template match="doc">
  <out>
    <sum-expr>
      <xsl:value-of select="$param1"/>
    </sum-expr>
    <xsl:apply-templates select="list"/>
  </out>
</xsl:template>

<xsl:template match="list">
    <xsl:element name="listout">
      <xsl:attribute name="sum-function">
        <xsl:value-of select="sum(item)"/>
      </xsl:attribute>
      <xsl:attribute name="sum-evaluate">
        <!-- Use string() to force the variable to be a string to be evaluated -->
        <xsl:value-of select="xalan:evaluate( string($param1) )"/>
      </xsl:attribute>
    <xsl:apply-templates select="list"/>
    </xsl:element>
</xsl:template>
</xsl:stylesheet>