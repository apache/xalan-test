<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MATHerr14 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.4 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of round() with zero arguments. -->
  <!-- ExpectedException: one argument expected -->
  <!-- ExpectedException: FuncRound only allows 1 arguments -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="round()"/>
  </out>
</xsl:template>

</xsl:stylesheet>
