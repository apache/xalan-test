<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- FileName: SimpleImport.xsl -->
  <!-- Author: shane_curcuru@lotus.com -->
  <!-- Purpose: Basic import and include tests for StreamSource APIs. -->

<xsl:template match="list">
  <import-list>
    <xsl:apply-templates/>
  </import-list>
</xsl:template>

<xsl:template match="item[@match-by='import']">
  <matched-by-import>
    <xsl:value-of select="." />
  </matched-by-import>
</xsl:template>

<xsl:template match="item">
  <matched-by-import-also>
    <xsl:value-of select="." />
  </matched-by-import-also>
</xsl:template>

</xsl:stylesheet>
