<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr08 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Show what happens if there are any filler digits (#) between
       zero-digits and the decimal-separator. Change both characters. -->
  <!-- ExpectedException: java.lang.RuntimeException: Malformed format string -->

<xsl:decimal-format digit="!" zero-digit="a" />

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number(4030201.050607,'#aa,aaa,!!!.!!!aaa0')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
