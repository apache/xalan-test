<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.lib.Redirect"
    extension-element-prefixes="redirect">

  <lxslt:component prefix="redirect" elements="write open close" functions="">
    <lxslt:script lang="javaclass" src="org.apache.xalan.lib.Redirect"/>
  </lxslt:component>  

  <xsl:param name="redirectOutputName" select="'Redirect1330.out'"/>
    
  <xsl:template match="doc">
    <out>
      <xsl:message>Your main output document should have a main-doc-comment and a main-doc-elem</xsl:message>
      <xsl:comment>main-doc-comment</xsl:comment>
        <redirect:write select="$redirectOutputName">
          <out>
            <xsl:message>Your redirected document <xsl:value-of select="$redirectOutputName"/> should have a redirect-doc-comment and a redirect-doc-elem</xsl:message>
            <xsl:comment>redirect-doc-comment</xsl:comment>
            <redirect-doc-elem/>
          </out>
        </redirect:write>
      <main-doc-elem/>
    </out>
  </xsl:template>
</xsl:stylesheet>
