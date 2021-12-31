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
/* Lista storico anagrafe uffici */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="filtroUffint" value="CODEIN = '${param.codiceUfficio}'"/> 

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="ListaStoUffint">
	<gene:setString name="titoloMaschera" value="Lista storico anagrafe uffici"/>
	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GENE.SchedaTecni")}'/>

	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
		<tr><td >
		<gene:formLista entita="STO_UFFINT" sortColumn="-2" pagesize="20" tableclass="datilista" gestisciProtezioni="true" where="${filtroUffint}" 
										gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreSTO_UFFINT"> 
			<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
			<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
				<gene:PopUp variableJs="rigaPopUpMenu" onClick="chiaveRiga='${chiaveRigaJava}'"/>
				<c:if test='${gene:checkProtFunz(pageContext,"DEL","DEL")}'>
					<input type="checkbox" name="keys" value="${chiaveRiga}"  />
				</c:if>
			</gene:campoLista>
			<% // Campi veri e propri %>

			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="DATA_FINE_VALIDITA" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}"/>
			<gene:campoLista campo="NOMEIN"/>
			<gene:campoLista campo="VIAEIN"/>
			<gene:campoLista campo="NCIEIN"/>
			<gene:campoLista campo="CITEIN"/>
			<gene:campoLista campo="CAPEIN"/>
			<gene:campoLista campo="PROEIN"/>
		</gene:formLista>
		</td></tr>
		<gene:redefineInsert name="pulsanteListaInserisci" />
		<gene:redefineInsert name="listaNuovo" />
		<c:if test='${!gene:checkProtFunz(pageContext, "DEL", "DEL")}' >
			<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
			<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
		</c:if>
		<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" /></tr>
		</table>
  </gene:redefineInsert>
	<% //Aggiunta dei menu sulla riga %> 
	<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENE.SchedaTecni")}' >
		<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.visualizza" title="Visualizza storico anagrafica"/>
	</c:if>
	<c:if test='${gene:checkProtFunz(pageContext, "DEL", "DEL")}' >
		<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.elimina" title="Elimina storico anagrafica" />
	</c:if>

</gene:template>
