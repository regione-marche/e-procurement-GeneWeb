<%/*
       * Created on 02-Nov-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE LA DEFINIZIONE DELLE VOCI DEI MENU COMUNI A TUTTE LE APPLICAZIONI
/////////////////////////////////////////////////////////////////
// Modifiche
// ----------------
// 23.10.2007: M.F. Aggiunta delle gestione dell'help da tabella NOTE
/////////////////////////////////////////////////////////////////

%>

<!-- Inserisco la Tag Library -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<c:choose>
	<c:when test="${modo eq 'VISUALIZZA' }">
		<c:set var="modoVisNota" value="1"/>
	</c:when>
	<c:when test="${modo eq 'NUOVO' }">
		<c:set var="modoVisNota" value="2"/>
	</c:when>
	<c:when test="${modo eq 'MODIFICA' }">
		<c:set var="modoVisNota" value="3"/>
	</c:when>
</c:choose>

<c:set var="note" value='${gene:callFunction4("it.eldasoft.gene.tags.functions.GestioneHelpPaginaFunction", pageContext,"2",param.idPagina, "4")}' />
<!-- inserisco il mio tag -->
<gene:template file="popup-template.jsp">
	<!-- Settaggio delle stringhe utilizzate nel template -->
	<gene:setString name="titoloMaschera" value="Pagina:  ${param.idPagina}"/>
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="corpo">
		<div class="help" >
		<gene:formPagine >
			<input type="hidden" name="idPagina" id="idPagina" value="${param.idPagina}" />
			<gene:pagina title="Informazioni" tooltip="Informazioni di pagina" >
				
					<table class="dettaglio-tab-help" >
					<tr>
						<td colspan="2" ><b>Informazioni pagina</b></td>
					</tr>
					<tr>
						<td class="etichetta-dato">Identificativo</td>
						<td class="valore-dato" width="200"><b>${param.idPagina}</b></td>
					</tr>
					<tr>
						<td class="etichetta-dato">Schema di appartenenza</td>
						<td class="valore-dato" width="200"><b>${help.schema}</b></td>
					</tr>
					<tr>
						<td class="etichetta-dato">Descrizione</td>
						<td class="valore-dato">${help.descrizione}</td>
					</tr>
					<!-- <//c:if test='${not empty help.idMaschera}' >
						<tr>
							<td class="etichetta-dato">Maschera derivante</td>
							<td class="valore-dato"><a href='javascript:vaiAMaschera("${help.idMaschera}")'>${help.idMaschera}</a> - ${help.descMaschera}</td>
						</tr>
					<///c:if -->
					<c:if test='${not empty help.pathFile}' >
						<tr>
							<td colspan="2" ><b>Informazioni supplementari</b></td>
						</tr>
						<tr>
							<td colspan="2" >
								<c:import url="${help.pathFile}"/>
							</td>
						</tr>
					</c:if>
					
				</table>
			</gene:pagina>
			<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou59#") || !empty help.nota1 || !empty help.nota2 || !empty help.nota3}'>
			<gene:pagina title="Note" tooltip="Note aggiuntive" >
				<jsp:include page="helpNote.jsp">
					<jsp:param name="tipoNota" value="2"/>
					<jsp:param name="idObj" value="${param.idPagina}"/>
					<jsp:param name="nameInput" value="idPagina"/>
					<jsp:param name="modo" value="${gene:if(empty help.nota1 && empty help.nota2 && empty help.nota3, 'NUOVO', modo)}"/>
				</jsp:include>
			</gene:pagina>
			</c:if>
		</gene:formPagine>
		</div>
  </gene:redefineInsert>
	<gene:javaScript>
		function vaiAMaschera(idMaschera){
			window.location=contextPath+"/ApriPagina.do?href=commons/helpDiPagina.jsp&idPagina="+idMaschera;
		}
	</gene:javaScript>
</gene:template>
