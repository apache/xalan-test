<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: VERerr05 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.1 XSLT Namespace  -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test use of an undefined element (garbage) as if was part of XSLT. -->
  <!-- ExpectedException: Unknown XSL element: garbage -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: Unknown XSL element: garbage -->

  <xsl:template match="/">
    <out>
      The garbage element is not part of XSLT!
      <xsl:garbage/>
    </out>
  </xsl:template>

</xsl:stylesheet>
