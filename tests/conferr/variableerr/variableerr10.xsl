<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr10 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters  -->
  <!-- Purpose: Try to set same top-level xsl:param twice -->
  <!-- ExpectedException: Duplicate global variable declaration. -->
  <!-- Author: David Marston -->

<xsl:param name="ExpressionTest" select="'ABC'"/>
<xsl:param name="ExpressionTest" select="'XYZ'"/>

<xsl:template match="doc">
   <out>
      <xsl:value-of select="$ExpressionTest"/>
   </out>
</xsl:template>

</xsl:stylesheet>