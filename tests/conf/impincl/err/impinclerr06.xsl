<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: impinclerr06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.6.1 Stylesheet Inclusion -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Check that proper error is reported if required attribute, href,
       is not included. -->
  <!-- Note: SCurcuru 28-Feb-00 added ExpectedException; seems like good error text to me. -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) Could not find href attribute for xsl:include -->
  <!-- ExpectedException: (StylesheetHandler) Could not find href attribute for xsl:include -->
  <!-- ExpectedException: xsl:include requires attribute: href -->

<xsl:include/>

<xsl:template match="doc">
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>