<%/*
   * Created on 12-nov-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

   // PAGINA CHE CONTIENE LA PARTE JAVASCRIPT DELLA PAGINA CON LA DOMANDA SCELTA
   // DI QUALE OPERAZIONE ESEGUIRE: IMPORT O EXPORT DEFINIZIONE REPORT
%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:template file="menuAzioni-template.jsp">
<%
	/* Inseriti i tag per la gestione dell' history:
	 * il template 'menuAzioni-template.jsp' e' un file vuoto, ma e' stato definito 
	 * solo perche' i tag <gene:insert>, <gene:historyAdd> richiedono di essere 
	 * definiti all'interno del tag <gene:template>
	 */
%>
	<gene:historyClear/>
</gene:template>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="contenitore" value="${sessionScope.recordDettRicerca}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">

<!-- 

	// Azioni di pagina

	function avanti(){
		var href = "";
		var importa = document.getElementById("import");
		var exporta = document.getElementById("export");

		if(importa.checked){
			href = 'WizardImportModello.do?'+csrfToken+'&pageTo=UPL';
		}
		
		if(exporta.checked){
			href = 'InitTrovaModelliExport.do?'+csrfToken;
		}
		
		if(href != ""){
			document.location.href = href;
		} else {
			alert('Selezionare una delle due opzioni');
		}
	}

	function annulla(){
		document.location.href = 'AnnullaImportExportModelli.do?'+csrfToken;
	}

-->
</script>