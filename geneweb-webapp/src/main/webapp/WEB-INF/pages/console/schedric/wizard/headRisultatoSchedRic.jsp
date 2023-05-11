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

	linksetjsPopUpHelpListaMail = "";
	linksetjsPopUpHelpListaMail +=  creaVocePopUpChiusura("${contextPath}/");
	linksetjsPopUpHelpListaMail += creaPopUpSubmenu("javascript:helpListaMail();hideMenuPopup();",0,"&nbsp Guida ");

	function helpListaMail(){
		var action = String("${contextPath}/schedric/ApriHelpListaMail.do");
		openPopUpActionCustom(action, null, "listaMail", 580, 265, false, false);
	}

	function avanti(){
		var esito = true;
		
	<c:if test='${empty schedRicForm.descFormato}'>
    if(esito && document.schedRicForm.formato.value == ""){
 	 	  esito = false;
 	 	  alert("Il campo 'Salva in formato' prevede un valore obbligatorio");
  	}
	</c:if>
	
		if(esito && (document.schedRicForm.email.value != "")){
			var esitoMail = true;
    	var listaMail = trim(document.schedRicForm.email).split(',');

		  for(var i = 0; i < listaMail.length && esitoMail; i++){
		  	listaMail[i] = trimStringa(listaMail[i]);
		  	esitoMail = isFormatoEmailValido(trimStringa(listaMail[i]));
		  	if(!esitoMail){
		  		if(listaMail.length > 1)
		  			alert("La lista degli indirizzi email presenta degli errori di sintassi.\nVerificare la sintassi di tutti gli indirizzi, consultando eventualmente la guida.");
		  		else
		  			alert("L'indirizzo email non e' sintatticamente valido.");
		  		esito = false;
		  		document.schedRicForm.email.focus();
		  	}
		  }
			if(esito){
			  var str = "";
			  for(var i = 0; i < listaMail.length; i++){
			    str += listaMail[i] + ", ";
		  	}
		  	str = str.substring(0, str.length-2);
		  	if((str.length - (listaMail.length -1)) <= 500){
			  	document.schedRicForm.email.value = str;
			  } else {
			  	alert("La lista degli indirizzi email supera di " + (str.length - (listaMail.length -1) - 500) + " caratteri la lunghezza massima del campo 'Email'.\nPer continuare ridurre il numero di indirizzi email specificati.");
			  	esito = false;
			  	document.schedRicForm.email.focus();
			  }
		  }
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
	  document.location.href='WizardSchedRic.do?'+csrfToken+'&pageTo=${pageFrom}';
	}
-->
</script>