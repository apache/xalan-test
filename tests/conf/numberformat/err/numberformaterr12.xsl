<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr12 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Set one-character attribute percent too large in decimal-format. -->
  <!-- ExpectedException: An XSLT attribute of type T_CHAR must be only 1 character -->

<xsl:decimal-format percent="toobig" />

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number('54.321','####.####toobig')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
