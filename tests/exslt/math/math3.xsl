<?xml version="1.0"?> 
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://exslt.org/math"
                extension-element-prefixes="math">

<!-- Test math:asin() -->

<xsl:variable name="zero" select="0"/>
<xsl:variable name="nzero" select="-0"/>
<xsl:variable name="num1" select="1.99"/>
<xsl:variable name="num2" select="3.1428475"/>
<xsl:variable name="temp1" select="-7"/>
<xsl:variable name="temp2" select="-9.99999"/>
<xsl:variable name="rad1" select="1.0"/>
<xsl:variable name="rad2" select="25"/>
<xsl:variable name="rad3" select="0.253"/>
<xsl:variable name="rad4" select="-0.888"/>
<xsl:variable name="input1" select="number(//number[1])"/>
<xsl:variable name="input2" select="number(//number[2])"/>
<xsl:variable name="input3" select="$input1 div $zero"/>

<xsl:template match="/">
   <out>
      ArcSin value of zero is:
      <xsl:value-of select="math:asin($zero)"/><br/>
      ArcSin value of nzero is:
      <xsl:value-of select="math:asin($nzero)"/><br/>
      ArcSin value of num1 is:
      <xsl:value-of select="math:asin($num1)"/><br/>
      ArcSin value of num2 is:
      <xsl:value-of select="math:asin($num2)"/><br/>
      ArcSin value of temp1 is:
      <xsl:value-of select="math:asin($temp1)"/><br/>
      ArcSin value of temp2 is:
      <xsl:value-of select="math:asin($temp2)"/><br/>
      ArcSin value of rad1 is:
      <xsl:value-of select="math:asin($rad1)"/><br/>
      ArcSin value of rad2 is:
      <xsl:value-of select="math:asin($rad2)"/><br/>
      ArcSin value of rad3 is:
      <xsl:value-of select="math:asin($rad3)"/><br/>
      ArcSin value of rad4 is:
      <xsl:value-of select="math:asin($rad4)"/><br/>
      ArcSin value of input1 number is:
      <xsl:value-of select="math:asin($input1)"/><br/>
      ArcSin value of input2 number is:
      <xsl:value-of select="math:asin($input2)"/><br/>
      ArcSin value of input3 number is:
      <xsl:value-of select="math:asin($input3)"/>
   </out>
</xsl:template>

</xsl:stylesheet>