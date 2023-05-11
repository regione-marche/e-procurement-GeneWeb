<%
/*
 * Created on: 08-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione del tecnico */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:callFunction obj="it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereIMPRFunction" />

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>

<c:set var="filtroUffint" value=""/> 
<c:set var="nomeContainerFiltri" value="deftrovaIMPR-${empty param.numeroPopUp ? 0 : param.numeroPopUp}"/> 
<c:if test="${!fn:contains(sessionScope[nomeContainerFiltri].trovaAddWhere, 'CGENIMP') && ! empty sessionScope.uffint && fn:contains(archiviFiltrati,'IMPR')}">
	<c:set var="filtroUffint" value="CGENIMP = '${sessionScope.uffint}'"/>
</c:if>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="isPopolatatW_PUSER" value='${gene:callFunction("it.eldasoft.gene.tags.functions.isPopolatatW_PUSERFunction", pageContext)}' />
	
<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="lista-imprese-popup">
	<gene:setString name="titoloMaschera" value="Selezione dell'impresa"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="IMPR" sortColumn="3" gestisciProtezioni="true" inserisciDaArchivio='${gene:checkProtFunz(pageContext,"INS","nuovo")}' where="${filtroUffint}">
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi della lista %>

<c:set var="hrefDettaglio" value=""/>
<c:if test='${!gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CODIMP")}'>
	<c:set var="hrefDettaglio" value="javascript:archivioSeleziona(${datiArchivioArrayJs});"/> 
</c:if>
			
			<gene:campoLista campo="CODIMP" headerClass="sortable" width="90" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="NOMEST" headerClass="sortable" href="${hrefDettaglio}"/>
			<gene:campoLista campo="CFIMP" headerClass="sortable" width="120"/>
			<gene:campoLista campo="PIVIMP" headerClass="sortable" width="120"/>
			<gene:campoLista campo="LOCIMP" headerClass="sortable" width="120"/>
			<gene:campoLista campo="CGENIMP" headerClass="sortable" width="120" visibile='${fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
			<gene:campoLista campo="NOMIMP"  visibile="false"/>
			
			<c:set var="impresaRegistrata" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.ImpresaRegistrataSuPortaleFunction",  pageContext, fn:substringAfter(chiaveRigaJava, ":") )}'/>
				
			<c:if test="${isPopolatatW_PUSER == 'SI'}">
				<gene:campoLista title="&nbsp;" width="20" >
					<c:if test="${impresaRegistrata == 'SI'}">
						<c:choose>
						<c:when test="${requestScope.registrazioneImpPortaleNonCompleta == 'SI'}">
							<img width="16" height="16" title="Impresa con registrazione non completa su portale" alt="Impresa con registrazione non completa su portale" src="${pageContext.request.contextPath}/img/ditta_acquisita_noncompleta.png"/>
						</c:when>
						<c:otherwise>
							<img width="16" height="16" title="Impresa registrata su portale" alt="Impresa registrata su portale" src="${pageContext.request.contextPath}/img/ditta_acquisita.png"/>				
						</c:otherwise>
						</c:choose>
					</c:if>
				</gene:campoLista>
			</c:if>
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
