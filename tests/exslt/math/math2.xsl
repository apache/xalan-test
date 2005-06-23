<?xml version="1.0"?> 
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://exslt.org/math"
                extension-element-prefixes="math">

<!-- Test math:acos() -->

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
      ArcCos value of zero is:
      <xsl:value-of select="math:acos($zero)"/><br/>
      ArcCos value of nzero is:
      <xsl:value-of select="math:acos($nzero)"/><br/>
      ArcCos value of num1 is:
      <xsl:value-of select="math:acos($num1)"/><br/>
      ArcCos value of num2 is:
      <xsl:value-of select="math:acos($num2)"/><br/>
      ArcCos value of temp1 is:
      <xsl:value-of select="math:acos($temp1)"/><br/>
      ArcCos value of temp2 is:
      <xsl:value-of select="math:acos($temp2)"/><br/>
      ArcCos value of rad1 is:
      <xsl:value-of select="math:acos($rad1)"/><br/>
      ArcCos value of rad2 is:
      <xsl:value-of select="math:acos($rad2)"/><br/>
      ArcCos value of rad3 is:
      <xsl:value-of select="math:acos($rad3)"/><br/>
      ArcCos value of rad4 is:
      <xsl:value-of select="math:acos($rad4)"/><br/>
      ArcCos value of input1 number is:
      <xsl:value-of select="math:acos($input1)"/><br/>
      ArcCos value of input2 number is:
      <xsl:value-of select="math:acos($input2)"/><br/>
      ArcCos value of input3 number is:
      <xsl:value-of select="math:acos($input3)"/>

   </out>
</xsl:template>

</xsl:stylesheet>