<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
     xmlns:fo="http://www.w3.org/1999/XSL/Format">


<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="myNode">
  <xsl:apply-templates select="myItem"/>
</xsl:template>

<xsl:template match="myItem">
  <xsl:variable name="nodeSet" select="ancestor::myNode[@id='main'] |ancestor::myNode[@id='aux']"/>
  <xsl:if test="$nodeSet">
    <xsl:value-of select="normalize-space($nodeSet)"/>
	<xsl:for-each select="preceding::* | dummy">
		<xsl:text>, </xsl:text><xsl:value-of select="name(preceding-sibling::* | dummy)"/>
	</xsl:for-each>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
