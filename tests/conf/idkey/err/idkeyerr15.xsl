<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr15 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Call key() with too many arguments. -->
  <!-- ExpectedException: key() requires two arguments -->
  <!-- ExpectedException: FuncKey only allows 2 arguments -->

<xsl:output indent="yes"/>

<xsl:key name="mykey" match="div" use="title" />

<xsl:template match="doc">
  <xsl:value-of select="key('mykey','Expressions','useless')/p"/>
</xsl:template>

</xsl:stylesheet>
