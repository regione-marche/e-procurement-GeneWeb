<%
/*
 * Created on 19-mar-2007
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


<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript"
  src="${contextPath}/js/forms.js"></script>
  
<script type="text/javascript">
<!--

	function vaiDettaglioRicerca(pos){
	historyVaiIndietroDi(pos);
  }

	var arrayFormatoParametri = new Array();
	<c:forEach items="${listaParametri}" var="parametro" varStatus="ciclo">
		arrayFormatoParametri[${ciclo.index}] = "${fn:trim(parametro.tipo)}";
	</c:forEach>

	function controllaNumeroIntero(unCampoDiInput, posArray){
		var risultato = true;
	  if(!isIntero(unCampoDiInput.value)){
	    alert('Il formato del parametro alla riga ' + (posArray+1) + ' non e\' corretto: inserire un numero intero');
	    unCampoDiInput.focus();
	    risultato = false;
	  }
	  return risultato;
	}
	
	function controllaNumeroFloating(unCampoDiInput, posArray){
		var risultato = true;
	  if(!isFloating(unCampoDiInput.value)){
	    alert('Il formato del parametro alla riga ' + (posArray+1) + ' non e\' corretto:  inserire un numero decimale');
	    unCampoDiInput.focus();
 	    risultato = false;
	  }
	  return risultato;
	}
	
	function controllaInputData(unCampoDiInput, posArray){
		var risultato = true;
	  risultato = isData(unCampoDiInput);
		if(risultato){
	  } else {
	  	alert('Il formato del parametro alla riga ' + (posArray+1) + ' non e\' corretto:  inserire una data nel formato GG/MM/AAAA');
	    unCampoDiInput.focus();
	  }
 	  return risultato;
	}

	function gestisciSubmit() {
		var esito = true;
		
		var arrayLen = arrayFormatoParametri.length;
		for(var i=0; i < arrayLen && esito; i++){
			var obj = document.getElementById(new String("parametro"+i));
			if(arrayFormatoParametri[i] == "D"){
				esito = controllaInputData(obj, i);
			} else if(arrayFormatoParametri[i] == "I"){
				esito = controllaNumeroIntero(obj, i);
			} else if(arrayFormatoParametri[i] == "F"){
				esito = controllaNumeroFloating(obj, i);
			} else if(arrayFormatoParametri[i] == "U"){
				esito = true;
			} else if(arrayFormatoParametri[i] == 'S'){
	 			esito = true;
			}else if(arrayFormatoParametri[i] == 'N'){
	 			esito = true;
			} else if(arrayFormatoParametri[i] == 'T'){
	 			esito = true;
			}
		}
		if (arrayLen > 0) {
		// array di parametri
		<c:forEach items="${listaParametri}" var="parametro" varStatus="ciclo">
			<c:if test="${parametro.obbligatorio eq 1 and parametro.tipo ne 'U'}">
				if (esito && !controllaCampoInputObbligatorio(document.getElementById(new String("parametro${ciclo.index}")), '${parametro.nomePerJs}')){
			  	esito = false;
				}
			</c:if>
		</c:forEach>
		} else {
		// 1 solo parametro
		<c:forEach items="${listaParametri}" var="parametro" varStatus="ciclo">
			<c:if test="${parametro.obbligatorio eq 1}">
	  		if (esito && !controllaCampoInputObbligatorio(document.getElementById(new String("parametro${ciclo.index}")), '${parametro.nomePerJs}')){
	  		  esito = false;
	  		}
			</c:if>
		</c:forEach>
		}

		if (esito){
			//bloccaRichiesteServer();
		  document.parametriProspettoForm.submit();
	  }
	}

-->
</script>