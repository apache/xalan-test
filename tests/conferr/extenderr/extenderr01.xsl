<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:ext="class:TestElemExt"
                xmlns:lxslt="http://xml.apache.org/xslt"
                extension-element-prefixes="ext">

  <!-- FileName: extenderr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 14 Extensions -->
  <!-- Purpose: Put xsl:fallback at top level, which is illegal. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: xsl:fallback is not allowed in this position in the stylesheet -->

<lxslt:component prefix="ext" elements="test"/>

<xsl:fallback>
  <xsl:text>Fallback: extension was not found.</xsl:text>
</xsl:fallback>

</xsl:stylesheet>