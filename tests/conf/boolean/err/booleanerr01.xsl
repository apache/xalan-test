<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: BOOLerr01 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.4 -->
  <!-- Purpose: Test of invalid function that resembles "not". -->
  <!-- ExpectedException: XSL Warning: Could not find function: nt -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="nt(true())"/>
  </out>
</xsl:template>
 
</xsl:stylesheet>
