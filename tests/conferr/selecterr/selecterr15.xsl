<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr15 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.4 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to use a boolean where a node-set is needed in apply-templates.-->
  <!-- ExpectedException: XPATH: Can not convert #UNKNOWN to a NodeList -->
  <!-- ExpectedException: Can not convert #BOOLEAN to a NodeList! -->

<xsl:template match="/">
  <out>
    <xsl:apply-templates select="true()"/>
  </out>
</xsl:template>

<xsl:template match="*">
  <xsl:text>.</xsl:text>
</xsl:template>

</xsl:stylesheet>
