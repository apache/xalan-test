<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.lib.Redirect"
    extension-element-prefixes="redirect"
    exclude-result-prefixes="lxslt">


<!-- Testing redirect:write append="" attribute -->

  <lxslt:component prefix="redirect" elements="write open close" functions="">
    <lxslt:script lang="javaclass" src="org.apache.xalan.lib.Redirect"/>
  </lxslt:component>  
    
  <xsl:template match="/doc">
    <standard-out>
      <p>Standard output:</p>
      <xsl:apply-templates select="list"/>
    </standard-out>
  </xsl:template>

  <xsl:template match="list">
      <xsl:apply-templates select="item"/>
  </xsl:template>

  <!-- redirected, using append -->
  <xsl:template match="item">
    <!-- Note append is treated as avt -->
    <redirect:write select="@file" append="{@append}">
      <item-out>
        <append><xsl:value-of select="@append"/></append>
        <data><xsl:value-of select="."/></data>
      </item-out>
    </redirect:write>
  </xsl:template>
  
</xsl:stylesheet>
