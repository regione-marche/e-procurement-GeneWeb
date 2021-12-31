<%
/*
 * Created on 30-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
 // GRUPPO (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>


<script type="text/javascript">
<!--

	<elda:jsBodyPopup varJS="linksetPopupentprinc" contextPath="${pageContext.request.contextPath}">
		<elda:jsVocePopup functionJS="popupFiltroEntita()" descrizione="Imposta filtro sull'entit&agrave;"/>
	</elda:jsBodyPopup>

	function creaModello(){
		document.location.href='Modello.do?'+csrfToken+'&metodo=creaModello';
	}

	function ricercaModelli(){
		document.location.href='InitTrovaModelli.do?'+csrfToken;
	}
	
	function annulla(){
		bloccaRichiesteServer();
		document.location.href='Modello.do?'+csrfToken+'&metodo=annullaCrea';
	}
	
	function listaModelli(){
		document.location.href='TrovaModelli.do?'+csrfToken+'&metodo=trovaModelli';
	}
	
  function modifica(id){
		document.location.href='Modello.do?'+csrfToken+'&metodo=modificaModello&idModello='+id;
  }
  
  function mostraModello(id, nomeFile){
  	if(confirm('<fmt:message key="info.download.confirm"/>'))
  		document.location.href='Modello.do?'+csrfToken+'&idModello='+id+'&da=dettaglio&metodo=downloadModello&nomeFile='+nomeFile;
  }

<!-- Azioni invocate dal tab menu -->

	function dettaglioModello(id){
		document.location.href='Modello.do?'+csrfToken+'&metodo=dettaglioModello&idModello='+id;
	}

	function listaGruppiModello(id){
		document.location.href='GruppiModello.do?'+csrfToken+'&metodo=listaGruppiModello&idModello='+id;
	}
	
	function listaParametriModello(id){
		document.location.href='ParametriModello.do?'+csrfToken+'&metodo=listaParametriModello&idModello='+id;
	}
	
	function modificaAssModelliGruppo(id){
		document.location.href='GruppiModello.do?'+csrfToken+'&metodo=modificaGruppiModello&idModello='+id;
	}
	
	function creaParametro(id){
		document.location.href='ParametriModello.do?'+csrfToken+'&metodo=creaParametroModello&idModello='+id;
	}

	function update(){
		// In questa funzione eseguo l'update della form
		var esito = true;
		
		
		if (esito && !controllaCampoInputObbligatorio(document.modelliForm.tipoModello, 'Tipo documento')){
		  esito = false;
		}
		if (esito && !controllaCampoInputObbligatorio(document.modelliForm.nomeModello, 'Nome')){
		  esito = false;
		}
		// Se si è in inserimento controllo che sia stato modificato il file
		if(document.modelliForm.nomeFile.value==""){
			// alert(document.modelliForm.selezioneFile.value);
			if (esito && !controllaCampoInputObbligatorio(document.modelliForm.selezioneFile, 'File')){
			  esito = false;
			}
		}
		
		if (esito && !controllaCampoInputObbligatorio(document.modelliForm.schemaPrinc, 'Schema principale')){
			esito = false;
		}
		
		if (esito && !controllaCampoInputObbligatorio(document.modelliForm.entPrinc, 'Argomento principale')){
			esito = false;
		}
		
		if (esito){
			bloccaRichiesteServer();
			document.modelliForm.submit();
		}		
	}
	
	function updateListaGruppi(){
		// Funzione che esegue l'update della lista dei gruppi
		bloccaRichiesteServer();
		gruppiModelliForm.submit();
	}
	
	function popupFiltroEntita(){
		var entitaSelezionata = document.modelliForm.entPrinc.value;
		if(entitaSelezionata != null && entitaSelezionata != ""){
			var href = csrfToken+'&metodo=apri&entita=' + entitaSelezionata;
			openPopUpAction('CreaFiltroEntita.do', href, 'popupFiltroEntita');
		} else {
			alert("Per definire una condizione di filtro e' necessario selezionare l'argomento principale");
		}
	}

-->
</script>
