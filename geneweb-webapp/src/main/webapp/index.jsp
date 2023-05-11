<% //Inserisco la Tag Library %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="protocolloSSO" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetProtocolloSSOFunction",  pageContext)}' scope="request" />

<html lang="it">
  <head>
<c:choose>
	<c:when test='${protocolloSSO eq "1"}'>
    <meta HTTP-EQUIV="REFRESH" content="0; url=<%=request.getContextPath()%>/ShibbolethLogin.do"></meta>
	</c:when>
	<c:when test='${protocolloSSO eq "2"}'>
    <meta HTTP-EQUIV="REFRESH" content="0; url=<%=request.getContextPath()%>/CohesionLoginResponseAction.do"></meta>
	</c:when>
	<c:when test='${protocolloSSO eq "3"}'>
    <meta HTTP-EQUIV="REFRESH" content="0; url=<%=request.getContextPath()%>/BartLogin.do"></meta>
	</c:when>
	<c:when test='${protocolloSSO eq "4"}'>
    <meta HTTP-EQUIV="REFRESH" content="0; url=<%=request.getContextPath()%>/OpenIDLogin.do"></meta>
	</c:when>
	<c:when test='${protocolloSSO eq "6"}'>
	<meta HTTP-EQUIV="REFRESH" content="0; url=<%=request.getContextPath()%>/SSOGatewayLogin.do"></meta>
	</c:when>
	<c:otherwise>
    <meta HTTP-EQUIV="REFRESH" content="0; url=<%=request.getContextPath()%>/InitLogin.do"></meta>
	</c:otherwise>
</c:choose>
  
  </head>
</html>
