<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: avterr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.6.2 -->
  <!-- Purpose: Test of nested curly braces. Not allowed. -->
  <!-- ExpectedException: XSL Warning: Found '}' but no attribute template open! -->

<xsl:template match="doc">
  <out href="{{'all'}{'done'}}"/>
</xsl:template>

</xsl:stylesheet>