<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Summary">

<html>

<head>
	<title>Summary</title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/ListFlightsServlet.css" type="text/css"/>
</head>

<body>
<center>

	<h1>Summary</h1>
	<h2><xsl:value-of select="@date"/></h2>
	
	<table>
		<tr>
			<th/>
			<th>Total</th>
			<th>Ldg</th>
		</tr>
		<xsl:for-each select="Totals">
			<tr>
				<td class="left"><xsl:value-of select="@title"/></td>
				<td class="right"><xsl:value-of select="@total"/></td>
				<td class="right"><xsl:value-of select="@ldg"/></td>
			</tr>
		</xsl:for-each>
	</table>
	<br/><br/><br/><br/>

</center>
</body>

</html>

</xsl:template>

</xsl:stylesheet>