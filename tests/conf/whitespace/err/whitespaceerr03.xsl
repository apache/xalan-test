<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: WHTEerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.4 Whitespace Stripping -->
  <!-- Purpose: Test placement of preserve-space inside atemplate, which is illegal. -->
  <!-- ExpectedException: xsl:preserve-space is not allowed inside a template! -->
  <!-- ExpectedException: XSLT: (StylesheetHandler) xsl:preserve-space is not allowed inside a template! -->

<xsl:template match="doc">
  <out>
    <xsl:preserve-space elements="test2"/>
  </out>
</xsl:template>
   
</xsl:stylesheet>
