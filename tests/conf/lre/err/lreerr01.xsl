<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:ext="http://somebody.elses.extension"
    xmlns:java="http://xml.apache.org/xslt/java"
    xmlns:ped="http://tester.com"
    xmlns:bdd="http://buster.com"
    xmlns="www.lotus.com"
    exclude-result-prefixes="java jad #default"
    extension-element-prefixes="ext">

  <!-- FileName: lreerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.1 Literal Result Elements-->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: It is an error if there is no namespace bound to the prefix named in
       the exclude-result-prefixes attribute of the stylesheet. -->
  <!-- Note: SCurcuru 28-Feb-00 added ExpectedException; seems like good error text to me. -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: Prefix in exclude-result-prefixes is not valid: jad -->
  <!-- ExpectedException: Prefix in exclude-result-prefixes is not valid: jad -->

<xsl:template match="doc">
  <out xsl:if= "my if" english="to leave"/>
</xsl:template>

</xsl:stylesheet>