<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MESSAGEerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 13 -->
  <!-- Purpose: Use "yes" on terminate option, causing exception -->                
  <!-- Creator: David Marston -->
  <!-- ExpectedException: This message came from the MESSAGE03 test. -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: This message came from the MESSAGE03 test. -->

<xsl:template match="/">
  <xsl:message terminate="yes">This message came from the MESSAGE03 test.</xsl:message>
</xsl:template>

</xsl:stylesheet>
