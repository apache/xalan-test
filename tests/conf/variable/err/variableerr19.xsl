<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr19 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.5 -->
  <!-- Purpose: Try to set a variable inside a template based on variable defined later in that template. -->
  <!-- ExpectedException: VariableReference given for variable out of context or without definition! -->
  <!-- Author: David Marston -->

<xsl:template match="doc">
  <out>
    <xsl:variable name="b" select="$a" />
    <xsl:variable name="a" select="'second'" /><!-- Two sets of quotes make it a string -->
    <xsl:value-of select="$b" />
  </out>
</xsl:template>

</xsl:stylesheet>