<?xml version="1.0"?>
<xsl:stylesheet version="1.1" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- FileName: ver08 -->
	<!-- Document: http://www.w3.org/TR/xslt -->
	<!-- DocVersion: 19991116 -->
	<!-- Section: 15 Fallback -->
	<!-- Author: Joanne Tong -->
	<!-- Purpose: Test fallback for unsupported xsl elements 
		, fallback for top-level-elements, and more than one
		fallback child -->
	<!-- related to bugs 23089 and 23706 -->

	<xsl:foo /><!-- should ignore -->

	<xsl:bar><!-- should ignore -->
		<xsl:fallback>
			<doh />
		</xsl:fallback>
	</xsl:bar>

	<xsl:template match="in">
			<xsl:import-table HREF="blah.asp" name="sample">
				<doh><!-- should ignore -->
					<xsl:value-of select="notok" />
				</doh>
				<xsl:fallback>
					<out1>
						<xsl:value-of select="ok" />
					</out1>
					<xsl:import-table>
						<xsl:fallback>
							<out2>
								<xsl:value-of select="ok" />
							</out2>
						</xsl:fallback>
						<xsl:fallback>
							<out3>
								<xsl:value-of select="ok" />
							</out3>
						</xsl:fallback>						
					</xsl:import-table>
					<xsl:fallback><!-- should ignore -->
						<doh />
					</xsl:fallback>
				</xsl:fallback>
			</xsl:import-table>
			<xsl:if test="false()">
				<xsl:import-table /><!-- should not throw error -->
			</xsl:if>			
	</xsl:template>
	
	<xsl:template match="/">
		<out>
			<xsl:apply-templates />
		</out>
	</xsl:template>	

</xsl:stylesheet>