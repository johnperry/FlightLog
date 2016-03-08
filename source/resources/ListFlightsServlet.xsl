<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Flights">

<html>

<head>
	<title><xsl:value-of select="@title"/></title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/ListFlightsServlet.css" type="text/css"/>
</head>

<body>
<center>

	<h1><xsl:value-of select="@title"/></h1>
	
	<table>
		<tr>
			<th/>
			<th>Date</th>
			<th>A/C ID</th>
			<th>Route</th>
			<th>Total</th>
			<th>Ldg</th>
			<th>App</th>
			<th>Day</th>
			<th>Night</th>
			<th>XC</th>
			<th>Inst</th>
			<th>Hood</th>
			<th>Dual</th>
			<th>PIC</th>
		</tr>
		<xsl:for-each select="Flight">
			<xsl:sort select="@date"/>
			<xsl:sort select="number(@id)"/>
			<tr>
				<td class="number">
					<xsl:if test="@notes">
						<xsl:attribute name="title"><xsl:value-of select="@notes"/></xsl:attribute>
						<b><xsl:value-of select="position()"/></b>
					</xsl:if>
					<xsl:if test="not(@notes)">
						<xsl:value-of select="position()"/>
					</xsl:if>
				</td>
				<td><a href="/addflight?id={@id}"><xsl:value-of select="@date"/></a></td>
				<td><a href="/addaircraft?acid={@acid}"><xsl:value-of select="@acid"/></a></td>
				<td><xsl:value-of select="@route"/></td>
				<td class="right">
					<xsl:if test="@tach">
						<xsl:attribute name="title">tach=<xsl:value-of select="@tach"/></xsl:attribute>
					</xsl:if>
					<xsl:value-of select="@total"/>
				</td>
				<td class="right"><xsl:value-of select="@ldg"/></td>
				<td class="right"><xsl:value-of select="@app"/></td>
				<td class="right"><xsl:value-of select="@tday"/></td>
				<td class="right"><xsl:value-of select="@tnt"/></td>
				<td class="right"><xsl:value-of select="@txc"/></td>
				<td class="right"><xsl:value-of select="@inst"/></td>
				<td class="right"><xsl:value-of select="@hood"/></td>
				<td class="right"><xsl:value-of select="@dual"/></td>
				<td class="right"><xsl:value-of select="@pic"/></td>
			</tr>
		</xsl:for-each>
		<xsl:if test="Totals">
			<tr/>
			<tr>
				<td class="totals"/>
				<td class="totals">Totals:</td>
				<td class="totals" colspan="2"/>
				<td class="right"><xsl:value-of select="Totals/@total"/></td>
				<td class="right"><xsl:value-of select="Totals/@ldg"/></td>
				<td class="right"><xsl:value-of select="Totals/@app"/></td>
				<td class="right"><xsl:value-of select="Totals/@tday"/></td>
				<td class="right"><xsl:value-of select="Totals/@tnt"/></td>
				<td class="right"><xsl:value-of select="Totals/@txc"/></td>
				<td class="right"><xsl:value-of select="Totals/@inst"/></td>
				<td class="right"><xsl:value-of select="Totals/@hood"/></td>
				<td class="right"><xsl:value-of select="Totals/@dual"/></td>
				<td class="right"><xsl:value-of select="Totals/@pic"/></td>
			</tr>
		</xsl:if>
		<tr/>
		<tr/>
		<tr/>
	</table>

</center>
</body>

</html>

</xsl:template>

</xsl:stylesheet>