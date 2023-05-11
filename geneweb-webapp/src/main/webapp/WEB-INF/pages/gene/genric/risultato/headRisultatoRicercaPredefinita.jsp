<%
/*
 * Created on 22-set-2006
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<script type="text/javascript">
<!-- 

  function listaRicerchePredefinite(){
		document.location.href='ListaRicerchePredefinite.do?'+csrfToken;
  }

  function parametriRicerca(){
  	bloccaRichiesteServer();
		document.location.href='VerificaParametriRicercaPredefinita.do?'+csrfToken;
  }

  function apriPopupGenModelli(){
    var apriPopup = true;
    var numeroOggetti = contaCheckSelezionati(document.risultatoRicerche.id);
    if (numeroOggetti == 0) {
    	var numRecordTotali = ${requestScope.risultatoRicerca.datiRisultato.numeroRecordTotali};
    	var numRecordPagina = ${fn:length(requestScope.risultatoRicerca.datiRisultato.righeRisultato)};
    	var msg = "Nessun elemento selezionato nella lista.\n";
    	if (numRecordTotali == 1)
    		msg += "Procedere ugualmente con l'unico record estratto?";
    	else {
    		msg += "Procedere ugualmente con tutti i " + numRecordTotali + " record estratti?";
    		if (numRecordTotali > numRecordPagina)
    			msg += "\nSi informa che la composizione potrebbe durare molto\no non produrre alcun risultato.";
    	}    	
    	if (!confirm(msg)) apriPopup = false;
    }
    if (apriPopup)
			compositoreModelli('${pageContext.request.contextPath}','${requestScope.risultatoRicerca.datiRisultato.entPrinc}','${fn:join(requestScope.risultatoRicerca.datiRisultato.campiChiave,";")}','risultatoRicerche.id');
	}
  
  function stampa(){
		alert("Attenzione: verrà aperta una popup per consentire la verifica dell'anteprima.\n" + 
		  "La stampa potrà essere attivata selezionando il pulsante \"Stampa\" posto al di sotto dei dati estratti.");
		var w = 600;
		var h = 400;
		var l = Math.floor((screen.width-w)/2);
		var t = Math.floor((screen.height-h)/2); 
		var popup = window.open('${pageContext.request.contextPath}/geneGenric/StampaRisultatoRicerca.do?'+csrfToken,
			'stampa',"status=1,resizable=1,scrollbars=1,width=" + w + ",height=" + h + ",top=" + t + ",left=" + l); 
		popup.focus();
  }
-->
</script>