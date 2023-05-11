<%/*
       * Created on 14-set-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // TEMPLATE DELLE PAGINE DI POPUP CON MESSAGGI PER L'UTENTE
    %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML lang="it">
<HEAD>
<c:if test="${requestScope.forzaRedirect}">
	<meta HTTP-EQUIV="REFRESH" content="0; url=${pageContext.request.contextPath}/ErrorOpenScheda.do"></meta>
</c:if>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" >
	<jsp:param name="title" value='${gene:getString(pageContext,"titoloMaschera",gene:resource("label.tags.template.popup.titolo"))}' />
</jsp:include>

<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />
  
$(function() {
	$( "input.data" ).datepicker($.datepicker.regional[ "it" ]);
});

window.onfocus=fnFocus;
  
  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    hideMenuPopup();
    hideSubmenuNavbar();
  }
-->
</script>
<!-- M.F. 27/09/2006 Aggiungo gli oggetti e le funzioni javascript per la gestione delle form e dei messaggi -->
<script type="text/javascript"
  src="${contextPath}/js/forms.js"></script>
<gene:insert name="jsAzioniTags" src="/WEB-INF/pages/commons/jsAzioniTags.jsp" />

<gene:insert name="head" >
  </gene:insert>

</HEAD>
<BODY onload="setVariables();checkAttivaBloccoPaginaPopup();">
<jsp:include page="/WEB-INF/pages/commons/bloccaRichieste.jsp" />

<div class="titolomaschera"><gene:getString name="titoloMaschera" defaultVal='${gene:resource("label.tags.template.popup.titolo")}'/></div>
<div class="contenitore-errori-arealavoro"><jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" /></div>
<div class="contenitore-errori-arealavoro"><jsp:include page="/WEB-INF/pages/commons/javascriptMsg.jsp" /></div>
		
<div class="contenitore-popup">
	<table class="ricerca"> 
		<tr>
			<td>
				<gene:insert name="corpo" >Corpo</gene:insert>
			</td>
		</tr>
		<tr>
			<td class="comandi-dettaglio">
				<gene:insert name="buttons">
					<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</gene:insert>
		 </td>
		</tr>
	</table>
</div>		
		<gene:insert name="debug" >
			<%/*<gene:callFunction obj="it.eldasoft.gene.tags.utils.functions.DebugFunctionTag" parametro="PARAMETRI"/>*/%>
		</gene:insert>
		<%/* Aggiungo l'eventuale archivio di passaggio */%>
		${gene:callFunction("it.eldasoft.gene.tags.utils.functions.ArchivioFormFunction",pageContext)}
		<!-- PARTE NECESSARIA PER VISUALIZZARE I POPUP MENU DI OPZIONI PER CAMPO -->
		<IFRAME class="gene" id="iframepopmenu"></iframe>
		<div id="popmenu" class="popupmenuskin"
			onMouseover="highlightMenuPopup(event,'on');"
			onMouseout="highlightMenuPopup(event,'off');"></div>
		<!-- CAMPO INDISPENSABILE PER ESEGUIRE IL COPIA/INCOLLA NEGLI APPUNTI DEL MNEMONICO CON IE -->
		<form action=""><input type="hidden" id="clipboard"></form>
		<gene:outJavaScript/>
		<script type="text/javascript">
    <!--
    	// Setto il numero di popup su tutte le form standard
    	setNumeroPopUp();
    	if (document.forms[0].numeroPopUp && document.forms[0].numeroPopUp.value >= 2 && document.getElementById("btnNuovo")) {
    	    document.getElementById("btnNuovo").style.visibility = "hidden";
    	}
    //-->
    </script>

</BODY>
</HTML>