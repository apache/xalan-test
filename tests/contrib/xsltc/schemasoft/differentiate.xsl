<?xml version='1.0'?>
<xsl:stylesheet
      version='1.0'
      xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

<!-- Description:  Differentiates a simple polynomial function -->
<!-- Author:  Charlie Halpern-Hamu, Ph.D. -->
<!-- Reference:  http://www.incrementaldevelopment.com/papers/xsltrick/#differentiate -->

<xsl:strip-space elements='*'/>

<xsl:output
        method='xml'
        indent='yes'/>

<xsl:template match='/function-of-x'>
<xsl:element name='function-of-x'>
<xsl:apply-templates select='term'/>
</xsl:element>
</xsl:template>

<xsl:template match='term'>
<term>
<coeff>
<xsl:value-of select='coeff * power'/>
</coeff>
<x/>
<power>
<xsl:value-of select='power - 1'/>
</power>
</term>
</xsl:template>
</xsl:stylesheet>
