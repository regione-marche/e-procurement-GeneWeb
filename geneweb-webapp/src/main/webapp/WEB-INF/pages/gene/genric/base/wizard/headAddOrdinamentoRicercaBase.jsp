<%/*
   * Created on 04-mag-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI CREAZIONE DI UN NUOVO ORDINAMENTO DA AGGIUNGERE AD UNA RICERCA BASE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript" src="${contextPath}/js/forms.js"></script>

<script type="text/javascript">
<!-- 

	// Azioni invocate dal menu contestuale

	// Azioni di pagina

	function avanti(){
		var esito = true;
		
    if(document.ordinamentoRicercaForm.mnemonicoCampo.value == "" ||
       document.ordinamentoRicercaForm.ordinamento.value == ""){
 	 	  esito = false;
  	}
    if(esito){
    	bloccaRichiesteServer();
		  document.ordinamentoRicercaForm.submit();
		} else {
			alert("E' necessario popolare tutti i campi presenti nella pagina.");
		}
	}
	
	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
		  document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}

	function indietro(){
	<c:choose>
		<c:when test='${fn:length(ordinamentoRicercaForm.progressivo) > 0}'>
			document.location.href = 'WizardBase.do?'+csrfToken+'&pageTo=ORD3';
		</c:when>
		<c:otherwise>
			document.location.href = 'WizardBase.do?'+csrfToken+'&pageTo=ORD1';
		</c:otherwise>
	</c:choose>
	}

-->
</script>