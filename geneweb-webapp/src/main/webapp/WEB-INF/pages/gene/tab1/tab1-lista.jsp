
<%
	/*
	 * Created on 6-Giu-2016
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */

%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="TAB1-LISTA" schema="GENE">

	<gene:redefineInsert name="addHistory">
		<c:if test='${param.metodo ne "nuova"}' >
			<gene:historyAdd titolo='Lista tabellato  ${param.titolo}' id="listaTab" />
		</c:if>
	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value="Lista tabellato ${param.titolo}" />
	<gene:setString name="entita" value="TAB1" />
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td>
					<gene:formLista entita="TAB1" pagesize="20" sortColumn="3;4" tableclass="datilista" 
						gestisciProtezioni="true" where="TAB1COD = '${param.cod}'" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreTab1">
						
						<input type="hidden" name="cod" value="${param.cod}" />
						<input type="hidden" name="titolo" value="${param.titolo}" />
						<gene:campoLista visibile="false">
							<input type="hidden" name="desc" value="${datiRiga.TAB1_TAB1DESC}" />
						</gene:campoLista>
						<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
							<c:if test="${currentRow >= 0}">
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
									<gene:PopUpItemResource	resource="popupmenu.tags.lista.visualizza" title="Visualizza" />
									<c:if test="${datiRiga.TAB1_TAB1MOD ne '1'}">
										<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
											<gene:PopUpItemResource	resource="popupmenu.tags.lista.modifica" title="Modifica" />
										</c:if>
										<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
											<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"	title="Elimina" />
										</c:if>
									</c:if>
								</gene:PopUp>
								<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
									<c:if test="${datiRiga.TAB1_TAB1MOD ne '1'}">
										<input type="checkbox" name="keys" value="${chiaveRiga}" />
									</c:if>
								</c:if>
							</c:if>
						</gene:campoLista>
						<gene:campoLista campo="TAB1COD" />
						<gene:campoLista campo="TAB1NORD" />
						<gene:campoLista campo="TAB1TIP" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>	
						<gene:campoLista campo="TAB1DESC" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>	
						<gene:campoLista campo="TAB1MOD" />
						<gene:campoLista campo="TAB1ARC" />
					</gene:formLista>
				</td>
			</tr>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
	</gene:redefineInsert>
</gene:template>