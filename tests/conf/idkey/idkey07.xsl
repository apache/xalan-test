<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkey07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.4 Miscellaneous Additional Functions  -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of 'generate-id()' -->
  <!-- Should see 2 IDs, blank, 2 more IDs, and all IDs should be different.
       Exact strings will vary by processor. -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="generate-id(c)"/><xsl:text>,</xsl:text>
    <xsl:value-of select="generate-id(b)"/><xsl:text>,</xsl:text>
    <xsl:value-of select="generate-id(d)"/><xsl:text>,</xsl:text>
    <xsl:value-of select="generate-id(a)"/><xsl:text>,</xsl:text>
    <xsl:value-of select="generate-id()"/><xsl:text></xsl:text>
  </out>
</xsl:template>

</xsl:stylesheet>