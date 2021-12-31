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
		 if (esito && !controllaCampoInputObbligatorio(document.ordinamentoRicercaForm.mnemonicoCampo, 'Campo')){
		 	esito = false;
		 }
		 if (esito){
 	 		 bloccaRichiesteServer();
		 	 document.ordinamentoRicercaForm.submit();
		 }
	}
	
	function annulla(){
		bloccaRichiesteServer();
		cambiaTab('ORD');
	}

-->
</script>
