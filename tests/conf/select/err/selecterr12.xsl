<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr12 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 8 Repetition -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to use a string where a node-set is needed in for-each.-->
  <!-- ExpectedException: XPATH: Can not convert #UNKNOWN to a NodeList -->

<xsl:variable name="which" select="'fish'"/>

<xsl:template match="/doc">
  <out>
    <xsl:for-each select="$which">
      <xsl:value-of select="name"/><xsl:text>,</xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
