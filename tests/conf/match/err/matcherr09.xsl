<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MATCHerr09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.4 -->
  <!-- Purpose: Put xsl:apply-templates at top level, which is illegal. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: (StylesheetHandler) xsl:apply-templates not allowed inside a stylesheet! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:apply-templates not allowed inside a stylesheet! -->

<xsl:apply-templates select="letters/letter"/>

<xsl:template match="letter">
  <xsl:value-of select="."/>
  <xsl:text> This should fail</xsl:text>
</xsl:template>

<xsl:template match="text()"/>

</xsl:stylesheet>
