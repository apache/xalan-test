<?xml version="1.0"?>

<!-- xt sampleXMLSchema.xml XMLSchema2c++.xslt -->

<xsl:stylesheet
 xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
 xmlns:xsch="http://www.w3.org/1999/XMLSchema" 
                 version="1.0" >

<xsl:output method="text"/>

<xsl:template match  = '/' >
typedef int integer;
   <xsl:apply-templates/>
</xsl:template>
<xsl:template match  = 'text()' >
</xsl:template>

<xsl:template match="@*|*">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match = 'xsch:complexType' >
class <xsl:value-of select="@name"/> 
  <xsl:if test="@derivedBy='extension'" > : public <xsl:value-of select="@source"/>
  </xsl:if>
{
  <xsl:if test="@type" >
    <xsl:value-of select="@type"/> content;
  </xsl:if>
<xsl:apply-templates/>
};
</xsl:template>

<xsl:template match = 'xsch:element' >
<xsl:variable name="type">
<xsl:value-of select="@type"/>
</xsl:variable>
<xsl:choose>
 <xsl:when test="$type != ''">
  <xsl:value-of select="$type"/>
 </xsl:when>
 <xsl:otherwise>
  no_type_specified 
 </xsl:otherwise>
</xsl:choose>
<xsl:text> </xsl:text><xsl:value-of select="@name"/> ;
</xsl:template>

</xsl:stylesheet>

