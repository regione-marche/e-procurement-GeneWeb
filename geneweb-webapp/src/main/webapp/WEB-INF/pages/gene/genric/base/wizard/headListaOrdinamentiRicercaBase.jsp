<%
/*
 * Created on 04-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA ORDINAMENTI
 // DI UNA RICERCA BASE(NEL WIZARD PER LA CREAZIONE DI UNA RICERCA) CONTENENTE 
 // LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />
<script type="text/javascript">
<!--

	function avanti(){
		var href = "";
		var confermaSi = document.getElementById("SiConfermaListaOrdinamenti");
		var confermaNo = document.getElementById("NoConfermaListaOrdinamenti");

		if(confermaSi.checked){
			href = 'WizardBase.do?'+csrfToken+'&pageTo=LAY';
			//href = 'ListaLayout.do';
		}
		if(confermaNo.checked){
			href = 'OrdinamentiBase.do?'+csrfToken+'&metodo=annullaListaOrdinamenti';
		}
		
		if(href != ""){
			bloccaRichiesteServer();
			document.location.href = href;
		} else {
			alert('Selezionare una delle due opzioni');
		}
	}

	function indietro(){
		document.location.href = 'WizardBase.do?'+csrfToken+'&pageTo=ORD1';
	}

	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
			document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}
	
	function fineWizard(){
		var href = "";
		var confermaSi = document.getElementById("SiConfermaListaOrdinamenti");
		var confermaNo = document.getElementById("NoConfermaListaOrdinamenti");
		
		if(confermaSi.checked){
			href = 'WizardBase.do?'+csrfToken+'&pageTo=DG&pageFrom=ORD3';
		}
		if(confermaNo.checked){
			href = 'OrdinamentiBase.do?'+csrfToken+'&metodo=annullaListaOrdinamenti&pageFrom=ORD2';
		}
		if(href != ""){
			bloccaRichiesteServer();
			document.location.href = href;
		}else
			alert('Selezionare una delle due opzioni');
	}

	function elimina(id) {
		if(confirm("Procedere con l'eliminazione del record?")){
		  document.location.href = 'OrdinamentiBase.do?'+csrfToken+'&metodo=elimina&id=' + id;
		}
	}

	
<% /*
	Il seguente codice e' stato commentato per possibili evoluzioni al wizard di
	creazione di una ricerca base: potrebbe essere che siano attivate le funzioni
	di modifica, sposta su, giu' e in posizione marcata.

	function generaPopupListaOpzioniRecord(id) {
	<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
	<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
	<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
	<elda:jsVocePopup functionJS="spostaSu('\"+id+\"')" descrizione="Sposta su"/>
	<elda:jsVocePopup functionJS="spostaGiu('\"+id+\"')" descrizione="Sposta giù"/>
	<elda:jsVocePopup functionJS="spostaInPosizioneMarcata('\"+id+\"')" descrizione="Sposta in posizione marcata"/>
	</elda:jsBodyPopup>
		return linkset;
	}
	
	// Azioni di pagina
	
	function modifica(id){
		var metodo = "modifica";
		document.location.href = 'ListaOrdinamentiRicercaBase.do?'+csrfToken+'&metodo=' + metodo + '&id=' + id;
	}

	function inviaRichiesta(id, metodo){
		bloccaRichiesteServer();
		document.location.href = 'ListaOrdinamentiRicercaBase.do?'+csrfToken+'&metodo=' + metodo + '&id=' + id;
	}

	function spostaInPosizioneMarcata(id){
		var numeroOggetti = contaCheckSelezionati(document.listaOrdinamenti.progressivo);
		if (numeroOggetti == 0)
			alert("Nessuna posizione selezionata nella lista");
		else if (numeroOggetti > 1)
			alert("Selezionare solo una posizione nella lista");
		else {
			// si individua la posizione dell'elemento selezionato e si esegue la chiamata
			var posizioneSelezionata = -1;
			var trovato = false;
			var indice = 0;
			while (posizioneSelezionata == -1 & !trovato & indice < document.listaOrdinamenti.progressivo.length) {
				if (document.listaOrdinamenti.progressivo[indice].checked) {
					trovato = true;
					posizioneSelezionata = indice;
				} else 
					indice++;
			}
			if (id == posizioneSelezionata)
				alert("Selezionare una posizione diversa dall'elemento da spostare");
			else {
				bloccaRichiesteServer();
				document.location.href = 'ListaOrdinamentiRicercaBase.do?'+csrfToken+'&metodo=spostaInPosizioneMarcata&id=' + id + '&idNew=' + posizioneSelezionata;
			}
		}
	}
*/
	%>
-->
</script>