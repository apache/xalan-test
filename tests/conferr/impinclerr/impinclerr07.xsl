<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: ImpInclerr07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.6 Overriding Template Rules -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Put xsl:apply-imports at top level, which is illegal. -->
  <!-- ExpectedException: xsl:apply-imports is not allowed in this position in the stylesheet -->

<xsl:import href="k.xsl"/>

<xsl:apply-imports/>

<xsl:template match="doc">
  <xsl:text>This should fail</xsl:text>
</xsl:template>

</xsl:stylesheet>
