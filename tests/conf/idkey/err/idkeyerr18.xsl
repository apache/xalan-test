<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr18 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.1 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test for id() with too few arguments. -->
  <!-- ExpectedException: id() should have one argument -->

<xsl:template match="t04">
  <out>
    <xsl:value-of select="id()/@id"/>
  </out>
</xsl:template>

</xsl:stylesheet>
