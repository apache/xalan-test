<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkey49 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.2 -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Test simple key(name,variable)//x/x filtered by a comples 
	   predicate, in a match pattern. -->


<xsl:key name="Info" match="Level1" use="@ID"/>
<xsl:variable name="testset" select="/Tree/grandparent/id"/>

<xsl:template match="/">
 <out>
	<xsl:apply-templates select="//Level3"/>
 </out>
</xsl:template>

<xsl:template match="key('Info',$testset)//Level3/Level3[Name[starts-with(@First,'J')]]">
	<xsl:text>&#10;</xsl:text>
	<xsl:element name="J-Name">
		<xsl:value-of select="Name/@First"/>
	</xsl:element>
</xsl:template>

<xsl:template match="*">
	<xsl:text>&#10;</xsl:text>
	<xsl:element name="Name">
		<xsl:value-of select="Name/@First"/>
	</xsl:element>
</xsl:template>
   
</xsl:stylesheet>