<%
/*
 * Created on 15-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE IL CODICE PER CHIUDERE UNA POPUP E REFRESHARE IL CHIAMANTE
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML>
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
</HEAD>


<c:choose>
<c:when test='${!empty (requestScope.url)}' >
<BODY onload="javascript:window.opener.location='${contextPath}${requestScope.url}';window.close();">
</c:when>
<c:otherwise>
<BODY onload="javascript:window.opener.location.reload(true);window.close();">
</c:otherwise>
</c:choose>
</BODY>

</HTML>


