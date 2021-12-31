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

<!-- Azioni invocate dal tab menu -->
	
	function annulla() {
		bloccaRichiesteServer();
		document.location.href='ParametriProspetti.do?'+csrfToken+'&metodo=listaParametriModello&idRicerca=${idOggetto}';
	}
	
	function inviaRichiesta(id, progr, metodo){
		var url = 'ParametriModello.do?'+csrfToken+'&metodo=' + metodo + '&idModello=' + id;
		if (progr != null)
			url += '&progressivo=' + progr;
		document.location.href = url;
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
	
	function attivaMenu(){
		var tipoPar = String(document.parametroModelloForm.tipo.value);
		if(tipoPar == 'M'){
			document.getElementById("trMenu").style.display = '';
			if (document.getElementById("trTabellato"))
				document.getElementById("trTabellato").style.display = 'none';
				document.parametroModelloForm.tabellato.value = '';
		} else {
			if(tipoPar == 'T'){
				if (document.getElementById("trTabellato"))
					document.getElementById("trTabellato").style.display = '';
				document.getElementById("trMenu").style.display = 'none';
				document.parametroModelloForm.menu.value = '';
			} else {
				document.getElementById("trMenu").style.display = 'none';
				document.parametroModelloForm.menu.value = '';
				if (document.getElementById("trTabellato"))
					document.getElementById("trTabellato").style.display = 'none';
				document.parametroModelloForm.tabellato.value = '';
			}
		}
	}
-->
</script>