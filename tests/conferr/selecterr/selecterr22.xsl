<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr22 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.1 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try '//' followed by a + as a function argument. -->
  <!-- ExpectedException: Abbreviation '//' must be followed by a step expression -->
  <!-- ExpectedException: Unexpected token -->

<xsl:template match="doc">
  <out>
    <xsl:apply-templates select="foo"/>
  </out>
</xsl:template>

<xsl:template match="foo">
  <subs><xsl:value-of select="//+17"/></subs>
</xsl:template>

</xsl:stylesheet>