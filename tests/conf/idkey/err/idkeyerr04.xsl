<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.2 -->
  <!-- Purpose: Test for two name attributes in xsl:key. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: Attribute "name" was already specified for element "xsl:key". -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: Attribute "name" was already specified for element "xsl:key". -->

<xsl:output indent="yes"/>

<xsl:key name="mykey" match="div" use="title" name="otherkey" />

<xsl:template match="doc">
  <xsl:value-of select="key('mykey', 'Introduction')/p"/>
  <xsl:value-of select="key('mykey', 'Stylesheet Structure')/p"/>
  <xsl:value-of select="key('mykey', 'Expressions')/p"/>
</xsl:template>

</xsl:stylesheet>
