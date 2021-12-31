<%
/*
 * Created on 03-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA FILTRI
 // DI UNA RICERCA BASE(NEL WIZARD PER LA CREAZIONE DI UNA RICERCA) CONTENENTE 
 // LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />
<c:set var="filtroRicercaForm" value="${filtroRicercaForm}" />

<script type="text/javascript">
<!--

	function avanti(){
		var href = "";
		var confermaSi = document.getElementById("SiConfermaListaFiltri");
		var confermaNo = document.getElementById("NoConfermaListaFiltri");

		if(confermaSi.checked){
			href = 'WizardBase.do?'+csrfToken+'&pageTo=ORD1';
		}
		if(confermaNo.checked){
			href = 'FiltriBase.do?'+csrfToken+'&metodo=annullaListaFiltri';
		}
		
		if(href != ""){
			bloccaRichiesteServer();
			document.location.href = href;
		} else {
			alert('Selezionare una delle due opzioni');
		}
	}

	function indietro(){
	<c:choose>
		<c:when test='${fn:length(filtroRicercaForm.progressivo) > 0}'>
			document.location.href = 'WizardBase.do?'+csrfToken+'&pageTo=FIL3';
		</c:when>
		<c:otherwise>
			document.location.href = 'WizardBase.do?'+csrfToken+'&pageTo=FIL1';
		</c:otherwise>
	</c:choose>
	}

	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
			document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}
	
	function fineWizard(){
		var href = "";
		var confermaSi = document.getElementById("SiConfermaListaFiltri");
		var confermaNo = document.getElementById("NoConfermaListaFiltri");
		
		if(confermaSi.checked){
			href = 'WizardBase.do?'+csrfToken+'&pageTo=DG&pageFrom=FIL3';
		}
		if(confermaNo.checked){
			href = 'FiltriBase.do?'+csrfToken+'&metodo=annullaListaFiltri&pageFrom=FIL3';
		}
		if(href != ""){
			bloccaRichiesteServer();
			document.location.href = href;
		} else
			alert('Selezionare una delle due opzioni');
	}
	
	function eliminaFiltro(idFiltro){
		if(confirm("Procedere con l'eliminazione del record?")){
			bloccaRichiesteServer();
			document.location.href = 'FiltriBase.do?'+csrfToken+'&metodo=elimina&id=' + idFiltro;
		}
	}

-->
</script>