<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                 version="1.0" >
<!-- SQL ================
select orders.*, customers.name from orders, customers
where orders.customer_id = customers.id
and orders.product = "my_reference"
 XSLT  ================ 
-->
<xsl:template match='/'>
<root>
 <xsl:apply-templates/>
</root>
</xsl:template>

<xsl:template match='customer' > </xsl:template>
<xsl:template match='orders' > </xsl:template>

<xsl:template match='orders[product = "my_reference"]'>
  <xsl:copy>  
   <xsl:apply-templates/>
  </xsl:copy>  
  <xsl:copy-of select="id(@customer_id)"/>
</xsl:template>
</xsl:stylesheet>
