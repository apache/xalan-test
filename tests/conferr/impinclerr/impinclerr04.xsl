<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: ImpInclerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.6.1 Style Inclusion -->
  <!-- Creator: David Marston -->
  <!-- Purpose: It is an error for a stylesheet to include itself. -->
  <!-- ExpectedException: (StylesheetHandler) file:/E:\builds\testsuite\.\conf\impincl\err\impinclerr04.xsl is directly or indirectly importing itself! -->
  <!-- ExpectedException: (StylesheetHandler) file:E:/builds/testsuite/conf/impincl/err/impinclerr04.xsl is directly or indirectly importing itself! -->
  <!-- ExpectedException: impinclerr04.xsl is directly or indirectly including itself! -->

<xsl:include href="impinclerr04.xsl"/>

<xsl:template match="doc">
  <out>
  </out>
</xsl:template>

</xsl:stylesheet>
