<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters -->
  <!-- Purpose: Negative test, attempt to access variable that's not in scope. -->
  <!-- Author: David Marston -->
  <!-- ExpectedException: Could not find variable with the name of input -->

<xsl:template match="doc">
  <out>
    <xsl:apply-templates/>
    Back to doc template.
    <xsl:value-of select="$input"/>
  </out>
</xsl:template>

<xsl:template match="inner">
  <xsl:variable name="input" select="2"/>
  Now in inner template.
</xsl:template>

</xsl:stylesheet>
