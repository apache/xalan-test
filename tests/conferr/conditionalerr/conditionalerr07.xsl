<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test attempt to put attribute on xsl:otherwise. -->
  <!-- ExpectedException: xsl:otherwise has an illegal attribute: test -->
  <!-- ExpectedException: "test" attribute is not allowed on the xsl:otherwise element! -->

<xsl:template match="doc">
  <out>
    <xsl:choose>
      <xsl:when test="blah">BAD</xsl:when>
      <xsl:otherwise test="not(blah)">0</xsl:otherwise>
    </xsl:choose>
  </out>
</xsl:template>

</xsl:stylesheet>
