<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test illegal attribute on decimal-format. -->
  <!-- ExpectedException: Invalid attribute on xsl:decimal-format. -->
  <!-- ExpectedException: "badattr" attribute is not allowed on the xsl:decimal-format element! -->

<xsl:decimal-format NaN="non-numeric" badattr="bad" />

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number('foo','#############')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
