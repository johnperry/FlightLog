<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Oddballs">

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
			<th>From</th>
			<th>To</th>
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
				<td><xsl:value-of select="@from"/></td>
				<td>
					<xsl:if test="@to!='local'"><xsl:value-of select="@to"/></xsl:if>
				</td>
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
				<td class="totals" colspan="3"/>
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
	</table>
	
	<xsl:apply-templates select="UnreferencedAC"/>
	
	<xsl:if test="MissingAC">
		<h1>Missing Aircraft</h1>
		<table>
			<xsl:for-each select="MissingAC">
				<xsl:sort select="@acid"/>
				<tr>
					<td>
						<a href="/addaircraft?acid={@acid}">
							<xsl:value-of select="@acid"/>
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:if>
	<br/><br/><br/><br/>
</center>
</body>

</html>

</xsl:template>

<xsl:template match="UnreferencedAC">
	<h1>Unreferenced Aircraft</h1>
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
				<td>
					<input type="button" value="Remove" onclick="window.open('?removeAC={@acid}','_self');"/>
				</td>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>

</xsl:stylesheet>