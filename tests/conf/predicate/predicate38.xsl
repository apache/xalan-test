<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: predicate38 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.4 -->
  <!-- Purpose: Stress test of nested and multiple predicates. The production rules
       allow such nesting. -->
  <!-- Author: Paul Dick -->

<xsl:template match="doc">
   <xsl:apply-templates select="foo[(bar[2])='this']"/>
   <xsl:apply-templates select="foo[(bar[2][(baz[2])='goodbye'])]"/>
</xsl:template>

<xsl:template match="foo[(bar[2])='this']">
 1	<xsl:for-each select="*">
    	<xsl:value-of select="@attr"/>,
	</xsl:for-each>
</xsl:template>

<xsl:template match="foo[(bar[(baz[2])='goodbye'])]">
 3	<xsl:for-each select="*">
    	<xsl:value-of select="@attr"/>,
	</xsl:for-each>
</xsl:template>

<xsl:template match="foo[(bar[2][(baz[2])='goodbye'])]">
 2	<xsl:for-each select="*">
    	<xsl:value-of select="@attr"/>,
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>