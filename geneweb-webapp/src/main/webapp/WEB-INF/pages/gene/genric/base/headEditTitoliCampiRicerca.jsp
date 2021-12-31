<%/*
   * Created on 13-set-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI MODIFICA DI UN ORDINAMENTO APPARTENENTE AD UNA RICERCA
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">
<!-- 

	// Azioni invocate dal menu contestuale
	
	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab){
		document.location.href = 'CambiaTabRicercaBase.do?'+csrfToken+'&tab=' + codiceTab;
	}

	// Azioni di pagina

	function gestisciSubmit(){
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
			document.listaForm.submit();
			bloccaRichiesteServer();
		} else
			alert('Valorizzare tutti i titoli colonna per proseguire');
	}
	
	function annulla(){
		bloccaRichiesteServer();
		cambiaTab('LAY');
	}

-->
</script>