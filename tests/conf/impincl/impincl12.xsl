<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: impincl12 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.6.1 Stylesheet Inclusion -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Test of basic Import & Include functionality over the network. -->

<!-- xsl:import href="http://www.oucs.ox.ac.uk/stylesheets/teihtml-param.xsl"/ -->
<xsl:import href="http://spitfire.lotus.com/Testsuite/Stylesheets/t12imp.xsl"/>
<xsl:include href="http://spitfire.lotus.com/Testsuite/Stylesheets/t12inc.xsl"/>

<xsl:template match="root-tag">
 <out>
  <xsl:apply-templates/>
 </out>
</xsl:template>

</xsl:stylesheet>