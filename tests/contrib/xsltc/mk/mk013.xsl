<xsl:stylesheet
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 version="1.0"
>

  <!-- Test FileName: mk013.xsl -->
  <!-- Source Attribution: 
       This test was written by Michael Kay and is taken from 
       'XSLT Programmer's Reference' published by Wrox Press Limited in 2000;
       ISBN 1-861003-12-9; copyright Wrox Press Limited 2000; all rights reserved. 
       Now updated in the second edition (ISBN 1861005067), http://www.wrox.com.
       No part of this book may be reproduced, stored in a retrieval system or 
       transmitted in any form or by any means - electronic, electrostatic, mechanical, 
       photocopying, recording or otherwise - without the prior written permission of 
       the publisher, except in the case of brief quotations embodied in critical articles or reviews.
  -->
  <!-- Example: poem.xml, number-lines.xsl -->
  <!-- Chapter/Page: 4-165 -->
  <!-- Purpose: Using an attribute set for numbering -->

<xsl:strip-space elements="*"/>
<xsl:output method="xml" indent="yes"/>

<xsl:template match="*">
	<xsl:copy>
	<xsl:apply-templates/>
	</xsl:copy>   
</xsl:template>

<xsl:template match="line">
	<xsl:copy use-attribute-sets="sequence">
	<xsl:apply-templates/>
	</xsl:copy>   
</xsl:template>

<xsl:attribute-set name="sequence">
	<xsl:attribute name="number"><xsl:value-of select="position()"/></xsl:attribute>
	<xsl:attribute name="of"><xsl:value-of select="last()"/></xsl:attribute>
</xsl:attribute-set>

</xsl:stylesheet>
