<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: WHTE01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 3.4 -->
  <!-- Purpose: Test strip-space directive. -->

<xsl:strip-space elements="test1"/>
  
<xsl:template match="doc">
  <out>
    <xsl:apply-templates select="*"/>
  </out>
</xsl:template>
   
</xsl:stylesheet>
