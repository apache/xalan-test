<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: SORTerr09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 10 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to use an AVT for the select value. -->
  <!-- ExpectedException: select value for xsl:sort cannot contain {} -->
  <!-- ExpectedException: Extra illegal tokens: '{.}' --><!-- Could use improvements for usability -sc -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="item">
      <xsl:sort select="{.}"/>
      <xsl:copy-of select="."/><xsl:text>
</xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
