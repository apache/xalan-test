<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr25 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of per-mille in middle of format string. -->
  <!-- ExpectedException: java.lang.RuntimeException: Malformed format string -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number('54.321','###&#8240;###.##')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
