<?xml version="1.0"?>
<!-- Suppress branches without text content
  -->

<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
>

 <xsl:template match="* [.//text()] | text() ">
  <xsl:copy>
    <xsl:apply-templates />
  </xsl:copy>
 </xsl:template>

 <xsl:template match="/">
  <xsl:apply-templates /> 
 </xsl:template>

</xsl:stylesheet>

