<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:lxslt="http://xml.apache.org/xslt"
                xmlns:extension1288="MyCounter"
                extension-element-prefixes="extension1288"
                version="1.0">

  <lxslt:component prefix="extension1288"
                   elements="" functions="read">
    <lxslt:script lang="javaclass" src="Bugzilla1288"/>
  </lxslt:component>

  <!-- XSL variable declaration -->
  <xsl:variable name="var">
  <history/>
  </xsl:variable>

  <!-- Extension function call -->
  <xsl:template match="/">
    <out>
      <p>Extension output below:</p>
      <xsl:variable name="result" select="extension1288:run($var)"/>
      <xsl:copy-of select="$var"/>
      <p>Extension output above:</p>
    </out>
  </xsl:template>
 
</xsl:stylesheet>
