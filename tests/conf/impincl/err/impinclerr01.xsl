<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: ImpInclerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.6.2 Style Import -->
  <!-- Creator: David Marston -->
  <!-- Purpose: It is an error for a stylesheet to import itself. -->
  <!-- ExpectedException: (StylesheetHandler) file:/E:\builds\testsuite\.\conf\impincl\err\impinclerr01.xsl is directly or indirectly importing itself! -->
  <!-- ExpectedException: (StylesheetHandler) file:E:/builds/testsuite/conf/impincl/err/impinclerr01.xsl is directly or indirectly importing itself! -->  
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) file:E:/builds/testsuite/conf/impincl/err/impinclerr01.xsl is directly or indirectly importing itself! -->
<xsl:import href="impinclerr01.xsl"/>

<xsl:template match="doc">
  <out>
  </out>
</xsl:template>

</xsl:stylesheet>
