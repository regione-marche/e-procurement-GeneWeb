<%/*
   * Created on 17-lug-2009
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // CONTIENE LA PAGINA CON LE INFO ACCESSIBILITA'
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML>
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />

  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    //hideSubmenuNavbar();
    hideMenuPopup();
    hideSubmenuNavbar();
  }
-->
</script>
<jsp:include page="/WEB-INF/pages/commons/jsSubMenuComune.jsp" />
<jsp:include page="/WEB-INF/pages/commons/jsSubMenuSpecifico.jsp" />
<BODY onload="setVariables();checkLocation();initPage();">
<TABLE class="arealayout">
	<!-- questa definizione dei gruppi di colonne serve a fissare la dimensione
	     dei td in modo da vincolare la posizione iniziale del menù di navigazione
	     sopra l'area lavoro appena al termine del menù contestuale -->
	<colgroup width="150px"></colgroup>
	<colgroup width="*"></colgroup>
	<TBODY>
		<TR class="testata">
			<TD colspan="2">
			<jsp:include page="/WEB-INF/pages/commons/testata.jsp" />
			</TD>
		</TR>
		<TR class="menuprincipale">
			<TD><img src="${contextPath}/img/spacer-azionicontesto.gif" alt=""></TD>
			<c:choose>
			<c:when test="${! empty sessionScope.profiloUtente}">
			<TD>
			<table class="contenitore-navbar">
				<tbody>
					<tr>
						<jsp:include page="/WEB-INF/pages/commons/menuSpecifico.jsp" />
						<jsp:include page="/WEB-INF/pages/commons/menuComune.jsp" />
					</tr>
				</tbody>
			</table>

			<!-- PARTE NECESSARIA PER VISUALIZZARE I SOTTOMENU DEL MENU PRINCIPALE DI NAVIGAZIONE -->
			<iframe id="iframesubnavmenu" class="gene"></iframe>
			<div id="subnavmenu" class="subnavbarmenuskin"
				onMouseover="highlightSubmenuNavbar(event,'on');"
				onMouseout="highlightSubmenuNavbar(event,'off');"></div>
			</TD>
			</c:when>
			<c:otherwise>
			<TD>&nbsp;</TD>
			</c:otherwise>
			</c:choose>
		</TR>
		<TR>
			<TD class="menuazioni" valign="top">
			<div id="menulaterale"></div>
			</TD>
			<TD class="arealavoro">
			
			<jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp" />
		
			<div class="contenitore-arealavoro">

			<div class="titolomaschera">Accessibilit&agrave;</div>
			

Nella realizzazione della presente applicazione web sono state adottate soluzioni e tecnologie tali da garantire la migliore fruibilit&agrave; e accessibilit&agrave; possibile delle funzionalit&agrave;, dei contenuti e della loro presentazione.  In particolare:
<ul>
<li>&egrave; stato utilizzato un linguaggio html standard (adottando un dizionario di tag e attributi conforme alle specifiche HTML 4.01 Transitional del Word Wide Web Consortium)</li>
<li>sono stati separati i contenuti delle pagine web dalla loro presentazione (utilizzando fogli di stile conformi agli standard CSS del Word Wide Web Consortium)</li>
<li>non viene fatto uso di frame</li>
<li>le immagini utilizzate nel sito sono state corredate da descrizioni testuali alternative attivate al passaggio del mouse</li>
<li>si sono scelti colori sobri, limitandone la variet&agrave;; i colori sono stati usati come elementi decorativi senza alcun significato informativo</li>
<li>i link sono realizzati con nomi chiari, non ambigui, autoesplicativi</li>
<li>nella navigazione del sito l'apertura di nuove finestre &egrave; stata limitata a link di documenti (quali: pdf, doc, xls, ecc.) o a funzionalit&agrave; particolari (es: accesso al manuale utente)</li>
</ul>
Per rendere possibile la navigazione senza mouse, tutte le funzionalit&agrave; contenute nelle pagine possono essere raggiunte utilizzando alcuni comandi da tastiera:
<ul>
<li>TAB per spostarsi tra link, campi, tag, bottoni, ecc.</li>
<li>INVIO per attivare link o funzioni (posizionandosi sopra i link o i bottoni con il tasto TAB, la pressione del tasto invio corrisponde al click con il mouse)</li>
</ul>

Per l'utilizzo dell'applicazione &egrave; sufficiente disporre di un browser internet e di una connessione alla rete internet preferibilmente a banda larga (tipo ADSL).<br>
Non &egrave; richiesta l'esecuzione di applet, plug-in o componenti client non standard; viene fatto uso di scripts lato client (javascript) di tipo standard (collaudati sui browser pi&ugrave; diffusi).<br>
<br>
Pur supportando qualunque sistema client (Linux, Mac, ecc.) e browser, il corretto funzionamento della procedura &egrave; collaudato sulla seguente configurazione:<br>
<br>
<i>Sistema operativo client:</i><ul>
<li>Microsoft Windows XP, Vista e Seven</li>
<li>Linux Ubuntu vers.10.04</li>
</ul>
<br>
<i>Browser:</i><ul>
<li>Microsoft Internet Explorer versioni 7 o successivi</li>
<li>Mozilla Firefox versioni 2.x o successivi</li>
<li>Google Chrome</li>
</ul>
<br>
<i>Risoluzione schermo:</i><ul>
<li>consigliato 1024 x 768 pixel a 65.000 colori, o superiore</li>
</ul>
<br>
Siamo convinti che l'accessibilit&agrave; sia innanzitutto un percorso.
<br>
Abbiamo lavorato e continuiamo a lavorare per rendere migliore il nostro 
sito, con l'obiettivo di renderlo sempre pi&ugrave; "accessibile" e fruibile 
per tutte le categorie di utenti.

<br>
<br>
<c:if test="${empty sessionScope.profiloUtente}">
Torna alla pagina di <a class="link-generico" href="${pageContext.request.contextPath}/">login</a>.
</c:if>
</div>
</TD>
		</TR>

		<TR>
			<TD COLSPAN="2">
			<div id="footer">
				<gene:insert name="footer" src="/WEB-INF/pages/commons/footer.jsp" />
			</div>
			</TD>
		</TR>

	</TBODY>
</TABLE>

</BODY>
</HTML>