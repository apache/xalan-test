<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:ext="http://somebody.elses.extension"
    xmlns:java="http://xml.apache.org/xslt/java"
    xmlns:ped="http://tester.com"
    xmlns:bdd="http://buster.com"
    xmlns="www.lotus.com"
    extension-element-prefixes="ext">

  <!-- FileName: lreerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.1 Literal Result Elements-->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: It is an error if there is no namespace bound to the prefix on 
       the element bearing the xsl:exclude-result-prefixes attribute. -->
  <!-- Note: SCurcuru 28-Feb-00 added ExpectedException; seems like good error text to me. -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: Prefix in exclude-result-prefixes is not valid: jad -->
  <!-- ExpectedException: Prefix in exclude-result-prefixes is not valid: jad -->

<xsl:template match="doc">
  <out xsl:if= "my if" english="to leave" xsl:exclude-result-prefixes="java jad #default"/>
</xsl:template>

</xsl:stylesheet>