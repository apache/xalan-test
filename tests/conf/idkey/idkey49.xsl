<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkey49 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.4 Generate-ID  -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Test generate-id() when nodes are coming from different
    documents. All id's should be distinct. The first for-each
    prints out info about the document and node value. The
    second loop prints out the id. -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="document(a)//body">
      <xsl:value-of select="."/><xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>&#10;</xsl:text>
    <xsl:for-each select="document(a)//body">
      <xsl:value-of select="generate-id(.)"/><xsl:text>,</xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>