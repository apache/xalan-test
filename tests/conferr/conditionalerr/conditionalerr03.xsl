<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of xsl:choose containing sub-element that is not a when or otherwise. -->
  <!-- ExpectedException: ElemTemplateElement error: Can not add #text to xsl:choose -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: ElemTemplateElement error: Can not add #text to xsl:choose -->
  <!-- ExpectedException: xsl:text is not allowed in this position in the stylesheet! -->

<xsl:template match="doc">
  <out>
    <xsl:choose>
      <xsl:text>Inside choose</xsl:text>
    </xsl:choose>
  </out>
</xsl:template>

</xsl:stylesheet>
