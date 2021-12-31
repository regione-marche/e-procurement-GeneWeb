<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div 	id="menulaterale" class="menulaterale" 
			onMouseover="highlightSubmenuLaterale(event,'on');"
			onMouseout="highlightSubmenuLaterale(event,'off');">
<table>
	<tbody>
		<tr>
			<td class="titolomenulaterale" title="<gene:getString name="titoloMaschera" defaultVal='${gene:resource("label.tags.template.lista.titolo")}'/>">
				${gene:resource("label.tags.template.lista.titoloAzioni")}</td>
		</tr>
		<gene:insert name="listaNuovo">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:listaNuovo();" title="Inserisci" tabindex="1501">
					${gene:resource("label.tags.template.lista.listaNuovo")}</a></td>
		</tr>
		</c:if>
		</gene:insert>
		<gene:insert name="listaEliminaSelezione">
		<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:listaEliminaSelezione();" title="Elimina selezionati" tabindex="1502">
					${gene:resource("label.tags.template.lista.listaEliminaSelezione")}</a>
			</td>
		</tr>
		</c:if>
		</gene:insert>
		<!-- Seziona per aggiungere eventuali nouve voci al menu azioni -->
		<gene:insert name="addToAzioni" />
		<tr>
			<td>&nbsp;</td>
		</tr>
<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.DOCUMENTI")}'>
		<tr>
			<td class="titolomenulaterale">${gene:resource("label.tags.template.documenti.titolo")}</td>
		</tr>
		<c:if test="${not empty gene:getIdPagina(pageContext)}">
			<gene:insert name="helpPagina" >
				<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.HELPPAGINA")}'>
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
		<!-- Seziona per aggiungere eventuali nouve voci al menu documenti -->
		<gene:insert name="addToDocumenti" />
		<tr>
			<td>&nbsp;</td>
		</tr>
</c:if>

<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />

		<gene:insert name="addAzioniContestoBottom" />
	</tbody>
</table>
</div>
