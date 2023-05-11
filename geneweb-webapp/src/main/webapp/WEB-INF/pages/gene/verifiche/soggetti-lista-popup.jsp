<%
/*
 * Created on: 29-mag-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Lista dei tecnici delle imprese */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:callFunction obj="it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereV_SOGGETTI_VERIFICHEFunction" />

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>

<c:set var="filtroUffint" value=""/> 
<c:set var="nomeContainerFiltri" value="deftrovaV_SOGGETTI_VERIFICHE-${empty param.numeroPopUp ? 0 : param.numeroPopUp}"/> 
<c:if test="${!fn:contains(sessionScope[nomeContainerFiltri].trovaAddWhere, 'CGENTIM') && ! empty sessionScope.uffint && fn:contains(archiviFiltrati,'TEIM')}">
	<c:set var="filtroUffint" value="CGENTIM = '${sessionScope.uffint}'"/>
</c:if>


<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="ListaTeim">
	<gene:setString name="titoloMaschera" value="Selezione del soggetto"/>

	<gene:redefineInsert name="corpo">
		<gene:formLista entita="V_SOGGETTI_VERIFICHE" sortColumn="3" pagesize="20" tableclass="datilista" gestisciProtezioni="true" where="${filtroUffint}"> 

<c:set var="hrefDettaglio" value=""/>

	<c:set var="hrefDettaglio" value="javascript:archivioSeleziona(${datiArchivioArrayJs});"/> 
			<gene:campoLista campo="CODTIM" headerClass="sortable" width="90" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
			<gene:campoLista campo="CFTIM" headerClass="sortable" width="120"/>
			<gene:campoLista campo="NOMTIM" headerClass="sortable" href="${hrefDettaglio}"/>
			<gene:campoLista campo="CODIMP" visibile="false"/>
			<gene:campoLista campo="INCARICO" headerClass="sortable" />
			<gene:campoLista campo="NOMIMP" title="Impresa"/>
			<gene:campoLista campo="CFIMP" title="Codice Fiscale Impresa"/>
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
