<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<tiles:insert  definition=".listaNoAzioniDef" flush="true">

	<tiles:put name="head" type="string">
	</tiles:put>

	<tiles:put name="titoloMaschera" type="string" value="Esecuzione comandi SQL" />
	 
	<tiles:put name="dettaglio" type="string">
	<form name="query" method="post" action="${pageContext.request.contextPath}/Query.do">
		<table class="ricerca">
			<tr>
				<td class="etichetta-dato">Inserire query di SELECT</td>
				<td class="valore-dato">
					<textarea rows="10" cols="80" name="sql" id="sql">${param.sql}</textarea>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Inserire il codice PIN</td>
				<td class="valore-dato">
					<input type="password" name="pin" size="10" />
				</td>
			</tr>
			<tr class="comandi-dettaglio">
				<td colspan="2">
					<input type="submit" value="Estrai" class="bottone-azione"/>
					<input type="button" value="Cancella" class="bottone-azione" onclick="document.getElementById('sql').value = '';"/>&nbsp;
				</td>
			</tr>
		</table>
	</form>
	<c:if test="${not empty requestScope.risultato}">
	<br/>
	<br/>
	Numero record estratti: ${fn:length(requestScope.risultato)}<br/>
	
	<table class="datilista" id="risultatoQuery">
	<c:forEach var="riga" items="${requestScope.risultato}" varStatus="status">
	<c:if test="${status.first}">
	<thead>
	<tr>
		<c:forEach var="nomeColonna" items="${riga}"><th>${nomeColonna.key}</th></c:forEach>
	</tr>
	</thead>
	<tbody>
	</c:if>
	<tr class="<c:if test="${status.index % 2 == 0}">odd</c:if><c:if test="${status.index % 2 == 1}">even</c:if>">
	<c:forEach var="campo" items="${riga}"><td><c:out value="${campo.value}"/></td></c:forEach>
	</tr>
	</c:forEach>
	</tbody>
	</table>
	</c:if>
	</tiles:put>

</tiles:insert>
