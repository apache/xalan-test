<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MATHerr07 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.4 -->
  <!-- Purpose: Test of sum() with too many arguments. -->
  <!-- ExpectedException: sum() has too many arguments. -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="sum(doc,8)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
