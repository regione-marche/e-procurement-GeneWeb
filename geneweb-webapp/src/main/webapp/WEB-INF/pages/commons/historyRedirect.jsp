<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="it">
<head>
	<title>${applicationScope.appTitle}</title>
</head>
<body onload="javascript:document.redirectForm.submit();">
<form name="redirectForm" id="redirectForm" action="${pageContext.request.contextPath}${historyRedirect.path}" method="post">
			<!-- Scrivo tutti i parametri nella form -->
			<c:set var="elencoParametriArray" value="${fn:join(historyRedirect.parametriMultipli, '#')}#"/>
			<c:forEach items="${historyRedirect.parametri}" var="item">
				<c:set var="chiavePerElenco" value="${item.key}#" />
				<c:if test="${!fn:contains(elencoParametriArray, chiavePerElenco)}">
					<input type="hidden" name="${item.key}" value="<c:out  value='${item.value}' escapeXml='false' />" />
				</c:if>
				<c:if test="${fn:contains(elencoParametriArray, chiavePerElenco)}">
				<c:forEach items="${item.value}" var="values">
					<input type="hidden" name="${item.key}" value="<c:out  value='${values}' escapeXml='false' />" />
				</c:forEach>
				</c:if>
			</c:forEach>
</form>
</body>
</html>
<c:remove var="historyRedirect" scope="session" />