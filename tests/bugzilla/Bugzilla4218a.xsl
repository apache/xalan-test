<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">   
<!-- Reproduce Bugzilla#4218, apply this to identity.xml or any file -->  
<!--
The following stylesheet puts out <out>abcabc</out>, which is absolutely
incorrect.  If you uncomment the commented value-of and variable, it results
"Variable accessed before it is bound!" error message.  The bug seems to be
related to the inner call-template... it looks like something in the stack
frame is not being restored???
-->
<xsl:template match="/">
    <out>
      <xsl:variable name="v1" select="'abc-should-appear-once'"/>
      <xsl:variable name="v2" select="'def-should-appear-once'"/>
     
      <!-- Comment this in, along with the value-of below,
           to get error message: Variable accessed before it is bound! 
           See Bugzilla4218.xsl -->
      <xsl:variable name="v3" select="'ghi-should-appear-once'"/>
     
      <xsl:call-template name="test-template">
        <xsl:with-param name="p1">
          <xsl:call-template name="xyz-template">
            <xsl:with-param name="p1" select="$v1"/>
          </xsl:call-template>
          <xsl:value-of select="$v2"/>
          <!-- Comment this in along with the v3 variable above to
               get error message! See Bugzilla4218.xsl -->
          <xsl:value-of select="$v3"/>
        </xsl:with-param>
      </xsl:call-template>
    </out>
  </xsl:template>
 
  <xsl:template name="test-template">
    <xsl:param name="p1" select="'error'"/>
    <test-template><xsl:value-of select="$p1"/></test-template>
  </xsl:template>
 
  <xsl:template name="xyz-template">
    <xsl:param name="p1" select="'error'"/>
    <xyz-template><xsl:value-of select="$p1"/></xyz-template>
  </xsl:template>
</xsl:stylesheet>