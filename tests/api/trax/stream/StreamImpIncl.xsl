<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- FileName: StreamImpIncl.xsl -->
  <!-- Author: shane_curcuru@lotus.com -->
  <!-- Purpose: Basic import and include tests for StreamSource APIs. -->

<xsl:import href="impincl/SimpleImport.xsl"/>
<xsl:include href="impincl/SimpleInclude.xsl"/>

<xsl:template match="doc">
 <out>
  <xsl:apply-templates/>
 </out>
</xsl:template>

<xsl:template match="list" priority="-1">
 <out-list>
  <xsl:apply-templates/>
 </out-list>
</xsl:template>

</xsl:stylesheet>
