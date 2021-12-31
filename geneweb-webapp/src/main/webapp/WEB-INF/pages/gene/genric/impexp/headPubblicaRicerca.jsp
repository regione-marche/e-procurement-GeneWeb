<%/*
   * Created on 23-ago-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE FUNZIONI 
  // JAVASCRIPT DELLA PAGINA CON LA DOMANDA DI PUBBLICAZIONE DI UNA RICERCA
  // DURANTE IL WIZARD DI IMPORTAZIONE REPORT
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
		var href = "PubblicaImport.do?"+csrfToken+"&";
		var pubblicaSi = document.getElementById("pubblicaSi");
		var pubblicaNo = document.getElementById("pubblicaNo");

		if(pubblicaSi.checked){
			href += 'disp=1';
		}
		if(pubblicaNo.checked){
			href += 'disp=0';
		}
		
		if(href.indexOf('disp') >= 0){
			bloccaRichiesteServer();
			document.location.href = href;
		} else {
			alert('Selezionare una delle due opzioni');
		}
	}
	
	function indietro(){
		document.location.href = 'WizardImportRicerca.do?'+csrfToken+'&pageTo=DG';
	}

	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaImport"/>')){
			bloccaRichiesteServer();
		  document.location.href='AnnullaImportExport.do?'+csrfToken;
		}
	}

-->
</script>