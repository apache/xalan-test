<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of two occurrences of the decimal-separator. -->
  <!-- ExpectedException: java.lang.RuntimeException: Malformed format string -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number(1886.201,'##,000.000.0000')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
