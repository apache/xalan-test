<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: BOOLEANerr12 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of boolean() with too many arguments. -->
  <!-- ExpectedException: expected one argument -->
  <!-- ExpectedException: FuncBoolean only allows 1 arguments -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="boolean(false(),doc)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
