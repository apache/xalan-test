<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Filename: EmbeddedRelative.xsl -->
  <xsl:template match="/list">
    <embedded-relative-level2>
      <xsl:apply-templates select="@*|node()"/>
    </embedded-relative-level2>
  </xsl:template>

  <xsl:template match="item">
      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
      </xsl:copy>
  </xsl:template>
     
</xsl:stylesheet>
