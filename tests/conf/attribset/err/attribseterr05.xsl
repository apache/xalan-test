<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribseterr05 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.4 -->
  <!-- Purpose: Put illegal instructions in xsl:attribute-set. -->
  <!-- ExpectedException: ElemTemplateElement error: Can not add xsl:apply-templates to xsl:attribute-set -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: ElemTemplateElement error: Can not add xsl:apply-templates to xsl:attribute-set -->

<xsl:template match="/">
  <out>
    <xsl:element name="test" use-attribute-sets="set1"/>
  </out>
</xsl:template>

<xsl:attribute-set name="set1">
  <xsl:attribute name="color">black</xsl:attribute>
  <xsl:apply-templates/>
</xsl:attribute-set>

</xsl:stylesheet>
