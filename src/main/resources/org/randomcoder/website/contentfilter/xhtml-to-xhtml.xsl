<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- we use US-ASCII here because it is a subset of UTF-8, and gives single-byte chars -->
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" encoding="US-ASCII" />
	<xsl:template match="/html/body">
		<div class="application-xhtml-xml"><xsl:copy-of select="*|@*|node()" /></div>
	</xsl:template>		
</xsl:stylesheet>