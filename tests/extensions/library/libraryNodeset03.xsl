<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:Nodeset="org.apache.xalan.xslt.extensions.Nodeset"
				xmlns:test="http://www.cnn.com"
				xmlns:default="http://www.hello.com"
                extension-element-prefixes="Nodeset"
                exclude-result-prefixes="test default">

  <!-- FileName: extend21 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 14 Extensions -->
  <!-- Purpose: Testing Lotus-specific extension "Nodeset". -->
 
<xsl:strip-space elements="*"/>
<xsl:output indent="yes"/>
             
<xsl:template match="/">
   <out>
	  <xsl:variable name="rtf">
		<docelem xmlns="http://www.hello.com" xmlns:test="http://www.cnn.com">
			<elem1>
				<elem1a>ELEMENT1A</elem1a>
				<elem1b>,ELEMENT1B</elem1b>
			</elem1>
			<elem2>
				<elem2a>ELEMENT2A</elem2a>
				<elem2b/>
			</elem2>
			<elem3>1</elem3>
			<elem3>2</elem3>
			<test:elem3>3</test:elem3>
			<elem3>4</elem3>
			<elem3>5</elem3>
			<elem4>Yahoo</elem4>
		</docelem>
	  </xsl:variable>

	  <xsl:value-of select="Nodeset:nodeset($rtf)/docelem/elem1"/><xsl:text>&#10;</xsl:text>

	  <xsl:for-each select="Nodeset:nodeset($rtf)/docelem/*">
		  <xsl:value-of select="name(.)"/><xsl:text>,</xsl:text>
	  </xsl:for-each><xsl:text>&#10;</xsl:text>
	   
	  <xsl:for-each select="Nodeset:nodeset($rtf)/docelem/elem2/*">
		  <xsl:value-of select="name(.)"/><xsl:text>,</xsl:text>
	  </xsl:for-each><xsl:text>&#10;</xsl:text>

	  <xsl:text>Apply-templates match=elem4: </xsl:text>
	  <xsl:apply-templates select="Nodeset:nodeset($rtf)/docelem/elem4"/>

	  <xsl:text>Sum: </xsl:text> 	  
	  <xsl:value-of select="sum(Nodeset:nodeset($rtf)/docelem/elem3)"/><xsl:text>&#10;</xsl:text>

	  <xsl:text>Count: </xsl:text> 	  
	  <xsl:value-of select="count(Nodeset:nodeset($rtf)/docelem/elem3)"/><xsl:text>&#10;</xsl:text>

	  <xsl:text>Number: </xsl:text> 	  
	  <xsl:value-of select="number(Nodeset:nodeset($rtf)/docelem/elem3[2])"/><xsl:text>&#10;</xsl:text>

	  <xsl:text>Name: </xsl:text>  
	  <xsl:value-of select="name(Nodeset:nodeset($rtf)/*)"/><xsl:text>&#10;</xsl:text>

	  <xsl:text>generate-id: </xsl:text>  
	  <xsl:value-of select="generate-id(Nodeset:nodeset($rtf)/docelem/elem4)"/><xsl:text>&#10;</xsl:text>

	  <xsl:text>Local-name: </xsl:text>
	  <xsl:value-of select="local-name(Nodeset:nodeset($rtf)/*)"/><xsl:text>&#10;</xsl:text>

	  <xsl:text>Namespace-uri: </xsl:text>
	  <xsl:value-of select="namespace-uri(Nodeset:nodeset($rtf)/docelem)"/>,
	  <xsl:value-of select="namespace-uri(Nodeset:nodeset($rtf)/default:docelem/default:elem1)"/>,
	  <xsl:value-of select="namespace-uri(Nodeset:nodeset($rtf)/default:docelem/test:elem3)"/>: <xsl:value-of select="(Nodeset:nodeset($rtf)/default:docelem/test:elem3)"/>

	  <xsl:text>&#10;</xsl:text>
	  <xsl:text>&#10;</xsl:text>
	  <xsl:text>xsl:copy-of($rtf): &#10; </xsl:text>
	  <xsl:copy-of select="Nodeset:nodeset($rtf)/docelem"/><xsl:text>&#10;</xsl:text>

	  <xsl:text>&#10;</xsl:text>
	  <xsl:text>xsl:copy-of(elem1b): &#10; </xsl:text>
	  <xsl:copy-of select="Nodeset:nodeset($rtf)/docelem/elem1/elem1b"/><xsl:text>&#10;</xsl:text>
	  
	  <xsl:text>&#10;</xsl:text>
	  <xsl:text>xsl:copy-of(test:elem3): &#10; </xsl:text>
	  <xsl:copy-of select="Nodeset:nodeset($rtf)/docelem/test:elem3"/><xsl:text>&#10;</xsl:text>

   </out>
</xsl:template>

<xsl:template match="elem4">
	  <xsl:value-of select="."/>,
</xsl:template>
  
</xsl:stylesheet>
