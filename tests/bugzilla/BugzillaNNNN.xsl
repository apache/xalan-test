<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0" >

<!-- Use this to reproduce stylesheet bugs; just change NNNN to be the bug number -->
<xsl:template match="/">
  <out>
    <title>
      <xsl:text>Reproducing Bugzilla#NNNN: oops!</xsl:text>
    </title>    
    <xsl:apply-templates />
  </out>
</xsl:template>

 
</xsl:stylesheet>
