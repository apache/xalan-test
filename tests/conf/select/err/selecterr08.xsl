<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr08 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.7 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: name after ) must be treated as operator -->
  <!-- ExpectedException: Extra illegal tokens: 'foo', '2' -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="(* - 4) foo 2"/>
  </out>
</xsl:template>

</xsl:stylesheet>
