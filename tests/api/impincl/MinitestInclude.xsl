<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Filename: MinitestInclude.xsl -->
<!-- Author: Paul_Dick@lotus.com -->

<xsl:template name="tree">
   <xsl:param name="Surname" select="Default"/>

   <xsl:comment> Create an simplified output tree for each family </xsl:comment><xsl:text>&#10;   </xsl:text>
   <xsl:element name="{$root}">
   <xsl:element name="{substring-after($Surname,'The ')}">

       <!-- Start with the Parents.-->
	   <xsl:element name="Parents">
	   <xsl:for-each select="Parents/Father | Parents/Mother">
		  <xsl:element name="{name(.)}">
		     <xsl:value-of select="."/>
		  </xsl:element>
	   </xsl:for-each>
	   </xsl:element>

	   <!-- Then the Children. -->
       <xsl:element name="Children">
	   <xsl:attribute name="number">
	      <xsl:value-of select="count(Children/Child)"/>
	   </xsl:attribute>
	   <xsl:for-each select="Children/Child">
		     <xsl:element name="{Personal_Information/Sex}">

			  <xsl:attribute name="name">
			     <xsl:value-of select="Basic_Information/Name/@First"/>
			  </xsl:attribute>
			  <xsl:choose>
			  <xsl:when test="Personal_Information/Sex='Male'">
				<xsl:attribute name="wife">
					<xsl:value-of select="Personal_Information/Family_Information/Wife/Name/@First"/>
				</xsl:attribute>
			  </xsl:when>
			  <xsl:when test="Personal_Information/Sex='Female'">
				<xsl:attribute name="husband">
					<xsl:value-of select="Personal_Information/Family_Information/Husband/Name/@First"/>
				</xsl:attribute>
			  </xsl:when>
			  </xsl:choose>

			  <xsl:attribute name="kids">
				  <xsl:value-of select="count(Personal_Information/Family_Information/Kids/Kid)"/>
			  </xsl:attribute>				

			  <xsl:if test="count(Personal_Information/Family_Information/Kids/Kid)">
			     <xsl:element name="Kids">
			        <xsl:for-each select="Personal_Information/Family_Information/Kids/Kid">
			           <xsl:element name="Grandkid"><xsl:value-of select="Name/@First"/></xsl:element>
			        </xsl:for-each>
			     </xsl:element>
			  </xsl:if>
			   
			  <xsl:value-of select="Basic_Information/Name/@First"/>
			 </xsl:element>
	   </xsl:for-each>
	   </xsl:element>

   </xsl:element>
   </xsl:element>
</xsl:template>
</xsl:stylesheet>