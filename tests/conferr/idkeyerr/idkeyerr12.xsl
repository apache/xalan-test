<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr12 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.2 -->
  <!-- Purpose: It is an error for the "use" attribute on xsl:key to contain a variable reference. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: Extra illegal tokens: 'v1' -->

<xsl:output indent="yes"/>
<xsl:variable name="v1">title</xsl:variable>

<xsl:key name="mykey" match="div" use="$v1" />

<xsl:template match="doc">
  <xsl:value-of select="key('mykey', 'Introduction')/p"/>
  <xsl:value-of select="key('mykey', 'Stylesheet Structure')/p"/>
  <xsl:value-of select="key('mykey', 'Expressions')/p"/>
</xsl:template>

</xsl:stylesheet>
