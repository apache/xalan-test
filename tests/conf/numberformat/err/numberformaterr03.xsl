<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Attempt to put a child on decimal-format. -->
  <!-- ExpectedException: xsl:text not allowed inside xsl:decimal-format -->

<xsl:decimal-format NaN="non-numeric">
  <xsl:text>This should not appear!</xsl:text>
</xsl:decimal-format>

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number('foo','#############')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
