<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MATCHerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to use xsl:template as something other than a top-level element. -->
  <!-- ExpectedException: (StylesheetHandler) xsl:template is not allowed inside a template! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:template is not allowed inside a template! -->

<xsl:template match="letters">
  <out>
    <xsl:template>
      <xsl:value-of select="."/>
      <xsl:text> Huh? </xsl:text>
    </xsl:template>
  </out>
</xsl:template>

<xsl:template match="text()"/>

</xsl:stylesheet>
