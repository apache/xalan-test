<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: output79 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 16.1 XML Output Method -->
  <!-- Purpose: Test ISO-8859-6 encoding. -->

<xsl:output encoding="ISO-8859-6"/>

<xsl:template match="doc">
  <out><xsl:text>&#10;</xsl:text>
     <xsl:for-each select="test">
		<xsl:value-of select="."/><xsl:text>&#10;</xsl:text>	
     </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>