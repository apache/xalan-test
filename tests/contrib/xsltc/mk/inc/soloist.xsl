<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<html><body>
<xsl:apply-templates/>
</body></html>
</xsl:template>


<xsl:template match="para">
   <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match="publication">
   <font face="arial"><xsl:apply-templates/></font>
</xsl:template>

<xsl:template match="quote">
   <xsl:text/>"<xsl:apply-templates/>"<xsl:text/>
</xsl:template>

<xsl:template match="work">
   <i><xsl:apply-templates/></i>
</xsl:template>

<xsl:template match="role">
   <u><xsl:apply-templates/></u>
</xsl:template>

</xsl:stylesheet>
