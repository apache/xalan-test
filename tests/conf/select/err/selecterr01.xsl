<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr01 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test for select with empty value. -->
  <!-- ExpectedException: null expression selected -->

<xsl:template match="/">
  <xsl:apply-templates select=""/>
</xsl:template>

<xsl:template match="*">
  We found a node!
</xsl:template>

</xsl:stylesheet>
