<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr11 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Set one-character attribute decimal-separator too large in decimal-format. -->
  <!-- ExpectedException: An XSLT attribute of type T_CHAR must be only 1 character -->

<xsl:decimal-format decimal-separator="toobig" />

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number('7654.321','#############')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
