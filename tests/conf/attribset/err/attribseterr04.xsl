<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribseterr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.4 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Omit name attribute in xsl:attribute-set. -->
  <!-- ExpectedException: xsl:attribute-set must have a name attribute. -->
  <!-- ExpectedException: xsl:attribute-set requires attribute: name -->
<xsl:template match="/">
  <out>
    <xsl:element name="test" use-attribute-sets="set1"/>
  </out>
</xsl:template>

<xsl:attribute-set>
  <xsl:attribute name="color">black</xsl:attribute>
</xsl:attribute-set>

</xsl:stylesheet>
