<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: SORTerr05 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 10 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to put a child node inside xsl:sort. -->
  <!-- ExpectedException: xsl:comment is not allowed in this position in the stylesheet -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="num">
      <xsl:sort data-type="number">
        <xsl:comment>Can't do this here!</xsl:comment>
      </xsl:sort>
      <xsl:value-of select="."/><xsl:text> </xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
