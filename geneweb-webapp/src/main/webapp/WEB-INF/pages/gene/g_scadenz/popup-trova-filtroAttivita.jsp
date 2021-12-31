<%
/*
 * Created on: 21-05-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Form per impostare il filtro sulla lista delle attività dello scadenzario */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty param.attivitaPerPagina}'>
		<c:set var="attivitaPerPagina" value="${param.attivitaPerPagina}" />
	</c:when>
	<c:otherwise>
		<c:set var="attivitaPerPagina" value="${attivitaPerPagina}" />
	</c:otherwise>
</c:choose>


<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Imposta filtro"/>
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	
	<gene:redefineInsert name="corpo">
  		<gene:formTrova entita="G_SCADENZ"  >
  			<gene:campoTrova campo="TIPOEV" />
			<gene:campoTrova campo="DATAFI" />
			<gene:campoTrova campo="DATACONS" />
			<tr>
				<td class="etichetta-dato">Stato</td>
				<td class="operatore-trova"/>
				<td class="valore-dato-trova">
					<select id="CampoFitt" name="CampoFitt" title="Stato" onchange="javascript:impostaFiltro(this.options[this.selectedIndex].value);"   >
						<option value="0" selected="selected"></option>
						<option value="1" >Completata</option>
						<option value="2" >Da svolgere</option>
					</select>
				</td>
			</tr>
			
			
			
			<input type="hidden" name="attivitaPerPagina" value="${attivitaPerPagina}"/>
			
		</gene:formTrova>
		
		<gene:javaScript>	
			document.forms[0].jspPathTo.value="gene/g_scadenz/popup-filtro.jsp";
						
			var attivitaPerPagina="${attivitaPerPagina}";
			var indiceSelezionato =  document.getElementById('risultatiPerPagina').selectedIndex;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].value = attivitaPerPagina;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].innerHTML = attivitaPerPagina;
			document.getElementById("risultatiPerPagina").disabled=true
			
			
			function impostaFiltro(valore){
								
				if(valore==1)
					document.forms[0].filtro.value = "G_SCADENZ.DATACONS is not null";
				else if(valore==2)
					document.forms[0].filtro.value = "G_SCADENZ.DATACONS is null";
				else
					document.forms[0].filtro.value ="";
				
			}
			
		</gene:javaScript>
		
	</gene:redefineInsert>
</gene:template>
