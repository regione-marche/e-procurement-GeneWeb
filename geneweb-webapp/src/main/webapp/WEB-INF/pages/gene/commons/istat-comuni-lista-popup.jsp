<%
			/*
       * Created on: 14.22 14/03/2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
      /*
				Descrizione:
					Lista degli archivi istat dei comuni
				Creato da:
					Marco Franceschin
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:if test='${(fn:contains(param.archLista, "set=true"))}'>
<gene:callFunction obj="it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereG_COMUNIFunction" />

<gene:template file="popup-template.jsp">
	<c:set var="entita" value="G_COMUNI" /> 
	<c:set var="nomeContainerFiltri" value="deftrovaG_COMUNI-${empty param.numeroPopUp ? 0 : param.numeroPopUp}"/> 
	<c:set var="tmp" value="${sessionScope[nomeContainerFiltri].trovaAddFilter}" /> 
	<gene:setString name="titoloMaschera" value="Seleziona comune"/>
	<gene:redefineInsert name="corpo">
	<c:choose>
		<c:when test='${(fn:contains(param.archLista, "onlyactive=true"))}'>
		<table class="dettaglio-noBorderBottom">
				<tr>
					<td colspan="2">
						Tipo di selezione:
						<input type="radio" value="1" name="filtroTutti" checked/>Comuni attivi
						&nbsp;
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
			</table>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test='${(!empty tmp) and (fn:contains(tmp, "dtfine"))}' >
					<c:set var="tipoFiltro" value="1" />
				</c:when>
				<c:otherwise>
					<c:set var="tipoFiltro" value="2" />
				</c:otherwise>
			</c:choose>
			<table class="dettaglio-noBorderBottom">
				<tr>
					<td colspan="2">
						Tipo di selezione:
						<input type="radio" value="1" name="filtroTutti" <c:if test='${tipoFiltro == 1 }'>checked="checked"</c:if><c:if test='${tipoFiltro == 2 }'>onclick="javascript:cambiaFiltro(1);"</c:if> />Comuni attivi
						&nbsp;
						<input type="radio" value="2" name="filtroAttivi" <c:if test='${tipoFiltro == 2 }'>checked="checked"</c:if><c:if test='${tipoFiltro == 1 }'>onclick="javascript:onCambiaFiltro2();"</c:if>/>Tutti i comuni
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
			</table>
		</c:otherwise>
	</c:choose>
	
		<gene:formLista pagesize="25" tableclass="datilista" 
				entita="G_COMUNI" sortColumn="2" inserisciDaArchivio="false" >
			<gene:campoLista title="Opzioni" width="50" >
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<gene:campoLista title="Comune" campo="DESCRI" definizione="T120" headerClass="sortable" href="javascript:onClick(${datiArchivioArrayJs});"/>
			<gene:campoLista title="Provincia" campo="PROVINCIA" definizione="T120" headerClass="sortable"/>
			<c:if test='${tipoFiltro == 2 }' >
				<gene:campoLista title="Inizio validità" campo="DTINIZIO" definizione="T10" headerClass="sortable"/>
				<gene:campoLista title="Fine validità" campo="DTFINE" definizione="T10" headerClass="sortable"/>
			</c:if>
			<gene:campoLista title="Codice ISTAT" campo="CODISTAT" definizione="T9;1" headerClass="sortable" />
			<gene:campoLista title="C.A.P." campo="CAP" definizione="T5" headerClass="sortable" />
		</gene:formLista>

  </gene:redefineInsert>
</gene:template>
</c:if>
<script>

		function onCambiaFiltro2(){
			var params = '${param.archLista}';
			cambiaFiltro(2);
			eval("window.opener.document." + parentFormName + ".archLista").value = removeFromURL("set=true"); 
		}	
	 
		var parentFormName = "";
		parentFormName = eval('window.opener.activeArchivioForm');
		
		var params = '${param.archLista}';
		var filters = '${param.archFunctionId}';
		var value = '${param.archValueCampoChanged}';
		var campo ='${param.archCampoChanged}';
		
		if(!params.includes("set=true") || campo){		
			if(campo){
				params = params.split("&campo=")[0].split("campo=")[0];
				appendToURL("campo="+campo);
			}
			appendToURL("set=true");
			eval("window.opener.document." + parentFormName + ".archLista").value = params; 
			cambiaFiltro(1);
		}
		function appendToURL(param){
			if(!params.includes("?")){
				params +="?";
			}
			if(!params.includes(param.split("=")[0])){
				params+="&"+param;
			}
			return params.replace("&&","&");
		}
		function removeFromURL(param){
			return params.replace("&"+param,"").replace(param,"").replace("&&","&");
		}
		
		function onClick(datiArchivioArrayJs){
			eval("window.opener.document." + parentFormName + ".archLista").value = removeFromURL("set=true");
			archivioSeleziona(datiArchivioArrayJs);
		}
		
		function cambiaFiltro(tipoCategoria){
			var functionId = "skip";
			
			functionId = '${param.archFunctionId}'.split('|')[0];
			if(value && params.includes("campo=G_COMUNI")){
				var field = params.split("campo=G_COMUNI.")[1].split("&")[0];
				functionId += "|"+field+":"+value.toUpperCase();
			}
			
			if (tipoCategoria == 1) {
				eval("window.opener.document." + parentFormName + ".archLista").value = appendToURL("set=true");
				var day = new Date();
				var dateObject;
				if(params.includes("date=")){
					var date=params.split("date=")[1].split("&")[0]; 
					var dateParts = date.split("/");
					dateObject = new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);
				}
				if(dateObject){
					day = dateObject;
				}
				const formattedDate = day.toLocaleDateString('it-IT', {
				day: '2-digit', month: '2-digit', year: 'numeric'
				}).replace(/ /g, '-');
			
				functionId += (functionId.includes("|")? "_" : "|") +"dateBetween:" + formattedDate;
				
			}
			
			eval("window.opener.document." + parentFormName + ".archFunctionId").value = functionId;
			eval("window.opener.document." + parentFormName + ".archWhereParametriLista").value = '${param.archWhereParametriLista}';
			
			// la seguente riga serve a modificare il nome della popup, in modo da
			// gestire la chiusura della presente e la riapertura della stessa in un'altra
			// popup in modo indipendente, evitando un problema di sequenzialità in IE per cui
			// con tale browser la close di una popup non è nel momento atteso 
			window.name = parentFormName + "Old";
					
			window.opener.arch = window.opener.getArchivio(parentFormName);
			window.opener.arch.fnLista(null); //nomeCampoArchivio
			
		}
		
		var day = new Date();
		var dateObject;
		var params = '${param.archLista}';
		if(params.includes("date=")){
			var date=params.split("date=")[1].split("&")[0]; 
			var dateParts = date.split("/");
			dateObject = new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);
		}
		if(dateObject){
			day = dateObject;
		}
		$(document).ready(function(){
			$("span[id^='colG_COMUNI_DTFINE']").find("span").filter(function(){ 
			  var isOld=false;
			  if($(this).text().length>0){
				var st = $(this).text();
				var dateParts = st.split("/");
				var dt = new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);
				if(dt<day){
					isOld=true;
				}
				return(isOld);  
			} 
		  })
		  .closest("tr").find("span").css({'color' : 'red',   'font-style': 'italic' });
		});
</script>
