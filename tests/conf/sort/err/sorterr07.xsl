<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: SORTerr07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 10 -->
  <!-- Purpose: Test use of xsl:sort at top level, where it's not allowed. -->
  <!-- ExpectedException: (StylesheetHandler) xsl:sort not allowed inside a stylesheet! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:sort not allowed inside a stylesheet! -->

<xsl:sort select="docs"/>

</xsl:stylesheet>