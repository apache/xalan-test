<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Filename: EmbeddedType.xsl -->
<!-- Explicitly only matches on /list so we don't get top-level PI's -->
  <xsl:template match="/list">
    <embedded-type>
      <xsl:apply-templates select="item"/>
    </embedded-type>
  </xsl:template>

  <xsl:template match="item">
    <xsl:copy-of select="."/>
  </xsl:template>
     
</xsl:stylesheet>
