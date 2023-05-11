<%/*
   * Created on 08-mag-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // PAGINA CHE CONTIENE LE VOCI DI AVANZAMENTO DEL WIZARD DI UNA RICERCA BASE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="strPagineVisitate" value=""/>
<c:forEach items="${pagineVisitate}" var="pagina">
	<c:set var="strPagineVisitate" value="${strPagineVisitate} -> ${pagina}"/>	
</c:forEach>
<c:set var="strPagineVisitate" value="${fn:substring(strPagineVisitate, 4, fn:length(strPagineVisitate))}" />

<c:set var="strPagineDaVisitare" value=""/>
<c:if test='${fn:length(pagineDaVisitare) > 0}'>
	<c:forEach items="${pagineDaVisitare}" var="pagina">
		<c:set var="strPagineDaVisitare" value="${strPagineDaVisitare} -> ${pagina}" />	
	</c:forEach>
	<c:set var="strPagineDaVisitare" value="${fn:substring(strPagineDaVisitare, 0, fn:length(strPagineDaVisitare))}" />
</c:if>


<span class="avanzamento-paginevisitate"><c:out value="${fn:trim(strPagineVisitate)}" escapeXml="true" /></span><span class="avanzamento-paginedavisitare"><c:out value="${strPagineDaVisitare}" escapeXml="true" /><c:if test="${fn:length(strPagineDaVisitare) eq 0}">&nbsp;</c:if></span>