<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.2 -->
  <!-- Purpose: Test xsl:choose having more than one xsl:otherwise. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: xsl:otherwise is not allowed in this position in the stylesheet -->

<xsl:template match="doc">
  <out>
    <xsl:choose>
      <xsl:when test="false()">Should not get this output!</xsl:when>
      <xsl:otherwise>1</xsl:otherwise>
      <xsl:otherwise>2</xsl:otherwise>
      <xsl:otherwise>3</xsl:otherwise>
    </xsl:choose>
  </out>
</xsl:template>

</xsl:stylesheet>
