<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >

  <!-- FileName: lreerr06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.1 Literal Result Elements-->
  <!-- Purpose: Try to put out value of a variable without a template. -->
  <!-- ExpectedException: Illegal top-level element -->
  <!-- Creator: David Marston -->

<xsl:variable name="var" select="Data"/>

<out>$var</out>

</xsl:stylesheet>