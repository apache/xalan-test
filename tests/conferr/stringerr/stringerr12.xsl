<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: stringerr12 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions  -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of 'substring()' with one argument -->
  <!-- ExpectedException: substring() requires 2-3 arguments -->
  <!-- ExpectedException: FuncSubstring only allows 2 or 3 arguments -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="substring('ENCYCLOPEDIA')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
