<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
		xmlns:xyz="http://www.lotus.com">

  <!-- FileName: variableman03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters -->
  <!-- Purpose:  Test external setting of parameter that has a QName. Run from
       special bat file which contains the following additional option
       -param xyz:in1 "'DATA'". -->
  <!-- Author: Paul Dick -->

<xsl:param name="xyz:in1" select="'default1'"/>

<xsl:template match="doc">
  <out>
    <xsl:value-of select="$xyz:in1"/>
  </out>
</xsl:template>

</xsl:stylesheet>
