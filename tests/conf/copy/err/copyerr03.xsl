<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: COPYerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.5 -->
  <!-- Purpose: Put xsl:copy at top level, which is illegal. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: (StylesheetHandler) xsl:copy not allowed inside a stylesheet! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:copy not allowed inside a stylesheet! -->

<xsl:copy>
  <xsl:apply-templates select="*|text()"/>
</xsl:copy>

<xsl:template match="/">
  <out>This should fail</out>
</xsl:template>

</xsl:stylesheet>
