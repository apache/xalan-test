<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Filename: MinitestImport.xsl -->
<!-- Author: Paul_Dick@lotus.com -->


<xsl:template match="GrandKids" mode="orginal">
They have <xsl:value-of select="count(*)"/> grandchildren: <xsl:text/>
	<xsl:for-each select="gkid">
		<xsl:value-of select="key('KidInfo',(.))/Name/@First"/>
		<xsl:if test="not(position()=last())">, </xsl:if>
		<xsl:if test="(position()=last()-1)">and </xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template match="GrandKids" mode="document">
They should have <xsl:value-of select="count(document('MinitestDocument.xml')/GrandKids/gkid)"/> grandchildren: <xsl:text/>
	<xsl:for-each select="gkid">
		<xsl:value-of select="key('KidInfo',.)/Name/@First"/>
		<xsl:if test="not(position()=last())">, </xsl:if>
		<xsl:if test="(position()=last()-1)">and </xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template match="GrandKids" mode="doc">
They got <xsl:value-of select="count(document('MinitestDocument.xml')/GrandKids/gkid)"/> grandchildren: <xsl:text/>
	<xsl:for-each select="document('MinitestDocument.xml')/GrandKids/gkid">
		<xsl:value-of select="key('KidInfo',(.))/Name/@First"/>
		<xsl:if test="not(position()=last())">, </xsl:if>
		<xsl:if test="(position()=last()-1)">and </xsl:if>
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>