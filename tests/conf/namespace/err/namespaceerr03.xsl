<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
 xmlns:axsl="http://www.w3.org/1999/XSL/TransAlias">

  <!-- FileName: NSPCerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.1 Literal Result Elements -->
  <!-- Purpose: Test missing attribute in namespace-alias. -->
  <!-- ExpectedException: XSLT: Missing namespace URI for specified prefix -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: Missing namespace URI for specified prefix -->
  <!-- ExpectedException: Missing namespace URI for specified prefix -->

<xsl:namespace-alias result-prefix="xsl"/>

<xsl:template match="/">
  <axsl:stylesheet>
    <xsl:apply-templates/>
  </axsl:stylesheet>
</xsl:template>

<xsl:template match="block">
  <axsl:template match="{.}">
    <axsl:apply-templates/>
  </axsl:template>
</xsl:template>

</xsl:stylesheet>