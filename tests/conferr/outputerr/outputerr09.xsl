<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: OUTPerr09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.2 Creating Elements -->
  <!-- Purpose: Put element at top level, which is illegal. -->
  <!-- ExpectedException: xsl:element is not allowed in this position in the stylesheet -->

<xsl:element name="elk">foo</xsl:element>

<xsl:template match="/">
 <out>This should fail</out>
</xsl:template>

</xsl:stylesheet>