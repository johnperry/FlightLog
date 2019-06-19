<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="Request">
<html>

<head>
	<title>Search Airports Database</title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/SearchServlet.css" type="text/css"/>
	<script language="JavaScript" type="text/javascript" src="/SearchServlet.js">;</script>
	<script>
		function loaded() {
			document.getElementById('apid').focus();
		}
		window.onload = loaded;
	</script>
</head>

<body>
<center>

	<h1>Search Airports Database</h1>
	<p><input type="button" value="Clear" onclick="clearForm()"/></p>

	<form method="POST" accept-charset="UTF-8">
	
	<table id="FormTable">
		<tr>
			<td>ID:</td>
			<td><input class="text" type="text" name="apid" id="apid" value="{ASC/@id}"/></td>
		</tr>
		<tr>
			<td>Name:</td>
			<td><input class="text" type="text" name="name" value="{ASC/@name}"/></td>
		</tr>
		<tr>
			<td>City:</td>
			<td><input class="text" type="text" name="city" value="{ASC/@city}"/></td>
		</tr>
		<tr>
			<td>State:</td>
			<td><input class="text" type="text" name="state" value="{ASC/@state}"/></td>
		</tr>
	</table>
	<p><input type="submit" value="Search"/></p>
	</form>

</center>
</body>

</html>
</xsl:template>

<xsl:template match="Airports">
<html>

<head>
	<title>Search Results</title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/ListFlightsServlet.css" type="text/css"/>
	<script language="JavaScript" type="text/javascript" src="/JSUtil.js">;</script>
</head>

<body>
<center>

	<h1>Search Results</h1>
	
	<xsl:if test="Airport">
		<table>
			<tr>
				<th/>
				<th>ID</th>
				<th>Name</th>
				<th>City</th>
				<th>Lat</th>
				<th>Lon</th>
				<th>Elev</th>
				<th>Var</th>
				<th>Runways</th>
			</tr>
			<xsl:for-each select="Airport">
				<xsl:sort select="@state"/>
				<xsl:sort select="@id"/>
				<tr>
					<td class="number"><xsl:value-of select="position()"/></td>
					<td>
						<a href="http://www.airnav.com/airport/{@id}">
							<xsl:value-of select="@id"/>
						</a>
					</td>
					<td><xsl:value-of select="@name"/></td>
					<td><xsl:value-of select="@city"/>, <xsl:value-of select="@state"/></td>
					<td class="right"><xsl:value-of select="@lat"/></td>
					<td class="right"><xsl:value-of select="@lon"/></td>
					<td class="right"><xsl:value-of select="@elev"/></td>
					<td class="right"><xsl:value-of select="@var"/></td>
					<td class="left">
						<xsl:for-each select="rwy">
							<xsl:value-of select="@id"/>
							<xsl:text>: </xsl:text>
							<xsl:value-of select="@len"/>
							<xsl:text>x</xsl:text>
							<xsl:value-of select="@wid"/>
							<xsl:text> </xsl:text>
							<xsl:value-of select="@type"/>
							<br/>
						</xsl:for-each>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:if>
	<xsl:if test="not(Airport)">
		<p>No matching airports found.</p>
	</xsl:if>

</center>
</body>

</html>
</xsl:template>

</xsl:stylesheet>