<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:xalan="http://xml.apache.org/xalan"
                              exclude-result-prefixes="xalan">

   <!-- This stylesheet tests the evaluate extension function for the 
        case where the expression to be evaluated contains a top-level variable -->

   <xsl:variable name="this" select="root" />

   <xsl:template match="root">
      <test>
         <xsl:apply-templates/>
      </test>
   </xsl:template>

   <xsl:template match="reference">
      <xsl:variable name="var-ref" select="concat('$this/', @ref)" />
      <xsl:for-each select="xalan:evaluate($var-ref)">
         <out value="{@value}" />
      </xsl:for-each>
   </xsl:template>

</xsl:stylesheet>
