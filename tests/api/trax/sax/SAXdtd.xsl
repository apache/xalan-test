<?xml version="1.0"?> 
<!DOCTYPE SAXDTD SYSTEM "SAXdtd.dtd">
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Use different encoding to see entities better -->
<xsl:output method="xml" encoding="ISO-8859-1"/>

  <xsl:template match="/">
    <out>
      <head>Testing &copy; entities &test-ent; here</head>
      <xsl:apply-templates/>
    </out>
  </xsl:template>


<!-- From the XSLT spec: "the identity transformation can be written using xsl:copy as follows:" -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
     
</xsl:stylesheet>
