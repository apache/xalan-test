<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
        xmlns:dyn="http://exslt.org/dynamic">

<!-- Test dyn:evaluate applied to a result tree fragment -->

<xsl:template match="doc">
    <xsl:variable name="category" select="'item'"/>
    <xsl:variable name="query" select="'book/title'"/>    
    <out>
        <xsl:for-each select="dyn:evaluate(concat($category,'/',$query))">
            <booktitle>
                <xsl:value-of select="."/>
            </booktitle>
        </xsl:for-each>
    </out>
</xsl:template>

</xsl:stylesheet>
