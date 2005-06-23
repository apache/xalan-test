<?xml version="1.0"?> 

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
xmlns:set="http://exslt.org/sets" >

<!-- Test set:difference -->

<xsl:variable name="i" select="//city[contains(@name,'i')]"/>
<xsl:variable name="e" select="//city[contains(@name,'e')]"/>
<xsl:variable name="B" select="//city[contains(@name,'B')]"/>

<xsl:template match="/">
  <out>
    Containing i and no e:
    <xsl:for-each select="set:difference($i, $e)">
         <xsl:value-of select="@name"/>;
    </xsl:for-each> 
    Containing e and no i:
    <xsl:for-each select="set:difference($e, $i)">
         <xsl:value-of select="@name"/>;
    </xsl:for-each>
    Containing B and no i and no e:
    <xsl:for-each select="set:difference(set:difference($B,$i),$e)">
         <xsl:value-of select="@name"/>;
    </xsl:for-each>
    
    <!-- test difference on empty sets -->

    Containing i:
    <xsl:for-each select="set:difference($i, /..)">
         <xsl:value-of select="@name"/>;
    </xsl:for-each>
    Containing B:
    <xsl:for-each select="set:difference($B, /..)">
         <xsl:value-of select="@name"/>;
    </xsl:for-each>
    Empty set:
    <xsl:for-each select="set:difference(/.., $i)">
         <xsl:value-of select="@name"/>;
    </xsl:for-each> 
  </out>
</xsl:template>
 
</xsl:stylesheet>
