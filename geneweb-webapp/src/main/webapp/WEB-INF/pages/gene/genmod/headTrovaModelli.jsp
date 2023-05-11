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
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA TROVA MODELLO 
 // CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<script type="text/javascript">

	function avviaModelloRic(){
		document.trovaModelliForm.metodo.value="trovaModelli";
		document.trovaModelliForm.submit();
	}
	
	function nuovaRicerca(){
		document.trovaModelliForm.metodo.value="nuovaRicerca";
		document.trovaModelliForm.submit();
	}

	function resetRicerca(){
		document.trovaModelliForm.reset();
	}
	
  function apriTrovaModelli(){
		document.location.href='InitTrovaModelli.do?'+csrfToken;
  }

  function creaNuovoModello(){
		document.location.href='Modello.do?'+csrfToken+'&metodo=creaModello';
  }
  
  function resetRicerca(){
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
  
	
</script>

