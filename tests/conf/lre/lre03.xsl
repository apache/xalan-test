<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
				xmlns:ext="http://somebody.elses.extension"
                xmlns:java="http://xml.apache.org/xslt/java"
				xmlns:ped="http://tester.com"
				xmlns:bdd="http://buster.com"
				xmlns:jad="http://administrator.com"
				xmlns="www.lotus.com"
                extension-element-prefixes="ext">

  <!-- FileName: lre03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.1 Literal Result Elements-->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: The created element node will also have a copy of the namespace nodes 
       that were present on the element node in the stylesheet tree with the exception 
       of any namespace node whose string-value is the XSLT namespace URI, a namespace 
       URI declared as an extension namespace, or a namespace URI designated as an 
       excluded namespace. Excluded namespaces specified with xsl:exclude-result-prefixes
       attribute of LRE. -->

<xsl:template match="doc">
  <out xsl:if= "my if" english="to leave" xsl:exclude-result-prefixes="java jad #default"/>
</xsl:template>

</xsl:stylesheet>
