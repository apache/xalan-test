<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:xalan="http://xml.apache.org/xalan">

  <!-- FileName: extend16 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 14 Extensions -->
  <!-- Purpose: Testing Lotus-specific extensions. -->
 
<xsl:variable name="ns0" select="/lists/*/*"/>
<xsl:variable name="ns3" select="/lists/list3/*"/>
<xsl:variable name="ns4" select="/lists/*/item"/>
<xsl:variable name="ns5" select="/lists/list5/item"/>
<xsl:variable name="ns6" select="/lists/*"/>

<xsl:template match="/">
<out>

*****				DATA				*****
ns0: /lists/*/*
<xsl:for-each select="$ns0">
	<xsl:value-of select="name()"/>=<xsl:value-of select="."/><xsl:text>,</xsl:text>
</xsl:for-each>

ns3: /lists/list3/*
<xsl:for-each select="$ns3">
	<xsl:value-of select="name()"/>=<xsl:value-of select="."/><xsl:text>,</xsl:text>
</xsl:for-each>

ns4: /lists/*/item
<xsl:for-each select="$ns4">
	<xsl:value-of select="name()"/>=<xsl:value-of select="."/><xsl:text>,</xsl:text>
</xsl:for-each>

ns5: /lists/list5/item
<xsl:for-each select="$ns5">
	<xsl:value-of select="name()"/>=<xsl:value-of select="."/><xsl:text>,</xsl:text>
</xsl:for-each>

ns6: /lists/*
<xsl:for-each select="$ns6">
	<xsl:copy/>
</xsl:for-each>


*****				TESTS				*****
Intersection - ns0, ns3:
<xsl:for-each select="xalan:intersection(lists/*/*, lists/list3/*)">
        <xsl:value-of select="."/>
        <xsl:if test="position() != last()">
        	<xsl:text>,</xsl:text>
		</xsl:if>
		<xsl:if test="position() = last()">
			<xsl:text>: </xsl:text>
		</xsl:if>
</xsl:for-each>
<xsl:copy-of select="xalan:intersection($ns3, $ns0)"/>

Difference - ns6, ns5:
<xsl:for-each select="xalan:difference(lists/*, lists/list5/item)">
        <xsl:value-of select="name(.)"/>
        <xsl:if test="position() != last()">
        	<xsl:text>,</xsl:text>
		</xsl:if>
		<xsl:if test="position() = last()">
			<xsl:text>: </xsl:text>
		</xsl:if>
</xsl:for-each>

Difference - ns5, ns6:
<xsl:for-each select="xalan:difference($ns5, $ns6 )">
        <xsl:value-of select="."/>
        <xsl:if test="position() != last()">
        	<xsl:text>,</xsl:text>
		</xsl:if>
		<xsl:if test="position() = last()">
			<xsl:text>: </xsl:text>
		</xsl:if>
</xsl:for-each>
<xsl:copy-of select="xalan:difference($ns5, $ns6 )"/>

Values of $ns4:
<xsl:for-each select="$ns4">
	<xsl:value-of select="."/>
</xsl:for-each>
Distinct - ns4:
<xsl:for-each select="xalan:distinct($ns4)">
        <xsl:value-of select="."/>
</xsl:for-each>

hasSameNodes - $ns0, lists/*/*:
<xsl:if test="xalan:hasSameNodes($ns0, lists/*/*)">
    <WHOA_It_is_True/>
</xsl:if>

evaluate - lists//*/@* ...
<xsl:value-of select="xalan:evaluate('lists//*/@*')"/>
<xsl:value-of select="xalan:evaluate('lists//*/@a2')"/>
<xsl:value-of select="xalan:evaluate('lists/list3/*/@a2')"/>
<xsl:value-of select="xalan:evaluate('//list3//@a1')"/><xsl:text> </xsl:text>
<xsl:value-of select="xalan:evaluate('//list2//@a1 and //list3//@a2')"/>
</out>
</xsl:template>

<xsl:template match="text()"/>

</xsl:stylesheet>
