<%
/*
 * Created on 29-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA FILTRI
 // DI UNA RICERCA BASE(IN FASE DI VISUALIZZAZIONE) CONTENENTE LA SEZIONE JAVASCRIPT
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
	</elda:jsBodyPopup>
		return linkset;
	}
	
	// Azioni invocate dal menu contestuale

	function apriTrovaRicerche(){
		document.location.href='InitTrovaRicerche.do';
	}
	
	function estraiCampiDistinti() {
		bloccaRichiesteServer();
		document.location.href = "ListaCampiRicercaBase.do?metodo=campiDistinti";
	}
	
	function salvaRicercaETrovaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaETrova&tab=FIL';
	}
	
	function filtroSuIdUtente() {
		bloccaRichiesteServer();
		document.location.href = "ListaFiltriRicercaBase.do?metodo=filtroPerIdUtente";
	}
	
	function filtroPerUfficioIntestatario() {
		bloccaRichiesteServer();
		document.location.href = "ListaFiltriRicercaBase.do?metodo=filtroPerUfficioIntestatario";
	}
	
	function listaRicerche(){
		document.location.href='TrovaRicerche.do?metodo=trovaRicerche';
	}
	
	function salvaRicercaEListaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaELista&tab=FIL';
	}
	
	function creaNuovaRicerca(){
		document.location.href='CreaNuovaRicerca.do';
	}
	
	function salvaRicercaECreaRicerca() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaECrea&tab=FIL';
	}

	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab) {
		document.location.href = 'CambiaTabRicercaBase.do?tab=' + codiceTab;
	}

	// Azioni di pagina
	
	function modifica(id){
		var metodo = "modifica";
		document.location.href = 'ListaFiltriRicercaBase.do?metodo=' + metodo + '&id=' + id;
	}

	function elimina(id) {
		if(confirm("Procedere con l'eliminazione del record?")){
			inviaRichiesta(id, "elimina");
		}
	}

	function inviaRichiesta(id, metodo){
		bloccaRichiesteServer();
		document.location.href = 'ListaFiltriRicercaBase.do?metodo=' + metodo + '&id=' + id;
	}

	// Azioni di pagina
		
	function aggiungiFiltro(){
		document.location.href='AddFiltroRicercaBase.do';
	}
	
	// azioni generali in sola visualizzazione

	function salvaRicerca(){
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salva&tab=FIL'
	}
	
	function ripristinaRicercaSalvata(){
		if (confirm('<fmt:message key="info.genRic.ripristinaRicerca"/>')){
			bloccaRichiesteServer();
		  document.location.href='DettaglioRicerca.do?metodo=visualizza&idRicerca=${sessionScope.recordDettRicerca.testata.id}';
		}
	}
	
	function annullaCreazioneRicerca(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
		  document.location.href='InitTrovaRicerche.do';
		}
	}

	function eseguiRicerca(){
		bloccaRichiesteServer();
		document.location.href='ControllaDatiRicercaBase.do?tab=FIL'
	}
	
	function salvaRicercaEEseguiRicerca() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaEEsegui&tab=FIL';
	}
	
	function esportaRicerca(){
		document.location.href='EsportaRicerca.do';
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