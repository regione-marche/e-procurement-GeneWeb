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

<script type="text/javascript">
<!-- 

	function avanti(){
		var esito = true;
		
    if(document.schedRicForm.nome.value == ""){
 	 	  esito = false;
 	 	  alert("Il campo 'Nome' prevede un valore obbligatorio");
  	}
	<c:if test='${! empty listaUtentiEsecutori}'>		
		var idAccount = document.schedRicForm.esecutore.selectedIndex;
    if(esito && idAccount < 1){
			esito = false;
			alert("Il campo 'Esegui come utente' prevede un valore obbligatorio");
    }
	</c:if>
	  	
    if(esito){
<% /* Il campo, se usato, e' stato usato per ricordare che il report associato 
		* alla schedulazione e' un report con modello. Prima di salvare la
		* definizione della schedulazione resetto il valore di tale campo 
		*/ %>
    	document.schedRicForm.descFormato.value="";
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
	  document.location.href='WizardSchedRic.do?'+csrfToken+'&pageTo=RIS';
	}
-->
</script>