<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: OUTPerr07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.3 Creating Attributes -->
  <!-- Purpose: Put attribute at top level, which is illegal. -->
  <!-- ExpectedException: (StylesheetHandler) xsl:attribute not allowed inside a stylesheet! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:attribute not allowed inside a stylesheet! -->

<xsl:attribute name="badattr">foo</xsl:attribute>

<xsl:template match="/">
 <out>This should fail</out>
</xsl:template>

</xsl:stylesheet>