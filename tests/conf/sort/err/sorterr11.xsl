<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: SORTerr11 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 10 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: If the select-expression in a sort isn't useful, should throw good error. -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="num">
      <xsl:sort select="document('sorterr11.dx')"/>
      <xsl:value-of select="."/><xsl:text> </xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>