<%/*
       * Created on 10-mar-2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
      // DATI GENERALI DI UNA RICERCA CON PROSPETTO (IN FASE DI VISUALIZZAZIONE)
      // CONTENENTE LA  SEZIONE JAVASCRIPT
    %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />
<script type="text/javascript">
<!--

	// Azioni invocate dal menu contestuale

	function apriTrovaRicerche(){
		//<c:if test="${!empty (sessionScope.recordDettModificato)}">
		//if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaETrovaRicerche();
		//else
		//</c:if>
		document.location.href='InitTrovaRicerche.do?'+csrfToken;
	}
	
	function salvaRicercaETrovaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salvaETrova&tab=DG';
	}
	
	function salvaRicercaEListaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=salvaELista&tab=DG';
	}
	
	function creaNuovaRicerca(){
		document.location.href='CreaNuovaRicerca.do?'+csrfToken;
	}

	// Azioni invocate dal tab menu
	function datiGenProspetto(idProspetto) {
		document.location.href='DettaglioProspetto.do?'+csrfToken+'&metodo=visualizza&idRicerca='+idProspetto;
	}

	function listaGruppiProspetto(idProspetto) {
		document.location.href='ListaGruppiProspetto.do?'+csrfToken+'&metodo=visualizzaLista&idRicerca='+idProspetto;
	}

	function listaParametriProspetto(idProspetto) {
		document.location.href='ParametriProspetti.do?'+csrfToken+'&metodo=listaParametriModello&idRicerca='+idProspetto;
	}

	// Azioni di pagina

	function modifica(){
		document.location.href='EditDatiGenProspetto.do?'+csrfToken+'&idRicerca=${idOggetto}';
	}
	
	function eseguiRicerca(){
		bloccaRichiesteServer();
		document.location.href='CheckParametriProspetto.do?'+csrfToken+'&idRicerca=${idOggetto}&fromPage=dettaglioRicerca';
	}

  function mostraProspetto(id, nomeFile){
  	if(confirm('<fmt:message key="info.download.confirm"/>'))
  		document.location.href='DettaglioProspetto.do?'+csrfToken+'&metodo=downloadModello&nomeFile='+nomeFile+'&idRicerca='+id;
  }
-->
</script>
