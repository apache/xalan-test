<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test for invalid value of name attribute in xsl:key. -->
  <!-- ExpectedException: name contains invalid characters -->
  <!-- ExpectedException: Illegal value: + used for QNAME attribute: name -->
  

<xsl:output indent="yes"/>

<xsl:key name="+" match="div" use="title"/>

<xsl:template match="doc">
  <xsl:value-of select="key('+', 'Introduction')/p"/>
  <xsl:value-of select="key('+', 'Stylesheet Structure')/p"/>
  <xsl:value-of select="key('+', 'Expressions')/p"/>
</xsl:template>

</xsl:stylesheet>
