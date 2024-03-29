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

<gene:callFunction obj="it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereTECNIFunction" />

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>

<c:set var="filtroUffint" value=""/> 

<c:set var="nomeContainerFiltri" value="deftrovaTECNI-${empty param.numeroPopUp ? 0 : param.numeroPopUp}"/> 
<c:if test="${!fn:contains(sessionScope[nomeContainerFiltri].trovaAddWhere, 'CGENTEI') && ! empty sessionScope.uffint && fn:contains(archiviFiltrati,'TECNI')}">
	<c:set var="filtroUffint" value="CGENTEI = '${sessionScope.uffint}'"/>
</c:if>

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="lista-tecni-popup">
	<gene:setString name="titoloMaschera" value="Selezione del tecnico"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="TECNI" sortColumn="3" gestisciProtezioni="true" inserisciDaArchivio='${gene:checkProtFunz(pageContext,"INS","nuovo")}' where="${filtroUffint}">
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
<c:if test='${!gene:checkProt(pageContext, "COLS.VIS.GENE.TECNI.CODTEC")}'>
	<c:set var="hrefDettaglio" value="javascript:archivioSeleziona(${datiArchivioArrayJs});"/> 
</c:if>

			<gene:campoLista campo="CODTEC" headerClass="sortable" width="90" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="NOMTEC" headerClass="sortable" href="${hrefDettaglio}"/>
			<gene:campoLista campo="CFTEC" headerClass="sortable" width="120"/>
			<gene:campoLista campo="PIVATEC" headerClass="sortable" width="120"/>
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
