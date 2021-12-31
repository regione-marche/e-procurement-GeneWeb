<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
       /*
       Storia della modifiche
       ----------------------
       Data - Utente - Descrizione
       01/12/2005 M.F. Eliminazione dell'annulla dalla ricerca e spostato inserisci alla fine

       */
%>
<div id="menulaterale" class="menulaterale"
	onMouseover="highlightSubmenuLaterale(event,'on');"
	onMouseout="highlightSubmenuLaterale(event,'off');">
<table>
	<tbody>
		<tr>
			<td class="titolomenulaterale" title="<gene:getString name="titoloMaschera" defaultVal='${gene:resource("label.tags.template.trova.titolo")}' />">
			${gene:resource("label.tags.template.trova.titoloAzioni")}</td>
		</tr>
		<gene:insert name="trovaEsegui">
			<tr>
				<td class="vocemenulaterale">
				<a href="javascript:trovaEsegui();" title="Trova" tabindex="1501">
				${gene:resource("label.tags.template.trova.trovaEsegui")}</a>
				</td>
			</tr>
		</gene:insert>
		<gene:insert name="trovaNuova">
			<tr>
				<td class="vocemenulaterale"><a href="javascript:trovaNuova();"
					title="Reset dei campi di ricerca" tabindex="1502">
				${gene:resource("label.tags.template.trova.trovaNuova")}</a></td>
			</tr>
		</gene:insert>
		<gene:insert name="trovaCreaNuovo">
			<c:if test='${gene:checkProtFunz(pageContext,"INS","TROVANUOVO")}'>
			<tr>
				<td class="vocemenulaterale"><a
					href="javascript:trovaCreaNuovo();" title="Inserisci"
					tabindex="1503">
				${gene:resource("label.tags.template.trova.trovaCreaNuovo")}</a></td>
			</tr>
			</c:if>
		</gene:insert>
		<%  //Sezione per aggiungere eventuali nuove voci al menu azioni %>
		<gene:insert name="addToAzioni" />
		<c:if test="${not empty gene:getIdPagina(pageContext)}">
			<%/* Se c è l identificativo della pagina aggiundo il link ai documenti di testo */%>
			<gene:insert name="helpPagina">
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.HELPPAGINA")}'>
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td class="titolomenulaterale">${gene:resource("label.tags.template.documenti.titolo")}</td>
				</tr>
				<tr>
					<c:choose>
						<c:when test='${isNavigazioneDisabilitata ne "1"}'>
							<td class="vocemenulaterale"><a
								href='javascript:helpDiPagina("${gene:getIdPagina(pageContext)}");'
								title="Informazioni sulla pagina" tabindex="1510">
							${gene:resource("label.tags.template.documenti.informazioniPagina")}
							</a></td>
						</c:when>
						<c:otherwise>
							<td>
							${gene:resource("label.tags.template.documenti.informazioniPagina")}
							</td>
						</c:otherwise>
					</c:choose>
				</tr>
			</c:if>
			</gene:insert>
		</c:if>
		<gene:insert name="addAzioniContestoBottom" />
	</tbody>
</table>
</div>
