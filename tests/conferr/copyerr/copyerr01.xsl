<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output indent="yes"/>

  <!-- FileName: COPYerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.3 -->
  <!-- Purpose: Test for xsl:copy-of without select. -->                
  <!-- Creator: David Marston -->
  <!-- ExpectedException: xsl:copy-of requires a select attribute -->
  <!-- ExpectedException: xsl:copy-of requires attribute: select -->

<xsl:template match="/">
  <root>
    <xsl:apply-templates/>
  </root>
</xsl:template>

<xsl:template match="*">
  <xsl:copy-of />
</xsl:template>

</xsl:stylesheet>
