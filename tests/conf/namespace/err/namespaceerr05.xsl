<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
 xmlns:axsl="http://www.w3.org/1999/XSL/TransAlias">

  <!-- FileName: NSPCerr05 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.1 Literal Result Elements -->
  <!-- Purpose: Test placement of namespace-alias inside atemplate, which is illegal. -->
  <!-- ExpectedException: Must put xsl:namespace-alias outside any template. -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: Must put xsl:namespace-alias outside any template. -->

<xsl:template match="/">
  <out>
    <xsl:namespace-alias stylesheet-prefix="axsl" result-prefix="xsl"/>
  </out>
</xsl:template>

</xsl:stylesheet>