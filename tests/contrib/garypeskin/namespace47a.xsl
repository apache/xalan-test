<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
                xmlns:ixsl="http://www.w3.org/1999/XSL/TransformAlias"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- FileName: namespace47a -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.1 Literal Result Elements -->
  <!-- Creator: Gary L Peskin based on test case from Jens Lautenbacher -->
  <!-- Purpose: Included stylesheet for test case namespace47. -->

  <xsl:template match="gen_a">
    <ixsl:template>
      <xsl:attribute name="match"><xsl:value-of select="@name"/></xsl:attribute>
      <ixsl:text>Recognized <xsl:value-of select="@name"/></ixsl:text>
    </ixsl:template>
  </xsl:template>
   
</xsl:stylesheet>
