<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr11 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 8 Repetition -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to use a boolean where a node-set is needed in for-each.-->
  <!-- ExpectedException: XPATH: Can not convert #UNKNOWN to a NodeList -->
  <!-- ExpectedException: Can not convert #BOOLEAN to a NodeList! -->

<xsl:template match="/">
  <out>
    <xsl:for-each select="true()">
      <xsl:value-of select="."/>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
