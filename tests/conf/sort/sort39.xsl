<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

<xsl:output
    method="xml"
    omit-xml-declaration="yes"
    indent="yes"/>
    
<xsl:param name="sortcolumn" select="'COLUMN1'"/>
<xsl:param name="sortorder" select="'ascending'"/>

<xsl:template match="xml">
  <xml>
    <data>
      <xsl:apply-templates/>
    </data>
  </xml>
</xsl:template>
 
<xsl:template match="data">
  <xsl:for-each select="row">
    <xsl:sort
        order="{$sortorder}"
        data-type="text"
        select="$sortcolumn"/>
    <xsl:apply-templates select="."/>
  </xsl:for-each>
</xsl:template>
 
<xsl:template match="row">
  <xsl:copy-of select="."/>
</xsl:template>
 
<xsl:template match="columntype">
  <xsl:copy-of select="."/>
</xsl:template>
 
<xsl:template match="truncated">
  <xsl:copy-of select="."/>
</xsl:template>
 
<xsl:template match="truncationsize">
  <xsl:copy-of select="."/>
</xsl:template>
 
</xsl:stylesheet>
