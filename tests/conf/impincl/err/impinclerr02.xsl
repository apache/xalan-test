<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: ImpInclerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.6 Overriding Template Rules -->
  <!-- Creator: David Marston -->
  <!-- Purpose: It is an error if apply-imports is instantiated when the current
       template rule is null, i.e. from within a xsl:for-each loop. -->
  <!-- ExpectedException   -->

<xsl:import href="k.xsl"/>

<xsl:template match="doc">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="doc1">
  <xsl:for-each select="*">
    <xsl:value-of select="."/>
    <xsl:apply-imports/>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
