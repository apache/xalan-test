<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
                xmlns:xalan="http://xml.apache.org/xslt"
                extension-element-prefixes="redirect" version="1.0">
	<xsl:output method="xml" indent="yes" xalan:indent-amount="2"
			doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
			doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" />

<!-- FileName: tableResults.xsl -->
<!-- Author:   Gordon Chiu <grchiu@ca.ibm.com> -->
<!-- Date:     10/10/2002 -->
<!-- Purpose:  Format org.apache.qetest.XMLFileLogger logFile results 
               into a table-based HTML page -->
<!-- Modified: 10/17/2002 removed 'trax', is 'systemId'
               10/22/2002 takes directory as argument -->

<!-- Quick how to use:

	Note: If using Xalan to run this stylesheet, it requires Xalan >= 2.4.1.

  This stylesheet operates on the results of a build alltest.conf.
  1. In place of viewResults.xsl to analyze results:
     In Windows, set RESULTSCANNER=tableResults.xsl
     Execute viewResults.bat results-alltest\conf\sax\results.xml.
		 (the stylesheet will find the other xml files).
     In UNIX, modify viewResults.sh to call use tableResults.xsl.
  2. To compare a current run against a previous run:
     Run the stylesheet with the parameter 'compareAgainst' which
     points to the results-alltest/conf directory of another test run.
  3. To analyze results from build alltest.conf.xsltc, run the
     stylesheet with the parameter 'resultDir' which points to the
     results-alltest.xsltc/conf directory of the xsltc test run.

