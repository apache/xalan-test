<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: IMPINCL02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 2.6 Combining Stylesheets -->
  <!-- Purpose: Included document's xsl:import (f imports g) is 
       moved into the including document. Import precedence is 
       impincl02, g, h -->

<xsl:import href="h.xsl"/>
<xsl:include href="f.xsl"/>

<xsl:template match="foo">
  <out>
    <good-match/>
  </out>
</xsl:template>

</xsl:stylesheet>
