<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters -->
  <!-- Purpose: Negative test, attempt to use variable RTF as a nodeset. -->
  <!-- ExpectedException: XPATH: Can not convert #RTREEFRAG to a NodeList! -->
  <!-- Author: Paul Dick -->

<xsl:template match="doc">
  <out>
    <xsl:variable name="foo">
       <foo><bar>some text</bar></foo>
    </xsl:variable>
    <xsl:apply-templates select="$foo/*"/>
  </out>
</xsl:template>

<xsl:template match="*">
  <xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>
