<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: SORTerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 10 -->
  <!-- Purpose: Undefined attribute on sort. -->
  <!-- ExpectedException: xsl:sort has an illegal attribute: invalidattr -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="t">
      <xsl:sort invalidattr="badvalue"/>
      <xsl:value-of select="."/><xsl:text>|</xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
