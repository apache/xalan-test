<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr05 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters  -->
  <!-- Purpose: Test for xsl:variable with both content and select. -->
  <!-- ExpectedException: xsl:variable element must not have both content and a select attribute. -->
  <!-- Author: David Marston -->

<xsl:template match="doc">
  <xsl:variable name="n" select="3">2</xsl:variable>
  <out>
    <xsl:value-of select="item[$n]"/>
  </out>
</xsl:template>

</xsl:stylesheet>
