<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr30 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test placement of decimal-format inside atemplate, which is illegal. -->
  <!-- ExpectedException: Must put xsl:decimal-format outside any template. -->

<xsl:template match="doc">
  <out>
    <xsl:decimal-format NaN="non-numeric"/>
    <xsl:value-of select="format-number('foo','#############')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
