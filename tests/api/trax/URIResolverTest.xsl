<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- FileName: URIResolverTest.xsl -->
  <!-- Author: shane_curcuru@lotus.com -->
  <!-- Purpose: Various kinds of URIs need to be resolved from this stylesheet. -->

<xsl:import href="impincl/SystemIdImport.xsl"/>
<xsl:include href="impincl/SystemIdInclude.xsl"/>

<xsl:template match="doc">
 <out>
  <head>
   <xsl:variable name="resolvedURI1" select="document('../impincl/SystemIdImport.xsl')/xsl:stylesheet/xsl:template[@match='list']"/>
   <xsl:variable name="resolvedURI2" select="document('impincl/SystemIdImport.xsl')/xsl:stylesheet/xsl:template[@match='list']"/>
   <xsl:variable name="resolvedURI3" select="document('systemid/impincl/SystemIdImport.xsl')/xsl:stylesheet/xsl:template[@match='list']"/>
   <xsl:text>Various document() calls: </xsl:text>
   <xsl:value-of select="$resolvedURI1"/>
   <xsl:text>, </xsl:text>
   <xsl:value-of select="$resolvedURI2"/>
   <xsl:text>, </xsl:text>
   <xsl:value-of select="$resolvedURI3"/>
   <xsl:text>.</xsl:text>
  </head>
  <xsl:apply-templates/>
 </out>
</xsl:template>

<xsl:template match="list" priority="-1">
 <main-list>
  <xsl:apply-templates/>
 </main-list>
</xsl:template>

<xsl:template match="item[not(@match-by)]">
  <matched-by-main>
    <xsl:value-of select="." />
  </matched-by-main>
</xsl:template>

</xsl:stylesheet>
