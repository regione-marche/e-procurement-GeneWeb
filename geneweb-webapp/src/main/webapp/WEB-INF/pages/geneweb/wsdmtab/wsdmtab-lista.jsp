
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

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="WSDMTAB-LISTA" schema="GENE">
	
	<c:set var="descri" value="${gene:callFunction2('it.eldasoft.gene.tags.functions.GetDescriWsdmcftabFunction',  pageContext, param.idcftab)}" scope="request"/>
			
	<gene:redefineInsert name="addHistory">
		<c:if test='${param.metodo ne "nuova"}' >
			<gene:historyAdd titolo='Lista tabellato  ${descri}' id="listaTab" />
		</c:if>
	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value="Lista tabellato ${descri}" />

	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td>
					<gene:formLista entita="WSDMTAB" pagesize="20" sortColumn="7;5" tableclass="datilista" 
						gestisciProtezioni="true"  where="idconfi = ${param.idconfi} and sistema = '${param.sistema}' and idcftab = ${param.idcftab}">
						
						<input type="hidden" name="codice" value="${param.codice}" />
						<input type="hidden" name="codapp" value="${param.codapp}" />
						<input type="hidden" name="idconfi" value="${param.idconfi}" />
						<input type="hidden" name="idcftab" value="${param.idcftab}" />
						<input type="hidden" name="sistema" value="${param.sistema}" />
						
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
						<gene:campoLista campo="ID" visibile="false" />
						<gene:campoLista campo="IDCONFI" visibile="false"/>
						<gene:campoLista campo="CODICE" visibile="false"/>	
						<gene:campoLista campo="VALORE" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>	
						<gene:campoLista campo="DESCRI" />
						<gene:campoLista campo="NUMORD" />
						<gene:campoLista campo="ISARCHI" />
						<gene:campoLista campo="SISTEMA" />
					</gene:formLista>
				</td>
			</tr>
			<gene:redefineInsert name="listaNuovo">
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:listaNuovo();" title="Inserisci" tabindex="1501">
							${gene:resource("label.tags.template.lista.listaNuovo")}</a></td>
				</tr>
			</gene:redefineInsert>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />

			</tr>
		</table>
	</gene:redefineInsert>
</gene:template>