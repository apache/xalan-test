<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MDOCSerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.1 Multiple Source Documents -->
  <!-- Purpose: Test document() function with zero arguments. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: document() function requires 1 or 2 arguments -->

<xsl:template match="defaultcontent">
  <out>
    <xsl:apply-templates select="document()/body"/>
  </out>
</xsl:template>

<xsl:template match="body">
  <xsl:text>This should fail</xsl:text>
</xsl:template>

</xsl:stylesheet>