<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr16 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Set one-character attribute pattern-separator too large in decimal-format. -->
  <!-- ExpectedException: An XSLT attribute of type T_CHAR must be only 1 character -->
  <!-- ExpectedException: Illegal value: toobig used for CHAR attribute: pattern-separator.  An attribute of type CHAR must be only 1 character! -->

<xsl:decimal-format pattern-separator="toobig" />

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number('-54321','000000toobig-######')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
