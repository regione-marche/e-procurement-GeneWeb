<%
	/*
	 * Created on: 28-Apr-2008
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
	/* Lista degli utentu */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="lista-template.jsp" gestisciProtezioni="true"
	schema="GENE" idMaschera="UTENT-lista">
	<gene:setString name="titoloMaschera" value="Lista soggetti" />
	<gene:setString name="entita" value="UTENT" />
	<c:set var="visualizzaLink"
		value='${gene:checkProt(pageContext, "MASC.VIS.GENE.UTENT-scheda")}' />

	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td><gene:formLista entita="UTENT" pagesize="20" tableclass="datilista" gestisciProtezioni="true"
					sortColumn="5" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreUTENT">
					<gene:campoLista title="Opzioni<br><center>${titoloMenu}</center>"
						width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
								<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.UTENT-scheda")}'>
									<gene:PopUpItemResource	resource="popupmenu.tags.lista.visualizza" title="Visualizza" />
								</c:if>
								<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.UTENT-scheda") and gene:checkProtFunz(pageContext, "MOD","MOD")}'>
									<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica" />
								</c:if>
								<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
									<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"	title="Elimina" />
								</c:if>
								<jsp:include page="/WEB-INF/pages/gene/utent/editPopUpMenu.jsp" />
							</gene:PopUp>
							<c:if	test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
								<input type="checkbox" name="keys" value="${chiaveRiga}" />
							</c:if>
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="CODUTE" visibile="false" />
					<gene:campoLista campo="COGUTE" headerClass="sortable" title="Cognome soggetto" visibile="false" />
					<gene:campoLista campo="NOMEUTE" headerClass="sortable" title="Nome soggetto" visibile="false" />
					
					<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
					<gene:campoLista campo="NOMUTE" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}" />
					<gene:campoLista campo="CFUTE" headerClass="sortable" />
					<gene:campoLista campo="PIVAUTE" headerClass="sortable" />
				</gene:formLista></td>
			</tr>
			<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" /></tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		<jsp:include page="/WEB-INF/pages/gene/utent/jsEditPopUpMenu.jsp" />
	</gene:javaScript>
</gene:template>