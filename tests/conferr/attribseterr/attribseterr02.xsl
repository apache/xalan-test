<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribseterr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.4 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Set up circular references of attribute-sets using each other -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: xsl:attribute-set 'set1' used itself, which will cause an infinite loop. -->
  <!-- ExpectedException: xsl:attribute-set set1 used itself, which will cause an infinite loop. -->

<xsl:template match="/">
    <out>
      <test1 xsl:use-attribute-sets="set1"></test1>
    </out>
</xsl:template>

<xsl:attribute-set name="set2" use-attribute-sets="set3">
  <xsl:attribute name="text-decoration">underline</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="set1" use-attribute-sets="set2">
  <xsl:attribute name="color">black</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="set3" use-attribute-sets="set1">
  <xsl:attribute name="font-size">14pt</xsl:attribute>
</xsl:attribute-set>

</xsl:stylesheet>