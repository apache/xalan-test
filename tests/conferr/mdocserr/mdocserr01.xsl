<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MDOCSerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.1 Multiple Source Documents -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test document() function with non-existent file. -->
  <!-- ExpectedException: File not found -->

<xsl:template match="defaultcontent">
  <out>
    <xsl:apply-templates select="document('notfound.xml')/body">
      <xsl:with-param name="arg1">ok</xsl:with-param>
    </xsl:apply-templates>
  </out>
</xsl:template>

<xsl:template match="body">
  <xsl:param name="arg1">not ok</xsl:param>
  <xsl:value-of select="$arg1"/>
</xsl:template>

</xsl:stylesheet>