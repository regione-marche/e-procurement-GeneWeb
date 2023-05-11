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
  // JAVASCRIPT DELLA PAGINA CON LA DOMANDA DI DEFINIZIONE DI UNA CONDIZIONE DI
  // FILTRO PER UNA RICERCA BASE
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
		var filtroSi = document.getElementById("filtroSi");
		var filtroNo = document.getElementById("filtroNo");

		if(filtroSi.checked){
			href = 'WizardBase.do?'+csrfToken+'&pageTo=FIL2';
		}
		
		if(filtroNo.checked){
  <c:choose>
  	<c:when test='${fn:length(contenitore.elencoFiltri) eq 0}'>
			href = 'WizardBase.do?'+csrfToken+'&pageTo=ORD1';
		</c:when>
		<c:otherwise>
			href = 'WizardBase.do?'+csrfToken+'&pageTo=FIL3';
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
		document.location.href = 'WizardBase.do?pageTo=DG&pageFrom=FIL1';
	}
	
	function indietro(){
	  document.location.href='WizardBase.do?'+csrfToken+'&pageTo=CAM';
	}

	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
			document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}

-->
</script>