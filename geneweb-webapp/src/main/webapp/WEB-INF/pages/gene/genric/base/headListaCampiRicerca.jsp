<%
/*
 * Created on 27-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE LA SEZIONE JAVASCRIPT DELLA PAGINA DI DETTAGLIO 
 // PER LA LISTA DI CAMPI IN UNA RICERCA
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />
<script type="text/javascript">
<!--
	function generaPopupListaOpzioniRecord(id) {
	<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
	<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
	<elda:jsVocePopup functionJS="spostaSu('\"+id+\"')" descrizione="Sposta su"/>
	<elda:jsVocePopup functionJS="spostaGiu('\"+id+\"')" descrizione="Sposta giù"/>
	<elda:jsVocePopup functionJS="spostaInPosizioneMarcata('\"+id+\"')" descrizione="Sposta in posizione marcata"/>
	</elda:jsBodyPopup>
		return linkset;
	}

	// Azioni invocate dal menu contestuale

	function apriTrovaRicerche(){
		<c:if test="${!empty (sessionScope.recordDettModificato)}">
		if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaETrovaRicerche();
		else
		</c:if>
		document.location.href='InitTrovaRicerche.do?'+csrfToken;
	}
	
	function salvaRicercaETrovaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salvaETrova&tab=CAM';
	}
	
	function listaRicerche(){
		<c:if test="${!empty (sessionScope.recordDettModificato)}">
		if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaEListaRicerche();
		else
		</c:if>
		document.location.href='TrovaRicerche.do?'+csrfToken+'&metodo=trovaRicerche';
	}
	
	function salvaRicercaEListaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salvaELista&tab=CAM';
	}
	
	function creaNuovaRicerca(){
		//<c:if test="${!empty (sessionScope.recordDettModificato)}">
		//if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaECreaRicerca();
		//else
		//</c:if>
		document.location.href='CreaNuovaRicerca.do?'+csrfToken;
	}
	
	function salvaRicercaECreaRicerca() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salvaECrea&tab=CAM';
	}
	
	// Azioni invocate dalle opzioni sul singolo record
	
	function modifica(id){
		inviaRichiesta(id, "modifica");
	}

	function elimina(id) {
		if(confirm("Procedere con l'eliminazione del record?")){
			inviaRichiesta(id, "elimina");
		}
	}
	
	function spostaSu(id){
		inviaRichiesta(id, "spostaSu");
	}

	function spostaGiu(id){
		inviaRichiesta(id, "spostaGiu");
	}

	function inviaRichiesta(id, metodo){
		bloccaRichiesteServer();
		document.location.href = 'ListaCampiRicercaBase.do?'+csrfToken+'&metodo=' + metodo + '&id=' + id;
	}

	function spostaInPosizioneMarcata(id){
		var numeroOggetti = contaCheckSelezionati(document.listaForm.id);
		if (numeroOggetti == 0)
			alert("Nessuna posizione selezionata nella lista");
		else if (numeroOggetti > 1)
			alert("Selezionare solo una posizione nella lista");
		else {
			// si individua la posizione dell'elemento selezionato e si esegue la chiamata
			var posizioneSelezionata = -1;
			var trovato = false;
			var indice = 0;
			while (posizioneSelezionata == -1 & !trovato & indice < document.listaForm.id.length) {
				if (document.listaForm.id[indice].checked) {
					trovato = true;
					posizioneSelezionata = indice;
				} else 
					indice++;
			}
			if (id == posizioneSelezionata)
				alert("Selezionare una posizione diversa dall'elemento da spostare");
			else {
				bloccaRichiesteServer();
				document.location.href = 'ListaCampiRicercaBase.do?'+csrfToken+'&metodo=spostaInPosizioneMarcata&id=' + id + '&idNew=' + posizioneSelezionata;
			}
		}
	}

	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab) {
		document.location.href = 'CambiaTabRicercaBase.do?'+csrfToken+'&tab=' + codiceTab;
	}

	// Azioni di pagina

	function aggiungiCampo(){
		document.location.href='AddCampoRicerca.do?'+csrfToken;
	}

	function aggiungiCampi(){
		document.location.href='AddElencoCampiRicercaBase.do?'+csrfToken;
	}
	
	function estraiCampiDistinti() {
		bloccaRichiesteServer();
		document.location.href = "ListaCampiRicercaBase.do?"+csrfToken+"&metodo=campiDistinti";
	}

	// azioni generali in sola visualizzazione

	function salvaRicerca(){
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salva&tab=CAM'
	}

	function ripristinaRicercaSalvata(){
		if (confirm('<fmt:message key="info.genRic.ripristinaRicerca"/>')){
			bloccaRichiesteServer();
		  document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=visualizza&idRicerca=${sessionScope.recordDettRicerca.testata.id}';
		}
	}
	
	function annullaCreazioneRicerca(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
		  document.location.href='InitTrovaRicerche.do?'+csrfToken;
		}
	}

	function eseguiRicerca(){
		bloccaRichiesteServer();
		<c:if test="${!empty (sessionScope.recordDettModificato)}">
		if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaEEseguiRicerca();
		else
		</c:if>
		document.location.href='ControllaDatiRicercaBase.do?'+csrfToken+'&tab=CAM'
	}
	
	function salvaRicercaEEseguiRicerca() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salvaEEsegui&tab=CAM';
	}
	
	function esportaRicerca(){
		document.location.href='EsportaRicerca.do?'+csrfToken;
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
-->
</script>