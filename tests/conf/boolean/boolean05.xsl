<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: BOOL05 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 3.4 -->
  <!-- Purpose: Test of boolean() function - conversion of empty string. -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="boolean('')"/>
  </out>
</xsl:template>
 
</xsl:stylesheet>
