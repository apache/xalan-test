<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: generated03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.4 -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Test generate-id() when nodes are coming from different documents. -->
  <!-- Elaboration: All IDs should be distinct. The first for-each prints out info about the document
    and node value. The second loop prints out the ID. Exact strings will vary by processor. All should
    meet the constraints of XML names. Use this test to catch unexplained changes in the naming scheme. -->

<xsl:output method="xml" encoding="UTF-8" indent="no"/>

<xsl:template match="doc">
  <out>
   <values>
    <xsl:for-each select="document(a)//body">
      <xsl:value-of select="."/><xsl:text>,  </xsl:text>
    </xsl:for-each></values>
    <xsl:text>&#10;</xsl:text>
   <ids>
    <xsl:for-each select="document(a)//body">
      <xsl:value-of select="generate-id(.)"/><xsl:text>,  </xsl:text>
    </xsl:for-each></ids>
  </out>
</xsl:template>

</xsl:stylesheet>