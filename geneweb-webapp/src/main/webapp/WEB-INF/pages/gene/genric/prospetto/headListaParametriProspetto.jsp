<%
/*
 * Created on 16-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
 // GRUPPO (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>


<script type="text/javascript">
<!--

	function generaPopupListaOpzioniRecord(id, progr) {
	<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
	<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
	<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
	<elda:jsVocePopup functionJS="spostaSu('\"+id+\"')" descrizione="Sposta su"/>
	<elda:jsVocePopup functionJS="spostaGiu('\"+id+\"')" descrizione="Sposta giù"/>
	<elda:jsVocePopup functionJS="spostaInPosizioneMarcata('\"+id+\"')" descrizione="Sposta in posizione marcata"/>
	</elda:jsBodyPopup>
		return linkset;
	}

	function creaNuovaRicerca(){
		document.location.href='CreaNuovaRicerca.do?'+csrfToken;
	}

<!-- Azioni invocate dal tab menu -->

	function datiGenProspetto(idProspetto) {
		document.location.href='DettaglioProspetto.do?'+csrfToken+'&metodo=visualizza&idRicerca='+idProspetto;
	}

	function listaGruppiProspetto(idProspetto) {
		document.location.href='ListaGruppiProspetto.do?'+csrfToken+'&metodo=visualizzaLista&idRicerca='+idProspetto;
	}

	function listaParametriProspetto(idProspetto) {
		document.location.href='ParametriProspetti.do?'+csrfToken+'&metodo=listaParametriModello&idRicerca='+idProspetto;
	}

	
	function creaParametro(id){
		var metodo = "creaParametroModello";
		var url = 'ParametriProspetti.do?'+csrfToken+'&metodo=' + metodo + '&idModello=' + id;
		document.location.href = url;
	}
	
	function modifica(id) {
		var metodo = "modificaParametroModello";
		var url = 'ParametriProspetti.do?'+csrfToken+'&metodo=' + metodo + '&idModello=${idModello}' + '&progressivo=' + id;
		document.location.href = url;
	}


	function modifica(progr) {
		inviaRichiesta(${idModello}, progr, "modificaParametroModello");
	}

	function elimina(progr) {
		if (confirm("Procedere con l'eliminazione del record?")){
			inviaRichiesta(${idModello}, progr, "eliminaParametroModello");
		}
	}

	function spostaSu(progr){
		inviaRichiesta(${idModello}, progr, "spostaSu");
	}

	function spostaGiu(progr){
		inviaRichiesta(${idModello}, progr, "spostaGiu");
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
				document.location.href = 'ParametriProspetti.do?'+csrfToken+'&metodo=spostaInPosizioneMarcata&idModello=${idModello}&progressivo=' + id + '&progressivoNew=' + posizioneSelezionata;
			}
		}
	}
	
	function inviaRichiesta(id, progr, metodo){
		var url = 'ParametriProspetti.do?'+csrfToken+'&metodo=' + metodo + '&idModello=' + id;
		if (progr != null)
			url += '&progressivo=' + progr;
		bloccaRichiesteServer();
		document.location.href = url;
	}
	
	function attivaMenu(){
		var tipoPar = String(document.parametroModelloForm.tipo.value);
		if(tipoPar == 'M'){
			document.getElementById("trMenu").style.display = '';
		} else {
			document.getElementById("trMenu").style.display = 'none';
			document.parametroModelloForm.menu.value = '';
		}
	}
	
	function salvaParametro() {
		var esito = true;
		if (esito && !controllaCampoInputObbligatorio(document.parametroModelloForm.codice, 'Codice')){
		  esito = false;
		}
		if (esito && !controllaCampoInputNoCarSpeciali(document.parametroModelloForm.codice, 'Codice')) {
		  esito = false;
		}		
		if (esito && !controllaCampoInputObbligatorio(document.parametroModelloForm.nome, 'Descrizione per inserimento')){
		  esito = false;
		}
		if (esito && document.parametroModelloForm.tipo.value == 'M' && 
		  !controllaCampoInputObbligatorio(document.parametroModelloForm.menu, 'Menu')){
		  esito = false;
		}
		if (esito){
			bloccaRichiesteServer();
			document.parametroModelloForm.submit();
		}
	}
	
	function eseguiRicerca(){
		bloccaRichiesteServer();
		document.location.href='CheckParametriProspetto.do?'+csrfToken+'&idRicerca=${idOggetto}&fromPage=dettaglioRicerca';
	}

-->
</script>