<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:str="http://exslt.org/strings"
                extension-element-prefixes="str">

<!-- str:concat() -->

<xsl:template match="/">
<out>
   Concatentating all a/* nodes from the input file gives us:
   <xsl:value-of select="str:concat(a/*)" /><br/>
   Concatenating a/c/d nodes from my input file gives us:
   <xsl:value-of select="str:concat(a/c/d)"/>
</out>
</xsl:template>

</xsl:stylesheet>