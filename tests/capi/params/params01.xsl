<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableman01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters -->
  <!-- Purpose: Test of passing a parameter to a stylesheet. Run from
     special bat file which contains the following additional option
     -param input 'testing 123'. -->
  <!-- Author: Paul Dick -->

<xsl:param name="input" select="'defaultvalue'"/>
<xsl:param name="input2" select="'defaultvalue'"/><!-- DON'T set externally -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="$input"/><xsl:text>; </xsl:text>
    <xsl:value-of select="$input2"/>
  </out>
</xsl:template>

</xsl:stylesheet>