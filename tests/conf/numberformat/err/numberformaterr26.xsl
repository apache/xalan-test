<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr26 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of more than two patterns. -->
  <!-- ExpectedException: java.lang.RuntimeException: Malformed format string -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number(-26931.4,'+##,###.000;-##,###.###;x##,###.###')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
