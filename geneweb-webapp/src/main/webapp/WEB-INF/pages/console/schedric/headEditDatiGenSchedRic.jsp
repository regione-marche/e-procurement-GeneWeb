<%
/*
 * Created on 28-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
 // DATI GENERALI DI UNA RICERCA (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA 
 // SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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

	// Azioni di pagina

	function gestisciSubmit(){
	  var esito = true;
	  var esitoMail = true;
	  var esitoFormato = true;

    if(esito && !controllaCampoInputObbligatorio(document.schedRicForm.nome, 'Nome')){
			esito = false;
    }

	<c:if test='${! empty listaUtentiEsecutori}'>		
		var idAccount = document.schedRicForm.esecutore.selectedIndex;
    if(esito && idAccount < 1){
			esito = false;
			alert("Il campo 'Esegui come utente' prevede un valore obbligatorio");
    }
	</c:if>

		var idRicerca = document.schedRicForm.idRicerca.selectedIndex;
		if(esito && idRicerca < 1){
			esito = false;
			alert("Il campo 'Report' prevede un valore obbligatorio");
		}
		
		if(esito && elencoFamiglie[idRicerca-1] != 2){
		  if(document.schedRicForm.formato.value == "-1"){
		  	esito = false;
				alert("Il campo 'Formato' prevede un valore obbligatorio");		  	
	    }
		}
		
    if(esito && (document.schedRicForm.email.value != "")){
    	var listaMail = trim(document.schedRicForm.email).split(',');
		  var esitoMail1 = true;

		  for(var i = 0; i < listaMail.length && esitoMail1; i++){
		  	listaMail[i] = trimStringa(listaMail[i]);
		  	esitoMail1 = isFormatoEmailValido(trimStringa(listaMail[i]));
		  	if(!esitoMail1){
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
			if(elencoFamiglie[idRicerca-1] == 2){
				document.schedRicForm.formato.disabled = true;
			}
			  bloccaRichiesteServer();
			  document.schedRicForm.submit();
		}
	}
	
	function annullaModifiche(){
		bloccaRichiesteServer();
		document.location.href = 'DettaglioSchedRic.do?'+csrfToken+'&metodo=visualizzaDettaglio&idSchedRic=${schedRicForm.idSchedRic}';
	}

<c:set var="array" value='var elencoFamiglie = new Array ('/>
<c:forEach items="${listaRicerche}" var="parametro" varStatus="status">
	<c:choose>
		<c:when test="${status.last}" >
			<c:set var="array" value='${array} \'${parametro.famiglia}\''/>
		</c:when>
		<c:otherwise>
			<c:set var="array" value='${array} \'${parametro.famiglia}\','/>
		</c:otherwise>
	</c:choose>
</c:forEach>
<c:set var="array" value="${array} );" />
<c:out value="${array}" escapeXml="false" />
<c:remove var="array" />

  function checkFormato(){
  	var idRicercaSel = document.schedRicForm.idRicerca.selectedIndex;
  	if(elencoFamiglie[idRicercaSel-1] == 2){
  		document.getElementById("formatoReport").style.display = 'none';
  		document.schedRicForm.formato.selectedIndex = 0;
  		document.getElementById("zeroRecord").style.display = 'none';
  		document.schedRicForm.noOutputVuoto.checked = false;
  	} else {
  		document.getElementById("formatoReport").style.display = '';
  		document.getElementById("zeroRecord").style.display = '';
  		document.schedRicForm.noOutputVuoto.checked = true;
  	}
  }

-->
</script>