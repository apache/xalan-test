<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:ped="http://www.tester.com">

  <!-- FileName: lreerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.3 Creating Attributes with xsl:attribute. -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Test error reporting if required attribute of xsl:attribute
       is not specified.-->

<xsl:template match="doc">
  <xsl:element name="test">
    <xsl:attribute>Hello</xsl:attribute>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>
