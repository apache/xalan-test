<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr08 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test attempt to put attribute on xsl:choose. -->
  <!-- ExpectedException: xsl:choose has an illegal attribute: name -->
  <!-- ExpectedException: "name" attribute is not allowed on the xsl:choose element! -->

<xsl:template match="doc">
  <out>
    <xsl:choose name="outer">
      <xsl:when test="foo">1</xsl:when>
      <xsl:otherwise>0</xsl:otherwise>
    </xsl:choose>
  </out>
</xsl:template>

</xsl:stylesheet>
