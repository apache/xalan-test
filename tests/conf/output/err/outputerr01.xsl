<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>

  <!-- FileName: outperr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.6.1 Generating Text with xsl:value -->
  <!-- Purpose: Test error reporting for missing required select attribute. -->
  <!-- ExpectedException: xsl:value-of requires a select attribute. -->

<xsl:template match="/">
	<xsl:value-of/>
</xsl:template>

</xsl:stylesheet>