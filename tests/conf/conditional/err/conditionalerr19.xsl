<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr19 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Put xsl:choose at top level, which is illegal. -->
  <!-- ExpectedException: (StylesheetHandler) xsl:choose not allowed inside a stylesheet! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:choose not allowed inside a stylesheet! -->

<xsl:choose>
  <xsl:when test="doc">Found a doc</xsl:when>
  <xsl:when test="person">Found a person</xsl:when>
  <xsl:otherwise>Who knows?</xsl:otherwise>
</xsl:choose>

</xsl:stylesheet>
