<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="doc">
  <out>
    <xsl:for-each select="item">
      <xsl:sort lang="en-US"/>
      <xsl:copy-of select="."/><xsl:text>
</xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
