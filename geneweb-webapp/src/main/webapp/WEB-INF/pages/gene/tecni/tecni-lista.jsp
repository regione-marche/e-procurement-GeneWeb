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
/* Lista dei tecnici progettisti */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint && fn:contains(archiviFiltrati,'TECNI')}">
	<c:set var="filtroUffint" value="CGENTEI = '${sessionScope.uffint}'"/>
</c:if>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="ListaTecni">
	<gene:setString name="titoloMaschera" value="Lista anagrafica dei tecnici"/>
	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GENE.SchedaTecni")}'/>

	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
		<tr><td >
		<gene:formLista entita="TECNI" sortColumn="3" pagesize="20" tableclass="datilista" gestisciProtezioni="true" where="${filtroUffint}" 
										gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreTECNI"> 
			<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
			<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
				<gene:PopUp variableJs="rigaPopUpMenu" onClick="chiaveRiga='${chiaveRigaJava}'"/>
				<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
					<input type="checkbox" name="keys" value="${chiaveRiga}"  />
				</c:if>
			</gene:campoLista>
			<% // Campi veri e propri %>

			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="CODTEC" headerClass="sortable" width="90" />
			<gene:campoLista campo="NOMTEC" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}"/>
			<gene:campoLista campo="CFTEC" headerClass="sortable" width="120"/>
			<gene:campoLista campo="PIVATEC" headerClass="sortable" width="120"/>
		</gene:formLista>
		</td></tr>
		<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" /></tr>
		</table>
  </gene:redefineInsert>
	<% //Aggiunta dei menu sulla riga %> 
	<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENE.SchedaTecni")}' >
		<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.visualizza" title="Visualizza anagrafica tecnico"/>
	</c:if>
	<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENE.SchedaTecni") and gene:checkProtFunz(pageContext, "MOD", "MOD")}' >
		<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.modifica" title="Modifica anagrafica tecnico" />
	</c:if>
	<c:if test='${gene:checkProtFunz(pageContext, "DEL", "DEL")}' >
		<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.elimina" title="Elimina anagrafica tecnico" />
	</c:if>
</gene:template>
