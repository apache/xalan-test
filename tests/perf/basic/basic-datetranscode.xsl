<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html"/>
<xsl:template match="doc">
  <doc>
    <xsl:for-each select="date">
	    <xsl:variable name="Day" select="substring-before(.,' ')"/>
    	<xsl:variable name="Month" select="substring-before(normalize-space(substring-after(., $Day)),' ')"/>
	    <xsl:variable name="Year" select="normalize-space(substring-after(., $Month))"/>
		<date orginal="{.}">
		<day><xsl:value-of select="$Day"/></day>
		<xsl:choose>
		<xsl:when test="$Month='January'"><month>1</month></xsl:when>
		<xsl:when test="$Month='February'"><month>2</month></xsl:when>
		<xsl:when test="$Month='March'"><month>3</month></xsl:when>
		<xsl:when test="$Month='April'"><month>4</month></xsl:when>
		<xsl:when test="$Month='May'"><month>5</month></xsl:when>
		<xsl:when test="$Month='June'"><month>6</month></xsl:when>
		<xsl:when test="$Month='July'"><month>7</month></xsl:when>
		<xsl:when test="$Month='August'"><month>8</month></xsl:when>
		<xsl:when test="$Month='September'"><month>9</month></xsl:when>
		<xsl:when test="$Month='October'"><month>10</month></xsl:when>
		<xsl:when test="$Month='November'"><month>11</month></xsl:when>
		<xsl:when test="$Month='December'"><month>12</month></xsl:when>
		</xsl:choose>
		<year><xsl:value-of select="$Year"/></year>
		</date>
    </xsl:for-each>
  </doc>
</xsl:template>

</xsl:stylesheet>
