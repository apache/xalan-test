<?xml version="1.0"?>
<xsl:stylesheet version="1.1" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:tes="http://www.test.org" 
	extension-element-prefixes="tes">

	<!-- FileName: extend06 -->
	<!-- Document: http://www.w3.org/TR/xslt -->
	<!-- DocVersion: 19991116 -->
	<!-- Section: 14 Extensions -->
	<!-- Author: Joanne Tong -->
	<!-- Purpose: Test fallback for unsupported xsl elements 
		and fallback for top-level-elements -->
	<!-- related to bug 23089 -->

	<xsl:foo /><!-- should ignore -->

	<xsl:bar>
		<!-- should ignore -->
		<xsl:fallback>
			<out />
		</xsl:fallback>
	</xsl:bar>

	<xsl:template match="/doc">
		<out>
			<xsl:import-table HREF="blah.asp" name="sample">
				<doh>
					<xsl:value-of select="notok" />
				</doh>
				<xsl:fallback>
					<import-table>
						<xsl:value-of select="ok" />
					</import-table>
					<tes:blah>
						<xsl:fallback>
							<blah>
								<xsl:value-of select="ok" />
							</blah>
						</xsl:fallback>
					</tes:blah>
				</xsl:fallback>
			</xsl:import-table>
			<xsl:blah>
				<doh>
					<xsl:value-of select="notok" />
				</doh>
			</xsl:blah>
			<blah>
				<xsl:value-of select="ok" />
			</blah>
		</out>
	</xsl:template>

</xsl:stylesheet>