<%/*
       * Created on 10-ott-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE IL TEMPLATE DELLA PAGINA DI LISTA
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />

<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />

$(function() {
	$( "input.data" ).datepicker($.datepicker.regional[ "it" ]);
});

  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    hideMenuPopup();
    hideSubmenuNavbar();
  }
-->
</script>
<tiles:insert attribute="linkJSSubMenuComune" />
<tiles:insert attribute="linkJSSubMenuSpecifico" />
<tiles:insert attribute="head" />
</HEAD>

<BODY <tiles:getAsString name="eventiDiPagina" /> >
<jsp:include page="/WEB-INF/pages/commons/bloccaRichieste.jsp" />
<TABLE class="arealayout">
	<!-- questa definizione dei gruppi di colonne serve a fissare la dimensione
	     dei td in modo da vincolare la posizione iniziale del menù di navigazione
	     sopra l'area lavoro appena al termine del menù contestuale -->
	<colgroup width="150px"></colgroup>
	<colgroup width="*"></colgroup>
	<TBODY>
		<TR class="testata">
			<TD colspan="2"><tiles:insert attribute="testata" /></TD>
		</TR>

		<TR class="menuprincipale">
			<TD><img src="${contextPath}/img/spacer-azionicontesto.gif" alt=""></TD>

			<TD>
			<table class="contenitore-navbar">
				<tbody>
					<tr>
						<tiles:insert attribute="menuSpecifico" />
						<tiles:insert attribute="menuComune" />
					</tr>
				</tbody>
			</table>

			<!-- PARTE NECESSARIA PER VISUALIZZARE I SOTTOMENU DEL MENU PRINCIPALE DI NAVIGAZIONE -->
			<IFRAME class="gene" id="iframesubnavmenu"></iframe>
			<div id="subnavmenu" class="subnavbarmenuskin"
				onMouseover="highlightSubmenuNavbar(event,'on');"
				onMouseout="highlightSubmenuNavbar(event,'off');"></div>
			</TD>
		</TR>

		<TR>
			<TD class="menuazioni" valign="top">
			<div id="menulaterale">
				</div>
			</TD>

			<TD class="arealavoro">

			<jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp" />

			<div class="contenitore-arealavoro">

			<div class="titolomaschera"><tiles:getAsString name="titoloMaschera" /></div>

			<jsp:include page="/WEB-INF/pages/commons/balloon.jsp" />

			<div class="contenitore-errori-arealavoro">
				<jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
			</div>

			<div class="contenitore-dettaglio"><tiles:insert
				attribute="dettaglio" /></div>

			</div>

			<!-- PARTE NECESSARIA PER VISUALIZZARE I POPUP MENU DI OPZIONI PER CAMPO -->
			<IFRAME class="gene" id="iframepopmenu"></iframe>
			<div id="popmenu" class="popupmenuskin"
				onMouseover="highlightMenuPopup(event,'on');"
				onMouseout="highlightMenuPopup(event,'off');"></div>

			</TD>
		</TR>

		<TR>
			<TD COLSPAN="2">
			<div id="footer">
				<jsp:include page="/WEB-INF/pages/commons/footer.jsp" />
			</div>
			</TD>
		</TR>

	</TBODY>
</TABLE>

<!-- CAMPO INDISPENSABILE PER ESEGUIRE IL COPIA/INCOLLA NEGLI APPUNTI DEL MNEMONICO CON IE -->
<form action=""><input type="hidden" id="clipboard"></form>
<%/* Eseguo l'output nel javascript se situazione mista con taglib GENE */%>
<gene:outJavaScript/>
</BODY>
</HTML>