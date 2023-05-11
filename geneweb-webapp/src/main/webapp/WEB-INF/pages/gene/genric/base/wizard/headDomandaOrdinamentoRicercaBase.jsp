<%/*
   * Created on 02-mag-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE FUNZIONI 
  // JAVASCRIPT DELLA PAGINA CON LA DOMANDA DI DEFINIZIONE DI UN ORDINAMENTO
  // PER UNA RICERCA BASE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="contenitore" value="${sessionScope.recordDettRicerca}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">
<!-- 

	// Azioni di pagina

	function avanti(){
		var href = "";
		var ordinamentoSi = document.getElementById("ordinamentoSi");
		var ordinamentoNo = document.getElementById("ordinamentoNo");

		if(ordinamentoSi.checked){
			href = 'WizardBase.do?'+csrfToken+'&pageTo=ORD2';
		}
		
		if(ordinamentoNo.checked){
  <c:choose>
  	<c:when test='${fn:length(contenitore.elencoOrdinamenti) eq 0}'>
			href = 'WizardBase.do?'+csrfToken+'&pageTo=LAY';
		</c:when>
		<c:otherwise>
			href = 'WizardBase.do?'+csrfToken+'&pageTo=ORD3';
		</c:otherwise>
	</c:choose>
		}
		
		if(href != ""){
			bloccaRichiesteServer();
			document.location.href = href;
		} else {
			alert('Selezionare una delle due opzioni');
		}
	}
	
	function fineWizard(){
		document.location.href = 'WizardBase.do?'+csrfToken+'&pageTo=DG&pageFrom=ORD1';
	}
	
	function indietro(){
		var href = "";
		<c:choose>
		 	<c:when test='${fn:length(contenitore.elencoFiltri) eq 0}'>
				href = 'WizardBase.do?'+csrfToken+'&pageTo=FIL1';
			</c:when>
			<c:otherwise>
				href = 'WizardBase.do?'+csrfToken+'&pageTo=FIL3';
			</c:otherwise>
		</c:choose>
		if(href != "")
			document.location.href = href;
	}

	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
			document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}

-->
</script>