<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: OUTPerr11 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.6.1 Generating Text -->
  <!-- Purpose: Put value-of at top level, which is illegal. -->
  <!-- ExpectedException: xsl:value-of is not allowed in this position in the stylesheet -->

<xsl:value-of select="concat('f','o','o')"/>

<xsl:template match="/">
 <out>This should fail</out>
</xsl:template>

</xsl:stylesheet>