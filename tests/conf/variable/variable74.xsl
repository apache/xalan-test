<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:ex="http://xml.apache.org/xalan"
                extension-element-prefixes="ex">

  <!-- FileName: variable74 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.4 -->
  <!-- Creator: Joe Kesselman -->
  <!-- Purpose: Ensure that traversal of global RTF gets the right one.
	Similar to Variable72
	-->

<xsl:output method="xml" indent="no" encoding="UTF-8"/>


<xsl:template match="/">
	<xsl:variable name="top1">
	<t1-far-north>
		<t1-north>
			<t1-near-north>
				<t1-far-west/>
				<t1-west/>
				<t1-near-west/>
				<center>Wrong variable, can you dig it?
					<t1-near-south>
						<t1-south>
							<t1-far-south/>
						</t1-south>
					</t1-near-south>
				</center>
					<t1-near-east/>
					<t1-east/>
					<t1-far-east/>
			</t1-near-north>
		</t1-north>
	</t1-far-north>
	</xsl:variable>

	<xsl:variable name="top2">
	<t2-far-north>
		<t2-north>
			<t2-near-north>
				<t2-far-west/>
				<t2-west/>
				<t2-near-west/>
				<center>Dig we must!
					<t2-near-south>
						<t2-south>
							<t2-far-south/>
						</t2-south>
					</t2-near-south>
				</center>
					<t2-near-east/>
					<t2-east/>
					<t2-far-east/>
			</t2-near-north>
		</t2-north>
	</t2-far-north>
	</xsl:variable>

  <out>
    <!-- First, force evaluation of each variable -->
    <junk>
      <xsl:text>$top1 summary: </xsl:text>
      <xsl:value-of select="$top1"/>
      <xsl:text>
</xsl:text>

      <xsl:text>$top2 summary: </xsl:text>
      <xsl:value-of select="$top2"/>
    </junk>
    <xsl:text>
</xsl:text>

    <xsl:text>The center nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/child::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/child::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/descendant::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/descendant::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/parent::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/parent::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/ancestor::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/ancestor::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/following-sibling::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/following-sibling::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/preceding-sibling::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/preceding-sibling::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/following::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/following::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/preceding::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/preceding::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/attribute::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/attribute::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/namespace::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/namespace::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/self::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/self::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/descendant-or-self::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/descendant-or-self::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

    <xsl:text>The center/ancestor-or-self::* nodes in $top2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($top2)//center/ancestor-or-self::*">
      <xsl:value-of select="name(.)"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>

  </out>
</xsl:template>

</xsl:stylesheet>
