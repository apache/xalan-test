<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableman04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters -->
  <!-- Purpose: Show that top-level xsl:variable is unaffected by an attempt to set
     it externally. Run from special bat file which contains the following additional option
     -param input 'testing'. -->
  <!-- Author: David Marston -->

<xsl:variable name="input" select="'defaultvalue'"/>

<xsl:template match="doc">
  <out>
    <xsl:value-of select="$input"/>
  </out>
</xsl:template>

</xsl:stylesheet>