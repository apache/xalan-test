<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.2 -->
  <!-- Purpose: Test for missing match attribute in xsl:key. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: xsl:key requires a match attribute! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: xsl:key requires a match attribute! -->
  <!-- ExpectedException: xsl:key requires attribute: match -->

<xsl:output indent="yes"/>

<xsl:key name="mykey" use="title"/>

<xsl:template match="doc">
  <xsl:value-of select="key('mykey', 'Introduction')/p"/>
  <xsl:value-of select="key('mykey', 'Stylesheet Structure')/p"/>
  <xsl:value-of select="key('mykey', 'Expressions')/p"/>
</xsl:template>

</xsl:stylesheet>
