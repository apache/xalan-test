<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!-- 
Bugzilla1009: Malformed attribute expression lacks line/column information 
http://nagoya.apache.org/bugzilla/show_bug.cgi?id=1009
bevan.arps@clear.net.nz (Bevan Arps)
-->

  <xsl:template match="doc">
    <out>
	<!-- Below line causes error due to unclosed '{', but:
		 error is not helpful either on cmd line or programmatically -->
	<link ref="{foo(bar)"></link>
	<out>
  </xsl:template>
     
</xsl:stylesheet>
