<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: ImpInclerr09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.6.1 Stylesheet Inclusion -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test placement of xsl:include inside a template, which is illegal. -->
  <!-- ExpectedException: XSLT: (StylesheetHandler) xsl:include is not allowed inside a template! -->
  <!-- ExpectedException: xsl:include is not allowed in this position in the stylesheet! -->

<xsl:template match="doc">
  <out>
    <xsl:include href="h.xsl"/>
  </out>
</xsl:template>

</xsl:stylesheet>
