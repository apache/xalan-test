<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:foo="http://foo.com">

  <!-- FileName: namedtemplateerr14 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to set local part of QName to null string. -->
  <!-- ExpectedException: Invalid template name -->
  <!-- ExpectedException: Illegal value: foo: used for QNAME attribute: name -->


<xsl:template match="doc">
  <out>
    <xsl:call-template name="foo:">
      <xsl:with-param name="pvar1" select="doc/a"/>
    </xsl:call-template>
  </out>
</xsl:template>

<xsl:template name="foo:tmplt1">
  <xsl:param name="pvar1">Default text in pvar1</xsl:param>
  <xsl:value-of select="$pvar1"/>
</xsl:template>

<xsl:template name="foo:">
  <xsl:text>Empty-named template got called!</xsl:text>
</xsl:template>

</xsl:stylesheet>
