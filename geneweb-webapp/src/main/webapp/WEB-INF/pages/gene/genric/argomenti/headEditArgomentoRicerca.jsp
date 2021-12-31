<%/*
   * Created on 07-set-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI MODIFICA DI UN ARGOMENTO ASSOCIATO AD UNA RICERCA
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript">
<!-- 
	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab){
		document.location.href = 'CambiaTabRicerca.do?'+csrfToken+'&tab=' + codiceTab;
	}

	// Azioni di pagina

	function gestisciSubmit(){
		bloccaRichiesteServer();
		document.tabellaRicercaForm.submit();
	}
	
	function annullaModifica(){
		bloccaRichiesteServer();
		cambiaTab('TAB');
	}
	
-->
</script>