<%
/*
 * Created on 27-gen-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE IL CODICE PER VISUALIZZARE LA POPUP DEI GRAFICI
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
</HEAD>

<BODY>
<div class="contenitore-errori-arealavoro"><jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" /></div>

<c:if test="${!empty requestScope.chartFilename}">
	<img src="${pageContext.request.contextPath}/servlet/DisplayChart?filename=${requestScope.chartFilename}" <c:if test="${!empty requestScope.mapHtml}">usemap="#${requestScope.chartFilename}"</c:if>>
	<c:if test="${!empty requestScope.mapHtml}">${requestScope.mapHtml}</c:if>
</c:if>
</BODY>

</HTML>


