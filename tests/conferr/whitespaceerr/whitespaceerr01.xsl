<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: whitespaceerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.4 Whitespace Stripping -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Test error reporting when required attribute, elements, is missing
       from xsl:strip-space. -->
  <!-- ExpectedException: (StylesheetHandler) xsl:strip-space requires an elements attribute! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:strip-space requires an elements attribute! -->
  <!-- ExpectedException: xsl:strip-space requires attribute: elements -->

<xsl:strip-space/>

<xsl:template match="doc">
    <xsl:apply-templates select="*"/>

</xsl:template>

</xsl:stylesheet>
