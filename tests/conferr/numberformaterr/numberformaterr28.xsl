<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr28 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of format-number with too few arguments. -->
  <!-- ExpectedException: format-number() must have at least 2 arguments -->
  <!-- ExpectedException: only allows 2 or 3 arguments -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number(2392.14*36.58)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
