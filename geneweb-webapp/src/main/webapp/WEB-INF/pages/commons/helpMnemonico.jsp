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

<c:set var="note" value='${gene:callFunction4("it.eldasoft.gene.tags.functions.GestioneHelpPaginaFunction", pageContext, "1",param.mnemonico, "#")}' />

<!-- inserisco il mio tag -->
<gene:template file="popup-template.jsp">
	<!-- Settaggio delle stringhe utilizzate nel template -->
	<gene:setString name="titoloMaschera" value="Campo ${param.mnemonico}"/>
	<gene:redefineInsert name="addHistory"/>
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="corpo">
		<div class="help" >
		<gene:formPagine >
			<input type="hidden" name="mnemonico" id="mnemonico" value="${param.mnemonico}" />
			<gene:pagina title="Informazioni" tooltip="Informazioni mnemonico" >
				
					<table class="dettaglio-tab-help" >
					<tr>
						<td colspan="2" ><b>Informazioni campo</b></td></tr>
					<tr>
						<td class="etichetta-dato"><span ondblclick="alert('${help.nomeFisico}')">Campo</span></td>
						<td class="valore-dato" width="200"><b>${param.mnemonico}</b></td>
					</tr>
					<tr>
						<td class="etichetta-dato">Descrizione</td>
						<td class="valore-dato">${help.descrizione}</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Formato</td>
						<td class="valore-dato">${help.tipoColonna}</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Mnemonico da utilizzare nel compositore</td>
						<td class="valore-dato">#${param.mnemonico}#</td>
					</tr>
						<c:if test="${help.isFlag}">
					<tr>
						<td class="etichetta-dato">Elenco valori</td>
						<td class="valore-dato">${help.elencoValori}</td>
					</tr>
						</c:if>
					<c:if test="${not empty help.pathFileHelp}">
					<tr>
						<td colspan="2" ><b>Informazioni supplementari</b></td></tr>
					<tr>
					<tr>
						<td class="etichetta-dato">Spiegazioni aggiuntive</td>
						<td class="valore-dato"><c:import url="${help.pathFileHelp}"/></td>
					</tr>
					</c:if>
					<tr>
						<td colspan="2"><b>Informazioni entità di appartenenza</b></td>
					</tr>
					<tr>
						<td class="etichetta-dato">Schema di appartenenza</td>
						<td class="valore-dato"><b>${help.schema}</b></td>
					</tr>
					<tr>
						<td class="etichetta-dato">Descrizione schema</td>
						<td class="valore-dato">${help.descrSchema}</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Entità di appartenenza</td>
						<td class="valore-dato"><b>${help.entita}</b></td>
					</tr>
					<tr>
						<td class="etichetta-dato">Descrizione entità</td>
						<td class="valore-dato">${help.descrEntita}</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Chiave entità</td>
						<td class="valore-dato">${help.chiavi}</td>
					</tr>
						<c:if test="${help.isTabellato}">
					<tr>
						<td colspan="2"><b>Informazioni tabellato</b></td>
					</tr>
					<tr>
						<td class="etichetta-dato">Codice tabellato</td>
						<td class="valore-dato"><b>${help.codTab}</b></td>
					</tr>
					<tr>
						<td class="etichetta-dato">Elenco valori</td>
						<td class="valore-dato">${help.elencoValori}</td>
					</tr>
						</c:if>
				</table>
			</gene:pagina>
			<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou59#") || !empty help.nota}'>
			<gene:pagina title="Note" tooltip="Note aggiuntive" >
				<jsp:include page="helpNote.jsp">
					<jsp:param name="tipoNota" value="1"/>
					<jsp:param name="idObj" value="${param.mnemonico}"/>
					<jsp:param name="nameInput" value="mnemonico"/>
					<jsp:param name="modo" value="${gene:if(empty help.nota, 'NUOVO', modo)}"/>
				</jsp:include>
			</gene:pagina>
			</c:if>
		</gene:formPagine>
		</div>
  </gene:redefineInsert>

</gene:template>
