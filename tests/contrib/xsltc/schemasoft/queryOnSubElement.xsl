<?xml version="1.0"?>
<!-- queryOnSubElement.xslt
Selects  a given tag according to the content of a subelement. This transform is completely parametrized.
Copyright J.M. Vanel 2000 - under GNU public licence 
-->

<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
                 version="1.0" >

<xsl:param name="wantedTag">b</xsl:param>
<xsl:param name="wantedsubElement">d</xsl:param>
<xsl:param name="wantedString">d in b</xsl:param>

  <xsl:template match="/">
    <Collection>
      <xsl:copy-of select="//* [ name(.) = $wantedTag]
                           [ * [ name(.) = $wantedsubElement] 
                           [contains(., $wantedString) ] ]" />
    </Collection>
  </xsl:template>

</xsl:stylesheet>
