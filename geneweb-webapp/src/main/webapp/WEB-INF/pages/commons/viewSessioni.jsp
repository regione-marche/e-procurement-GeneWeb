<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<html>
<head>
<title>Sessioni Utente Connesse</title>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<META http-equiv="Pragma" content="no-cache">
<META http-equiv="Expires" content="-1">
<c:choose>
<c:when test="${!empty (applicationScope.pathCss)}">
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/css/${applicationScope.pathCss}elda.css">
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/css/${applicationScope.pathCss}elda-custom.css">
</c:when>
<c:otherwise>
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/css/std/elda.css">
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/css/std/elda-custom.css">
</c:otherwise>
</c:choose>
<script type="text/javascript"
	src="${contextPath}/js/general.js"></script>

</head>
<body>

<div style="margin: 10px">

	<div class="contenitore-errori-arealavoro">
		<jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
	</div>

	<h3>Lista delle sessioni di utenti connessi all'applicativo</h3>

	Accessi utente disponibili:
	<c:out value="${requestScope.numConnessioniDisponibili}" />
	su
	<c:out value="${requestScope.numeroMaxConnessioni}" />

		<c:set var="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</c:set>

<form method="POST" action="${pageContext.request.contextPath}/ProcessSessioniAttive.do">
	<table class="griglia">
		<tr>
			<td><b>Seleziona ${titoloMenu}</b></td>
			<td><b>IP</b>
			</td>
			<td><b>Utente</b>
			</td>
			<td><b>Primo accesso</b>
			</td>
			<td><b>Ultimo accesso</b></td>
			<td><b>ID Sessione</b>
			</td>
		</tr>

		<c:forEach items="${requestScope.idSessioniUtentiConnessi}" var="id">
			<c:set var="dati"
				value="${requestScope.datiSessioniUtentiConnessi[id]}" />
			<tr>
				<td><input type="checkbox" name="keys" value="${id}" />
				</td>
				<td><c:out value="${dati[0]}" />
				</td>
				<td><c:out value="${dati[1]}" />
				</td>
				<td><c:out value="${dati[2]}" />
				</td>
				<td>
				<jsp:useBean id="dateValue" class="java.util.Date" /> 
				<jsp:setProperty name="dateValue" property="time" value="${requestScope.sessioniUtenti[id].lastAccessedTime}" /> 
				<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />
				</td>
				<td><c:out value="${id}" />
				</td>
			</tr>
		</c:forEach>
	</table>

<hr/>
Vuoi inviare un messaggio o terminare la sessione a qualche utente? Seleziona gli utenti, inserisci il testo, inserisci il PIN e premi il pulsante <br>
<input type="radio" name="metodo" value="refresh" checked="checked">Aggiorna l'elenco sessioni</input>&nbsp;&nbsp;&nbsp;
<input type="radio" name="metodo" value="sendMessage">Invia messaggi</input>&nbsp;&nbsp;&nbsp;
<input type="radio" name="metodo" value="invalidate">Termina sessioni</input><br/>
Messaggio da inviare: <textarea name="message" cols="40" rows="5"></textarea><br/>
Codice PIN: <input type="password" name="pin" /><br/>
<input type="submit" value="INVIA RICHIESTA"></input>
</form>

</div>

</body>

</html>
