<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 8 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test for-each lacking a select. -->
  <!-- ExpectedException: xsl:for-each requires attribute: select -->

<xsl:template match="doc">
  <out>
    <xsl:for-each>
      <xsl:copy-of select="//item"/>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
