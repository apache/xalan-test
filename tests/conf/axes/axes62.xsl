<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:ped="http://ped.lotus.com"
                xmlns:bdd="http://bdd.lotus.com"
                xmlns:admin="http://administrator.com">

  <!-- FileName: axes62 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2 Axes-->
  <!-- Creator: David Marston -->
  <!-- Purpose: Check the namespace axes with a specified name. -->

<xsl:template match="/">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="doc">
<!-- Output number of NS nodes and what they are -->
There are <xsl:value-of select="count(namespace::*)"/> Namespace Nodes.<xsl:text>&#010;</xsl:text>
	<xsl:for-each select="namespace::*">
		<xsl:value-of select="name(.)"/><xsl:text>=</xsl:text><xsl:value-of select="."/><xsl:text>,&#010;</xsl:text>
	</xsl:for-each><xsl:text>&#010;</xsl:text>

<!-- Map stylesheet's "ped" NS node to source document's equivalent ~ OK1 -->		
	<xsl:for-each select="namespace::ped">
		<xsl:value-of select="name(.)"/><xsl:text> = </xsl:text><xsl:value-of select="."/><xsl:text>,&#010;</xsl:text>
	</xsl:for-each>

<!-- Map stylesheet's "bdd" NS node to source document's equivalent ~ OK2 -->
	<xsl:for-each select="namespace::bdd">
		<xsl:value-of select="name(.)"/><xsl:text> = </xsl:text><xsl:value-of select="."/><xsl:text>,&#010;</xsl:text>
	</xsl:for-each>

<!-- Map stylesheet's "admin" NS node to source document's equivalent ~ jad -->
	<xsl:for-each select="namespace::admin">
		<xsl:value-of select="name(.)"/><xsl:text> = </xsl:text>
	</xsl:for-each>

	<xsl:value-of select="string(namespace::admin)"/><xsl:text>&#010;</xsl:text>

</xsl:template>

</xsl:stylesheet>
