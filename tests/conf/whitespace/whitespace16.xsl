<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: whte16 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.4 -->
  <!-- Purpose: This is a general test of whitespace handling.
  	   It verifies the handling of the special whitespace
  	   characters; space, tab, CR, LF. In different situations.
  			1. within xsl:text where they should not be stripped,
  			2. within LRE's <end2> where they may be stripped. -->

<xsl:template match="/">
  <out>
	<xsl:text> a. <!-- This -->
	 ,</xsl:text>
    <xsl:text>&#32;</xsl:text>,	<!-- Contains space -->
    <xsl:text>&#09;	</xsl:text>, <!-- Contains tab and 1 tab -->
    <xsl:text>&#13;		</xsl:text>, <!-- Contains CR and 2 tabs -->
    <xsl:text>&#10;			</xsl:text> <!-- Contains NL and 3 tabs -->




	 


	<end>		This will not be stripped.	</end>,
	<end2>&#32;	&#09;	&#13;	&#10;     </end2>
  </out>
</xsl:template>
 
</xsl:stylesheet>
