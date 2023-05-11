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

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">
<!-- 

	function avanti(){
		var esito = true;
		
    if(document.schedRicForm.oraAvvio.value == "" || document.schedRicForm.minutoAvvio.value == ""){
   		esito = false;
   		alert("Per proseguire impostare correttamente l'ora di avvio");
   	} else if(document.getElementById("radioGiorno1").checked && document.schedRicForm.giorno.value == ""){
   		esito = false;
   		alert("Il campo 'ogni ... giorni' prevede un valore obbligatorio");
   	} else if(!(controllaCampoInputObbligatorio(document.schedRicForm.dataPrimaEsec, 'Data di inizio'))){
   	  esito = false;
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
	
	function gestioneGiorni() {
		if (document.getElementById('radioGiorno1').checked) {
			document.schedRicForm.giorno.disabled = false;
			document.schedRicForm.ripetiDopoMinuti.value = "";
			document.schedRicForm.ripetiDopoMinuti.disabled = true;
		}
		
		if (document.getElementById('radioGiorno0').checked) {
			document.schedRicForm.ripetiDopoMinuti.disabled = false;
			document.schedRicForm.giorno.value = "";
			document.schedRicForm.giorno.disabled = true;
		}
	}
	
	function controllaInputData(unCampoDiInput){
	  return isData(unCampoDiInput);
	}
-->
</script>