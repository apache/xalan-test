<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: BOOLEANerr11 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of boolean() with no argument. -->
  <!-- ExpectedException: expected one argument -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="boolean()"/>
  </out>
</xsl:template>

</xsl:stylesheet>
