<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkey30 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.4 Miscellaneous Additional Functions  -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of 'generate-id()' on various kinds of nodes -->
  <!-- Results will vary by processor. -->

<xsl:template match="/doc">
  <out>
    <xsl:apply-templates select="*|@*|comment()|processing-instruction()|text()"/>
  </out>
</xsl:template>

<xsl:template match="*"><!-- for child elements -->
  <xsl:text>
  E(</xsl:text>
  <xsl:value-of select="name()"/>
  <xsl:text>):</xsl:text>
  <xsl:value-of select="generate-id()"/>
  <xsl:text> has </xsl:text>
  <xsl:apply-templates select="@*"/>
  <xsl:apply-templates/>
  <xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="@*">
  <xsl:text>A(</xsl:text>
  <xsl:value-of select="name()"/>
  <xsl:text>):</xsl:text>
  <xsl:value-of select="generate-id()"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="text()">
  <xsl:text>T:</xsl:text>
  <xsl:value-of select="generate-id()"/>
  <xsl:text>,</xsl:text>
</xsl:template>

<xsl:template match="comment()">
  <xsl:text>C:</xsl:text>
  <xsl:value-of select="generate-id()"/>
  <xsl:text>,</xsl:text>
</xsl:template>

<xsl:template match="processing-instruction()">
  <xsl:text>P(</xsl:text>
  <xsl:value-of select="name()"/>
  <xsl:text>):</xsl:text>
  <xsl:value-of select="generate-id()"/>
  <xsl:text>;</xsl:text>
</xsl:template>

</xsl:stylesheet>
