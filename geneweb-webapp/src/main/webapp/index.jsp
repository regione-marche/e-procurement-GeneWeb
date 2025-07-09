<% //Inserisco la Tag Library %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<c:set var="loginMultiEnte" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "loginMultiEnte")}'/>

<html lang="it">
  <head>
    <c:choose>
    <c:when test='${loginMultiEnte eq "1" and empty(sessionScope.uffint)}'>
    <%-- in caso di installazione multiente, la prima cosa da fare risulta la selezione ente, quindi quando viene selezionato si ripassa per usare uno dei sistemi di autenticazione sotto riportati --%>
  	<meta HTTP-EQUIV="REFRESH" content="0; url=<%=request.getContextPath()%>/ListaEntiLogin.do"></meta>
  	</c:when>
    <c:otherwise>
	<meta HTTP-EQUIV="REFRESH" content="0; url=<%=request.getContextPath()%>/InitLogin.do"></meta>
    </c:otherwise>
    </c:choose>
  </head>
</html>
