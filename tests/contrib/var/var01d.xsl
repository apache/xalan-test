<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   
<!-- var01.xsl: variations on a theme:Bugzilla#4218; input .xml is ignored-->

<!-- Theme: passing with-param, somehow variable stack frame gets messed up -->

<!-- The result does not contain <template2> tags because the returned RTF is -->
<!-- converted to a string in which element tags are nixed.  -->

<xsl:template match="/">
    <out>
      <!-- Variables declared at same level as call-template -->
      <xsl:variable name="v1" select="'abc-should-appear-once'"/>
      <xsl:variable name="v2" select="'def-should-appear-once'"/>

      <!-- Param name is same in all cases -->      
      <xsl:call-template name="template1">
        <xsl:with-param name="param1">

          <!-- Theme change: value-of before call-template -->
          <xsl:value-of select="$v2"/><xsl:text>,</xsl:text>
          <xsl:call-template name="template2">
            <xsl:with-param name="param1" select="$v1"/>
          </xsl:call-template>
        </xsl:with-param>

      </xsl:call-template>

    </out>
  </xsl:template>
 
  <xsl:template name="template1">
    <xsl:param name="param1" select="'error'"/>
    <template1><xsl:value-of select="$param1"/><xsl:text>!!</xsl:text></template1>
  </xsl:template>
 
  <xsl:template name="template2">
    <xsl:param name="param1" select="'error'"/>
    <template2><xsl:value-of select="$param1"/><xsl:text>.</xsl:text></template2>
  </xsl:template>

</xsl:stylesheet>
