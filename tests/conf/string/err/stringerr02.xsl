<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: stringerr02 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of invalid function that resembles "true" with string-length(). -->
  <!-- ExpectedException: XSL Warning: Could not find function: troo -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="string-length(troo())"/>
  </out>
</xsl:template>

</xsl:stylesheet>
