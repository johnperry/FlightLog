<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Request">

<html>

<head>
	<title>Add Aircraft</title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/AddAircraftServlet.css" type="text/css"/>
	<script language="JavaScript" type="text/javascript" src="/JSUtil.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSAJAX.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSPage.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/AddAircraftServlet.js">;</script>
</head>

<body>
<center>

	<h1>Add Aircraft</h1>
	
	<form action="" method="POST" accept-charset="UTF-8">

		<table>

			<tr>
				<td>A/C ID:</td>
				<td>
					<input class="text" id="id" type="text" name="acid" value="{Aircraft/@acid}"/>
				</td>
			</tr>
			<tr>
				<td>Model:</td>
				<td>
					<input class="text" type="text" name="model" value="{Aircraft/@model}"/>
				</td>
			</tr>
			<tr>
				<td>Class:</td>
				<td class="radio">
					<input id="ASEL" type="radio" name="category" value="ASEL">
						<xsl:if test="not(Aircraft) or (Aircraft/@category='ASEL')">
							<xsl:attribute name="checked">true</xsl:attribute>
						</xsl:if>
						<label for="ASEL">ASEL</label>
					</input><br/>
					<input id="ASES" type="radio" name="category" value="ASES">
						<xsl:if test="Aircraft/@category='ASES'">
							<xsl:attribute name="checked">true</xsl:attribute>
						</xsl:if>
						<label for="ASES">ASES</label>
					</input><br/>
					<input id="AMEL" type="radio" name="category" value="AMEL">
						<xsl:if test="Aircraft/@category='AMEL'">
							<xsl:attribute name="checked">true</xsl:attribute>
						</xsl:if>
						<label for="AMEL">AMEL</label>
					</input><br/>
					<input id="AMES" type="radio" name="category" value="AMES">
						<xsl:if test="Aircraft/@category='AMES'">
							<xsl:attribute name="checked">true</xsl:attribute>
						</xsl:if>
						<label for="AMES">AMES</label>
					</input><br/>
					<input id="Glider" type="radio" name="category" value="Glider">
						<xsl:if test="Aircraft/@category='Glider'">
							<xsl:attribute name="checked">true</xsl:attribute>
						</xsl:if>
						<label for="Glider">Glider</label>
					</input><br/>
					<input id="Helicopter" type="radio" name="category" value="Helicopter">
						<xsl:if test="Aircraft/@category='Helicopter'">
							<xsl:attribute name="checked">true</xsl:attribute>
						</xsl:if>
						<label for="Helicopter">Helicopter</label>
					</input><br/>
				</td>
			</tr>
			<tr>
				<td><label for="tailwheel">Tailwheel</label>:</td>
				<td>
					<input id="tailwheel" type="checkbox" name="tailwheel" value="yes">
						<xsl:if test="Aircraft/@tailwheel='yes'">
							<xsl:attribute name="checked">true</xsl:attribute>
						</xsl:if>
					</input>
				</td>
			</tr>
			<tr>
				<td><label for="retractable">Retractable</label>:</td>
				<td>
					<input id="retractable" type="checkbox" name="retractable" value="yes">
						<xsl:if test="Aircraft/@retractable='yes'">
							<xsl:attribute name="checked">true</xsl:attribute>
						</xsl:if>
					</input>
				</td>
			</tr>
			<tr>
				<td><label for="complex">Complex</label>:</td>
				<td>
					<input id="complex" type="checkbox" name="complex" value="yes">
						<xsl:if test="Aircraft/@complex='yes'">
							<xsl:attribute name="checked">true</xsl:attribute>
						</xsl:if>
					</input>
			</td>
			</tr>

		</table>

		<p><input type="submit" value="Submit Changes"/></p>
	
	</form>

</center>
</body>

</html>

</xsl:template>

</xsl:stylesheet>