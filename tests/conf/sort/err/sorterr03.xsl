<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: SORTerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 10 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Incorrect value for case-order attribute on sort. -->
  <!-- ExpectedException: Sorting case-order must be "upper-first" or "lower-first" -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="item">
      <xsl:sort lang="en-US" case-order="bad-order"/>
      <xsl:copy-of select="."/><xsl:text>
</xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
