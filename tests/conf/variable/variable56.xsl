<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- FileName: variable56 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 -->
  <!-- Author: darrylf@schemasoft.com -->
  <!-- Purpose: variables in inner scope with same name as variables in outter scope -->
 
  <xsl:output method="xml" indent="yes" />

  <xsl:template match="/">
     <xsl:variable name="bar">outer</xsl:variable>
     <outer bar="{$bar}">
        <xsl:for-each select="./*">
           <xsl:variable name="bar">inner</xsl:variable>
              <inner bar="{$bar}"/>
        </xsl:for-each>
     </outer>
  </xsl:template>
                
</xsl:stylesheet>


