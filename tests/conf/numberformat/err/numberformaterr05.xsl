<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr05 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Set one-character attribute grouping-separator too large in decimal-format. -->
  <!-- ExpectedException: Invalid attribute on xsl:decimal-format. -->

<xsl:decimal-format grouping-separator="toobig" />

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number('7654321','#############')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
