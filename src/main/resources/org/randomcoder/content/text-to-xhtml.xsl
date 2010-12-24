<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- we use US-ASCII here because it is a subset of UTF-8, and gives single-byte chars -->
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" encoding="US-ASCII" />
	<xsl:variable name="newline">
		<xsl:text>
</xsl:text>
	</xsl:variable>
	<xsl:template match="/">
		<div class="text-plain">			
			<xsl:value-of select="$newline" />
			<xsl:for-each select="text/line">
				<xsl:value-of select="." />
				<xsl:if test="position() != last()">					
					<br />
				</xsl:if>
				<xsl:value-of select="$newline" />
			</xsl:for-each>
		</div>
	</xsl:template>	
</xsl:stylesheet>