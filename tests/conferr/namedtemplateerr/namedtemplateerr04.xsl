<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test child of call-template other than with-param. -->
  <!-- ExpectedException: Can not add #text to xsl:call-template -->
  <!-- ExpectedException: ElemTemplateElement error: Can not add #text to xsl:call-template -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: ElemTemplateElement error: Can not add #text to xsl:call-template -->
  <!-- ExpectedException: xsl:text is not allowed in this position in the stylesheet! -->

<xsl:template match="doc">
  <out>
    <xsl:call-template name="tmplt1">
      <xsl:text>This should not come out</xsl:text>
    </xsl:call-template>
  </out>
</xsl:template>

<xsl:template name="tmplt1">
  <xsl:param name="pvar1">Default text in pvar1</xsl:param>
  <xsl:value-of select="$pvar1"/>
</xsl:template>

</xsl:stylesheet>
