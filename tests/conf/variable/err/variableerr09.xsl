<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters  -->
  <!-- Purpose: Try to set same top-level xsl:variable twice -->
  <!-- ExpectedException: Duplicate global variable declaration. -->
  <!-- Author: David Marston -->

<xsl:variable name="ExpressionTest" select="'ABC'"/>
<xsl:variable name="ExpressionTest" select="'XYZ'"/>

<xsl:template match="doc">
   <out>
      <xsl:value-of select="$ExpressionTest"/>
   </out>
</xsl:template>

</xsl:stylesheet>