<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: OUTPerr08 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.4 Creating Comments -->
  <!-- Purpose: Put comment at top level, which is illegal. -->
  <!-- ExpectedException: xsl:comment is not allowed in this position in the stylesheet -->

<xsl:comment>boo</xsl:comment>

<xsl:template match="/">
 <out>This should fail</out>
</xsl:template>

</xsl:stylesheet>