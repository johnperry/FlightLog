<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Images">
	<html>
	<head>
		<title>Images</title>
		<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
		<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
		<link rel="stylesheet" href="/ListFlightsServlet.css" type="text/css"/>
	<style>
		h1 {text-align:center;}
		p {margin-left:40px; }
	</style>
	</head>
	<body>
		<h1>Images</h1>
		<xsl:apply-templates select="Flight"/>
	</body>
	</html>
</xsl:template>

<xsl:template match="Flight">
	<h2>
		<a href="/addflight?id={@id}">
			<xsl:value-of select="@date"/>
		</a>
	</h2>
	<xsl:apply-templates select="Image"/>
</xsl:template>

<xsl:template match="Image">
	<p>
		<img src="/images/{@name}"/>
	</p>
</xsl:template>

</xsl:stylesheet>