<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: mdocs19 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.1 Multiple Source Documents E14 -->
  <!-- AdditionalSpec: http://www.w3.org/1999/11/REC-xslt-19991116-errata/#E14 -->
  <!-- Creator: Christine Li -->
  <!-- Purpose: Test document() function: Provides multiple input sources. -->
  <!-- Two arguments: string, node-set. The second node-set is empty -->
  <!-- It is an error, recover by returning an empty node-set -->

<xsl:template match="defaultcontent">
  <out>
    <xsl:copy-of select="document('mdocs19a.xml',empty)"/>
  </out>
</xsl:template>

</xsl:stylesheet>