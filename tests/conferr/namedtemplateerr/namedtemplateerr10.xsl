<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr10 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 Named Templates -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Put xsl:with-param in a template, which is illegal. -->
  <!-- ExpectedException: xsl:with-param must be child of xsl:apply-templates or xsl:call-template -->
  <!-- ExpectedException: xsl:with-param is not allowed in this position in the stylesheet! -->

<xsl:template match="doc">
  <xsl:with-param name="par1" select="'foo'"/>
</xsl:template>

</xsl:stylesheet>
