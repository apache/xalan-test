<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr10 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test for use of key() without a corresponding xsl:key declaration. -->
  <!-- ExpectedException: There is no xsl:key declaration for mykey -->

<xsl:output indent="yes"/>

<xsl:key name="mykeyspace" match="div" use="title"/>

<xsl:template match="doc">
  <xsl:value-of select="key('mykey', 'Introduction')/p"/>
  <xsl:value-of select="key('mykey', 'Stylesheet Structure')/p"/>
  <xsl:value-of select="key('mykey', 'Expressions')/p"/>
</xsl:template>

</xsl:stylesheet>
