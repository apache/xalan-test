<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
				xmlns:ped="http://ped.test.com"
				xmlns:bdd="http://bdd.test.com">

<xsl:output indent="yes"/>

  <!-- FileName: attribset24 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.3 Creating Attributes -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: The namespace attribute is interpreted as an attribute value template. -->

<xsl:template match="/">
 <root>
  <Out>
	<xsl:attribute name="{docs/a}" namespace="http://ped.test.com">YoBaby</xsl:attribute>
	<xsl:attribute name="{docs/b}" namespace="http://ped.test.com">jaminben</xsl:attribute>
  </Out>
 </root>
</xsl:template>

</xsl:stylesheet>