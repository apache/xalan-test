<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribvaltemplateerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.6.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Value with a colon is interpreted as having a namespace prefix. -->
  <!-- ExpectedException: Prefix must resolve to a namespace -->

<xsl:template match="doc">
  <out href="{http:val}"/>
</xsl:template>

</xsl:stylesheet>