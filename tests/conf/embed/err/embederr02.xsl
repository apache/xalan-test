<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/TR/xhtml1/strict">

  <!-- FileName: EMBEDerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.3 LRE as Stylesheet -->
  <!-- Purpose: See what happens when version number is missing from xsl namespace (above). -->
  <!-- ExpectedException: stylesheet must have a "version" attribute -->
  <!-- ExpectedException: xsl:stylesheet requires attribute: version -->

<xsl:template match="/">
<html>
  <head>
    <title>Expense Report Summary</title>
  </head>
  <body>
    <p>Total Amount: <xsl:value-of select="expense-report/total"/></p>
  </body>
</html>
</xsl:template>

</xsl:stylesheet>