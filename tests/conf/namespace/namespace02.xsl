<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output indent="yes"/>

  <!-- FileName: namespace02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.4 -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Apply namespaces to elements. Location of declaration is
     allowed to vary, as long as it's in scope when needed. -->

<xsl:template match="/">
  <out xmlns:anamespace="foo.com">
    <p><xsl:element name="test" namespace="foo.com"/></p>
    <p><xsl:element name="test" namespace="baz.com"/>
       <xsl:element name="foo" namespace="baz.com"/>
    </p>
  </out>
</xsl:template>

</xsl:stylesheet>
