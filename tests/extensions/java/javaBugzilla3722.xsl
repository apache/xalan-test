<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:lxslt="http://xml.apache.org/xslt"
                xmlns:bug3722="javaBugzilla3722-namespace"
                extension-element-prefixes="bug3722"
                version="1.0">

  <lxslt:component prefix="bug3722"
                   functions="dumpConfig">
    <lxslt:script lang="javaclass" src="javaBugzilla3722"/>
  </lxslt:component>

  <xsl:template match="/doc">
    <out>
      <xsl:apply-templates/>
    </out>
  </xsl:template>

   <xsl:template match="matcher">
      <xsl:variable name="config"><xsl:copy-of select="."/></xsl:variable>
      <xsl:value-of select="bug3722:dumpConfig($config)"/>
   </xsl:template>
 
</xsl:stylesheet>
