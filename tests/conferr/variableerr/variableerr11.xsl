<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr11 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters  -->
  <!-- Purpose: Try to set same top-level param twice using different instructions, param first -->
  <!-- ExpectedException: Duplicate global variable declaration. -->
  <!-- Author: David Marston -->

<xsl:param name="ExpressionTest" select="'ABC'"/>
<xsl:variable name="ExpressionTest" select="'XYZ'"/>

<xsl:template match="doc">
   <out>
      <xsl:value-of select="$ExpressionTest"/>
   </out>
</xsl:template>

</xsl:stylesheet>