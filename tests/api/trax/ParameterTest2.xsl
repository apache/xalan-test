<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" indent="yes"/>
  <!-- FileName: ParameterTest2.xsl -->
  <!-- Purpose: Reproduce Bugzilla1611 -->

<xsl:variable name="globalVarAttr" select="/doc/@filename" />

<xsl:template match="/">
  <out>
    <globalVarAttr><xsl:value-of select="$globalVarAttr"/><xsl:text>:</xsl:text><xsl:copy-of select="$globalVarAttr"/></globalVarAttr>
  </out>
</xsl:template>
  
</xsl:stylesheet>