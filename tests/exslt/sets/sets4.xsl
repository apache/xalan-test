<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
xmlns:set="http://exslt.org/sets" >

<!-- Test set:intersection -->

<xsl:variable name="i" select="//city[contains(@name,'i')]"/>
<xsl:variable name="e" select="//city[contains(@name,'e')]"/>
<xsl:variable name="B" select="//city[contains(@name,'B')]"/>

<xsl:template match="/">
  <out>
    Containing i and e:
    <xsl:for-each select="set:intersection($i, $e)">
         <xsl:value-of select="@name"/>;
    </xsl:for-each>     
    
    <!-- test intersection and difference on empty sets -->
    Empty set:
    <xsl:for-each select="set:intersection($i, /..)">
         <xsl:value-of select="@name"/>;
    </xsl:for-each>     
    Empty set:
    <xsl:for-each select="set:intersection(/.., $i)">
         <xsl:value-of select="@name"/>;
    </xsl:for-each> 

  </out>
</xsl:template>
 
</xsl:stylesheet>
