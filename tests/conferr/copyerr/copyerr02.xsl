<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: COPYerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.3 -->
  <!-- Purpose: Put xsl:copy-of at top level, which is illegal. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: xsl:copy-of is not allowed in this position in the stylesheet -->

<xsl:copy-of select="OL"/>

</xsl:stylesheet>
