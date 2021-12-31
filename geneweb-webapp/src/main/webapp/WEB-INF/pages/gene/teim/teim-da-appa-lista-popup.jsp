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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="ListaTeim">
	<gene:setString name="titoloMaschera" value="Selezione del tecnico"/>
	<c:set var="tmp" value='${trovaAddWhere}' /> 
	<c:choose>
		<c:when test='${(!empty tmp) and (fn:contains(tmp, "TEIM.CODTIM IN"))}' >
			<c:set var="tipoFiltro" value="1" />
		</c:when>
		<c:otherwise>
			<c:set var="tipoFiltro" value="2" />
		</c:otherwise>
	</c:choose>
	<gene:redefineInsert name="corpo">
	<table class="dettaglio-noBorderBottom">
		<tr>
			<td colspan="2">
				Tipo di selezione:
				<input type="radio" value="1" name="filtroTecnici" <c:if test='${tipoFiltro == 1 }'>checked="checked"</c:if> onclick="javascript:cambiaFiltro(1);" /><span id="tipo"></span>
				&nbsp;
				<input type="radio" value="2" name="filtroTecnici" <c:if test='${tipoFiltro == 2 }'>checked="checked"</c:if>onclick="javascript:cambiaFiltro(2);"/>Tutti
			</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
	</table>
		<gene:formLista entita="TEIM" sortColumn="3" pagesize="20" tableclass="datilista" gestisciProtezioni="true" inserisciDaArchivio='${gene:checkProtFunz(pageContext,"INS","nuovo")}'> 
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
					<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
					</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi veri e propri %>
			<gene:campoLista campo="CODTIM" headerClass="sortable" width="90" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
			<gene:campoLista campo="NOMTIM" headerClass="sortable"/>
			<gene:campoLista campo="CFTIM" headerClass="sortable" width="120"/>
			<gene:campoLista campo="PIVATEI" headerClass="sortable" width="120"/>
		</gene:formLista>
		<gene:javaScript>
  
 	 var parentFormName = "";
 	 parentFormName = eval('window.opener.activeArchivioForm');
	 if(parentFormName == "formTeimLeg")
		document.getElementById("tipo").innerHTML = "Legali rappresentanti dell'impresa esecutrice";
	 if(parentFormName == "formTeimDte")
		document.getElementById("tipo").innerHTML = "Direttori tecnici dell'impresa esecutrice";

	
	function cambiaFiltro(tipoCategoria){
	
		var condizioneWhere = "";
		var nomeCampoArchivio = null;
		
		if(parentFormName == "formTeimLeg"){
			if(document.archivioSchedaForm.archCampoChanged.value == "TEIM.CODTIM")
				nomeCampoArchivio = "APPA_APCLEG";
			if(document.archivioSchedaForm.archCampoChanged.value == "TEIM.NOMTIM")
				nomeCampoArchivio = "APPA_APNLEG";
		} else if(parentFormName == "formTeimDte"){
			if(document.archivioSchedaForm.archCampoChanged.value == "TEIM.CODTIM")
				nomeCampoArchivio = "APPA_APCDTE";
			if(document.archivioSchedaForm.archCampoChanged.value == "TEIM.NOMTIM")
				nomeCampoArchivio = "APPA_APNDTE";
		}
		
		if (parentFormName == "formTeimLeg" && tipoCategoria == 1)
			condizioneWhere = window.opener.activeForm.getValue("WLEGFILTRATI");
		
		if (parentFormName == "formTeimDte" && tipoCategoria == 1)
			condizioneWhere = window.opener.activeForm.getValue("WDTEFILTRATI");
		
		eval("window.opener.document." + parentFormName + ".archWhereLista").value = condizioneWhere;
		
		if(nomeCampoArchivio != null){
			eval("window.opener.document.forms[0]." + nomeCampoArchivio).value = document.archivioSchedaForm.archValueCampoChanged.value.replace("%", "");
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
  </gene:redefineInsert>
</gene:template>
