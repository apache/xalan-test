<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: BOOLEANerr10 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of not() with too many arguments. -->
  <!-- ExpectedException: expected one argument -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="not(false(),doc)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
