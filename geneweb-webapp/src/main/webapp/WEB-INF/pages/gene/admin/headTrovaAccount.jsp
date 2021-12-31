<%
/*
 * Created on 30-mar-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA TROVA ACCOUNT 
 // CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<script type="text/javascript">

	function avviaAccountRic(){
		document.trovaAccountForm.metodo.value="trovaAccount";
		document.trovaAccountForm.submit();
	}
	
	function nuovaRicerca(){
		document.trovaAccountForm.metodo.value="nuovaRicerca";
		document.trovaAccountForm.submit();
	}

	function creaNuovoAccount(){
		document.location.href='InitCreaAccount.do?'+csrfToken;
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

