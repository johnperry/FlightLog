<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Aircraft">

<html>

<head>
	<title>Aircraft</title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/ListAircraftServlet.css" type="text/css"/>
</head>

<body>
<center>

	<h1>Aircraft</h1>
	
	<table>
		<tr>
			<th>A/C ID</th>
			<th>Model</th>
			<th>Category</th>
			<th>Tailwheel</th>
			<th>Retractable</th>
			<th>Complex</th>
		</tr>
		<xsl:for-each select="Aircraft">
			<xsl:sort select="@category"/>
			<xsl:sort select="@model"/>
			<xsl:sort select="@acid"/>
			<tr>
				<td><a href="/addaircraft?acid={@acid}"><xsl:value-of select="@acid"/></a></td>
				<td><xsl:value-of select="@model"/></td>
				<td class="center"><xsl:value-of select="@category"/></td>
				<td class="center">
					<xsl:if test="@tailwheel='yes'">&#10003;</xsl:if>
				</td>
				<td class="center">
					<xsl:if test="@retractable='yes'">&#10003;</xsl:if>
				</td>
				<td class="center">
					<xsl:if test="@complex='yes'">&#10003;</xsl:if>
				</td>
			</tr>
		</xsl:for-each>
	</table>

</center>
</body>

</html>

</xsl:template>

</xsl:stylesheet>