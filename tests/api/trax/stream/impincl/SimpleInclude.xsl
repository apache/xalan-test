<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- FileName: SimpleInclude.xsl -->
  <!-- Author: shane_curcuru@lotus.com -->
  <!-- Purpose: Basic import and include tests for StreamSource APIs. -->

<xsl:template match="list">
  <simple-include>
    <xsl:copy-of select="." />
  </simple-include>
</xsl:template>

<xsl:template match="item[@version='1']">
  <simple-include>
    <xsl:value-of select="." />
  </simple-include>
</xsl:template>

</xsl:stylesheet>
