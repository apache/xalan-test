<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Author:  Chris Rathman -->
<!-- Reference:  http://www.angelfire.com/tx4/cus/notes/xslfactorial.html -->
<!-- Description:  Computes factorial as a demonstration of recursion -->

<xsl:output method="text"/>

<!-- x := 5 -->
<xsl:variable name="x" select="5"/>

<!-- y := factorial(x) -->
<xsl:variable name="y">
<xsl:call-template name="factorial">
<xsl:with-param name="n" select="$x"/>
</xsl:call-template>
</xsl:variable>

<!-- factorial(n) - compute the factorial of a number -->
<xsl:template name="factorial">
<xsl:param name="n" select="1"/>
<xsl:variable name="sum">
<xsl:if test="$n = 1">1</xsl:if>
<xsl:if test="$n != 1">
<xsl:call-template name="factorial">
<xsl:with-param name="n" select="$n - 1"/>
</xsl:call-template>
</xsl:if>
</xsl:variable>
<xsl:value-of select="$sum * $n"/>
</xsl:template>

<!-- output the results -->
<xsl:template match="/">
<xsl:text>factorial(</xsl:text>
<xsl:value-of select="$x"/>
<xsl:text>) = </xsl:text>
<xsl:value-of select="$y"/>
<xsl:text>&#xA;</xsl:text>

<!-- calculate another factorial for grins -->
<xsl:text>factorial(4) = </xsl:text>
<xsl:call-template name="factorial">
<xsl:with-param name="n" select="5"/>
</xsl:call-template>
<xsl:text>&#xA;</xsl:text>
</xsl:template>

</xsl:stylesheet>

