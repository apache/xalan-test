<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: POS13 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 4.1 -->
  <!-- Purpose: Test of 'last()' function. -->

<xsl:template match="doc">
  <out>
    <xsl:apply-templates select="p"/>
  </out>
</xsl:template>

<xsl:template match="p">
  <xsl:value-of select="last()=3"/><xsl:text>,</xsl:text>
</xsl:template>

</xsl:stylesheet>
