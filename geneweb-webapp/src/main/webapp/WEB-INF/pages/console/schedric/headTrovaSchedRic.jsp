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


<script type="text/javascript">
<!--

	function avviaRicercaRic(){
		document.trovaSchedRicForm.metodo.value="trovaSchedRic";
		document.trovaSchedRicForm.submit();
	}
	
	function nuovaRicerca(){
		document.trovaSchedRicForm.metodo.value="nuovaRicerca";
		document.trovaSchedRicForm.submit();
	}

	function resetRicerca(){
		document.trovaSchedRicForm.reset();
	}
	
  function apriTrovaRicerche(){
		document.location.href='InitTrovaSchedRic.do?'+csrfToken;
  }

	function creaNuovaSchedRic(){
		document.location.href='WizardSchedRic.do?'+csrfToken+'&pageTo=REP';
	}
	
	function gestisciVisualizzazioneAvanzata() {
		var checkboxavanzate = document.getElementById("visualizzazioneAvanzata");
		var test = checkboxavanzate.checked;
		if (test) {
			trovaVisualizzazioneOperatori('visualizza');
		} else {
			nuovaRicerca();
		}
	}
-->
</script>

