<?xml version="1.0" encoding="UTF-8"?>

  <!-- FileName: conflictres37 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.5 -->
  <!-- Creator: Ilene Seelemann -->
  <!-- Purpose: Test that qname with predicate has precedence over ncname:*, 
                which in turn has precedence over * in a match pattern. -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:xalan="http://xml.apache.org/xslt"
    xmlns:xf="http://xml.apache.org/cocoon/xmlform/2002">
    
    <xsl:template match="/doc">
      <out>
         <xsl:apply-templates/>
       </out>
     </xsl:template>
     <xsl:template match="xf:output[@form]">
        <OutWithForm>
           <xsl:value-of select="."/>
        </OutWithForm>
     </xsl:template>
     <xsl:template match="xf:*">
        <OutWithoutForm>
        <xsl:value-of select="."/>
        </OutWithoutForm>
     </xsl:template>
     <xsl:template match="*">
         <General>
         <xsl:value-of select="."/>
         </General>
     </xsl:template>
</xsl:stylesheet>

