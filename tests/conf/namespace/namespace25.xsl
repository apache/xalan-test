<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:foo="aaa">

  <!-- FileName: nspc25 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.1 Literal Result Elements -->
  <!-- Purpose: Simple case of creating LRE with nested namespace
       declarations. Bugzilla[105]. -->

<xsl:template match="/">
    <foo:stuff xmlns:foo="bbb">
      <foo:stuff xmlns:foo="ccc"/>
    </foo:stuff>
</xsl:template>

</xsl:stylesheet>


