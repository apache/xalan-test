<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr05 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.2 -->
  <!-- Purpose: Test xsl:when after xsl:otherwise, match on final when. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: (StylesheetHandler) misplaced xsl:when! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) misplaced xsl:when! -->

<xsl:template match="doc">
  <out>
    <xsl:choose>
      <xsl:when test="blah">BAD</xsl:when>
      <xsl:otherwise>0</xsl:otherwise>
      <xsl:when test="foo">1</xsl:when>
    </xsl:choose>
  </out>
</xsl:template>

</xsl:stylesheet>
