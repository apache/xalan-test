<?xml version="1.0"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				version="1.0">

  <!-- FileName: VERerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2 Stylesheet element -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Don't allow transform to be matched with stylesheet. -->
  <!-- ExpectedException: The element type "xsl:transform" must be terminated by the matching end-tag "&lt;/xsl:transform&gt;". -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: The element type "xsl:transform" must be terminated by the matching end-tag "&lt;/xsl:transform&gt;". -->

<!-- Explicitly match text nodes so the output is just 39 -->
<xsl:template match="text()">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="doc/version">
  <out>
    <xsl:value-of select="./@theattrib"/>
  </out>
</xsl:template>

</xsl:stylesheet>