<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: SORTerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 10 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Incorrect value for order attribute on sort. -->
  <!-- ExpectedException: Sorting order must be "ascending" or "descending" -->
  <!-- ExpectedException: Attribute: order has an illegal value: sideways -->

<xsl:template match="doc">
  <out>
    Sideways order....
    <xsl:for-each select="num">
      <xsl:sort data-type="number" order="sideways"/>
      <xsl:value-of select="."/><xsl:text> </xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
