<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr17 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.1 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test xsl:if that has bad content in "test" attribute. -->
  <!-- ExpectedException: Invalid token -->

<xsl:template match="/">
  <out>
    <xsl:if test="Œnot(name(.)='')">
      <xsl:text>string</xsl:text>
    </xsl:if>
  </out>
</xsl:template>
 
</xsl:stylesheet>
