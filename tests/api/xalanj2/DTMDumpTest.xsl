<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="http://xml.apache.org/xslt/java"
                version="1.0">

<xsl:output method="xml" indent="yes"/>
                
  <xsl:param name="rtf">
    <selfrtf>
      <paramitem name="P1"/>
      <paramitem>param2 content</paramitem>
    </selfrtf>
  </xsl:param>

  <xsl:template match="/list">
  <xsl:variable name="selfdoc" select="document('')//selfrtf"/>
    <out>
      <global>
        <dumpdtm><xsl:value-of select="java:org.apache.qetest.xalanj2.DTMDumpTest.dumpDTM($rtf)"/></dumpdtm>
        <dumpdtm><xsl:value-of select="java:org.apache.qetest.xalanj2.DTMDumpTest.dumpDTM($selfdoc)"/></dumpdtm>
      </global>
      <xsl:apply-templates select="item | list"/> 
    </out>
  </xsl:template>
 
  <xsl:template match="item">
    <item> 
      <value-of><xsl:value-of select="."/></value-of>
      <dumpdtm><xsl:value-of select="java:org.apache.qetest.xalanj2.DTMDumpTest.dumpDTM(.)"/></dumpdtm>
    </item> 
  </xsl:template>
 
  <xsl:template match="list">
    <list> 
      <value-of><xsl:value-of select="."/></value-of>
      <dumpdtm><xsl:value-of select="java:org.apache.qetest.xalanj2.DTMDumpTest.dumpDTM(.)"/></dumpdtm>
    </list> 
  </xsl:template>
</xsl:stylesheet>