-->

	<!-- Directory of results to compare against
	     Specify a directory of old results to get cross comparison. -->
	<xsl:param name="resultDir" select="string('results-alltest/conf')"/>
	<xsl:param name="compareAgainst" select="$resultDir"/>

	<!-- Basic Structure of output document -->
	<xsl:template match="resultsfile">
		<HTML>
			<HEAD>
				<STYLE TYPE="text/css">
					body { font-family: Verdana; font-size: 9pt; }
					tr { font-family: Verdana; font-size: 9pt; }
					td { font-family: Verdana; font-size: 9pt; }
				</STYLE>
			</HEAD>
			<BODY>
				<xsl:apply-templates select="testfile[last()]"/>
			</BODY>
		</HTML>
	</xsl:template>

	<!-- Output information about the test run from the hashtable -->
	<xsl:template match="testfile" mode="hashElements">
		<xsl:value-of select="hashtable/hashitem[@key='version.xalan2x']"/> (<xsl:value-of select="@time"/>) and
		<xsl:value-of select="hashtable/hashitem[@key='version.xerces2']"/><BR/>
		<xsl:value-of select="hashtable/hashitem[@key='java.vm.vendor']"/>
		JDK <xsl:value-of select="hashtable/hashitem[@key='java.vm.version']"/><BR/>
		Test run by: <xsl:value-of select="hashtable/hashitem[@key='user.name']"/>
		on a <xsl:value-of select="hashtable/hashitem[@key='os.arch']"/>
		running <xsl:value-of select="hashtable/hashitem[@key='os.name']"/>
	</xsl:template>
  
	<!-- Output the summary and category summaries for test sets. -->
	<xsl:template match="testfile">
		<!-- Set up two variables for test results, and old test results. -->
		<xsl:variable name="resultsfile">
			<xsl:value-of select="document(concat($resultDir,'/dom/results.xml'))/resultsfile 
			  | document(concat($resultDir,'/sax/results.xml'))/resultsfile 
			  | document(concat($resultDir,'/stream/results.xml'))/resultsfile 
			  | document(concat($resultDir,'/file/results.xml'))/resultsfile 
			  | document(concat($resultDir,'/systemId/results.xml'))/resultsfile 
			  | document(concat($resultDir,'/localPath/results.xml'))/resultsfile"/>
		</xsl:variable>
		<xsl:variable name="oldfile">
			<xsl:value-of select="document(concat($compareAgainst,'/dom/results.xml'))/resultsfile 
					| document(concat($compareAgainst,'/sax/results.xml'))/resultsfile 
					| document(concat($compareAgainst,'/stream/results.xml'))/resultsfile 
					| document(concat($compareAgainst,'/file/results.xml'))/resultsfile 
					| document(concat($compareAgainst,'/systemId/results.xml'))/resultsfile 
					| document(concat($compareAgainst,'/localPath/results.xml'))/resultsfile"/>
		</xsl:variable>
		<!-- Number of flavours; used for column widths -->
		<xsl:variable name="flavs" select="count($resultsfile/testfile)"/>
		<TABLE BORDER="1" CELLPADDING="5" CELLSPACING="0">
			<TR>
				<xsl:element name="TD">
					<xsl:attribute name="COLSPAN">
						<xsl:value-of select="$flavs + 2"/>
					</xsl:attribute>
					<B>Conformance Test Results on <xsl:value-of select="@time"/></B>
				</xsl:element>
			</TR>
			<TR>
				<TD COLSPAN="1"><B>Environment</B></TD>
				<xsl:element name="TD">
					<xsl:attribute name="COLSPAN">
						<xsl:value-of select="$flavs + 1"/>
					</xsl:attribute>
					<xsl:variable name="flavor">
						<xsl:value-of select="hashtable/hashitem[@key='flavor']"/>
					</xsl:variable>
					<b>Currently Tested:</b><br/>
					<xsl:apply-templates select="." mode="hashElements"/><br/>
					<b>Compared Against:</b><br/>
					<xsl:apply-templates select="$oldfile/testfile[hashtable/hashitem[@key='flavor'] = $flavor]" mode="hashElements"/>
				</xsl:element>
			</TR>
			<xsl:call-template name="teststatus">
				<xsl:with-param name="resultsfile" select="$resultsfile"/>
			</xsl:call-template>
			<TR>
				<xsl:element name="TD">
					<xsl:attribute name="COLSPAN">
						<xsl:value-of select="$flavs + 2"/>
					</xsl:attribute><B>Category Summaries</B>
				</xsl:element>
			</TR>
			<xsl:for-each select="testcase">
				<xsl:call-template name="testcase">
					<xsl:with-param name="resultsfile" select="$resultsfile"/>
					<xsl:with-param name="oldfile" select="$oldfile"/>
					<xsl:with-param name="bg">
						<xsl:choose>
							<xsl:when test="position() mod 2 = 1">#F0F0F0</xsl:when>
							<xsl:otherwise>#FFFFFF</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
			<TR>
				<xsl:element name="TD">
					<xsl:attribute name="COLSPAN">
						<xsl:value-of select="$flavs + 2"/>
					</xsl:attribute><B>Legend</B>
				</xsl:element>
			</TR>
			<TR>
				<TD><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></TD>
				<xsl:element name="TD">
					<xsl:attribute name="COLSPAN">
						<xsl:value-of select="$flavs + 1"/>
					</xsl:attribute>
					<BR/>
					<UL>
						<LI><B><FONT COLOR="#FF0000">New/Regression Failure</FONT></B></LI>
						<LI><FONT COLOR="#FF0000">Existing Failure</FONT></LI>
						<LI><FONT COLOR="#008000"><STRIKE>New Pass</STRIKE></FONT></LI>
						<LI><FONT COLOR="#FF00FF">Excluded Test</FONT></LI>
					</UL>
				</xsl:element>
			</TR>
		</TABLE>
	</xsl:template>

	<xsl:template name="testcase">
		<xsl:param name="resultsfile"/>
		<xsl:param name="oldfile"/>
		<xsl:param name="bg"/>
		<xsl:variable name="category" select="substring-after(@desc,': ')"/>
		<xsl:variable name="excludesListAll">
			<xsl:call-template name="buildList">
				<xsl:with-param name="category" select="$category"/>
				<xsl:with-param name="list" select="$resultsfile//hashtable/hashitem[@key='excludes']"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="oldFailListAll">
			<xsl:for-each select="$oldfile/testfile/testcase[contains(@desc,$category)]/checkresult[not(@result='Pass') and contains(@desc,$category)]">#</xsl:for-each>
		</xsl:variable>
		<xsl:element name="TR">
			<xsl:attribute name="BGCOLOR"><xsl:value-of select="$bg"/></xsl:attribute>
			<xsl:element name="TD">
				<xsl:attribute name="ROWSPAN">
					<xsl:choose>
						<xsl:when test="count($resultsfile/testfile/testcase[$category = substring-after(@desc,': ')]/checkresult[not(@result='Pass')]) > 0 or not($excludesListAll = '') or not($oldFailListAll = '')">
							4
						</xsl:when>
						<xsl:otherwise>
							3
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:value-of select="$category"/>
			</xsl:element>
		</xsl:element>
		<xsl:element name="TR">
			<xsl:attribute name="BGCOLOR"><xsl:value-of select="$bg"/></xsl:attribute>
			<TD VALIGN="TOP">#cases</TD>
			<xsl:for-each select="$resultsfile/testfile/testcase[$category = substring-after(@desc,': ')]">
				<TD VALIGN="TOP">
					<xsl:value-of select="count(checkresult)"/>
				</TD>
			</xsl:for-each>
		</xsl:element>
		<xsl:element name="TR">
			<xsl:attribute name="BGCOLOR"><xsl:value-of select="$bg"/></xsl:attribute>
			<TD VALIGN="TOP">pass</TD>
			<xsl:for-each select="$resultsfile/testfile/testcase[$category = substring-after(@desc,': ')]">
				<TD VALIGN="TOP">
					<xsl:variable name="passList">
						<xsl:for-each select="checkresult[@result='Pass']">
							<xsl:sort select="substring-before(substring-after(@desc,$category),'.')" data-type="number"/>
							<xsl:variable name="cur" select="substring-before(substring-after(@desc,$category),'.')"/>
							<xsl:value-of select="$cur"/>
							<xsl:if test="not(position()=last())">
								<xsl:text> </xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>
					<xsl:call-template name="condense">
						<xsl:with-param name="list" select="$passList"/>
						<xsl:with-param name="prev" select="-1"/>
					</xsl:call-template>
				</TD>
			</xsl:for-each>
		</xsl:element>
		<xsl:if test="count($resultsfile/testfile/testcase[$category = substring-after(@desc,': ')]/checkresult[not(@result='Pass')]) > 0 or not($excludesListAll = '') or count($oldfile/testfile/testcase[contains(@desc,$category)]/checkresult[not(@result='Pass')]) > 0">
			<xsl:element name="TR">
				<xsl:attribute name="BGCOLOR"><xsl:value-of select="$bg"/></xsl:attribute>
				<TD VALIGN="TOP">fail/err</TD>
				<xsl:for-each select="$resultsfile/testfile/testcase[$category = substring-after(@desc,': ')]">
					<xsl:variable name="flavor">
						<xsl:value-of select="../hashtable/hashitem[@key='flavor']"/>
					</xsl:variable>
					<xsl:variable name="excludesList">
						<xsl:call-template name="buildList">
							<xsl:with-param name="category" select="$category"/>
							<xsl:with-param name="list" select="../hashtable/hashitem[@key='excludes']"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="oldFailList">
						<xsl:for-each select="$oldfile/testfile[hashtable/hashitem[@key='flavor'] = $flavor]/testcase[contains(@desc,$category)]/checkresult[not(@result='Pass') and contains(@desc,$category)]">
							<xsl:sort select="substring-before(substring-after(@desc,$category),'.')" data-type="number"/>
							<xsl:variable name="cur" select="substring-before(substring-after(@desc,$category),'.')"/>
							<xsl:value-of select="$cur"/>
							<xsl:if test="not(position()=last())">
								<xsl:text> </xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="count(checkresult[not(@result='Pass')]) > 0 or not($excludesList = '') or not($oldFailList = '')">
							<TD VALIGN="TOP">
								<FONT COLOR="#FF0000">
									<xsl:variable name="failList">
										<xsl:for-each select="checkresult[not(@result='Pass')]">
											<xsl:sort select="substring-before(substring-after(@desc,$category),'.')" data-type="number"/>
											<xsl:variable name="cur" select="substring-before(substring-after(@desc,$category),'.')"/>
											<xsl:value-of select="$cur"/>
											<xsl:if test="not(position()=last())">
												<xsl:text> </xsl:text>
											</xsl:if>
										</xsl:for-each>
									</xsl:variable>
									<xsl:call-template name="diff">
										<xsl:with-param name="newlist" select="$failList"/>
										<xsl:with-param name="oldlist" select="$oldFailList"/>
									</xsl:call-template>
								</FONT>
								<FONT COLOR="#FF00FF">
									<xsl:variable name="excludeCond">
										<xsl:call-template name="condense">
											<xsl:with-param name="list" select="substring($excludesList,1,string-length($excludesList)-1)"/>
											<xsl:with-param name="prev" select="-1"/>
										</xsl:call-template>
									</xsl:variable>
									<xsl:if test="not($excludeCond = '')">
										<xsl:text>(</xsl:text><xsl:value-of select="$excludeCond"/><xsl:text>)</xsl:text>
									</xsl:if>
								</FONT>
							</TD>
						</xsl:when>
						<xsl:otherwise>
							<TD><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></TD>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<!-- helper function used when extracting list of excludes. -->
	<xsl:template name="buildList">
		<xsl:param name="category"/>
		<xsl:param name="list"/>
		<xsl:if test="contains($list,$category)">
			<xsl:value-of select="substring-before(substring-after($list,$category),'.')"/>
			<xsl:text> </xsl:text>
			<xsl:call-template name="buildList">
				<xsl:with-param name="category" select="$category"/>
				<xsl:with-param name="list" select="substring-after(substring-after($list,$category),'.')"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- helper function used to find difference between two lists of failed tests. -->
	<xsl:template name="diff">
		<xsl:param name="newlist"/>
		<xsl:param name="oldlist"/>
		<xsl:variable name="nf">
			<xsl:choose>
				<xsl:when test="contains($newlist,' ')">
					<xsl:value-of select="substring-before($newlist,' ')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$newlist"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="of">
			<xsl:choose>
				<xsl:when test="contains($oldlist,' ')">
					<xsl:value-of select="substring-before($oldlist,' ')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$oldlist"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="not($of = '' and $nf = '')">
			<xsl:choose>
				<xsl:when test="$of = $nf">
					<FONT COLOR="#FF0000">
						<xsl:value-of select="$of"/>
						<xsl:text> </xsl:text>
					</FONT>
					<xsl:call-template name="diff">
						<xsl:with-param name="newlist" select="substring-after($newlist,' ')"/>
						<xsl:with-param name="oldlist" select="substring-after($oldlist,' ')"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$of &lt; $nf or $nf = ''">
					<FONT COLOR="#008000">
						<STRIKE>
							<xsl:value-of select="$of"/>
						</STRIKE>
						<xsl:text> </xsl:text>
					</FONT>
					<xsl:call-template name="diff">
						<xsl:with-param name="newlist" select="$newlist"/>
						<xsl:with-param name="oldlist" select="substring-after($oldlist,' ')"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$of &gt; $nf or $of = ''">
					<FONT COLOR="#FF0000">
						<B>
							<xsl:value-of select="$nf"/>
						</B>
						<xsl:text> </xsl:text>
					</FONT>
					<xsl:call-template name="diff">
						<xsl:with-param name="newlist" select="substring-after($newlist,' ')"/>
						<xsl:with-param name="oldlist" select="substring-after($oldlist,' ')"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
					
	<!-- helper function used to condense test numbers: 01, 02, 03 -> 01-03 -->
	<xsl:template name="condense">
		<xsl:param name="list"/>
		<xsl:param name="prev"/>
		<xsl:variable name="cur" select="substring-before($list,' ')"/>
		<xsl:variable name="next">
			<xsl:choose>
				<xsl:when test="not(substring-before(substring-after($list,' '),' ') = '')">
					<xsl:value-of select="substring-before(substring-after($list,' '),' ')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="substring-after($list,' ')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="not($cur = '')">
				<xsl:choose>
					<xsl:when test="not($prev + 1 = $cur) and $cur + 1 = $next">
						<xsl:value-of select="$cur"/>
						<xsl:text>-</xsl:text>
					</xsl:when>
					<xsl:when test="not($cur + 1 = $next)">
						<xsl:value-of select="$cur"/>
						<xsl:text>, </xsl:text>
					</xsl:when>
				</xsl:choose>
				<xsl:call-template name="condense">
					<xsl:with-param name="list" select="substring-after($list,' ')"/>
					<xsl:with-param name="prev" select="$cur"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$list"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- output Overall summaries -->
	<xsl:template name="flavour">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="$resultsfile/testfile/hashtable/hashitem[@key='flavor']">
			<TD><B><xsl:value-of select="."/></B></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="totalCases">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="$resultsfile/testfile/teststatus">
			<TD><xsl:value-of select="@Pass-cases + @Fail-cases + @Errr-cases"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="passCases">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="$resultsfile/testfile/teststatus">
			<TD><xsl:value-of select="@Pass-cases"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="failCases">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="$resultsfile/testfile/teststatus">
			<TD><xsl:value-of select="@Fail-cases"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="errrCases">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="$resultsfile/testfile/teststatus">
			<TD><xsl:value-of select="@Errr-cases"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="totalChecks">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="$resultsfile/testfile/teststatus">
			<TD><xsl:value-of select="@Pass-checks + @Fail-checks + @Errr-checks"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="passChecks">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="$resultsfile/testfile/teststatus">
			<TD><xsl:value-of select="@Pass-checks"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="failChecks">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="$resultsfile/testfile/teststatus">
			<TD><xsl:value-of select="@Fail-checks"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="errrChecks">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="$resultsfile/testfile/teststatus">
			<TD><xsl:value-of select="@Errr-checks"/></TD>
		</xsl:for-each>
	</xsl:template>

	<!-- Output Overall summary formatting -->
	<xsl:template name="teststatus">
		<xsl:param name="resultsfile"/>
		<xsl:variable name="flavs" select="count($resultsfile/testfile) + 1"/>
		<TR>
			<TD ROWSPAN="11"><B>Overall</B></TD>
			<TD>Flavour:</TD><xsl:call-template name="flavour"><xsl:with-param name="resultsfile" select="$resultsfile"/></xsl:call-template>
		</TR>
		<TR>
			<TD COLSPAN="{$flavs+1}">
				Categories
			</TD>
		</TR>
		<TR>
			<TD>Total:</TD><xsl:call-template name="totalCases"><xsl:with-param name="resultsfile" select="$resultsfile"/></xsl:call-template>
		</TR>
		<TR>
			<TD>Pass:</TD><xsl:call-template name="passCases"><xsl:with-param name="resultsfile" select="$resultsfile"/></xsl:call-template>
		</TR>
		<TR>
			<TD>Fail:</TD><xsl:call-template name="failCases"><xsl:with-param name="resultsfile" select="$resultsfile"/></xsl:call-template>
		</TR>
		<TR>
			<TD>Error:</TD><xsl:call-template name="errrCases"><xsl:with-param name="resultsfile" select="$resultsfile"/></xsl:call-template>
		</TR>
		<TR>
			<TD COLSPAN="{$flavs + 1}">
				Test Cases
			</TD>
		</TR>
		<TR>
			<TD>Total:</TD><xsl:call-template name="totalChecks"><xsl:with-param name="resultsfile" select="$resultsfile"/></xsl:call-template>
		</TR>
		<TR>
			<TD>Pass:</TD><xsl:call-template name="passChecks"><xsl:with-param name="resultsfile" select="$resultsfile"/></xsl:call-template>
		</TR>
		<TR>
			<TD>Fail:</TD><xsl:call-template name="failChecks"><xsl:with-param name="resultsfile" select="$resultsfile"/></xsl:call-template>
		</TR>
		<TR>
			<TD>Error:</TD><xsl:call-template name="errrChecks"><xsl:with-param name="resultsfile" select="$resultsfile"/></xsl:call-template>
		</TR>
	</xsl:template>
</xsl:stylesheet>
