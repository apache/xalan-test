<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: SORTerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 10 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Undefined value for data-type attribute on sort. -->
  <!-- ExpectedException: Sorting data-type must be "text" or "number" -->
  <!-- ExpectedException: Attribute: data-type has an illegal value: badtype -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="t">
      <xsl:sort data-type="badtype"/>
      <xsl:value-of select="."/><xsl:text>|</xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
