<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: ImpInclerr08 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.6.2 Stylesheet Import -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test placement of xsl:import inside a template, which is illegal. -->
  <!-- ExpectedException: XSLT: (StylesheetHandler) xsl:import is not allowed inside a template! -->

<xsl:template match="doc">
  <out>
    <xsl:import href="h.xsl"/>
  </out>
</xsl:template>

</xsl:stylesheet>
