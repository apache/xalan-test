<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: whitespace20 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.4 Whitespace Stripping -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: xml:space attributes need to be perserved. -->

<xsl:output indent="yes"/>

<xsl:template match="/">
 <root>
   <out xml:space="default">        Test of xml:space w/default         </out>
   <out xml:space="preserve">       Test of xml:space w/perserve        </out>
 </root>
</xsl:template>

</xsl:stylesheet>