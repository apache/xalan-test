<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr18 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test xsl:when that has bad content in "test" attribute. -->
  <!-- ExpectedException: Invalid token -->

<xsl:template match="doc">
  <out>
    <xsl:choose>
      <xsl:when test="Œnot(name(.)='')">1</xsl:when>
      <xsl:otherwise>0</xsl:otherwise>
    </xsl:choose>
  </out>
</xsl:template>
 
</xsl:stylesheet>
