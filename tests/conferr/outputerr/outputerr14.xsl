<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: OUTPerr14 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 16.3 Text Output Method -->
  <!-- Purpose: Attempt text output of characters above 127 when encoding doesn't support them. -->
  <!-- "If the result tree contains a character that cannot be represented in the encoding
       that the XSLT processor is using for output, the XSLT processor should signal an error." -->
  <!-- ExpectedException: Attempt to output character not represented in specified encoding. -->
  <!-- ExpectedException: Attempt to output character of integral value 264 that is not represented in specified output encoding of US-ASCII. -->

<xsl:output method="text" encoding="US-ASCII"/>

<xsl:template match="doc">
  <xsl:apply-templates select="dat"/><xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="dat">
  <xsl:text>&#264;</xsl:text><xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>
