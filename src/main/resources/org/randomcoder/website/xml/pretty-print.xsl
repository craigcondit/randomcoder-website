<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" encoding="UTF-8" />
<xsl:strip-space elements="*" />
<xsl:template match="*">
 <xsl:copy>
   <xsl:copy-of select="@*" />
   <xsl:apply-templates />
 </xsl:copy>
</xsl:template>
</xsl:stylesheet>