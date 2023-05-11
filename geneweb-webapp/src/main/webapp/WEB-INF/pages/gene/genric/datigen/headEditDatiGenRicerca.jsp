<%
/*
 * Created on 28-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
 // DATI GENERALI DI UNA RICERCA (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA 
 // SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>


<script type="text/javascript">
<!-- 

	// Azioni invocate dal menu contestuale

	function apriTrovaRicerche(){
		document.location.href='InitTrovaRicerche.do?'+csrfToken;
	}
	
	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab) {
		document.location.href = 'CambiaTabRicerca.do?'+csrfToken+'&tab=' + codiceTab;
	}

	// Azioni di pagina

	function gestisciSubmit() {
	  var esito = true;
	  if (esito && !controllaCampoInputObbligatorio(testataRicercaForm.nome, 'Titolo')){
		  esito = false;
		}
	  if (esito && !controllaCampoInputNoCarSpeciali(testataRicercaForm.codReportWS, 'Pubblica il report come servizio con codice')){
		  esito = false;
		}
		if (esito){
			bloccaRichiesteServer();
			document.testataRicercaForm.submit();
		}
	}
	
	function annullaModifiche(){
		bloccaRichiesteServer();
		document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
	}

-->
</script>