<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Request">

<html>

<head>
	<title>Admin</title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/AdminServlet.css" type="text/css"/>
	<script language="JavaScript" type="text/javascript" src="/JSUtil.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSAJAX.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSPage.js">;</script>
</head>

<body>
<center>

	<h1>Admin</h1>

	<table>

		<tr>
			<td><input type="button" onclick="window.open('/users?suppress','_self');" value="User Manager"/></td>
		</tr>
		<tr>
			<td><input type="button" onclick="window.open('/logs?suppress','_self');" value="Log Viewer"/></td>
		</tr>
		<tr>
			<td><input type="button" onclick="window.open('/ping','_self');" value="Ping"/></td>
		</tr>
		<tr>
			<td><input type="button" onclick="window.open('/admin/shutdown','_self');" value="Shutdown"/></td>
		</tr>

	</table>

</center>
</body>

</html>

</xsl:template>

</xsl:stylesheet>