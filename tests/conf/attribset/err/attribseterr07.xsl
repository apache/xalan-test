<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribseterr07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.4 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Set attributes of element, using an attribute set that doesn't exist. -->
  <!-- NOTE that the spec is unclear about what behavior is required! In a private email,
       James Clark said that it should be an error. Erratum to come? -->
  <!-- ExpectedException: attribute-set named set2 does not exist -->

<xsl:template match="/">
  <out>
    <xsl:element name="test" use-attribute-sets="set1 set2"/>
  </out>
</xsl:template>

<xsl:attribute-set name="set1">
  <xsl:attribute name="color">black</xsl:attribute>
</xsl:attribute-set>

</xsl:stylesheet>
