<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:str="http://exslt.org/strings"
                extension-element-prefixes="str">

<xsl:template match="a/name">
   <xsl:apply-templates/>
</xsl:template>

<xsl:template match="*">
<out>
   Left-justified with padding longer than name:
   <xsl:value-of select="//name[1]" /> -
   <xsl:value-of select="str:align(//name[1], '-X-O-X-O-X-O-X-O-X-O-X-O-X-O-X-O-X-O-X-O-X-O', 'left')" /> 
   Right-justified with padding longer than name:
   <xsl:value-of select="//name[2]" /> -
   <xsl:value-of select="str:align(//name[2], 'nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn', 'right')" /> 
   Centered with padding longer than name:
   <xsl:value-of select="//name[3]" /> -
   <xsl:value-of select="str:align(//name[3], 'DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD', 'center')" /> 
   Left-justified with padding shorter than name:
   <xsl:value-of select="//name[4]" /> -
   <xsl:value-of select="str:align(//name[4], 'DDDDD', 'left')" /> 
   Right-justified with null padding than name:
   <xsl:value-of select="//name[5]" /> -
   <xsl:value-of select="str:align(//name[5], '', 'right')" /> 
   Default justification is left-justified:
   <xsl:value-of select="//name[6]" /> -
   <xsl:value-of select="str:align(//name[6], 'ooooooooooooooooooooooooooooooo')" /> 
</out>
</xsl:template>

</xsl:stylesheet>