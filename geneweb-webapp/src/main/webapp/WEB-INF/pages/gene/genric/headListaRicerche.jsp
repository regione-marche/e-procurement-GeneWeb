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

	function generaPopupListaOpzioniRecord(id) {
	<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
	<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
	<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
	<elda:jsVocePopup functionJS="copia('\"+id+\"')" descrizione="Copia report"/>
	<elda:jsVocePopup functionJS="eseguiRicerca('\"+id+\"')" descrizione="Esegui report"/>
	<!--elda:jsVocePopup functionJS="esportaRicerca('\"+id+\"')" descrizione="Esporta report"/-->
	</elda:jsBodyPopup>
		return linkset;
	}
	
	function generaPopupListaOpzioniProspetto(id, famiglia) {
	<elda:jsBodyPopup varJS="linksetProspetto" contextPath="${pageContext.request.contextPath}">
	<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
	<elda:jsVocePopup functionJS="eliminaProspetto('\"+id+\"')" descrizione="Elimina"/>
	<elda:jsVocePopup functionJS="copiaProspetto('\"+id+\"')" descrizione="Copia report"/>
	<elda:jsVocePopup functionJS="eseguiProspetto('\"+id+\"')" descrizione="Esegui report"/>
	</elda:jsBodyPopup>
		return linksetProspetto;
	}

   function generaPopupListaOpzioniBase(id, famiglia) {
	<elda:jsBodyPopup varJS="linksetBase" contextPath="${pageContext.request.contextPath}">
	<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
	<elda:jsVocePopup functionJS="eliminaBase('\"+id+\"')" descrizione="Elimina"/>
	<elda:jsVocePopup functionJS="copia('\"+id+\"')" descrizione="Copia report"/>
	<elda:jsVocePopup functionJS="eseguiRicerca('\"+id+\"')" descrizione="Esegui report"/>
	</elda:jsBodyPopup>
		return linksetBase;
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
  
  function visualizza(id){
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=visualizza&idRicerca=' + id;
  }

  function elimina(id) {
  		if(confirm("Procedere con l'eliminazione del record?")){
  			bloccaRichiesteServer();
  			document.location.href='CreaEliminaRicerca.do?'+csrfToken+'&metodo=elimina&idRicerca=' + id;
  		}
  }

  function copia(id) {
  		if(confirm("Procedere con la creazione di una copia del record?")){
  			bloccaRichiesteServer();
  			document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=copia&idRicerca=' + id;
  		}
  }
  
  function eliminaBase(id) {
  		if(confirm("Procedere con l'eliminazione del record?")){
  			bloccaRichiesteServer();
  			document.location.href='CreaEliminaRicercaBase.do?'+csrfToken+'&metodo=elimina&idRicerca=' + id;
  		}
  }
  
  function eseguiRicerca(id){
  	bloccaRichiesteServer();
  	document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=caricaEEsegui&idRicerca=' + id +'&fromPage=listaRicerche';
  }

  function eseguiProspetto(id){
  	bloccaRichiesteServer();
		document.location.href='CheckParametriProspetto.do?'+csrfToken+'&idRicerca=' + id + '&fromPage=listaRicerche';
  }

  function copiaProspetto(id) {
  		if(confirm("Procedere con la creazione di una copia del record?")){
  			bloccaRichiesteServer();
  			document.location.href='CopiaProspetto.do?'+csrfToken+'&id=' + id;
  		}
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

	function eliminaProspetto(idProspetto){
		if(confirm("Procedere con l'eliminazione del record?")){
			bloccaRichiesteServer();
 			document.location.href='CreaEliminaRicercaProspetto.do?'+csrfToken+'&metodo=elimina&idRicerca=' + idProspetto;
  	}
	}
	
-->
</script>