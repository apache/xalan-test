<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namespaceerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.1 XSLT Namespace -->
  <!-- Creator: David Marston -->
  <!-- Purpose:  It is an error for an element from XSLT namespace to have attributes
       with expanded-names that have null namespace URI's. -->
  <!-- ExpectedException: XSL Error: xsl:copy-of has an illegal attribute: -->

<xsl:template match="doc">
  <out>
    <xsl:copy-of select="doc" test="0"/>
  </out>
</xsl:template>

</xsl:stylesheet>
