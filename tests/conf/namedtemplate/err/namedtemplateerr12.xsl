<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr12 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 Named Templates -->
  <!-- Purpose: Error to have a stylesheet contain more then one template
     with the same name and same import precedence. -->
  <!-- Author: Paul Dick -->
  <!-- ExpectedException: There is already a template named: a -->
  <!-- ExpectedException: Found more than one template named: a -->

<xsl:import href="a.xsl"/>

<xsl:template match="doc">
  <out>
    <xsl:call-template name="a"/>
	<xsl:apply-imports/>
  </out>
</xsl:template>

<xsl:template name="a">
  a
</xsl:template>

<xsl:template name="a">
  b
</xsl:template>

</xsl:stylesheet>