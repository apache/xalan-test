<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr22 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Put xsl:otherwise at top level, which is illegal. -->
  <!-- ExpectedException: (StylesheetHandler) xsl:otherwise not allowed inside a stylesheet! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:otherwise not allowed inside a stylesheet! -->

<xsl:otherwise>Female: </xsl:otherwise>

</xsl:stylesheet>
