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

<script type="text/javascript">
<!-- 

	function avanti(){
		var esito = true;
		
    if(!document.getElementById("tipoG").checked &&
    	!document.getElementById("tipoS").checked &&
    	!document.getElementById("tipoM").checked &&
    	!document.getElementById("tipoU").checked ){
 	 	  esito = false;
  	}
    if(esito){
		  document.schedRicForm.submit();
		} else {
			alert("Per continuare scegliere una delle opzioni disponibili");
		}
	}
	
	function annulla(){
		if (confirm('<fmt:message key="info.schedRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
		  document.location.href='DettaglioSchedRic.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}
	
	function indietro(){
	  document.location.href='WizardSchedRic.do?'+csrfToken+'&pageTo=REP';
	}
-->
</script>