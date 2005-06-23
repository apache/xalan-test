<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:str="http://exslt.org/strings"
                extension-element-prefixes="str">

<xsl:template match="/">
   <out>
   <xsl:apply-templates select="a/c"/>
   <xsl:apply-templates select="a/c/d"/>
   </out>
</xsl:template>

<xsl:template match="a/c/d">
   <xsl:value-of select="str:padding(20, 'Xn')" />
   <xsl:value-of select="." />
</xsl:template>

<xsl:template match="a/c">
   <xsl:value-of select="str:padding(20, 'yZaB')" />
   <xsl:value-of select="." />
</xsl:template>

</xsl:stylesheet>