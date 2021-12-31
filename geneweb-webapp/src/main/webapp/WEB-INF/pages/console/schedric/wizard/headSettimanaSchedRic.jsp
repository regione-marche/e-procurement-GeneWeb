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
		
    if(document.schedRicForm.oraAvvio.value == "" ||
    	document.schedRicForm.minutoAvvio.value == ""){
   		esito = false;
   		alert("Per proseguire impostare correttamente l'ora di avvio");
   	} else if(document.schedRicForm.settimana.value == ""){
   		esito = false;
   		alert("Il campo 'Esegui l\'operazione' prevede un valore obbligatorio");
   	} else if(!document.schedRicForm.opzioneLunedi.checked && 
   						!document.schedRicForm.opzioneMartedi.checked &&
    	 				!document.schedRicForm.opzioneMercoledi.checked &&
    	 				!document.schedRicForm.opzioneGiovedi.checked &&
    	 				!document.schedRicForm.opzioneVenerdi.checked &&
    	 				!document.schedRicForm.opzioneSabato.checked &&
    	 				!document.schedRicForm.opzioneDomenica.checked){
 	 	  esito = false;
 	 	  alert('Per proseguire selezionare almeno un giorno della settimana');
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
-->
</script>