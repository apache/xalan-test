<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>

  <!-- FileName: OUTPerr06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.2 Creating Text -->
  <!-- Purpose: Test for bad child in xsl:text -->
  <!-- ExpectedException: Can not add xsl:value-of to xsl:text -->
  <!-- ExpectedException: ElemTemplateElement error: Can not add xsl:value-of to xsl:text -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: ElemTemplateElement error: Can not add xsl:value-of to xsl:text -->
  <!-- ExpectedException: xsl:value-of is not allowed in this position in the stylesheet! -->

<xsl:template match="/">
  <HTML>
    <BODY>
      <xsl:text disable-output-escaping="yes"><xsl:value-of select="2 + 2"/></xsl:text>
    </BODY>
  </HTML>
</xsl:template>

</xsl:stylesheet>
