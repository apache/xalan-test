<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr11 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 Named Templates -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to use an AVT for the template name when invoking. -->
  <!-- ExpectedException: Could not find template named: {$tocall} -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: Could not find template named: {$tocall} -->
  <!-- ExpectedException: Illegal value: {$tocall} used for QNAME attribute: name -->

<xsl:template match="doc">
  <out>
    <xsl:variable name="tocall" select="'a'"/>
    <xsl:call-template name="{$tocall}"/>
  </out>
</xsl:template>

<xsl:template name="tocall">
  <xsl:text>This should NOT display!</xsl:text>
</xsl:template>

<xsl:template name="a">
  <xsl:text>We are in template a!</xsl:text>
</xsl:template>

</xsl:stylesheet>
