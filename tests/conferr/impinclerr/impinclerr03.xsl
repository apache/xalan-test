<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: ImpInclerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.6.2 Stylesheet Import -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Imports must precede all other elements, including 
  	   Includes, in a stylesheet. -->
  <!-- ExpectedException: xsl:import is not allowed in this position in the stylesheet -->

<xsl:include href="f.xsl"/>
<xsl:import href="h.xsl"/>

<xsl:template match="doc">
  <out>
  </out>
</xsl:template>

</xsl:stylesheet>
