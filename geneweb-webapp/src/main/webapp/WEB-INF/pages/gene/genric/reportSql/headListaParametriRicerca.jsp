<%
/*
 * Created on 19-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA PARAMETRI
 // DI UNA RICERCA (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<script type="text/javascript">
<!--

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
	
	// Azioni invocate dal menu contestuale

	function apriTrovaRicerche(){
		//<c:if test="${!empty (sessionScope.recordDettModificato)}">
		//if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaETrovaRicerche();
		//else
		//</c:if>
		document.location.href='InitTrovaRicerche.do?'+csrfToken;
	}
	
	function salvaRicercaETrovaRicerche() {
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salvaETrova&tab=PAR';
	}
	
	function listaRicerche(){
		//<c:if test="${!empty (sessionScope.recordDettModificato)}">
		//if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaEListaRicerche();
		//else
		//</c:if>
		document.location.href='TrovaRicerche.do?'+csrfToken+'&metodo=trovaRicerche';
	}
	
	function salvaRicercaEListaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salvaELista&tab=PAR';
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
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salvaECrea&tab=PAR';
	}

	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab) {
		document.location.href = 'CambiaTabRicercaSql.do?'+csrfToken+'&tab=' + codiceTab;
	}

	// Azioni di pagina
	
	function modifica(id){
	  bloccaRichiesteServer();
		document.location.href = 'EditParametroRicercaSql.do?'+csrfToken+'&id=' + id;
	}

	function elimina(id) {
		if(confirm("L'eliminazione del record comporta la cancellazione dei filtri che fanno uso del parametro stesso.\nProcedere con l'eliminazione del record?")){
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
		document.location.href = 'ListaParametriRicercaSql.do?'+csrfToken+'&metodo=' + metodo + '&id=' + id;
	}

	function spostaInPosizioneMarcata(id){
		var numeroOggetti = contaCheckSelezionati(document.listaParametri.progressivo);
		if (numeroOggetti == 0)
			alert("Nessuna posizione selezionata nella lista");
		else if (numeroOggetti > 1)
			alert("Selezionare solo una posizione nella lista");
		else {
			// si individua la posizione dell'elemento selezionato e si esegue la chiamata
			var posizioneSelezionata = -1;
			var trovato = false;
			var indice = 0;
			while (posizioneSelezionata == -1 & !trovato & indice < document.listaParametri.progressivo.length) {
				if (document.listaParametri.progressivo[indice].checked) {
					trovato = true;
					posizioneSelezionata = indice;
				} else 
					indice++;
			}
			if (id == posizioneSelezionata)
				alert("Selezionare una posizione diversa dall'elemento da spostare");
			else {
				bloccaRichiesteServer();
				document.location.href = 'ListaParametriRicercaSql.do?'+csrfToken+'&metodo=spostaInPosizioneMarcata&id=' + id + '&idNew=' + posizioneSelezionata;
			}
		}
	}

	// Azioni di pagina
	
	// azioni generali in sola visualizzazione

	function salvaRicerca(){
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salva&tab=PAR'
	}
	
	function ripristinaRicercaSalvata(){
		if (confirm('<fmt:message key="info.genRic.ripristinaRicerca"/>')){
			bloccaRichiesteServer();
		  document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=visualizza&idRicerca=${sessionScope.recordDettRicerca.testata.id}';
		}
	}
	
	function annullaCreazioneRicerca(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')) {
			bloccaRichiesteServer();
		  document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}

	function eseguiRicerca(){
		bloccaRichiesteServer();
		document.location.href='VerificaParametriRicerca.do?'+csrfToken+'&tab=PAR'
	}
	
	function salvaRicercaEEseguiRicerca() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salvaEEsegui&tab=PAR';
	}

	function esportaRicerca(){
		document.location.href='EsportaRicerca.do?'+csrfToken;
	}
	
	function addParametro(id){
	  bloccaRichiesteServer();
		document.location.href = 'EditParametroRicercaSql.do?'+csrfToken;
	}
	
-->
</script>