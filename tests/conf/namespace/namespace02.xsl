<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output indent="yes"/>

  <!-- FileName: NSPC02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.4 -->
  <!-- Purpose: Apply namespaces to elements. We differ from XT here due to the
       fact that we declare necessary namespace nodes on parent nodes of 
       subelements, this allows for simplier tracking of namespace nodes. -->

<xsl:template match="/">
  <out xmlns:anamespace="foo.com">
    <p><xsl:element name="test" namespace="foo.com"/></p>
    <p><xsl:element name="test" namespace="baz.com"/>
       <xsl:element name="foo" namespace="baz.com"/>
    </p>
  </out>
</xsl:template>
 
</xsl:stylesheet>
