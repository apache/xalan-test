<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	
  <xsl:variable name="globalVar" select="/doc/body/list/item"/>
	
  <xsl:template match="/">
    <out>
      <item><xsl:value-of select="$globalVar"/></item>
      <item>Text and:<xsl:value-of select="$globalVar"/></item>
    </out>
  </xsl:template>
	
</xsl:stylesheet>
