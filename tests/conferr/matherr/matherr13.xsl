<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MATHerr13 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.4 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of round() with too many arguments. -->
  <!-- ExpectedException: round() has too many arguments. -->
  <!-- ExpectedException: FuncRound only allows 1 arguments -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="round(8,7)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
