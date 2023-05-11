<%
/*
 * Created on 30-giu-2006
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
<script type="text/javascript"
  src="${contextPath}/js/forms.js"></script>

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

	function creaModello(){
		document.location.href='Modello.do?'+csrfToken+'&metodo=creaModello';
	}

	function ricercaModelli(){
		document.location.href='InitTrovaModelli.do?'+csrfToken;
	}
	
	function listaModelli(){
		document.location.href='TrovaModelli.do?'+csrfToken+'&metodo=trovaModelli';
	}

<!-- Azioni invocate dal tab menu -->

	function dettaglioModello(id){
		document.location.href='Modello.do?'+csrfToken+'&metodo=dettaglioModello&idModello='+id;
	}

	function listaGruppiModello(id){
		document.location.href='GruppiModello.do?'+csrfToken+'&metodo=listaGruppiModello&idModello='+id;
	}
	
	function listaParametriModello(id){
		inviaRichiesta(id, null, "listaParametriModello");
	}
	
	function creaParametro(id){
		var metodo = "creaParametroModello";
		var url = 'ParametriModello.do?'+csrfToken+'&metodo=' + metodo + '&idModello=' + id;
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
				document.location.href = 'ParametriModello.do?'+csrfToken+'&metodo=spostaInPosizioneMarcata&idModello=${idModello}&progressivo=' + id + '&progressivoNew=' + posizioneSelezionata;
			}
		}
	}
	
	function inviaRichiesta(id, progr, metodo){
		var url = 'ParametriModello.do?'+csrfToken+'&metodo=' + metodo + '&idModello=' + id;
		if (progr != null)
			url += '&progressivo=' + progr;
		bloccaRichiesteServer();
		document.location.href = url;
	}
	
	function attivaMenu(){
		var tipoPar = String(document.parametroModelloForm.tipo.value);
		if(tipoPar == 'M'){
			document.getElementById("trMenu").style.display = '';
			document.getElementById("trTabellato").style.display = 'none';
				document.parametroModelloForm.tabellato.value = '';
		} else {
			if(tipoPar == 'T'){
				document.getElementById("trTabellato").style.display = '';
				document.getElementById("trMenu").style.display = 'none';
				document.parametroModelloForm.menu.value = '';
			} else {
				document.getElementById("trMenu").style.display = 'none';
				document.parametroModelloForm.menu.value = '';
				document.getElementById("trTabellato").style.display = 'none';
				document.parametroModelloForm.tabellato.value = '';
			}
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
		if (esito && document.parametroModelloForm.tipo.value == 'T' && 
		  !controllaCampoInputObbligatorio(document.parametroModelloForm.tabellato, 'Tabellato')){
		  esito = false;
		}
		if (esito){
			bloccaRichiesteServer();
			document.parametroModelloForm.submit();
		}
	}
	
-->
</script>