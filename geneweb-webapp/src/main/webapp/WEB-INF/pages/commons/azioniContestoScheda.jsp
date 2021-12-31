<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />

<div 	id="menulaterale" class="menulaterale" 
			onMouseover="highlightSubmenuLaterale(event,'on');"
			onMouseout="highlightSubmenuLaterale(event,'off');">
<table>
	<tbody>
		<tr>
			<td class="titolomenulaterale" title="<gene:getString name="titoloMaschera" defaultVal='${gene:resource("label.tags.template.dettaglio.titolo")}' />">
				${gene:resource("label.tags.template.dettaglio.titoloAzioni")}</td>
		</tr>
		
		<!-- Azioni di contesto di una pagina scheda -->
		<c:if test='${tipoPagina eq "SCHEDA"}'>
			<c:choose>
				<c:when test='${modoAperturaScheda eq "MODIFICA" or modoAperturaScheda eq "NUOVO" }'>
					<gene:insert name="schedaConferma">
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:schedaConferma();" title="Salva modifiche" tabindex="1501">
								${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a></td>
					</tr>
					</gene:insert>
					<gene:insert name="schedaAnnulla">
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:schedaAnnulla();" title="Annulla modifiche" tabindex="1502">
							${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}</a></td>
					</tr>
					</gene:insert>
				</c:when>
				<c:otherwise>
					<gene:insert name="schedaModifica">
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:schedaModifica();" title="Modifica dati" tabindex="1501">
							${gene:resource("label.tags.template.dettaglio.schedaModifica")}</a></td>
					</tr>
					</c:if>
					</gene:insert>
					
					<gene:insert name="schedaNuovo" >
					<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
					<tr>
						<td class="vocemenulaterale" >
							<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:schedaNuovo();" title="Inserisci" tabindex="1502"></c:if>
								${gene:resource("label.tags.template.lista.listaNuovo")}
							<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
						</td>
					</tr>
					</c:if>
					</gene:insert>
					
				</c:otherwise>
			</c:choose>
		</c:if>
		<!-- Azioni di contesto di una pagina a lista -->
		<c:if test='${tipoPagina eq "LISTA"}'>
			<gene:insert name="listaNuovo">
			<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
			<tr>
				<td class="vocemenulaterale">
					<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:listaNuovo();" title="Inserisci" tabindex="1501"></c:if>
					${gene:resource("label.tags.template.lista.listaPageNuovo")}
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
			</c:if>
			</gene:insert>
			<gene:insert name="listaEliminaSelezione">
				<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
				<tr>
					<td class="vocemenulaterale">
						<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:listaEliminaSelezione();" title="Elimina selezionati" tabindex="1502"></c:if>
							${gene:resource("label.tags.template.lista.listaEliminaSelezione")}
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
				</c:if>
			</gene:insert>
		</c:if>
		<% // Sezione per aggiungere eventuali nuove voci al menu azioni %>
		<gene:insert name="addToAzioni" />
		<tr>
			<td>&nbsp;</td>
		</tr>

<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.DOCUMENTI")}'>
	<gene:insert name="documentiAzioni">
			<tr>
				<td class="titolomenulaterale">${gene:resource("label.tags.template.documenti.titolo")}</td>
			</tr>
		<c:if test='${tipoPagina eq "SCHEDA" and (empty activePage or activePage eq 0 or (not empty requestScope.modelliPredispostiAttivoIncondizionato))}'>
			<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.W_MODELLI")}'>
				<gene:insert name="modelliPredisposti">
					<tr>
						<c:choose>
			        <c:when test='${isNavigazioneDisattiva ne "1"}'>
			          <td class="vocemenulaterale">
								  <a href="javascript:modelliPredisposti();" title="Modelli predisposti" tabindex="1510">
									  ${gene:resource("label.tags.template.documenti.modelliPredisposti")}
									</a>
			   				</td>
			        </c:when>
			        <c:otherwise>
			          <td>
									${gene:resource("label.tags.template.documenti.modelliPredisposti")}
							  </td>
			        </c:otherwise>
						</c:choose>
					</tr>
				</gene:insert>
			</c:if>
		</c:if>	
		<c:if test='${fn:toUpperCase(entita) ne "C0OGGASS" and tipoPagina ne "LISTA"}' >
			<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.C0OGGASS")}'>
				<gene:insert name="documentiAssociati">
					<tr>
						<c:choose>
			        <c:when test='${isNavigazioneDisattiva ne "1"}'>
			          <td class="vocemenulaterale">
									<a href="javascript:documentiAssociati();" title="Documenti associati" tabindex="1511">
										${gene:resource("label.tags.template.documenti.documentiAssociati")}
									  <c:if test="${not empty requestScope.numRecordDocAssociati}">(${requestScope.numRecordDocAssociati})</c:if>
									</a>
			   				</td>
			        </c:when>
			        <c:otherwise>
			          <td>
								  ${gene:resource("label.tags.template.documenti.documentiAssociati")}
									  <c:if test="${not empty requestScope.numRecordDocAssociati}">(${requestScope.numRecordDocAssociati})</c:if>
							  </td>
			        </c:otherwise>
						</c:choose>
					</tr>
				</gene:insert>
			</c:if>
		</c:if>

		<c:if test='${fn:toUpperCase(entita) ne "G_NOTEAVVISI" and tipoPagina ne "LISTA"}' >
			<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.G_NOTEAVVISI")}'>
				<gene:insert name="noteAvvisi" >
					<tr>
						<c:choose>
							<c:when test='${isNavigazioneDisabilitata ne "1"}'>
								<td class="vocemenulaterale">
									<a href='javascript:noteAvvisi();' title="Note ed avvisi" tabindex="1512">
										${gene:resource("label.tags.template.documenti.noteAvvisi")}
									  <c:if test="${not empty requestScope.numRecordNoteAvvisi}">(${requestScope.numRecordNoteAvvisi})</c:if>
									</a>
								</td>
							</c:when>
							<c:otherwise>
								<td>
									${gene:resource("label.tags.template.documenti.noteAvvisi")}
									  <c:if test="${not empty requestScope.numRecordNoteAvvisi}">(${requestScope.numRecordNoteAvvisi})</c:if>
								</td>
							</c:otherwise>
						</c:choose>
					</tr>
				</gene:insert>
			</c:if>
		</c:if>

		<c:if test="${not empty gene:getIdPagina(pageContext)}">
			<gene:insert name="helpPagina" >
				<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.HELPPAGINA")}'>
				<tr>
					<c:choose>
						<c:when test='${isNavigazioneDisabilitata ne "1"}'>
							<td class="vocemenulaterale"><a
								href='javascript:helpDiPagina("${gene:getIdPagina(pageContext)}");'
								title="Informazioni sulla pagina" tabindex="1513">
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

		<% // Sezione per aggiungere eventuali nouve voci al menu documenti %>
		<gene:insert name="addToDocumenti" />
			<tr>
				<td>&nbsp;</td>
			</tr>
		</gene:insert>
</c:if>

<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />

		<gene:insert name="addAzioniContestoBottom" />
	</tbody>
</table>
</div>