<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: AVT07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.2 Creating Elements with xsl:element. -->
  <!-- Purpose: Use of Curly brace to set value of html attributes. This was a 
  	   test based on SPR PDIK4D2JCF. Apparently we could not parse the attribute
  	   that had a space in it i.e. Edit Accounts. 	-->

<xsl:output method="html" indent="yes"/>

<xsl:template match="doc">
<HTML>
  <a href="{./link[@desc='Edit Accounts']/@value}"></a>
</HTML>
</xsl:template>
 
</xsl:stylesheet>
