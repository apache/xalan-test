<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
     xmlns:sql="org.apache.xalan.lib.sql.XConnection" 
     xmlns:xalan="http://xml.apache.org/xalan" 
     exclude-result-prefixes="xalan java" 
     extension-element-prefixes="sql"
     xmlns:java="http://xml.apache.org/xslt/java">
	<xsl:output method="xml" omit-xml-declaration="yes" standalone="yes"/>

	<!-- Varaible that will be replaced by the XSL Dynamic Query Processor -->
	<xsl:param name="stylesheets">
		<STYLESHEETS>
			<SUCCESS>
				<XSL_SHEET MEDIA="ns">success1.xsl</XSL_SHEET>
				<XSL_SHEET MEDIA="ie">success2.xsl</XSL_SHEET>
				<XSL_SHEET MEDIA="123">success3.xsl</XSL_SHEET>
			</SUCCESS>
			<ERROR>
				<XSL_SHEET MEDIA="456">error1.xsl</XSL_SHEET>
				<XSL_SHEET MEDIA="789">error2.xsl</XSL_SHEET>
				<XSL_SHEET MEDIA="000">error3.xsl</XSL_SHEET>
			</ERROR>
		</STYLESHEETS>
	</xsl:param>

	<xsl:template match="/">
		<!-- P911X Response Element -->
		<xsl:element name="TEMPLATES">
		    <!--xsl:copy-of select="xalan:nodeset($stylesheets)"/-->
		    <!-- This is a test to make sure we can still call methods on the 
			     passed in node. -->
		    <xsl:value-of select="java:getNodeName($stylesheets)" />

			<xsl:copy-of select="$stylesheets"/>
			<xsl:copy-of select="xalan:nodeset($stylesheets)"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>

