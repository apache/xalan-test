<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conditionalerr21 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 9.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Put xsl:when at top level, which is illegal. -->
  <!-- ExpectedException: xsl:when is not allowed in this position in the stylesheet -->

<xsl:when test="sex='M'">Male: </xsl:when>

</xsl:stylesheet>
