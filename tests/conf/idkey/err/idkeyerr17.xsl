<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr17 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test placement of xsl:key inside atemplate, which is illegal. -->
  <!-- ExpectedException: XSLT: (StylesheetHandler) xsl:key is not allowed inside a template! -->

<xsl:template match="doc">
  <out>
    <xsl:key name="titles" match="div" use="title"/>
  </out>
</xsl:template>

</xsl:stylesheet>
