<%/*
   * Created on 24-lug-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
  // DI UN DOCUMENTO ASSOCIATO (IN FASE DI EDIT) CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript"
  src="${contextPath}/js/forms.js"></script>
<script type="text/javascript">
<!--

	// Azioni di pagina

	function gestisciSubmit(){
		var continua = true;
		//Controllo del titolo del documento associato
		if(continua && !controllaCampoInputObbligatorio(document.documentoAssociatoForm.titolo, 'Titolo')){
			continua = false;
		}
	<c:if test='${empty documento.id}'>
			//Controllo che il file sia stato inserito
		if(!controllaCampoInputObbligatorio(document.documentoAssociatoForm.selezioneFile, 'Nome File')){
			continua = false;
		}
	</c:if>
		
		if(continua)
			document.documentoAssociatoForm.submit();
	}

	function download(id){
		document.location.href='DocumentoAssociato.do?'+csrfToken+'&metodo=download&id=${documento.id}';
	}
	
	function annulla(){
		document.documentoAssociatoForm.metodo.value = "annulla";
		document.documentoAssociatoForm.submit();
	}
	
	function annullaModifiche(id){
		document.documentoAssociatoForm.metodo.value = "annullaModifiche";
		document.documentoAssociatoForm.submit();
	}
	
	function controllaInputData(unCampoDiInput){
	  return isData(unCampoDiInput);
	}
	
-->
</script>
