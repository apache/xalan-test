<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MATHerr11 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.4 -->
  <!-- Purpose: Test of ceiling() with too many arguments. -->
  <!-- ExpectedException: ceiling() has too many arguments. -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="ceiling(8,7)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
