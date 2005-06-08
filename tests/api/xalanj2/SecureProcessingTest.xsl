<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
xmlns:math="http://exslt.org/math">
  <xsl:template match="/">
    <xsl:value-of select="math:max(/doc/num)"/>
  </xsl:template>
</xsl:stylesheet>