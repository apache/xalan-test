<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- FileName: SystemIdInclude.xsl -->
  <!-- Author: shane_curcuru@lotus.com -->
  <!-- Purpose: Basic import and include tests with differen systemId's. -->

<xsl:template match="list">
  <include-list-level0>
    <xsl:apply-imports/>
  </include-list-level0>
</xsl:template>

<xsl:template match="item[@match-by='include']">
  <matched-by-include-level0>
    <xsl:value-of select="." />
  </matched-by-include-level0>
</xsl:template>

</xsl:stylesheet>
