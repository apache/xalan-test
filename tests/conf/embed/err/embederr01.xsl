<html xsl:version="1.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns="http://www.w3.org/TR/xhtml1/strict">

  <!-- FileName: EMBEDerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.3 LRE as Stylesheet -->
  <!-- Purpose: Literal Result Element used as stylesheet
  	   cannot contain top-level elements. Should fail at line
  	   containing xsl:key statement -->
  <!-- ExpectedException: (StylesheetHandler) xsl:key is not allowed inside a template! -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:key is not allowed inside a template! -->
  <!-- ExpectedException: xsl:key is not allowed in this position in the stylesheet! -->

  <xsl:key name="test" match="para" use="@id"/>

  <head>
    <title>Expense Report Summary</title>
  </head>
  <body>
    <p>Total Amount: <xsl:value-of select="expense-report/total"/></p>
  </body>
</html>
