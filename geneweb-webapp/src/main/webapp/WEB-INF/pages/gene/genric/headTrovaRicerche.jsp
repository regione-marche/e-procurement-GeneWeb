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
		document.trovaRicercheForm.metodo.value="trovaRicerche";
		document.trovaRicercheForm.submit();
	}
	
	function nuovaRicerca(){
		document.trovaRicercheForm.metodo.value="nuovaRicerca";
		document.trovaRicercheForm.submit();
	}

	function resetRicerca(){
		document.trovaRicercheForm.reset();
	}
	
  function apriTrovaRicerche(){
		document.location.href='InitTrovaRicerche.do?'+csrfToken;
  }

	function creaNuovaRicerca(){
		document.location.href='CreaNuovaRicerca.do?'+csrfToken;
	}
	
	function creaNuovaRicercaWizard(){
		document.location.href='CreaNuovaRicercaWizard.do?'+csrfToken;
  }
  
  function importaRicerca(){
		document.location.href='WizardImportRicerca.do?'+csrfToken+'&pageTo=UPL';
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

