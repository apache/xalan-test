<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     version="1.0">
<xsl:output method="text"/>

<!-- Author: Bob DuCharme -->
<!-- Reference:  http://www.xml.com/pub/a/2001/05/07/xsltmath.html -->
<!-- Description: Compute pi. Based on Leibniz's algorithm that 
       pi/4 = 1 - 1/3 + 1/5 - 1/7 + 1/9 - 1/11... which I did as
       pi = 4 - 4/3 + 4/5 - 4/7 + 4/9 - 4/11...
-->

<xsl:variable name="iterations" select="4500"/>

<xsl:template name="pi">
  <!-- named template called by main template below -->
  <xsl:param name="i">1</xsl:param>
  <xsl:param name="piValue">0</xsl:param>

  <xsl:choose>
  <!-- If there are more iterations to do, add the passed
       value of pi to another round of calculations. -->
  <xsl:when test="$i &lt;= $iterations">
    <xsl:call-template name="pi">
      <xsl:with-param name="i" select="$i + 4"/>
      <xsl:with-param name="piValue" 
           select="$piValue + (4 div $i) - (4 div ($i + 2))"/>
    </xsl:call-template>
  </xsl:when>

  <!-- If no more iterations to do, add 
       computed value to result tree. -->
  <xsl:otherwise>
   <xsl:value-of select="$piValue"/>
  </xsl:otherwise>

  </xsl:choose>

</xsl:template>


<xsl:template match="/">
  <xsl:call-template name="pi"/>
</xsl:template>

</xsl:stylesheet>

