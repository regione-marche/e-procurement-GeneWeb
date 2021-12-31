<%
/*
 * Created on: 29-02-2012
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>

<c:choose>
	<c:when test="${not empty param.filtroNomest}">
		<c:set var="filtroNomest" value="${param.filtroNomest}"/>
	</c:when>
	<c:otherwise>
		<c:set var="filtroNomest" value="${filtroNomest}"/>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not empty param.tipoRTI}">
		<c:set var="tipoRTI" value="${param.tipoRTI}"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipoRTI" value="${tipoRTI}"/>
	</c:otherwise>
</c:choose>
<c:set var="filtro" value="IMPR.TIPIMP=${tipoRTI}"/>
<c:if test="${! empty filtroNomest}">
	<c:set var="filtro" value="${filtro} and ${filtroNomest }"/>
</c:if>
<c:if test="${! empty sessionScope.uffint && fn:contains(archiviFiltrati,'IMPR') }">
	<c:set var="filtro" value="${filtro } and CGENIMP = '${sessionScope.uffint}'"/>
</c:if>

<c:set var="modo" value="MODIFICA" scope="request" />

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="lista-imprese-popup" >
	<gene:setString name="titoloMaschera" value="Selezione del raggruppamento temporaneo"/>
	
	<gene:redefineInsert name="corpo">
		<table class="lista">
		<tr><td >
			<gene:formLista entita="IMPR" sortColumn="3" pagesize="20" tableclass="datilista"
			where="${filtro}" 
			gestisciProtezioni="true" > 
				<gene:redefineInsert name="listaNuovo" />
				<gene:redefineInsert name="listaEliminaSelezione" />
								
				<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						
						<% //Aggiunta dei menu sulla riga %> 
						
						<gene:PopUpItem title="Seleziona" href="javascript:seleziona('${chiaveRigaJava}',${datiRiga.row });" />
						
					</gene:PopUp>
								
					
				</gene:campoLista>
				<% // Campi veri e propri %>

				<gene:campoLista campo="CODIMP" headerClass="sortable" width="90" href="javascript:seleziona('${chiaveRigaJava}',${datiRiga.row });"/>
				<gene:campoLista campo="NOMEST" headerClass="sortable"/>
				<gene:campoLista campo="CFIMP" headerClass="sortable" width="120"/>
				<gene:campoLista campo="PIVIMP" headerClass="sortable" width="100"/>
				<gene:campoLista campo="LOCIMP" headerClass="sortable" width="120"/>
				<gene:campoLista campo="INTERD" headerClass="sortable" width="80"/>
				<gene:campoLista campo="CODIMP_HIDDEN" visibile="false" edit="true" campoFittizio="true" definizione="T10"/>
				<input type="hidden" name="filtroNomest" id="filtroNomest" value="${filtroNomest}" />
				<input type="hidden" name="tipoRTI" id="tipoRTI" value="${tipoRTI}" />
			</gene:formLista>
		</td></tr>
		</table>
  </gene:redefineInsert>
	<gene:javaScript>
		function seleziona(chiaveRigaJava,indice){
			var codimp= chiaveRigaJava.substring(chiaveRigaJava.indexOf(":")+1);
			window.opener.document.getElementById('codiceRaggruppamento').value=codimp;
			window.opener.bloccaRichiesteServer();
			window.opener.aggiornaTitoli(${tipoRTI});
			window.close();
		}
	</gene:javaScript>	
</gene:template>
