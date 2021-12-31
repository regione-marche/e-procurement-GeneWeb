<%/*
   * Created on 20-set-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI CREAZIONE DI UN NUOVO PARAMETRO DA AGGIUNGERE AD UNA RICERCA
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="tipoParam" value="${parametroRicercaForm.tipoParametro}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript"
  src="${contextPath}/js/forms.js"></script>
  
<script type="text/javascript">
<!-- 

	// Azioni invocate dal menu contestuale
	
	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab){
		document.location.href = 'CambiaTabRicerca.do?'+csrfToken+'&tab=' + codiceTab;
	}

	// Azioni di pagina

	function gestisciSubmit(){
		var esito = false;
		if(   controllaCampoInputObbligatorio(document.parametroRicercaForm.nome, 'Nome') 
			 && controllaLunghezzaInput(document.parametroRicercaForm.descrizione, 'Descrizione', 2000)){
			esito = true;	
		}
    if(esito){
    	bloccaRichiesteServer();
		  document.parametroRicercaForm.submit();
		}
	}
	
	function annulla(){
		bloccaRichiesteServer();
		cambiaTab('PAR');
	}
	
-->
</script>