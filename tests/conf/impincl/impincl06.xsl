<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: IMPINCL06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 2.6 -->
  <!-- Purpose: Two includes, each of which has an import. -->

<xsl:include href="b.xsl"/>
<xsl:include href="c.xsl"/>

<xsl:template match="/*">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>
 
<xsl:template match="title">
  Includ01-title: 
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="chapter">
  <xsl:value-of select="@num"/> .  
  <xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>
