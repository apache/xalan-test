<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: COPYerr08 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.5 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to put an illegal attribute on xsl:copy. -->
  <!-- ExpectedException: "select" attribute is not allowed on the xsl:copy element -->

<xsl:output method="xml" encoding="UTF-8"/>

<xsl:template match="/">
  <out>
    <xsl:copy select="doc/a"/>
  </out>
</xsl:template>

</xsl:stylesheet>