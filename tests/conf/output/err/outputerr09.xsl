<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: OUTPerr09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.2 Creating Elements -->
  <!-- Purpose: Put element at top level, which is illegal. -->
  <!-- ExpectedException: (StylesheetHandler) xsl:element not allowed inside a stylesheet! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:element not allowed inside a stylesheet! -->

<xsl:element name="elk">foo</xsl:element>

<xsl:template match="/">
 <out>This should fail</out>
</xsl:template>

</xsl:stylesheet>