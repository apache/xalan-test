<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0" >

<!-- User mark@ssglimited.com (Mark Peterson) claims in Xalan-J 2.2.0:
The XSL file, below, should make $flights= {"1","2"}, but it contains {"1"} - 
when using the XML example file shown below.
xml-xalan CVS 11-Feb-02 9AM returns <out>1<br/></out> -sc
-->

<xsl:variable name="flights" select="/report/colData[@colId='F' and not(.=preceding::colData)]"/>

<xsl:template match="/report">
<out>
    <xsl:for-each select="$flights">
        <xsl:value-of select="." /><br />
    </xsl:for-each>
</out>
</xsl:template>

 
</xsl:stylesheet>
