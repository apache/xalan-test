<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

  <!-- FileName: attribvaltemplate08 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.2 Creating Elements with xsl:element. -->
  <!-- Purpose: Compare the results of attribute value generated by AVT vs 
       xsl:value-of, with the output specified to be html. We differ from both
       XT and Saxon on this.                                    			  -->

<xsl:template match="doc/problem">
 <out value="{@title}">
	<xsl:value-of select="@title"/>
 </out>
</xsl:template>

</xsl:stylesheet>