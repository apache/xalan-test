<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribseterr06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.4 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test placement of attribute-set inside a template, which is illegal. -->
  <!-- ExpectedException: xsl:attribute-set is not allowed inside a template! -->
  <!-- ExpectedException: XSLT: (StylesheetHandler) xsl:attribute-set is not allowed inside a template! -->
  <!-- ExpectedException: xsl:attribute-set is not allowed in this position in the stylesheet! -->
<xsl:template match="/">
  <out>
    <xsl:attribute-set name="set2">
      <xsl:attribute name="text-decoration">underline</xsl:attribute>
    </xsl:attribute-set>
    <test1 xsl:use-attribute-sets="set1">This should fail</test1>
  </out>
</xsl:template>

</xsl:stylesheet>