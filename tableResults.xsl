<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
                xmlns:xalan="http://xml.apache.org/xalan"
                extension-element-prefixes="redirect" version="1.0">
	<xsl:output method="xml" indent="yes" xalan:indent-amount="2"
			doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
			doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" />

<!-- FileName: tableResults.xsl -->
<!-- Author:   Gordon Chiu <grchiu@ca.ibm.com> -->
<!-- Date:     10/10/2002 -->
<!-- Purpose:  Format org.apache.qetest.XMLFileLogger logFile results 
               into a table-based HTML page -->
<!-- Modified: 10/17/2002 - removed 'trax', is 'systemId'
               10/22/2002 - takes directory as argument
               12/10/2002 - some bug fixes for regression checker
                          - bug fix for 0-failure or 0-error reports
                          - added capability to tabulate other testTypes -->

<!-- Quick how to use:

  This stylesheet operates on the results of a build alltest.conf.
  1. In place of viewResults.xsl to analyze results:
     In Windows or UNIX, set the RESULTSCANNER environment 
     variable to tableResults.xsl
     Execute viewResults.bat results-alltest\conf\sax\results.xml.
     (the stylesheet will find the other xml files).
     In UNIX, execute viewResults.sh as above.
  2. To compare a current run against a previous run:
     Run the stylesheet with the parameter 'compareAgainst' which
     points to the results-alltest/conf directory of another test run.
  3. To analyze results from build alltest.conf.xsltc, run the
     stylesheet with the parameter 'resultDir' which points to the
     results-alltest.xsltc/conf directory of the xsltc test run.

  Alternate (newer) way to run based on results of a specific test:

  1. build smoketest, build alltest, or build conf
  2. Run stylesheet with the parameter 'testType' set to either smoketest,
     alltest, or conf. tableResults will automatically choose the correct
     output directory and names. See below.

