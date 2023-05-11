<%/*
   * Created on 02-mag-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE FUNZIONI 
  // JAVASCRIPT DELLA PAGINA DI DEFINIZIONE DEI DATI GENERALI DI UNA RICERCA BASE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="contenitore" value="${sessionScope.recordDettRicerca}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">
<!-- 

	// Azioni di pagina

	function avanti(){
		var esito = true;
		if(!controllaCampoInputObbligatorio(document.testataRicercaForm.nome, 'Titolo')){
			esito = false;
		}
		if(esito){
			bloccaRichiesteServer();
			document.testataRicercaForm.submit();
		}
	}
	
<% /*
	La funzione indietro riporta alla pagina precedente: tuttavia l'implementazione
	di questa funzionalita' richiede la conoscenza la pagina di provenienza. Per questo
	si cerca fra i parameter prima e fra gli attributi poi una un oggetto indentificato
	con la stringa 'pageFrom'. Tale oggetto deve essere valorizzato con uno dei codici
	di seguito definiti e associati a ciascuna pagina del wizard:
	  - ARG : pagina per la definizione della tabella dai cui estrarre i campi  -->  argomentoRicercaBaseDef
	  - CAM : pagina per la definizione della lista dei campi da estrarre  -->  campiRicercaBaseDef
	  - FIL1: pagina con la domanda 'Vuoi definire alcuni filtri?'  -->  domandaFiltroRicercaBaseDef
	  - FIL2: pagina di definizione di una condizione di filtro  -->  editFiltroRicercaBaseDef
	  - FIL3: pagina di riepilogo dei filtri definiti  -->  riepilogoFiltriRicercaBaseDef
	  - ORD1: pagina con la domanda 'Vuoi definire alcuni ordinamenti?'  --> domandaOrdinamentoRicercaBaseDef
	  - ORD2: pagina di definizione di un criterio di filtro  -->  editOrdinamentoRicercaDef
	  - ORD3: pagina di riepilogo degli ordinamenti definiti  -->  riepilogoOrdinamentiRicercaBaseDef
	  - LAY : pagina per la definizione dei titoli delle colonne da estrarre  -->  layoutRicercaBaseDef
	  - PUB : pagina con la domanda: 'Vuoi pubblicare la ricerca?'  -->  pubblicaRicecaBaseDef
	  - GRP : pagina per associare la ricerca ai gruppi  -->  editGruppiRicercaBaseDef
	  - DG  : pagina dei dati generali della ricerca  --> datiGeneraliRicercaBaseDef
*/ %>

	function indietro(){
<c:choose>
	<c:when test="${fn:length(contenitore.elencoFiltri) > 0}">
		var numeroFiltri = ${fn:length(contenitore.elencoFiltri)};
	</c:when>
	<c:otherwise>
		var numeroFiltri = 0;
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${fn:length(contenitore.elencoOrdinamenti) > 0}">
		var numeroOrdinamenti = ${fn:length(contenitore.elencoOrdinamenti)};
	</c:when>
	<c:otherwise>
		var numeroOrdinamenti = 0;
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${!empty param.pageFrom}">
	  var pageFrom = "${param.pageFrom}";
	</c:when>
	<c:otherwise>
	  var pageFrom = "${pageFrom}";
	</c:otherwise>
</c:choose>
	  var href = "";
	  if(pageFrom == "")
	  	href = 'WizardBase.do?pageTo=GRP';
	  else if(pageFrom == "PUB")
	  	href = 'WizardBase.do?pageTo=PUB';
 	  else if(pageFrom == "LAY")
 	  	href = 'WizardBase.do?pageTo=LAY';
 	  else if(pageFrom == "ORD3"){
 	  	if(numeroOrdinamenti > 0)
 	  		href = 'WizardBase.do?pageTo=ORD3';
 	  	else
	 	  	href = 'WizardBase.do?pageTo=ORD1';
 	  } else if(pageFrom == "ORD1")
 	  	href = 'WizardBase.do?pageTo=ORD1';
 	  else if(pageFrom == "FIL3"){
 	  	if(numeroFiltri > 0)
	 	  	href = 'WizardBase.do?pageTo=FIL3';
 	  	else
 	  		href = 'WizardBase.do?pageTo=FIL1';
 	  } else if(pageFrom == "FIL1")
 	  	href = 'WizardBase.do?pageTo=FIL1';
 	  else if(pageFrom == "CAM")
 	  	href = 'WizardBase.do?pageTo=CAM';
 	  else if(pageFrom == "ARG")
			href = 'WizardBase.do?'+csrfToken+'&pageTo=ARG';

		if(href != "")
			document.location.href = href;
	}

	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
			document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}

-->
</script>