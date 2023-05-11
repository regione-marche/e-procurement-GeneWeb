<%
/*
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE LA  
 // PARTE JAVASCRIPT PER L'UPLOAD DEL FILE DI IMPORT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript">
<!-- 

  function avanti(){
		var esito = true;
			
		if (esito && !controllaCampoInputObbligatorio(document.uploadFileForm.selezioneFile, 'File')){
		  esito = false;
		} else {
			// controllo dell'estensione del file specificato dall'utente
			var nomeFile = new String(document.uploadFileForm.selezioneFile.value);
			var posizione = nomeFile.lastIndexOf(".");
			var estensioneFile = null;
			if(posizione >= 0){
				estensioneFile = nomeFile.substr(posizione+1);
				if(estensioneFile.toLowerCase() != "xml"){
				  esito = false;
					alert("Il file da importare deve avere estensione xml. Cambiare il file specificato");
					document.uploadFileForm.selezioneFile.value = "";
					document.uploadFileForm.selezioneFile.focus();
				}
			} else {
				esito = false;
				alert("Il file da importare deve avere estensione xml. Cambiare il file specificato");
				document.uploadFileForm.selezioneFile.value = "";
				document.uploadFileForm.selezioneFile.focus();
			}
		}
		
		if(esito){
			document.uploadFileForm.submit();
		}
  }

	function annulla(){
		if (confirm('<fmt:message key="info.genMod.annullaImport"/>')){
			bloccaRichiesteServer();
		  document.location.href='AnnullaImportExportModelli.do?'+csrfToken;
		}
	}
	
	function indietro(){
		document.location.href='InitFunzAvanzateModelli.do?'+csrfToken;
	}
	
-->
</script>