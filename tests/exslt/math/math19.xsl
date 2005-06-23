<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://exslt.org/math"
                exclude-result-prefixes="math"
                version="1.0">
<!-- Using math:max() within a xsl:template name -->
                
<xsl:output method="html" encoding="ISO-8859-1" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"  />
 
<xsl:template match="/">
   <xsl:call-template name = "calcul" >
      <xsl:with-param name="list" select="items/item" />
   </xsl:call-template>
</xsl:template>		  
		  
<xsl:template name="calcul">
   <xsl:param name="list"/>
   <xsl:choose>
      <xsl:when test="$list"><xsl:value-of select="math:max($list)" /></xsl:when>		 
      <xsl:otherwise>0</xsl:otherwise>
   </xsl:choose>		 
</xsl:template>
</xsl:stylesheet>