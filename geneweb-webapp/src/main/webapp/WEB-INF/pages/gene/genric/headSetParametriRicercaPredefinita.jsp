<%/*
   * Created on 21-set-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI SETTING DEI PARAMETRI DI UNA RICERCA IN FASE DI ESTRAZIONE
  // DELLA RICERCA STESSA
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaParametri" value="${sessionScope.recordDettRicerca.elencoParametri}"  scope="request" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript">
<!-- 

<c:set var="array" value='var elencoCodiciParametri = new Array ('/>
<c:forEach items="${listaParametri}" var="parametro" varStatus="status">
	<c:choose>
		<c:when test="${status.last}" >
			<c:set var="array" value='${array} "${parametro.nome}"'/>
		</c:when>
		<c:otherwise>
			<c:set var="array" value='${array} "${parametro.nome}",'/>
		</c:otherwise>
	</c:choose>
</c:forEach>
<c:set var="array" value="${array} );" />
<c:out value="${array}" escapeXml="false" />

<c:set var="array" value='var elencoFormatoParametri = new Array ('/>
<c:forEach items="${listaParametri}" var="parametro" varStatus="status">
	<c:choose>
		<c:when test="${status.last}" >
			<c:set var="array" value='${array} "${fn:trim(parametro.tipoParametro)}"'/>
		</c:when>
		<c:otherwise>
			<c:set var="array" value='${array} "${fn:trim(parametro.tipoParametro)}",'/>
		</c:otherwise>
	</c:choose>
</c:forEach>
<c:set var="array" value="${array} );" />
<c:out value="${array}" escapeXml="false" />
<c:remove var="array" />

	// Azioni invocate dal menu contestuale

	function listaRicerchePredefinite() {
		document.location.href='ListaRicerchePredefinite.do?'+csrfToken;
	}

	function annulla() {
		historyVaiA(0);
	}
	
	function svuotaInput() {
		if (elencoFormatoParametri.length == 1) {
			document.parametriRicercaForm.parametriRicerca.value="";
		} else {
			for (var i = 0; i < elencoFormatoParametri.length; i++)
				document.parametriRicercaForm.parametriRicerca[i].value="";
		}
		<c:if test="${requestScope.kronos eq 1}">
		document.parametriRicercaForm.KRDATINVAL.value="";
		document.parametriRicercaForm.KRDATFINVAL.value="";
		</c:if>
	}

	function eseguiRicerca() {
		var esitoObblig = true;
		var esitoFormato = true;

		<c:if test="${requestScope.kronos eq 1}">
		// controllo data inizio validità
		if (esitoObblig && esitoFormato) {
			esitoObblig = controllaCampoInputObbligatorio(document.parametriRicercaForm.KRDATINVAL, "Data inizio validità");
			if (esitoObblig)
				esitoFormato = controllaInputData(document.parametriRicercaForm.KRDATINVAL);
			if (!esitoObblig || !esitoFormato)
				document.parametriRicercaForm.KRDATINVAL.select();
		}
		// controllo data fine validità
		if (esitoObblig && esitoFormato) {
			esitoObblig = controllaCampoInputObbligatorio(document.parametriRicercaForm.KRDATFINVAL, "Data fine validità");
			if (esitoObblig)
				esitoFormato = controllaInputData(document.parametriRicercaForm.KRDATFINVAL);
			if (!esitoObblig || !esitoFormato)
				document.parametriRicercaForm.KRDATFINVAL.select();
		}
		</c:if>

		if (esitoObblig && esitoFormato) {
			var numeroParametri = document.parametriRicercaForm.numParametri.value;
			if (numeroParametri > 1) {
				for (var i=0; i < numeroParametri && esitoObblig && esitoFormato; i++) {
					esitoObblig = controllaCampoInputObbligatorio(document.parametriRicercaForm.parametriRicerca[i], elencoCodiciParametri[i]);
					if (esitoObblig) {
						esitoFormato = controllaParametro(i);
						if (!esitoObblig || !esitoFormato)
							document.parametriRicercaForm.parametriRicerca[i].select();
					}
				}
			} else if (numeroParametri == 1) {
				esitoObblig = controllaCampoInputObbligatorio(document.parametriRicercaForm.parametriRicerca, elencoCodiciParametri[0]);
				if (esitoObblig)
					esitoFormato = controllaParametro(0);
				if (!esitoObblig || !esitoFormato)
					document.parametriRicercaForm.parametriRicerca.select();
			}
		}
		
    if (esitoObblig && esitoFormato) {
 	  	bloccaRichiesteServer();
		  document.parametriRicercaForm.submit();
		}
	}
	
	function controllaParametro(indice) {
		var numParametri = document.parametriRicercaForm.numParametri.value;
		var campoDiInput = null;
		if (numParametri > 1) {
			campoDiInput = document.parametriRicercaForm.parametriRicerca[indice];
		} else {
			campoDiInput = document.parametriRicercaForm.parametriRicerca;
		}
		
		var esito1 = null;
		if (elencoFormatoParametri[indice] == "T") {
			esito1 = true;
		} else if (elencoFormatoParametri[indice] == "D") {
  		esito1 = controllaInputData(campoDiInput);
		} else if (elencoFormatoParametri[indice] == 'I' || elencoFormatoParametri[indice] == 'UC') {
 		  esito1 = controllaNumeroIntero(campoDiInput);
		} else if (elencoFormatoParametri[indice] == 'F') {
 		  esito1 = controllaNumeroFloating(campoDiInput);
		} else if (elencoFormatoParametri[indice] == 'S' || elencoFormatoParametri[indice] == 'UI') {
	 		esito1 = true;
		}
  	return esito1;
	}
	
	function controllaNumeroIntero(unCampoDiInput) {
		var esitoInteger = isIntero(unCampoDiInput.value);
	  if (!esitoInteger) {
	    alert('Formato del parametro errato: inserire un numero intero');
	  }
	  return esitoInteger;
	}
	
	function controllaNumeroFloating(unCampoDiInput) {
		var esitoFloating = isFloating(unCampoDiInput.value)
	  if (!esitoFloating) {
	    alert('Formato del parametro errato: inserire un numero decimale');
	  }
	  return esitoFloating;
	}
	
	function controllaInputData(unCampoDiInput) {
	  return isData(unCampoDiInput)
	}
	
-->
</script>