<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    exclude-result-prefixes="xalan">

  <!-- FileName: xalanj2Nodeset01.xsl -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Creator: Shane Curcuru -->
  <!-- Purpose: Nodeset sample from documentation -->

<xsl:template match="/">
  <out>
    <!-- Declare a variable, which is a result tree fragment -->  
    <xsl:variable name="rtf">
      <docelem>
        <elem1>
          <elem1a>ELEMENT1A</elem1a>
          <elem1b>ELEMENT1B</elem1b>
        </elem1>
        <elem2>
          <elem2a>ELEMENT2A</elem2a>
        </elem2>
      </docelem>
    </xsl:variable>

    <!-- Use nodeset extension to treat the rtf as a nodeset -->
    <xsl:for-each select="xalan:nodeset($rtf)/docelem//*">
      <xsl:value-of select="name(.)"/><xsl:text>,</xsl:text>
    </xsl:for-each>
    </out>
</xsl:template>
</xsl:stylesheet>