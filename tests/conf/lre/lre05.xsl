<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
				xmlns:ped="http://tester.com">

<xsl:import href="implre05.xsl"/>
<xsl:include href="inclre05.xsl"/>

  <!-- FileName: lre05 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.1 Literal Result Elements-->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: a subtree rooted at an xsl:stylesheet element does not include any 
       stylesheets imported or included by children of that xsl:stylesheet element.	-->

<xsl:template match="doc1">
  <out xsl:exclude-result-prefixes="ped">
     <xsl:apply-imports/>
     <xsl:apply-templates select="doc3"/>
  </out>
</xsl:template>

</xsl:stylesheet>
