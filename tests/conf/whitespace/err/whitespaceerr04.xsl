<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: WHTEerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.4 Whitespace Stripping -->
  <!-- Purpose: Test placement of strip-space inside atemplate, which is illegal. -->
  <!-- ExpectedException: xsl:strip-space is not allowed inside a template! -->
  <!-- ExpectedException: XSLT: (StylesheetHandler) xsl:strip-space is not allowed inside a template! -->

<xsl:template match="doc">
  <out>
    <xsl:strip-space elements="test2"/>
  </out>
</xsl:template>
   
</xsl:stylesheet>
