<%
/*
 * Created on: 13-lug-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione degli uffici intestatari */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<gene:callFunction obj="it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUFFINTFunction" />

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint && fn:contains(archiviFiltrati,'UFFINT')}">
	<c:set var="filtroUffint" value="CODEIN = '${sessionScope.uffint}'"/>
</c:if>

<fmt:setBundle basename="AliceResources" />
<c:set var="nomeEntitaSingolaParametrizzata">
	<fmt:message key="label.tags.uffint.singolo" />
</c:set>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<c:set var="nomeContainerFiltri" value="deftrovaUFFINT-${empty param.numeroPopUp ? 0 : param.numeroPopUp}"/> 
<c:set var="tmp" value="${sessionScope[nomeContainerFiltri].trovaAddFilter}" /> 

<c:choose>
	<c:when test='${(!empty tmp) and fn:contains(tmp, "IS NOT NULL")}' >
		<c:set var="tipoAttivazione" value="2" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoAttivazione" value="1" />
	</c:otherwise>
</c:choose>

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="ListaUffint">
	<gene:setString name="titoloMaschera" value="Selezione dell'${fn:toLowerCase(nomeEntitaSingolaParametrizzata)}"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista entita="UFFINT" where="${filtroUffint}" sortColumn="3" pagesize="25" tableclass="datilista" gestisciProtezioni="true" inserisciDaArchivio='${empty filtroUffint && gene:checkProtFunz(pageContext,"INS","nuovo") && !fn:contains(listaOpzioniUtenteAbilitate, "ou214#")}' >
			<table class="dettaglio-noBorderBottom">
				<tr>
					<td colspan="2">
							<input type="radio" value="2" id="radioAttivi" name="filtroAbilitazione" <c:if test='${tipoAttivazione eq 1}'>checked="checked"</c:if> onclick="javascript:cambiaAbilitazione(1);" /> elementi attivi				
							&nbsp;&nbsp;
							<input type="radio" value="1" id="radioDisabilitati" name="filtroAbilitazione" <c:if test='${tipoAttivazione eq 2}'>checked="checked"</c:if> onclick="javascript:cambiaAbilitazione(2);"/>elementi non attivi
							
						
					</td>
				</tr>
			</table>
			
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
<c:if test='${!gene:checkProt(pageContext, "COLS.VIS.GENE.UFFINT.CODEIN")}'>
	<c:set var="hrefDettaglio" value="javascript:archivioSeleziona(${datiArchivioArrayJs});"/> 
</c:if>

			<gene:campoLista campo="CODEIN" headerClass="sortable" width="90" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="NOMEIN" headerClass="sortable" href="${hrefDettaglio}"/>
			<gene:campoLista campo="CFEIN" headerClass="sortable" width="120"/>
			<gene:campoLista campo="ISCUC" visibile="false"/>
		</gene:formLista>
  </gene:redefineInsert>
  <gene:javaScript>
  
	function cambiaAbilitazione(abilitazione){
		var parentFormName = eval('window.opener.activeArchivioForm');

        var functionId = '${param.archFunctionId}'.split('|')[0];
		functionId += "|abilitazione:" + abilitazione;
		functionId += "_parentFormName:" + parentFormName.replace('formUFFINT', '');
		
		eval('window.opener.document.' + parentFormName + '.archFunctionId').value = functionId;
    	
    	var nomeCampoArchivio = null;
		if(parentFormName == "formUFFINTGare"){
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.CODEIN")
				nomeCampoArchivio = "TORN_CENINT";
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.NOMEIN")
				nomeCampoArchivio = "NOMEIN";
		}else if(parentFormName =="formUFFINTTORNAltriSogg" || parentFormName =="formUFFINTGareAltriSogg"){
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.CODEIN")
				nomeCampoArchivio = "CENINT_GARALTSOG";
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.NOMEIN")
				nomeCampoArchivio = "NOMEIN_GARALTSOG";
		}else if(parentFormName == "formUFFINTElenchi" || parentFormName == "formUFFINTTORN" || parentFormName == "formGAREAVVISSITORN"){
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.CODEIN")
				nomeCampoArchivio = "TORN_CENINT";
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.NOMEIN")
				nomeCampoArchivio = "UFFINT_NOMEIN";
		}else if(parentFormName == "formUFFINTPeri" ){
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.CODEIN")
				nomeCampoArchivio = "PERI_CENINT";
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.NOMEIN")
				nomeCampoArchivio = "PERI_ENTINT";
		}else if(parentFormName == "formUFFINTPiatri" ){
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.CODEIN")
				nomeCampoArchivio = "PIATRI_CENINT";
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.NOMEIN")
				nomeCampoArchivio = "PIATRI_NOMEIN";
		}else if(parentFormName == "formUFFINTG1AQSPESA"){
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.CODEIN")
				nomeCampoArchivio = "G1AQSPESA_CENINT";
			if(document.archivioSchedaForm.archCampoChanged.value == "UFFINT.NOMEIN")
				nomeCampoArchivio = "NOMEIN";
		}
				
		if(nomeCampoArchivio != null) {
			eval("window.opener.document.forms[0]." + nomeCampoArchivio).value = document.archivioSchedaForm.archValueCampoChanged.value.replace(/%/g, "");
		}

		// la seguente riga serve a modificare il nome della popup, in modo da
		// gestire la chiusura della presente e la riapertura della stessa in un'altra
		// popup in modo indipendente, evitando un problema di sequenzialità in IE per cui
		// con tale browser la close di una popup non è nel momento atteso		
		window.name = parentFormName + "Old";
		window.opener.arch = window.opener.getArchivio(parentFormName);
		window.opener.arch.fnLista(nomeCampoArchivio);
				
	}
	
		
	</gene:javaScript>
  
</gene:template>
