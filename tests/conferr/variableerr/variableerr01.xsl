<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters -->
  <!-- Purpose: Negative test, attempt to access undefined variable. -->
  <!-- Author: David Marston -->
  <!-- ExpectedException: Could not find variable with the name of input -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="$input"/>
  </out>
</xsl:template>

</xsl:stylesheet>