-->

	
	<!-- 4 possible modes of operation:
	  1. Analyzing the results of an alltest.conf or alltest.accept;
	     must specify resultDir as pointing to results-alltest/conf
	     or results-alltest/accept. Default operation, testType="default"
	  2. Analyzing the results of an alltest; must specify
	     resultDir as pointing to results-alltest, testType="alltest"
	  3. Analyzing the results of an smoketest; must specify
	     resultDir as pointing to smoketest, testType="smoketest"
	  4. Analyzing the results of a conf or accept; must specify resultDir
	     as pointing to results-conf or results-accept, testType="conf"
        -->
	<xsl:param name="testType" select="default"/>
	<xsl:variable name="defaultPath">
		<xsl:choose>
			<xsl:when test="$testType = 'alltest'">
				<xsl:text>results-alltest</xsl:text>
			</xsl:when>
			<xsl:when test="$testType = 'smoketest'">
				<xsl:text>smoketest</xsl:text>
			</xsl:when>
			<xsl:when test="$testType = 'conf'">
				<xsl:text>results-conf</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>results-alltest/conf</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- Directory of results to analyze
	     the 'default' setting should be ok if testType is used -->
	<xsl:param name="resultDir" select="$defaultPath"/>

	<!-- Directory of results to compare against
	     Specify a directory of old results to get cross comparison. -->
	<xsl:param name="compareAgainst" select="$resultDir"/>

	<!-- Basic Structure of output document -->
	<xsl:template match="/">
		<HTML>
			<HEAD>
				<STYLE TYPE="text/css">
					body { font-family: Verdana; font-size: 9pt; }
					tr { font-family: Verdana; font-size: 9pt; }
					td { font-family: Verdana; font-size: 9pt; }
				</STYLE>
			</HEAD>
			<BODY>
				<TABLE BORDER="1" CELLPADDING="5" CELLSPACING="0">
					<xsl:choose>
						<xsl:when test="$testType = 'smoketest' or $testType = 'alltest'">
							<xsl:call-template name="execute">
								<xsl:with-param name="bucket" select="'conf'"/>
								<xsl:with-param name="nolegend" select="1"/>
							</xsl:call-template>
							<xsl:call-template name="execute">
								<xsl:with-param name="bucket" select="'accept'"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="execute"/>
						</xsl:otherwise>
					</xsl:choose>
				</TABLE>
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
		<xsl:if test="hashtable/hashitem[@key='processor']">
		  <BR/>
		  <xsl:text>Processor: </xsl:text>
			<xsl:value-of select="hashtable/hashitem[@key='processor']"/>
		</xsl:if>
	</xsl:template>
  
	<!-- Output the summary and category summaries for test sets. -->
	<xsl:template name="execute">
		<xsl:param name="bucket" select="'conf'"/>
		<xsl:param name="nolegend" select="0"/>
		<!-- Set up two variables for test results, and old test results. -->
		<xsl:variable name="resultsfile">
			<xsl:choose>
				<xsl:when test="$testType = 'alltest'">
					<xsl:copy-of select="document(concat($resultDir,'/',$bucket,'/dom/results.xml'))/resultsfile/testfile 
						| document(concat($resultDir,'/',$bucket,'/sax/results.xml'))/resultsfile/testfile 
						| document(concat($resultDir,'/',$bucket,'/stream/results.xml'))/resultsfile/testfile 
						| document(concat($resultDir,'/',$bucket,'/file/results.xml'))/resultsfile/testfile 
						| document(concat($resultDir,'/',$bucket,'/systemId/results.xml'))/resultsfile/testfile 
						| document(concat($resultDir,'/',$bucket,'/localPath/results.xml'))/resultsfile/testfile"/>
				</xsl:when>
				<xsl:when test="$testType = 'smoketest'">
					<xsl:copy-of select="document(concat($resultDir,'/results-',$bucket,'.xml'))/resultsfile/testfile"/>
				</xsl:when>
				<xsl:when test="$testType = 'conf'">
					<xsl:copy-of select="document(concat($resultDir,'/results.xml'))/resultsfile/testfile"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="document(concat($resultDir,'/dom/results.xml'))/resultsfile/testfile 
						| document(concat($resultDir,'/sax/results.xml'))/resultsfile/testfile 
						| document(concat($resultDir,'/stream/results.xml'))/resultsfile/testfile 
						| document(concat($resultDir,'/file/results.xml'))/resultsfile/testfile 
						| document(concat($resultDir,'/systemId/results.xml'))/resultsfile/testfile 
						| document(concat($resultDir,'/localPath/results.xml'))/resultsfile/testfile"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="oldfile">
			<xsl:choose>
				<xsl:when test="$testType = 'alltest'">
					<xsl:copy-of select="document(concat($compareAgainst,'/',$bucket,'/dom/results.xml'))/resultsfile/testfile 
							| document(concat($compareAgainst,'/',$bucket,'/sax/results.xml'))/resultsfile/testfile 
							| document(concat($compareAgainst,'/',$bucket,'/stream/results.xml'))/resultsfile/testfile 
							| document(concat($compareAgainst,'/',$bucket,'/file/results.xml'))/resultsfile/testfile 
							| document(concat($compareAgainst,'/',$bucket,'/systemId/results.xml'))/resultsfile/testfile 
							| document(concat($compareAgainst,'/',$bucket,'/localPath/results.xml'))/resultsfile/testfile"/>
				</xsl:when>
				<xsl:when test="$testType = 'smoketest'">
					<xsl:copy-of select="document(concat($compareAgainst,'/results-',$bucket,'.xml'))/resultsfile/testfile"/>
				</xsl:when>
				<xsl:when test="$testType = 'conf'">
					<xsl:copy-of select="document(concat($compareAgainst,'/results.xml'))/resultsfile/testfile"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="document(concat($compareAgainst,'/dom/results.xml'))/resultsfile/testfile 
							| document(concat($compareAgainst,'/sax/results.xml'))/resultsfile/testfile 
							| document(concat($compareAgainst,'/stream/results.xml'))/resultsfile/testfile 
							| document(concat($compareAgainst,'/file/results.xml'))/resultsfile/testfile 
							| document(concat($compareAgainst,'/systemId/results.xml'))/resultsfile/testfile 
							| document(concat($compareAgainst,'/localPath/results.xml'))/resultsfile/testfile"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- Number of flavours; used for column widths -->
		<xsl:variable name="flavs" select="count(xalan:nodeset($resultsfile)/testfile)"/>
		<TR>
			<xsl:element name="TD">
				<xsl:attribute name="COLSPAN">
					<xsl:value-of select="$flavs + 2"/>
				</xsl:attribute>
				<B>Conformance Test Results: <xsl:value-of select="$bucket"/></B>
			</xsl:element>
		</TR>
		<TR>
			<TD COLSPAN="1"><B>Environment</B></TD>
			<xsl:element name="TD">
				<xsl:attribute name="COLSPAN">
					<xsl:value-of select="$flavs + 1"/>
				</xsl:attribute>
				<xsl:variable name="flavor">
					<xsl:value-of select="xalan:nodeset($resultsfile)/testfile[last()]/hashtable/hashitem[@key='flavor']"/>
				</xsl:variable>
				<b>Currently Tested:</b><br/>
				<xsl:apply-templates select="xalan:nodeset($resultsfile)/testfile[last()]" mode="hashElements"/><br/>
				<b>Compared Against:</b><br/>
				<xsl:apply-templates select="xalan:nodeset($oldfile)/testfile[hashtable/hashitem[@key='flavor'] = $flavor]" mode="hashElements"/>
			</xsl:element>
		</TR>
		<xsl:call-template name="teststatus">
			<xsl:with-param name="resultsfile" select="$resultsfile"/>
			<xsl:with-param name="flavs" select="$flavs"/>
		</xsl:call-template>
		<TR>
			<xsl:element name="TD">
				<xsl:attribute name="COLSPAN">
					<xsl:value-of select="$flavs + 2"/>
				</xsl:attribute><B>Category Summaries</B>
			</xsl:element>
		</TR>
		<xsl:for-each select="xalan:nodeset($resultsfile)/testfile[last()]/testcase">
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
		<xsl:if test="not($nolegend)">
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
		</xsl:if>
	</xsl:template>

	<xsl:template name="testcase">
		<xsl:param name="resultsfile"/>
		<xsl:param name="oldfile"/>
		<xsl:param name="bg"/>
		<xsl:variable name="category" select="substring-after(@desc,': ')"/>
		<xsl:variable name="excludesListAll">
			<xsl:call-template name="buildList">
				<xsl:with-param name="category" select="$category"/>
				<xsl:with-param name="list" select="xalan:nodeset($resultsfile)//hashtable/hashitem[@key='excludes']"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="oldFailListAll">
			<xsl:for-each select="xalan:nodeset($oldfile)/testfile/testcase[contains(@desc,$category)]/checkresult[not(@result='Pass') and contains(@desc,$category)]">#</xsl:for-each>
		</xsl:variable>
		<xsl:element name="TR">
			<xsl:attribute name="BGCOLOR"><xsl:value-of select="$bg"/></xsl:attribute>
			<xsl:element name="TD">
				<xsl:attribute name="ROWSPAN">
					<xsl:choose>
						<xsl:when test="count(xalan:nodeset($resultsfile)/testfile/testcase[$category = substring-after(@desc,': ')]/checkresult[not(@result='Pass')]) > 0 or not($excludesListAll = '') or not($oldFailListAll = '')">
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
			<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/testcase[$category = substring-after(@desc,': ')]">
				<TD VALIGN="TOP">
					<xsl:value-of select="count(checkresult)"/>
				</TD>
			</xsl:for-each>
		</xsl:element>
		<xsl:element name="TR">
			<xsl:attribute name="BGCOLOR"><xsl:value-of select="$bg"/></xsl:attribute>
			<TD VALIGN="TOP">pass</TD>
			<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/testcase[$category = substring-after(@desc,': ')]">
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
		<xsl:if test="count(xalan:nodeset($resultsfile)/testfile/testcase[$category = substring-after(@desc,': ')]/checkresult[not(@result='Pass')]) > 0 or not($excludesListAll = '') or count(xalan:nodeset($oldfile)/testfile/testcase[contains(@desc,$category)]/checkresult[not(@result='Pass')]) > 0">
			<xsl:element name="TR">
				<xsl:attribute name="BGCOLOR"><xsl:value-of select="$bg"/></xsl:attribute>
				<TD VALIGN="TOP">fail/err</TD>
				<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/testcase[$category = substring-after(@desc,': ')]">
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
						<xsl:for-each select="xalan:nodeset($oldfile)/testfile[hashtable/hashitem[@key='flavor'] = $flavor]/testcase[contains(@desc,$category)]/checkresult[not(@result='Pass') and contains(@desc,$category)]">
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
						<xsl:with-param name="oldlist" select="$oldlist"/>
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
		<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/hashtable/hashitem[@key='flavor']">
			<TD><B><xsl:value-of select="."/></B></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="totalCases">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/teststatus">
			<TD><xsl:value-of select="sum(@Pass-cases) + sum(@Fail-cases) + sum(@Errr-cases)"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="passCases">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/teststatus">
			<TD><xsl:value-of select="sum(@Pass-cases)"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="failCases">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/teststatus">
			<TD><xsl:value-of select="sum(@Fail-cases)"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="errrCases">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/teststatus">
			<TD><xsl:value-of select="sum(@Errr-cases)"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="totalChecks">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/teststatus">
			<TD><xsl:value-of select="sum(@Pass-checks) + sum(@Fail-checks) + sum(@Errr-checks)"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="passChecks">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/teststatus">
			<TD><xsl:value-of select="sum(@Pass-checks)"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="failChecks">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/teststatus">
			<TD><xsl:value-of select="sum(@Fail-checks)"/></TD>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="errrChecks">
		<xsl:param name="resultsfile"/>
		<xsl:for-each select="xalan:nodeset($resultsfile)/testfile/teststatus">
			<TD><xsl:value-of select="sum(@Errr-checks)"/></TD>
		</xsl:for-each>
	</xsl:template>

	<!-- Output Overall summary formatting -->
	<xsl:template name="teststatus">
		<xsl:param name="resultsfile"/>
		<xsl:param name="flavs"/>
		<TR>
			<TD ROWSPAN="11"><B>Overall</B></TD>
			<TD>Flavour:</TD><xsl:call-template name="flavour"><xsl:with-param name="resultsfile" select="$resultsfile"/></xsl:call-template>
		</TR>
		<TR>
			<TD COLSPAN="{$flavs + 1}">
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
