<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:javaElem="javaElem01"
    extension-element-prefixes="javaElem"
    exclude-result-prefixes="lxslt">

  <lxslt:component prefix="javaElem" 
        elements="putString putBoolean putDouble putNode" functions="getCounter">
    <lxslt:script lang="javaclass" src="javaElem01"/>
  </lxslt:component>  

<xsl:output method="xml" indent="yes"/>
                
  <xsl:template match="doc">
    <out>
      <extension-string>
        <javaElem:putString attr="attr-String"/>
      </extension-string> 
      <extension-ctr><xsl:value-of select="javaElem:getCounter()"/></extension-ctr> 
      <extension-boolean>
        <javaElem:putBoolean attr="attr-Boolean"/>
      </extension-boolean> 
      <extension-ctr><xsl:value-of select="javaElem:getCounter()"/></extension-ctr> 
      <extension-double>
        <javaElem:putDouble attr="attr-Double"/>
      </extension-double> 
      <extension-ctr><xsl:value-of select="javaElem:getCounter()"/></extension-ctr> 
      <extension-node>
        <javaElem:putNode attr="attr-Node"/>
      </extension-node> 
      <extension-ctr><xsl:value-of select="javaElem:getCounter()"/></extension-ctr> 
    </out>
  </xsl:template>
 
</xsl:stylesheet>
