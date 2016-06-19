<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <html><head></head><body bgcolor="#FFFFFF" text="#000000" link="#FF0000" vlink="#AA0000" alink="#000000" style="font-family:Arial; font-size:13px;">
	<xsl:apply-templates />
    </body></html>
  </xsl:template>


  <xsl:template match="titel">
    <center>
    <h1><xsl:value-of select="." /></h1>
    </center>
  </xsl:template>


  <xsl:template match="cover">
    <center>
      <img><xsl:attribute name="src"><xsl:value-of select="." /></xsl:attribute>
	<xsl:attribute name="alt"><xsl:value-of select="." /></xsl:attribute>
	<xsl:value-of select="." /></img>
    </center>
  </xsl:template>


  <xsl:template match="links">
    <center><table><tr>
	  <xsl:apply-templates/>
    </tr></table></center> 
  </xsl:template>


  <xsl:template match="link">
    <td><a><xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute>
	<xsl:value-of select="@typ" /></a></td>
  </xsl:template>


  <xsl:template match="zitate">
    <p>
      <b>Zitate: </b> 
      <ul>
	<xsl:apply-templates/>
      </ul>
    </p>
  </xsl:template>


  <xsl:template match="zitat">
    <li> <xsl:value-of select="."/></li>
  </xsl:template>


  <xsl:template match="textfelder/rss">
    <p>
      <center>
      <b><xsl:value-of select="."/></b>
      </center>
    </p>
  </xsl:template>


  <xsl:template match="textfelder/handlung">
    <p>
      <b>Handlung: </b><xsl:value-of select="."/>
    </p>
  </xsl:template>


  <xsl:template match="textfelder/technisch">
    <p>
      <b>Fehler (Technisch): </b><xsl:value-of select="."/>
    </p>
  </xsl:template>


  <xsl:template match="textfelder/wissenschaft">
    <p>
      <b>Fehler (Wissenschaftlich): </b><xsl:value-of select="."/>
    </p>
  </xsl:template>


  <xsl:template match="textfelder/inhalt">
    <p>
      <b>Fehler (Inhaltlich-Logisch): </b><xsl:value-of select="."/>
    </p>
  </xsl:template>


  <xsl:template match="textfelder/bild">
    <p>
      <b>Welches Bild vermittelt der Film? </b><xsl:value-of select="."/>
    </p>
  </xsl:template>


  <xsl:template match="textfelder/bemerkungen">
    <p>
      <b>Bemerkungen: </b><xsl:value-of select="."/>
    </p>
  </xsl:template>


  <xsl:template match="details/originaltitel">
      <b>Originaltitel: </b><xsl:value-of select="."/>
  </xsl:template>


  <xsl:template match="details/jahr">
      <b>Jahr: </b><xsl:value-of select="."/>
  </xsl:template>


  <xsl:template match="details/fsk">
      <b>FSK: </b><xsl:value-of select="."/>
  </xsl:template>


  <xsl:template match="details/land">
      <b>Land: </b><xsl:value-of select="."/>
  </xsl:template>


  <xsl:template match="details/genre">
      <b>Genre: </b><xsl:value-of select="."/>
  </xsl:template>


  <xsl:template match="details">
    <center>
      <xsl:apply-templates/>
    </center>
  </xsl:template>

</xsl:stylesheet>
