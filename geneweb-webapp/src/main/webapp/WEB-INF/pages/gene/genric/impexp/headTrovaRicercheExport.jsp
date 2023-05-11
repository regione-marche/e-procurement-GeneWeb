<%
/*
 * Created on 03-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA TROVA RICERCA 
 // CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />

<script type="text/javascript">
<!--

	function avviaRicercaRic(){
		document.trovaRicercheExportForm.metodo.value="trovaRicerche";
		document.trovaRicercheExportForm.submit();
	}
	
	function nuovaRicerca(){
		document.trovaRicercheExportForm.metodo.value="nuovaRicerca";
		document.trovaRicercheExportForm.submit();
	}

	function resetRicerca(){
		document.trovaRicercheExportForm.reset();
	}
	
  function apriTrovaRicerche(){
		document.location.href='InitTrovaRicercheExport.do?'+csrfToken;
  }

	function annulla(){
		if(confirm('<fmt:message key="info.genRic.annullaExport"/>')){
		  document.location.href='AnnullaImportExport.do?'+csrfToken;
		}		
	}

	function indietro(){
		document.location.href='InitFunzAvanzate.do?'+csrfToken;
	}

	function gestisciVisualizzazioneAvanzata() {
		var checkboxavanzate = document.getElementById("visualizzazioneAvanzata");
		var test = checkboxavanzate.checked;
		if (test == true) {
			trovaVisualizzazioneOperatori('visualizza');
		} else {
			nuovaRicerca();
		}
	}
	
	
-->
</script>