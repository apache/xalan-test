<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test repeat declaration of decimal-format, un-named. -->
  <!-- ExpectedException: Only one default xsl:decimal-format declaration is allowed. -->

<xsl:decimal-format NaN="non-numeric" />

<xsl:decimal-format NaN="wrong-number" />

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number('foo','#############')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
