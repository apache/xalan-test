<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: STR134 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions -->
  <!-- Purpose: Test of default (no functions) conversion of decimal numbers with decimal point at all positions. -->

<xsl:template match="/">
  <out><xsl:text>Positive numbers:&#10;</xsl:text>
    <xsl:value-of select="9.87654321012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="98.7654321012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="987.654321012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="9876.54321012345"/><xsl:text>&#10;</xsl:text>
    <xsl:value-of select="98765.4321012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="987654.321012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="9876543.21012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="98765432.1012345"/><xsl:text>&#10;</xsl:text>
    <xsl:value-of select="987654321.012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="9876543210.12345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="98765432101.2345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="987654321012.345"/><xsl:text>&#10;</xsl:text>
    <xsl:value-of select="9876543210123.45"/><xsl:text>,</xsl:text>
    <xsl:value-of select="98765432101234.5"/><xsl:text>&#10;</xsl:text>
    <xsl:text>Negative numbers:&#10;</xsl:text>
    <xsl:value-of select="-9.87654321012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="-98.7654321012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="-987.654321012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="-9876.54321012345"/><xsl:text>&#10;</xsl:text>
    <xsl:value-of select="-98765.4321012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="-987654.321012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="-9876543.21012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="-98765432.1012345"/><xsl:text>&#10;</xsl:text>
    <xsl:value-of select="-987654321.012345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="-9876543210.12345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="-98765432101.2345"/><xsl:text>,</xsl:text>
    <xsl:value-of select="-987654321012.345"/><xsl:text>&#10;</xsl:text>
    <xsl:value-of select="-9876543210123.45"/><xsl:text>,</xsl:text>
    <xsl:value-of select="-98765432101234.5"/><xsl:text>&#10;</xsl:text>
  </out>
</xsl:template>

</xsl:stylesheet>
