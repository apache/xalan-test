<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:bogus="http://www.bogus_ns.com"
        xmlns:lotus="http://www.lotus.com"
	xmlns:ped="www.ped.com">

  <!-- FileName: namespaceerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.1 XSLT Namespace -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose:  Testing an attribute not from the XSLT namespace, which is
       legal provided that the expanded name of the attribute has a non-null 
       namespace URI. This tests for many xslt TOP-LEVEL elements, apparent code path 
       are different for numerous elements. Should not have namespaces to inherit. -->

<!-- xsl:import href="..\test1.xsl"  a="a"/ -->
<!-- xsl:include href="..\test2.xsl" b="b"/ -->
<xsl:output method="xml" indent="yes" ped:c="c"/>

<xsl:key name="sprtest" match="TestID" use="Name" ped:d="d"/>

<xsl:strip-space elements="a" ped:e="e"/>
<xsl:preserve-space elements="b" ped:f="f"/>

<xsl:variable name="Var1" ped:g="g">
DefaultValueOfVar1
</xsl:variable>

<xsl:param name="Param1" ped:h="h">
DefaultValueOfParam1
</xsl:param>

<xsl:attribute-set name="my-style" ped:i="i">
  <xsl:attribute name="my-size" ped:j="j">12pt</xsl:attribute>
  <xsl:attribute name="my-weight">bold</xsl:attribute>
</xsl:attribute-set>

<xsl:namespace-alias stylesheet-prefix="bogus" result-prefix="xsl" ped:k="k"/>
<xsl:decimal-format decimal-separator="," grouping-separator=" " ped:l="l"/>

<xsl:template match="docs" ped:m="m">
  <bogus:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<bogus:template match="/">
	  <out>
		Yeee ha
		<xsl:for-each select="block" ped:n="n">
			<xsl:if test=" .='p'" ped:o="o">
				Whoopie
			</xsl:if>
			<xsl:value-of select="." ped:p="p"/>
		</xsl:for-each>
      </out>
	</bogus:template>
  </bogus:stylesheet>
</xsl:template>

</xsl:stylesheet>
