<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: BOOLerr09 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.3 -->
  <!-- Purpose: Test of not() with no argument. -->
  <!-- ExpectedException: expected one argument -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="not()"/>
  </out>
</xsl:template>

</xsl:stylesheet>
