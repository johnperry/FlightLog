<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Request">

<html>

<head>
	<title>Add Flight</title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/AddFlightServlet.css" type="text/css"/>
	<script language="JavaScript" type="text/javascript" src="/JSUtil.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSAJAX.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSPage.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/AddFlightServlet.js">;</script>
	<script>
		function setFocus() {
			document.getElementById("date").focus();
		}
		window.onload = setFocus;
	</script>
</head>

<body>
<center>

	<h1>Add an Image to Flight (<xsl:value-of select="Flight/@id"/>)</h1>

	<form method="POST" accept-charset="UTF-8" enctype="multipart/form-data">
	
	<input type="hidden" id="id" name="id" value="{Flight/@id}"/>

	<table>
		<tr>
			<td>Date:</td>
			<td><xsl:value-of select="Flight/@date"/></td>
		</tr>
		<tr>
			<td>Aircraft ID:</td>
			<td><xsl:value-of select="Flight/@acid"/></td>
		</tr>
		<tr>
			<td>Route:</td>
			<td><xsl:value-of select="Flight/@route"/></td>
		</tr>
		<xsl:if test="Flights/@notes != ''">
			<tr>
				<td colspan="2">
					Notes:<br/>
					<xsl:value-of select="Flight/@notes"/>
				</td>
			</tr>
		</xsl:if>
		<tr>
			<td>Add image:</td>
			<td><input size="75" name="file" id="file" type="file"/></td>
		</tr>
	</table>
	
	<p>
		<input type="submit" value="Submit Image" tabindex="17"/>
	</p>
	
	</form>

</center>
</body>

</html>

</xsl:template>

</xsl:stylesheet>