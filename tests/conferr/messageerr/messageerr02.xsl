<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MESSAGEerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 199911116 -->
  <!-- Section: 13 -->
  <!-- Purpose: Illegal value "duh" on terminate option -->                
  <!-- Creator: David Marston -->
  <!-- ExpectedException: Value for terminate should equal yes or no -->
  <!-- ExpectedException: Illegal value: duh used for boolean attribute: terminate -->

<xsl:template match="/">
  <xsl:message terminate="duh">This message came from the MESSAGEerr02 test.</xsl:message>
</xsl:template>

</xsl:stylesheet>
