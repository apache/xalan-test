<?xml version="1.1"?>

  <!-- FileName: spec1107.xsl -->
  <!-- Purpose: To output a document with method 'xml' and version '1.1'. 
                When NEL (0x0085 / ) and LSEP (0x2028 /  ) appear as actual value
                they must be treated as a linefeed.  XML parser normailize them
                to a linefeed.(0xA).
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" version="1.1" encoding="UTF-8" />
  <xsl:template match="/">
    <out>     </out>
  </xsl:template>
</xsl:stylesheet>
