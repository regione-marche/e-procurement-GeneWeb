<%
/*
 * Created on 18-gen-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE LA GENERAZIONE DEI MESSAGGI DI ERRORI LATO SERVER
%>
<!-- Parte generale per la visualizzazione dei messaggi javascript-->
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<logic:messagesPresent message="true">
<div class="errori-javascript-titolo">
	<div><img src="${contextPath}/img/jsMsgOn.gif" alt="Messaggi" />Messaggi</div>
	<div class="errori-javascript-dettaglio">
	<ul>
	<html:messages id="error" message="true" property="error">
		<li class="errori-javascript-err">ERRORE:<small>
			<c:choose>
				<c:when test='${fn:startsWith( error, "<HTML>" )}'>
					${fn:substringAfter(error, "<HTML>")}
				</c:when>
				<c:otherwise>
					<c:out value="${error}"/>
				</c:otherwise>
			</c:choose>
		</small></li>
	</html:messages>
	<html:messages id="warning" message="true" property="warning">
		<li class="errori-javascript-war">ATTENZIONE:<small>
			<c:choose>
				<c:when test='${fn:startsWith( warning, "<HTML>")}'>
					${fn:substringAfter(warning, "<HTML>")}
				</c:when>
				<c:otherwise>
					<c:out value="${warning}"/>
				</c:otherwise>
			</c:choose>
		</small></li>
	</html:messages>
	<html:messages id="info" message="true" property="info">
		<li class="errori-javascript-msg">NOTA:<small>
			<c:choose>
				<c:when test='${fn:startsWith( info, "<HTML>")}'>
					${fn:substringAfter(info, "<HTML>")}
				</c:when>
				<c:otherwise>
					<c:out value="${info}"/>
				</c:otherwise>
			</c:choose>
		</small></li>
	</html:messages>
	</ul>
	</div>
</div>
</logic:messagesPresent>
