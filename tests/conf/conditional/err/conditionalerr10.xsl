<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr10 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.1 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test xsl:if that has an empty "test" attribute. -->
  <!-- ExpectedException: xsl:if must have a test attribute -->

<xsl:template match="/">
  <out>
    <xsl:if test="">
      <xsl:text>string</xsl:text>
    </xsl:if>
  </out>
</xsl:template>

</xsl:stylesheet>
