<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 Named Templates -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Put xsl:with-param at top level, which is illegal. -->
  <!-- ExpectedException: xsl:with-param is not allowed in this position in the stylesheet -->

<xsl:with-param select="'foo'"/>

</xsl:stylesheet>
