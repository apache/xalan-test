<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: whitespaceerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.4 Whitespace Stripping -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test placement of preserve-space inside a template, which is illegal. -->
  <!-- ExpectedException: xsl:preserve-space is not allowed inside a template! -->
  <!-- ExpectedException: XSLT: (StylesheetHandler) xsl:preserve-space is not allowed inside a template! -->
  <!-- ExpectedException: xsl:preserve-space is not allowed in this position in the stylesheet! -->

<xsl:template match="doc">
  <out>
    <xsl:preserve-space elements="test2"/>
  </out>
</xsl:template>

</xsl:stylesheet>
