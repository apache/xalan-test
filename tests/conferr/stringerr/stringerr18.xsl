<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: stringerr18 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions  -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of 'string()' with too many arguments -->
  <!-- ExpectedException: string() has too many arguments. -->
  <!-- ExpectedException: FuncString only allows 1 arguments -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="string(22,44)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
