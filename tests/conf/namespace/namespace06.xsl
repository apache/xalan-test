<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" lotus:foo="baz"
                xmlns:lotus="http://www.lotus.com">

  <!-- FileName: Nspc06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.1 XSLT Namespace -->
  <!-- Purpose:  Testing an attribute not from the XSLT namespace,
       which is legal provided that the expanded name of the attribute
       has a non-null namespace URI. -->

<xsl:template match="/" lotus:QE="ped">
  <out xmlns:foo="http://foo.com">
    <xsl:copy-of select="doc" foo:test="0"/>
  </out>
</xsl:template>
 
</xsl:stylesheet>
