<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr12 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to use otherwise outside of choose. -->
  <!-- ExpectedException: (StylesheetHandler) xsl:otherwise not parented by xsl:choose! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:otherwise not parented by xsl:choose! -->

<xsl:template match="/doc">
  <out>
    <xsl:for-each select="person">
      <xsl:otherwise>&#xa;Male: </xsl:otherwise>
      <xsl:value-of select="name"/>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
