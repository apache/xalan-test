<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	
  <xsl:variable name="regionNumber" select="/Adjustments/PageData/HiddenForm/Region"/>
	
  <xsl:template match="/">
    <html>
      <body>Hello <xsl:value-of select="$regionNumber"/> World</body>
    </html>
  </xsl:template>
	
</xsl:stylesheet>
