<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: SORTerr07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 10 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test use of xsl:sort at top level, where it's not allowed. -->
  <!-- ExpectedException: xsl:sort is not allowed in this position in the stylesheet -->

<xsl:sort select="docs"/>

</xsl:stylesheet>