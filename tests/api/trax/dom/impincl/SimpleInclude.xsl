<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- FileName: SimpleInclude.xsl -->
  <!-- Author: shane_curcuru@lotus.com -->
  <!-- Purpose: Basic import and include tests for StreamSource APIs. -->

<xsl:template match="list">
  <include-list>
    <xsl:apply-imports/>
  </include-list>
</xsl:template>

<xsl:template match="item[@match-by='include']">
  <matched-by-include>
    <xsl:value-of select="." />
  </matched-by-include>
</xsl:template>

</xsl:stylesheet>
