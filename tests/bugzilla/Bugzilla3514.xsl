<?xml version="1.0" ?> 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- "computational (recursive) template is very slow compared to xalan 1.2.2" c.mallwitz@intershop.de -->
  <xsl:template match="/">
    <foo>
    <xsl:call-template name="func1">
      <xsl:with-param name="list" select="//x" /> 
    </xsl:call-template>
    </foo>
  </xsl:template>

  <xsl:template name="func1">
    <xsl:param name="list" /> 
    <xsl:choose>
      <xsl:when test="$list">
        <xsl:call-template name="func1">
          <xsl:with-param name="list" select="$list[position()!=1]" /> 
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>0</xsl:otherwise> 
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>