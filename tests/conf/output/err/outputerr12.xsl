<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: outperr12 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 16 -->
  <!-- Purpose: Test placement of xsl:output inside atemplate, which is illegal. -->
  <!-- ExpectedException: Must put xsl:output outside any template. -->

<xsl:template match="/">
  <xsl:output method="html"/>
  <HTML>
  </HTML>
</xsl:template>

</xsl:stylesheet>