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
      // DI DI UN DOCUMENTO ASSOCIATO (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA
      // SEZIONE JAVASCRIPT
    %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<fmt:setBundle basename="AliceResources" />

<script type="text/javascript">
<!--

	// Azioni di pagina

	function modifica(){
		document.location.href='DocumentoAssociato.do?'+csrfToken+'&metodo=modifica&id=${documento.id}';
	}
	
	function mostraDocumento(){
<c:choose>
	<c:when test='${!empty download or !gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.C0OGGASS-Scheda.MOD") or sessionScope.entitaPrincipaleModificabile ne "1"}'>
		if(confirm('<fmt:message key="info.download.confirm"/>'))
			document.location.href='DocumentoAssociato.do?'+csrfToken+'&metodo=download&id=${documento.id}';
	</c:when>
	<c:otherwise>
		<c:set var="nomeCompleto" value="${documento.pathDocAss}${documento.nomeDocAss}"/>
		<c:if test='${fn:indexOf(documento.pathDocAss, "\\\\") eq 0}'>
			<c:set var="nomeCompleto" value="\\${documento.pathDocAss}${documento.nomeDocAss}"/>
		</c:if>
			apriDocumento("${nomeCompleto}");
		</c:otherwise>
</c:choose>
	}

-->
</script>