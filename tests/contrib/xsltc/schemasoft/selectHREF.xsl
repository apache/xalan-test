<?xml version="1.0"?>
<!-- Extracts href attributes

Copyright J.M. Vanel 2000 - under GNU public licence 
xt testHREF.xml selectHREF.xslt > selectHREF.txt
 -->

<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
                version="1.0" >

<xsl:output method="text"/>

<xsl:template match ="*" >
  <xsl:apply-templates select="*|@*" />
 </xsl:template>

 <xsl:template match ="@href|@HREF" >
   href=<xsl:value-of select='.' />
 </xsl:template>

 <!-- Suppress element text content : -->
 <xsl:template match ="text()" ></xsl:template>

 <xsl:template match ="/" >
  <xsl:apply-templates/>
 </xsl:template>
</xsl:stylesheet>
