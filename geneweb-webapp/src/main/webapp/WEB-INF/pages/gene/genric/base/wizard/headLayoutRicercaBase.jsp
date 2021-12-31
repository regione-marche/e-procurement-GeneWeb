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

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE FUNZIONI 
  // JAVASCRIPT DELLA PAGINA LAYOUT CON I TITOLI DELLE COLONNE DEI CAMPI DA 
  // ESTRARRE IN UNA RICERCA BASE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="contenitore" value="${sessionScope.recordDettRicerca}" />

<script type="text/javascript">
<!-- 
	// Azioni di pagina

	function avanti(){
		var esito = true;
		var numeroTitoli = document.listaForm.id.length;
		var arrayLen = "" + numeroTitoli;
		if(arrayLen != 'undefined') {
			for(var i=0; i < numeroTitoli && esito; i++){
				if(document.listaForm.id[i].value.length == 0)
					esito = false;
			}
		} else {
	    	if (document.listaForm.id.value.length == 0)
	    		esito = false;
		}
		if(esito){
			bloccaRichiesteServer();
			document.listaForm.submit();
		} else
			alert('Valorizzare tutti i titoli colonna per proseguire');
	}
	
	function fineWizard(){
		var esito = true;
		var numeroTitoli = document.listaForm.id.length;
		var arrayLen = "" + numeroTitoli;
		if(arrayLen != 'undefined') {
			for(var i=0; i < numeroTitoli && esito; i++){
				if(document.listaForm.id[i].value.length == 0)
					esito = false;
			}
		} else {
	    	if (document.listaForm.id.value.length == 0)
	    		esito = false;
		}
		if(esito){
			document.getElementById("pageFrom").value = "LAY";
			document.listaForm.submit();
		} else
			alert('Valorizzare tutti i titoli colonna per proseguire');
	}
	
	function indietro(){
<c:choose>
 	<c:when test='${fn:length(contenitore.elencoOrdinamenti) eq 0}'>
 		href = 'WizardBase.do?'+csrfToken+'&pageTo=ORD1';
	</c:when>
	<c:otherwise>
 		href = 'WizardBase.do?'+csrfToken+'&pageTo=ORD3';
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