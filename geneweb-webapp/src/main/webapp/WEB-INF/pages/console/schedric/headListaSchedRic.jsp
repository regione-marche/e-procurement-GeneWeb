<%
/*
 * Created on 04-ago-2006
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

	
   function generaPopupListaOpzioniAttiva(id, famiglia) {
	<elda:jsBodyPopup varJS="linksetBase" contextPath="${pageContext.request.contextPath}">
	<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
	<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica"/>
	<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
	<elda:jsVocePopup functionJS="attiva('\"+id+\"')" descrizione="Attiva"/>
	</elda:jsBodyPopup>
		return linksetBase;
	}

		
   function generaPopupListaOpzioniDisattiva(id, famiglia) {
	<elda:jsBodyPopup varJS="linksetBase" contextPath="${pageContext.request.contextPath}">
	<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
	<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica"/>
	<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
	<elda:jsVocePopup functionJS="disattiva('\"+id+\"')" descrizione="Disattiva"/>
	</elda:jsBodyPopup>
		return linksetBase;
	}
	
  function apriTrovaRicerche(){
		document.location.href='InitTrovaSchedRic.do?'+csrfToken;
  }

  function creaNuovaSchedRic(){
		document.location.href='WizardSchedRic.do?'+csrfToken+'&pageTo=REP';
  }
  
  
  function visualizza(id){
		document.location.href='DettaglioSchedRic.do?'+csrfToken+'&metodo=visualizzaDettaglio&idSchedRic=' + id;
  }

  function elimina(id) {
  		if(confirm("Procedere con l'eliminazione del record?")){
  			bloccaRichiesteServer();
  			document.location.href='DettaglioSchedRic.do?'+csrfToken+'&metodo=elimina&idSchedRic=' + id;
  		}
  }

  function modifica(id){
		document.location.href='DettaglioSchedRic.do?'+csrfToken+'&metodo=modificaDettaglio&idSchedRic=' + id;
  }
  
  
  function gestisciSubmit(azione){
	    var numeroOggetti = contaCheckSelezionati(document.listaForm.id);
	    if (numeroOggetti == 0) {
	      alert("Nessun elemento selezionato nella lista");
	    } else {
	   	  if (azione=='elimina')
	        if (confirm("Sono stati selezionati " + numeroOggetti + " record. Procedere con l'eliminazione?")) {
	        	bloccaRichiesteServer();
		        document.listaForm.submit();
	   	    }
	    }
	}

	function attiva(id){
		document.location.href='DettaglioSchedRic.do?'+csrfToken+'&metodo=attiva&idSchedRic=' + id;
  	}
	
	function disattiva(id){
		document.location.href='DettaglioSchedRic.do?'+csrfToken+'&metodo=disattiva&idSchedRic=' + id;
  	}
-->
</script>