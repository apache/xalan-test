<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr20 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.1 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Put xsl:if at top level, which is illegal. -->
  <!-- ExpectedException: (StylesheetHandler) xsl:if not allowed inside a stylesheet! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:if not allowed inside a stylesheet! -->

<xsl:if test="true()">
  <xsl:text>This should fail</xsl:text>
</xsl:if>

</xsl:stylesheet>
