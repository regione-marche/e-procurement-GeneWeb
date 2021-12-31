<%/*
   * Created on 29-mar-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI CREAZIONE DI UN NUOVO FILTRO DA AGGIUNGERE AD UNA RICERCA BASE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript">
<!-- 

	function avanti(){
		var esito = true;
		
    if(document.schedRicForm.oraAvvio.value == "" ||
    	document.schedRicForm.minutoAvvio.value == ""){
   		esito = false;
   		alert("Per proseguire impostare correttamente l'ora di avvio");
   	} else if(document.getElementById("radioGiorno0").checked && document.schedRicForm.giorniMese.value == ""){
   		esito = false;
   		alert("Il campo 'giorno ...' prevede un valore obbligatorio");
   	} else if(document.getElementById("radioGiorno1").checked && (document.schedRicForm.settimana.value == "" || document.schedRicForm.giorniSettimana.value == "")){
   		esito = false;
   		alert("Il campo 'ogni' prevede due valori obbligatori");
   	} else if(
   				!document.schedRicForm.opzioneGennaio.checked &&
   				!document.schedRicForm.opzioneFebbraio.checked &&
   				!document.schedRicForm.opzioneMarzo.checked &&
   				!document.schedRicForm.opzioneAprile.checked &&
    	 		!document.schedRicForm.opzioneMaggio.checked &&
    	 		!document.schedRicForm.opzioneGiugno.checked &&
    	 		!document.schedRicForm.opzioneLuglio.checked &&
    	 		!document.schedRicForm.opzioneAgosto.checked &&
    	 		!document.schedRicForm.opzioneSettembre.checked &&
    	 		!document.schedRicForm.opzioneOttobre.checked &&
    	 		!document.schedRicForm.opzioneNovembre.checked &&
    	 		!document.schedRicForm.opzioneDicembre.checked){
 	 	  esito = false;
 	 	  alert('Per proseguire selezionare almeno un mese dell\'anno');
  	}
    if(esito){
		  document.schedRicForm.submit();
		}
	}
	
	function annulla(){
		if (confirm('<fmt:message key="info.schedRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
		  document.location.href='DettaglioSchedRic.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}
	
	function indietro(){
	  document.location.href='WizardSchedRic.do?'+csrfToken+'&pageTo=FRE';
	}
	
	function gestioneGiorniMese() {
	 //alert(document.getElementById('radioGiorno1').checked);
		if (document.getElementById('radioGiorno1').checked) {
			document.schedRicForm.giorniMese.value = "";
			document.schedRicForm.giorniMese.disabled = true;
			document.schedRicForm.giorniSettimana.disabled = false;
			document.schedRicForm.settimana.disabled = false;
		}
		if (document.getElementById('radioGiorno0').checked) {
			document.schedRicForm.giorniMese.disabled = false;
			document.schedRicForm.giorniSettimana.value = "";
			document.schedRicForm.giorniSettimana.disabled = true;
			document.schedRicForm.settimana.value = "";
			document.schedRicForm.settimana.disabled = true;

		}
	}
-->
</script>