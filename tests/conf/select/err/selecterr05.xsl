<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr05 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 8 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Put xsl:for-each at top level, which is illegal. -->
  <!-- ExpectedException: (StylesheetHandler) xsl:for-each not allowed inside a stylesheet! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:for-each not allowed inside a stylesheet! -->

<xsl:for-each select="/doc/foo">
  <xsl:text>This should fail</xsl:text>
</xsl:for-each>

</xsl:stylesheet>
