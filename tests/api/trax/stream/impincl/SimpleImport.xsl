<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- FileName: SimpleImport.xsl -->
  <!-- Author: shane_curcuru@lotus.com -->
  <!-- Purpose: Basic import and include tests for StreamSource APIs. -->

<xsl:template match="list">
  <simple-import>
    <xsl:copy-of select="." />
  </simple-import>
</xsl:template>

<xsl:template match="item[@version='2']">
  <simple-import>
    <xsl:value-of select="." />
  </simple-import>
</xsl:template>

</xsl:stylesheet>
