<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: AVT02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 7.6.2 -->
  <!-- Purpose: Test two AVTs with literal element between 
       them (based on example in the spec). -->

<xsl:template match="photograph">
  <xsl:variable name="image-dir">/images</xsl:variable>
  <out src="{$image-dir}/{href}" width="{size/@width}"/>
</xsl:template>
 
</xsl:stylesheet>
