<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: WHTE09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 3.4 -->
  <!-- Purpose: Test output of newline via CDATA section in template. -->

<xsl:template match="/"><out>
  <![CDATA[
      ]]>
      </out></xsl:template>
 
</xsl:stylesheet>
