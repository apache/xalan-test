<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: OUTPerr05 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.3 Creating Processing Instructions -->
  <!-- Purpose: Put processing-instruction at top level, which is illegal. -->
  <!-- ExpectedException: xsl:processing-instruction not allowed inside a stylesheet -->
  <!-- ExpectedException: (StylesheetHandler) xsl:processing-instruction not allowed inside a stylesheet! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:processing-instruction not allowed inside a stylesheet! -->
  
<xsl:processing-instruction name="my-pi">href="book.css" type="text/css"</xsl:processing-instruction>

<xsl:template match="/">
 <out>This should fail</out>
</xsl:template>
 
</xsl:stylesheet>