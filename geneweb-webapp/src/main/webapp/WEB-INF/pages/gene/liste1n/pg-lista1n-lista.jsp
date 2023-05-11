
<%
	/*
	 * Created on 05-ago-2008
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<%/* Chiamo la funzione che estrae l'elenco dei campi della tabella dinamica figlia 1:N da utilizzare nella lista */%>
<gene:callFunction obj="it.eldasoft.gene.tags.functions.GetElencoCampiLista1NFunction" />

<gene:set name="opzioniLista">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>
<table class="dettaglio-tab-lista">
	<tr>
		<td>
		<gene:formLista
			entita="${param.entita}" pagesize="20"
			where="${param.where}" tableclass="datilista"
			gestisciProtezioni="true" pathScheda="/gene/liste1n/pg-lista1n-scheda.jsp" sortColumn="2">

			<gene:campoLista title="Opzioni <br><center>${opzioniLista}</center>"
				width="50">
				<c:if test="${currentRow >= 0}">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
						onClick="chiaveRiga='${chiaveRigaJava}'">
						<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" />
						<c:if test='${param.modificabile && gene:checkProtFunz(pageContext, "MOD","MOD")}'>
							<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" />
						</c:if>
						<c:if test='${param.modificabile && gene:checkProtFunz(pageContext, "DEL","DEL")}'>
							<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" />
						</c:if>
					</gene:PopUp>
					<c:if test='${param.modificabile && gene:checkProtFunz(pageContext, "DEL","LISTADELSEL")}' >
						<input type="checkbox" name="keys" value="${chiaveRiga}" />
					</c:if>
				</c:if>
			</gene:campoLista>
			
			<c:set var="usatoUnCampoChiave" value="false"/>
			<c:forEach items="${campi}" var="campo">
				<c:set var="hrefCampo" value=""/>
				<c:if test="${!usatoUnCampoChiave && campo.DYNCAM_PK eq 1 && campo.DYNCAM_LIS eq 1}">
					<c:set var="usatoUnCampoChiave" value="true"/>
					<c:set var="hrefCampo" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>									
				</c:if>
				<gene:campoLista campo="${campo.DYNCAM_NAME}" title="${campo.DYNCAM_DESC}" visibile="${campo.DYNCAM_LIS eq 1}" headerClass="sortable" href="${hrefCampo}" gestisciProtezioni="false"/>
			</c:forEach>
			
			<input type="hidden" name="modificabile" value="${param.modificabile}" />
		</gene:formLista></td>
	</tr>
	<c:if test="${param.modificabile}">
	<tr>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiListaPage.jsp" />
	</tr>
	</c:if>
	<c:if test="${!param.modificabile}">
		<gene:redefineInsert name="listaNuovo"  />
		<gene:redefineInsert name="listaEliminaSelezione"  />
	</c:if>
</table>


