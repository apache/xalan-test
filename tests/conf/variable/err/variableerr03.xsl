<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: VARerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters  -->
  <!-- Purpose: Test for missing name attribute in xsl:variable -->
  <!-- ExpectedException: xsl:variable must have a name attribute. -->

<xsl:variable select="'ABC'"/>

<xsl:template match="doc">
  <out>
    <xsl:value-of select="$ExpressionTest"/>
  </out>
</xsl:template>

</xsl:stylesheet>