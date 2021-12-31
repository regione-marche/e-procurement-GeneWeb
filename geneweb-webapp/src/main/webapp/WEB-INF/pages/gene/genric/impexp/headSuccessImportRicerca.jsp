<%
/*
 * Created on 15-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI SUCCESSO 
 // DELL'IMPORT DI UN REPORT CONTENENTE LA SEZIONE JAVASCRIPT
 // (USATA SOLO IN CONFIGURAZIONE CHIUSA)
%>

<script type="text/javascript">
<!-- 

	function avanti(){
		var href = "";
		if(document.getElementById("importa").checked){
			href = 'WizardImportRicerca.do?'+csrfToken+'&pageTo=UPL';
		}
		if(document.getElementById("annullaImporta").checked){
			href = 'AnnullaImportExport.do?'+csrfToken;
		}
		if(href != ""){
			document.location.href = href;
		} else {
			alert('Selezionare una delle opzioni per continuare');
		}
	}
	
-->
</script>