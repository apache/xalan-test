<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribvaltemplate07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.2 Creating Elements with xsl:element. -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Use of Curly brace to set value of HTML attributes. Predicate and quotes inside. -->

<xsl:output method="html" indent="yes"/>

<xsl:template match="doc">
<HTML>
  <a href="{./link[@desc='Edit Accounts']/@value}"></a>
</HTML>
</xsl:template>

</xsl:stylesheet>
