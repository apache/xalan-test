<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MATCHerr11 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test '//' in a match pattern -->
  <!-- ExpectedException: Unexpected token in pattern -->

<xsl:template match="defaultcontent">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="//">
  <xsl:value-of select="name(.)"/>
</xsl:template>

</xsl:stylesheet>