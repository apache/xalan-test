<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MESSAGEerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 13 Messages -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Put xsl:message at top level, which is illegal. -->
  <!-- ExpectedException: xsl:message is not allowed in this position in the stylesheet -->

<xsl:message terminate="no">This should not appear</xsl:message>

<xsl:template match="/">
 <out>This should fail</out>
</xsl:template>

</xsl:stylesheet>