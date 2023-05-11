<%
/*
 * Created on 14-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA UTENTI
 // DI ASSOCIATI ALL'ENTITA' IN ANALISI (IN FASE DI VISUALIZZAZIONE) CONTENENTE
 // LA SEZIONE JAVASCRIPT
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<script type="text/javascript">
<!--

  function modificaCondivisioneLavoro(){
		document.location.href='ListaPermessiEntita.do?'+csrfToken+'&metodo=modifica&campoChiave=${campoChiave}&valoreChiave=${valoreChiave}&genereGara=${genereGara}';
  }

<c:if test='${empty setPermessiPredefiniti}'>
	function impostaCondivisionePredefinita(){
<c:choose>
	<c:when test='${! empty esisteCondivisionePredefinita}'>
		if(confirm('<fmt:message key="info.permessi.condivisionePredefinita.sovrascrittura"/>')){
	</c:when>
	<c:otherwise>
		if(confirm('<fmt:message key="info.permessi.condivisionePredefinita"/>')){
	</c:otherwise>
</c:choose>
			bloccaRichiesteServer();
			document.location.href='ListaPermessiEntita.do?'+csrfToken+'&metodo=setPermessiPredefiniti&campoChiave=${campoChiave}&valoreChiave=${valoreChiave}&genereGara=${genereGara}';
		}
	}
</c:if>

-->
</script>