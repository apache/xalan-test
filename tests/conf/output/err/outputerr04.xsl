<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" 
            doctype-public="-//W3C//DTD HTML 4.0 Transitional"/>

  <!-- FileName: OUTPerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.3 Creating Processing Instructions -->
  <!-- Purpose: Try to create processing instruction with an improper name. -->
  <!-- ExpectedException: java.lang.RuntimeException: processing-instruction name must be a valid NCName -->

<xsl:template match="/">
 <HTML>
   <xsl:processing-instruction name="+">href="book.css" type="text/css"</xsl:processing-instruction>
 </HTML>
</xsl:template>
 
</xsl:stylesheet>